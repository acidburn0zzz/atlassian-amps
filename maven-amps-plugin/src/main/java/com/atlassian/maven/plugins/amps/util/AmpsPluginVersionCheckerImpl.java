package com.atlassian.maven.plugins.amps.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.Preferences;

import com.atlassian.maven.plugins.amps.codegen.prompter.PrettyPrompter;
import com.atlassian.plugins.codegen.AmpsVersionUpdate;
import com.atlassian.plugins.codegen.MavenProjectRewriter;
import com.atlassian.plugins.codegen.PluginProjectChangeset;

import org.apache.commons.lang.StringUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;

import jline.ANSIBuffer;

/**
 * Compares the running version of amps to the version stated in the pom.
 * If the pom version is lower than the sdk version, it will prompt the developer
 * asking if they would like to have their pom updated with the new version.
 */
public class AmpsPluginVersionCheckerImpl extends AbstractLogEnabled implements AmpsPluginVersionChecker
{
    public static final List<String> YN_ANSWERS = new ArrayList<String>(Arrays.asList("Y", "y", "N", "n"));
    public static final String NO_VERSION_DEFINED = "no-version-defined";
    public static final String POM_UPDATE_PREF_PREFIX = "sdk-pom-update-check";
    private boolean useAnsiColor;
    private Prompter prompter;
    private boolean skipCheck;

    public AmpsPluginVersionCheckerImpl()
    {
        this.skipCheck = false;
        
        String mavencolor = System.getenv("MAVEN_COLOR");
        if (mavencolor != null && !mavencolor.equals(""))
        {
            useAnsiColor = Boolean.parseBoolean(mavencolor);
        } else
        {
            useAnsiColor = false;
        }
    }
    
    @Override
    public void checkAmpsVersionInPom(String currentVersion, MavenProject project)
    {
        if(skipCheck || (null == project.getFile() || !project.getFile().exists()))
        {
            return;
        }

        String prefKey = POM_UPDATE_PREF_PREFIX + "-" + currentVersion + "-" + project.getArtifactId();
        Preferences prefs = Preferences.userNodeForPackage(AmpsPluginVersionCheckerImpl.class);
        String alreadyRan = prefs.get(prefKey, null);

        if(null != alreadyRan)
        {
            return;
        }

        prefs.put(prefKey,"true");

        try
        {
            DefaultArtifactVersion runningVersion = new DefaultArtifactVersion(currentVersion);
            MavenProjectRewriter rewriter = new MavenProjectRewriter(project.getFile());

            String managementVersion = rewriter.getAmpsPluginManagementVersionInPom();
            boolean hasManagementVersion = false;
            boolean managementNeedsUpdate = false;
            boolean pluginNeedsUpdate = false;

            DefaultArtifactVersion ampsManagementVersionInPom = null;
            DefaultArtifactVersion ampsVersionInPom = null;
            DefaultArtifactVersion versionToReport = null;
            
            if(StringUtils.isNotBlank(managementVersion))
            {
                hasManagementVersion = true;
                ampsManagementVersionInPom = getPomVersion(managementVersion,project);

                if(null != ampsManagementVersionInPom && (NO_VERSION_DEFINED.equalsIgnoreCase(ampsManagementVersionInPom.toString()) || runningVersion.compareTo(ampsManagementVersionInPom) > 0))
                {
                    managementNeedsUpdate = true;
                    versionToReport = ampsManagementVersionInPom;
                }
            }

            ampsVersionInPom = getPomVersion(rewriter.getAmpsVersionInPom(),project);
            
            if(null != ampsVersionInPom && (NO_VERSION_DEFINED.equalsIgnoreCase(ampsVersionInPom.toString()) || runningVersion.compareTo(ampsVersionInPom) > 0))
            {
                if(NO_VERSION_DEFINED.equalsIgnoreCase(ampsVersionInPom.toString()) && hasManagementVersion)
                {
                    pluginNeedsUpdate = false;
                }
                else
                {
                    pluginNeedsUpdate = true;
                    versionToReport = ampsVersionInPom;
                }
            }
            
            if(pluginNeedsUpdate || managementNeedsUpdate)
            {
                boolean doUpdate = promptToUpdatePom(versionToReport,runningVersion);
                if(doUpdate)
                {
                    PluginProjectChangeset changes = new PluginProjectChangeset();
                    
                    if(pluginNeedsUpdate)
                    {
                        changes = changes.with(AmpsVersionUpdate.ampsVersionUpdate(currentVersion,AmpsVersionUpdate.PLUGIN));
                    }
                    
                    if(managementNeedsUpdate)
                    {
                        changes = changes.with(AmpsVersionUpdate.ampsVersionUpdate(currentVersion,AmpsVersionUpdate.MANAGEMENT));
                    }
                    rewriter.applyChanges(changes);
                    getLogger().info("AMPS version in pom updated to " + currentVersion);
                }
            }
            
        }
        catch (Throwable t)
        {
            getLogger().error("unable to check amps version in pom...", t);
        }
    }

    @Override
    public void skipPomCheck(boolean skip)
    {
        this.skipCheck = skip;
    }

    private boolean promptToUpdatePom(DefaultArtifactVersion ampsVersionInPom, DefaultArtifactVersion runningVersion) throws PrompterException
    {
        if (useAnsiColor)
        {
            return promptForUpdateAnsi(ampsVersionInPom, runningVersion);
        } else
        {
            return promptForUpdatePlain(ampsVersionInPom, runningVersion);
        }
    }

    private boolean promptForUpdateAnsi(DefaultArtifactVersion ampsVersionInPom, DefaultArtifactVersion runningVersion) throws PrompterException
    {
        ANSIBuffer ansiBuffer = new ANSIBuffer();
        ansiBuffer.append(ANSIBuffer.ANSICodes.attrib(PrettyPrompter.FG_YELLOW))
                .append("You are running SDK version ")
                .append(runningVersion.toString())
                .append(" but your pom is using version ")
                .append(ampsVersionInPom.toString())
                .append("\n")
                .append(ANSIBuffer.ANSICodes.attrib(PrettyPrompter.OFF))
                .append(ANSIBuffer.ANSICodes.attrib(PrettyPrompter.BOLD))
                .append("Would you like to have your pom updated?")
                .append(ANSIBuffer.ANSICodes.attrib(PrettyPrompter.OFF));
        
        return promptForBoolean(ansiBuffer.toString(),"Y");
    }

    private boolean promptForUpdatePlain(DefaultArtifactVersion ampsVersionInPom, DefaultArtifactVersion runningVersion) throws PrompterException
    {
        StringBuilder builder = new StringBuilder();
        builder.append("You are running SDK version ")
                  .append(runningVersion.toString())
                  .append(" but your pom is using version ")
                  .append(ampsVersionInPom.toString())
                  .append("\n")
                  .append("Would you like to have your pom updated?");

        return promptForBoolean(builder.toString(), "Y");
    }
    
    private DefaultArtifactVersion getPomVersion(String ampsVersionOrProperty, MavenProject project)
    {
        DefaultArtifactVersion ampsVersionInPom = null;
        
        if(ampsVersionOrProperty.startsWith("$"))
        {
            String propName = StringUtils.substringBetween(ampsVersionOrProperty,"${","}");
            if(StringUtils.isNotBlank(propName))
            {
                ampsVersionInPom = new DefaultArtifactVersion(project.getProperties().getProperty(propName,"0.0"));
            }
        }
        else if(StringUtils.isNotBlank(ampsVersionOrProperty))
        {
            ampsVersionInPom = new DefaultArtifactVersion(ampsVersionOrProperty);
        }
        else
        {
            ampsVersionInPom = new DefaultArtifactVersion(NO_VERSION_DEFINED);
        }
        
        return ampsVersionInPom;
    }

    private boolean promptForBoolean(String message, String defaultValue) throws PrompterException
    {
        String answer;
        boolean bool;
        if (StringUtils.isBlank(defaultValue))
        {
            answer = prompter.prompt(message, YN_ANSWERS);
        } else
        {
            answer = prompter.prompt(message, YN_ANSWERS, defaultValue);
        }

        if ("y".equals(answer.toLowerCase()))
        {
            bool = true;
        } else
        {
            bool = false;
        }

        return bool;
    }

}
