package com.atlassian.maven.plugins.amps;

import java.io.File;
import java.util.List;

import com.atlassian.maven.plugins.amps.codegen.ConditionFactory;
import com.atlassian.maven.plugins.amps.codegen.ContextProviderFactory;
import com.atlassian.maven.plugins.amps.codegen.PluginModuleSelectionQueryer;
import com.atlassian.maven.plugins.amps.codegen.jira.ActionTypeFactory;
import com.atlassian.maven.plugins.amps.codegen.jira.CustomFieldSearcherFactory;
import com.atlassian.maven.plugins.amps.codegen.jira.CustomFieldTypeFactory;
import com.atlassian.maven.plugins.amps.codegen.prompter.PluginModulePrompter;
import com.atlassian.maven.plugins.amps.codegen.prompter.PluginModulePrompterFactory;
import com.atlassian.maven.plugins.amps.product.ProductHandlerFactory;
import com.atlassian.maven.plugins.amps.util.GoogleAmpsTracker;
import com.atlassian.plugins.codegen.MavenProjectRewriter;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.PluginXmlRewriter;
import com.atlassian.plugins.codegen.ProjectFilesRewriter;
import com.atlassian.plugins.codegen.modules.PluginModuleCreator;
import com.atlassian.plugins.codegen.modules.PluginModuleCreatorFactory;
import com.atlassian.plugins.codegen.modules.PluginModuleLocation;
import com.atlassian.plugins.codegen.modules.PluginModuleProperties;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * @since 3.6
 */
@Mojo(name = "create-plugin-module", requiresDependencyResolution = ResolutionScope.COMPILE)
public class PluginModuleGenerationMojo extends AbstractProductAwareMojo
{

    @Component
    private PluginModuleSelectionQueryer pluginModuleSelectionQueryer;

    @Component
    private PluginModulePrompterFactory pluginModulePrompterFactory;

    @Component
    private PluginModuleCreatorFactory pluginModuleCreatorFactory;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        getUpdateChecker().check();

        trackFirstRunIfNeeded();
        
        //can't figure out how to get plexus to fire a method after injection, so doing it here
        pluginModulePrompterFactory.setLog(getLog());
        try
        {
            pluginModulePrompterFactory.scanForPrompters();
        } catch (Exception e)
        {
            String message = "Error initializing Plugin Module Prompters";
            getLog().error(message);
            throw new MojoExecutionException(message);
        }

        String productId = getProductId();

        MavenProject project = getMavenContext().getProject();
        File javaDir = getJavaSourceRoot(project);
        File testDir = getJavaTestRoot(project);
        File resourcesDir = getResourcesRoot(project);

        initHelperFactories(productId, project);

        PluginModuleLocation moduleLocation = new PluginModuleLocation.Builder(javaDir)
                .resourcesDirectory(resourcesDir)
                .testDirectory(testDir)
                .templateDirectory(new File(resourcesDir, "templates"))
                .groupAndArtifactId(project.getGroupId(), project.getArtifactId())
                .build();

        if (!moduleLocation.getPluginXml()
                .exists())
        {
            String message = "Couldn't find the atlassian-plugin.xml, please run this goal in an atlassian plugin project root.";
            getLog().error(message);
            throw new MojoExecutionException(message);
        }

        runGeneration(productId, project, moduleLocation);

    }

    private void runGeneration(String productId, MavenProject project, PluginModuleLocation moduleLocation) throws MojoExecutionException
    {
        PluginModuleCreator creator = null;
        try
        {
            creator = pluginModuleSelectionQueryer.selectModule(pluginModuleCreatorFactory.getModuleCreatorsForProduct(productId));

            String trackingLabel = getPluginInformation().getId() + ":" + creator.getModuleName();
            getGoogleTracker().track(GoogleAmpsTracker.CREATE_PLUGIN_MODULE,trackingLabel);

            PluginModulePrompter modulePrompter = pluginModulePrompterFactory.getPrompterForCreatorClass(creator.getClass());
            if (modulePrompter == null)
            {
                String message = "Couldn't find an input prompter for: " + creator.getClass()
                        .getName();
                getLog().error(message);
                throw new MojoExecutionException(message);
            }

            modulePrompter.setDefaultBasePackage(project.getGroupId());
            modulePrompter.setPluginKey(project.getGroupId() + "." + project.getArtifactId());

            PluginModuleProperties moduleProps = modulePrompter.getModulePropertiesFromInput(moduleLocation);
            moduleProps.setProductId(getGadgetCompatibleProductId(productId));

            PluginProjectChangeset changeset = creator.createModule(moduleProps);

            getLog().info("Adding the following items to the project:");
            for (String desc : changeset.getChangeDescriptionsOrSummaries())
            {
                getLog().info("  " + desc);
            }

            // edit pom if needed
            try
            {
                new MavenProjectRewriter(project.getFile()).applyChanges(changeset);
            }
            catch (Exception e)
            {
                getLog().error("Unable to apply changes to POM: " + e);
            }

            // apply changes to project files
            new ProjectFilesRewriter(moduleLocation).applyChanges(changeset);
            new PluginXmlRewriter(moduleLocation).applyChanges(changeset);

            if (pluginModuleSelectionQueryer.addAnotherModule())
            {
                runGeneration(productId, project, moduleLocation);
            }

        } catch (Exception e)
        {
            e.printStackTrace();
            throw new MojoExecutionException("Error creating plugin module", e);
        }

    }

    private String getGadgetCompatibleProductId(String pid)
    {
        String productId = pid;
        if (ProductHandlerFactory.JIRA
                .equals(pid))
        {
            productId = "JIRA";
        } else if (ProductHandlerFactory.CONFLUENCE
                .equals(pid))
        {
            productId = "Confluence";
        } else if (ProductHandlerFactory.BAMBOO
                .equals(pid))
        {
            productId = "Bamboo";
        } else if (ProductHandlerFactory.CROWD
                .equals(pid))
        {
            productId = "Crowd";
        } else if (ProductHandlerFactory.FECRU
                .equals(pid))
        {
            productId = "FishEye";
        } else
        {
            productId = "Other";
        }

        return productId;

    }

    private File getJavaSourceRoot(MavenProject project)
    {
        return new File(project.getModel()
                .getBuild()
                .getSourceDirectory());
    }

    private File getJavaTestRoot(MavenProject project)
    {
        return new File(project.getModel()
                .getBuild()
                .getTestSourceDirectory());
    }

    private File getResourcesRoot(MavenProject project)
    {
        File resourcesRoot = null;
        for (Resource resource : (List<Resource>) project.getModel()
                .getBuild()
                .getResources())
        {
            String pathToCheck = "src" + File.separator + "main" + File.separator + "resources";
            if (StringUtils.endsWith(resource.getDirectory(), pathToCheck))
            {
                resourcesRoot = new File(resource.getDirectory());
            }
        }
        return resourcesRoot;
    }

    private void initHelperFactories(String productId, MavenProject project) throws MojoExecutionException
    {
        List<String> pluginClasspath;
        try
        {
            pluginClasspath = project.getCompileClasspathElements();
        } catch (DependencyResolutionRequiredException e)
        {
            throw new MojoExecutionException("Dependencies MUST be resolved", e);
        }

        try
        {
            ConditionFactory.locateAvailableConditions(productId, pluginClasspath);
        } catch (Exception e)
        {
            String message = "Error initializing Plugin Module Conditions";
            getLog().error(message);
            //keep going, doesn't matter
        }

        try
        {
            ContextProviderFactory.locateAvailableContextProviders(productId, pluginClasspath);
        } catch (Exception e)
        {
            String message = "Error initializing Plugin Module Context Providers";
            getLog().error(message);
            //keep going, doesn't matter
        }

        if (ProductHandlerFactory.JIRA
                .equals(productId))
        {
            try
            {
                ActionTypeFactory.locateAvailableActionTypes(pluginClasspath);
            } catch (Exception e)
            {
                String message = "Error initializing JIRA Action Types";
                getLog().error(message);
                //keep going, doesn't matter
            }

            try
            {
                CustomFieldTypeFactory.locateAvailableCustomFieldTypes(pluginClasspath);
            } catch (Exception e)
            {
                String message = "Error initializing JIRA Custom Field Types";
                getLog().error(message);
                //keep going, doesn't matter
            }

            try
            {
                CustomFieldSearcherFactory.locateAvailableCustomFieldSearchers(pluginClasspath);
            } catch (Exception e)
            {
                String message = "Error initializing JIRA Custom Field Searchers";
                getLog().error(message);
                //keep going, doesn't matter
            }
        }
    }
}
