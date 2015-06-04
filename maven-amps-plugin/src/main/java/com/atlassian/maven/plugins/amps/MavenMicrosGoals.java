package com.atlassian.maven.plugins.amps;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;

import com.atlassian.maven.plugins.amps.util.AmpsCreatePluginPrompter;
import com.atlassian.maven.plugins.amps.util.CreateMicrosProperties;
import com.atlassian.maven.plugins.amps.util.VersionUtils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.twdata.maven.mojoexecutor.MojoExecutor;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

public class MavenMicrosGoals extends MavenGoals
{

    private static final String MICROS_SERVICE_ARCHETYPE = "micros-service-archetype";

    public MavenMicrosGoals(MavenContext ctx)
    {
        super(ctx);
    }

    public void createMicros(AmpsCreatePluginPrompter createPrompter) throws MojoExecutionException
    {
        CreateMicrosProperties props = null;
        Properties systemProps = System.getProperties();

        if (systemProps.containsKey("groupId")
                && systemProps.containsKey("artifactId")
                && systemProps.containsKey("version")
                && systemProps.containsKey("package")
                )
        {
            props = new CreateMicrosProperties(systemProps.getProperty("groupId")
                    , systemProps.getProperty("artifactId")
                    , systemProps.getProperty("version")
                    , systemProps.getProperty("package")
            );
        }

        if (null == props)
        {
            try
            {
                props = createPrompter.prompt();
            }
            catch (PrompterException e)
            {
                throw new MojoExecutionException("Unable to gather properties", e);
            }
        }

        if (null != props)
        {
            MojoExecutor.ExecutionEnvironment execEnv = executionEnvironment();

            Properties sysProps = execEnv.getMavenSession().getExecutionProperties();
            sysProps.setProperty("groupId", props.getGroupId());
            sysProps.setProperty("artifactId", props.getArtifactId());
            sysProps.setProperty("version", props.getVersion());
            sysProps.setProperty("package", props.getThePackage());
            sysProps.setProperty("microsServiceName", "Branch new Java Micros Service");

            executeMojo(
                    plugin(
                            groupId("org.apache.maven.plugins"),
                            artifactId("maven-archetype-plugin"),
                            version(defaultArtifactIdToVersionMap.get("maven-archetype-plugin"))
                    ),
                    goal("generate"),
                    configuration(
                            element(name("archetypeGroupId"), "com.atlassian.maven.archetypes"),
                            element(name("archetypeArtifactId"), MICROS_SERVICE_ARCHETYPE),
                            element(name("archetypeVersion"), VersionUtils.getVersion()),
                            element(name("interactiveMode"), "false")
                    ),
                    execEnv);

            File pluginDir = new File(ctx.getProject().getBasedir(), props.getArtifactId());

            if (pluginDir.exists())
            {
                File src = new File(pluginDir, "src");
                File test = new File(src, "test");
                File java = new File(test, "java");

                String packagePath = props.getThePackage().replaceAll("\\.", Matcher.quoteReplacement(File.separator));
                File packageFile = new File(java, packagePath);
                File packageUT = new File(packageFile, "ut");
                File packageIT = new File(packageFile, "it");

                File ut = new File(new File(java, "ut"), packagePath);
                File it = new File(new File(java, "it"), packagePath);

                if (packageFile.exists())
                {
                    try
                    {
                        if (packageUT.exists())
                        {
                            FileUtils.copyDirectory(packageUT, ut);
                        }

                        if (packageIT.exists())
                        {
                            FileUtils.copyDirectory(packageIT, it);
                        }

                        IOFileFilter filter = FileFilterUtils.and(FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter("it")), FileFilterUtils.notFileFilter(FileFilterUtils.nameFileFilter("ut")));

                        com.atlassian.maven.plugins.amps.util.FileUtils.cleanDirectory(java, filter);

                    }
                    catch (IOException e)
                    {
                        //for now just ignore
                    }
                }
            }
        }
    }
}
