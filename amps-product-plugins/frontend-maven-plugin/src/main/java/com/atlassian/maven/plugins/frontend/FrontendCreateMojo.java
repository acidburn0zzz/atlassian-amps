package com.atlassian.maven.plugins.frontend;

import java.util.Properties;
import java.util.stream.Stream;

import com.atlassian.maven.plugins.amps.AbstractAmpsMojo;
import com.atlassian.maven.plugins.amps.util.AmpsCreatePluginPrompter;
import com.atlassian.maven.plugins.amps.util.CreatePluginProperties;
import com.atlassian.maven.plugins.amps.util.MojoUtils;
import com.atlassian.maven.plugins.amps.util.VersionUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.components.interactivity.PrompterException;

import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

@Mojo(name = "create-frontend-plugin", requiresProject = false)
public class FrontendCreateMojo extends AbstractAmpsMojo {

    private static final String GROUP_ID = "groupId";
    private static final String ARTIFACT_ID = "artifactId";
    private static final String VERSION = "version";
    private static final String PACKAGE = "package";
    private static final String ARCHETYPE_ARTIFACT_ID = "atlaskit-plugin-archetype";
    private static final String ARCHETYPE_GROUP_ID = "com.atlassian.maven.archetypes";

    @Component
    private AmpsCreatePluginPrompter ampsCreatePluginPrompter;

    @Override public void execute() throws MojoExecutionException, MojoFailureException {

        final CreatePluginProperties pluginProperties = getCreatePluginProperties();
        final Properties userProperties = getMavenContext().getExecutionEnvironment().getMavenSession().getUserProperties();

        userProperties.setProperty(GROUP_ID, pluginProperties.getGroupId());
        userProperties.setProperty(ARTIFACT_ID, pluginProperties.getArtifactId());
        userProperties.setProperty(VERSION, pluginProperties.getVersion());
        userProperties.setProperty(PACKAGE, pluginProperties.getThePackage());

        MojoUtils.executeWithMergedConfig(plugin(
                groupId("org.apache.maven.plugins"),
                artifactId("maven-archetype-plugin"),
                version("3.0.1")
                ),
                goal("generate"),
                configuration(
                        element(name("archetypeGroupId"), ARCHETYPE_GROUP_ID),
                        element(name("archetypeArtifactId"), ARCHETYPE_ARTIFACT_ID),
                        element(name("interactiveMode"), "false")
                ),getMavenContext().getExecutionEnvironment());
    }

    private CreatePluginProperties getCreatePluginProperties() throws MojoExecutionException {
        CreatePluginProperties pluginProperties = null;
        final Properties systemProps = System.getProperties();

        if (Stream.of(GROUP_ID, ARTIFACT_ID, VERSION, PACKAGE).allMatch(systemProps::containsKey)) {
            pluginProperties = new CreatePluginProperties(systemProps.getProperty(GROUP_ID),
                    systemProps.getProperty(ARTIFACT_ID), systemProps.getProperty(VERSION),
                    systemProps.getProperty(PACKAGE), systemProps.getProperty("useOsgiJavaConfig", "N"));
        }

        if (pluginProperties == null) {
            try {
                pluginProperties = ampsCreatePluginPrompter.prompt();
            } catch (final PrompterException e) {
                throw new MojoExecutionException("Unable to gather properties", e);
            }
        }
        return pluginProperties;
    }
}
