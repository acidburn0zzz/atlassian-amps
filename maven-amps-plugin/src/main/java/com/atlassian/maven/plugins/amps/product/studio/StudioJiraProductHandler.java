package com.atlassian.maven.plugins.amps.product.studio;

import com.atlassian.maven.plugins.amps.MavenContext;
import com.atlassian.maven.plugins.amps.MavenGoals;
import com.atlassian.maven.plugins.amps.Product;
import com.atlassian.maven.plugins.amps.ProductArtifact;
import com.atlassian.maven.plugins.amps.product.JiraProductHandler;
import com.atlassian.maven.plugins.amps.util.ConfigFileUtils;
import com.atlassian.maven.plugins.amps.util.ConfigFileUtils.Replacement;
import com.atlassian.maven.plugins.amps.util.JvmArgsFix;
import com.google.common.collect.Lists;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.atlassian.maven.plugins.amps.product.ProductHandlerFactory.STUDIO_JIRA;

/**
 * Handler for Studio-JIRA
 * @since 3.6
 */
public class StudioJiraProductHandler extends JiraProductHandler implements StudioComponentProductHandler
{
    public StudioJiraProductHandler(final MavenContext context, final MavenGoals goals, ArtifactFactory artifactFactory)
    {
        super(context, goals,artifactFactory);
    }


    @Override
    public String getId()
    {
        return STUDIO_JIRA;
    }

    @Override
    public ProductArtifact getArtifact()
    {
        return new ProductArtifact("com.atlassian.jira", "jira-ondemand-webapp", "RELEASE");
    }

    @Override
    protected void customiseInstance(Product ctx, File homeDir, File explodedWarDir) throws MojoExecutionException
    {

        // change database to hsql
        List<File> configFiles = Lists.newArrayList();
        configFiles.add(new File(explodedWarDir, "WEB-INF/classes/entityengine.xml"));
        configFiles.add(new File(explodedWarDir, "WEB-INF/web.xml"));

        List<Replacement> replacements = Lists.newArrayList();
        replacements.add(new Replacement("field-type-name=\"postgres72\"", "field-type-name=\"hsql\"", false));
        replacements.add(new Replacement("schema-name=\"public\"", "schema-name=\"PUBLIC\"", false));
        replacements.add(new Replacement("%JIRA-HOME%", homeDir.getAbsolutePath()));

        ConfigFileUtils.replace(configFiles, replacements, false, log);

        File importsDir = new File(homeDir, "import");
        if (importsDir.exists())
        {
            configFiles = Lists.newArrayList(importsDir.listFiles(new FilenameFilter()
            {
                @Override
                public boolean accept(File dir, String name)
                {
                    return name.endsWith(".xml");
                }
            }));
            replacements = Lists.newArrayList(new Replacement("%JIRA-HOME%", homeDir.getAbsolutePath()));
            ConfigFileUtils.replace(configFiles, replacements, false, log);
        }

        StudioProductHandler.addProductHandlerOverrides(log, ctx, homeDir, explodedWarDir);
    }

    // JIRA needs a bit more heap and PermGen - default is -Xmx512m -XX:MaxPermSize=256m (see JvmArgsFix)
    protected void fixJvmArgs(Product ctx)
    {
        final String jvmArgs = JvmArgsFix.empty()
                .with("-Xms", "256m")
                .with("-Xmx", "768m")
                .with("-XX:MaxPermSize=", "512m")
                .apply(ctx.getJvmArgs());
        ctx.setJvmArgs(jvmArgs);
    }

    @Override
    public Map<String, String> getSystemProperties(Product product)
    {
        Map<String, String> properties = new HashMap<String, String>(super.getSystemProperties(product));

        // We also add common studio system properties
        properties.putAll(product.getStudioProperties().getSystemProperties());

        return properties;
    }
}