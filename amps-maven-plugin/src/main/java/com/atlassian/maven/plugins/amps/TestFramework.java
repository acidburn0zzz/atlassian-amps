package com.atlassian.maven.plugins;

import com.atlassian.maven.plugins.amps.AbstractProductHandlerMojo;
import com.atlassian.maven.plugins.amps.codegen.prompter.PrettyPrompter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.codehaus.plexus.components.interactivity.Prompter;

@Mojo(name = "testjira", requiresProject = false)
public class TestFramework extends AbstractProductHandlerMojo {
    private Prompter prompter = new PrettyPrompter();

    @Override
    protected void doExecute() throws MojoExecutionException, MojoFailureException {



      }

    }
}
