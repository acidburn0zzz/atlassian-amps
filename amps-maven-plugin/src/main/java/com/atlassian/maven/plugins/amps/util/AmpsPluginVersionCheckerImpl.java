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
import com.atlassian.plugins.codegen.ProjectRewriter;
import io.atlassian.fugue.Pair;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.components.interactivity.Prompter;
import org.codehaus.plexus.components.interactivity.PrompterException;
import org.codehaus.plexus.logging.AbstractLogEnabled;
import org.codehaus.plexus.logging.Logger;
import org.dom4j.DocumentException;

import jline.ANSIBuffer;

import static com.google.common.base.Preconditions.checkNotNull;

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

        String prefKey = POM_UPDATE_PREF_PREFIX + "-" + currentVersion + "-" + DigestUtils.md5Hex(project.getArtifactId());
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
            Pair<Pair<DefaultArtifactVersion,PomVersionElement>,ProjectRewriter> versionInfo = getVersionAndRewriter(project);

            DefaultArtifactVersion versionInPom = versionInfo.left().left();
            PomVersionElement pomElement = versionInfo.left().right();
            String pomElementType = pomElement.getType();
            String versionProp = pomElement.getVersionProperty();
            
            ProjectRewriter rewriter = versionInfo.right();
            
            boolean managementNeedsUpdate = false;
            boolean pluginNeedsUpdate = false;

            
            if(pomElementType.equals(PomVersionElement.MANAGEMENT))
            {

                if(NO_VERSION_DEFINED.equalsIgnoreCase(versionInPom.toString()) || runningVersion.compareTo(versionInPom) > 0)
                {
                    managementNeedsUpdate = true;
                }
            }
            
            if(pomElementType.equals(PomVersionElement.PLUGIN) && (NO_VERSION_DEFINED.equalsIgnoreCase(versionInPom.toString()) || runningVersion.compareTo(versionInPom) > 0))
            {
                    pluginNeedsUpdate = true;
            }
            
            if(pluginNeedsUpdate || managementNeedsUpdate)
            {
                boolean doUpdate = promptToUpdatePom(versionInPom,runningVersion);
                if(doUpdate)
                {
                    PluginProjectChangeset changes = new PluginProjectChangeset();
                    
                    if(pluginNeedsUpdate)
                    {
                        changes = changes.with(AmpsVersionUpdate.ampsVersionUpdate(currentVersion,AmpsVersionUpdate.PLUGIN,true,false));
                    }
                    
                    if(managementNeedsUpdate)
                    {
                        changes = changes.with(AmpsVersionUpdate.ampsVersionUpdate(currentVersion,AmpsVersionUpdate.MANAGEMENT,true,false));
                    }
                    rewriter.applyChanges(changes);
                    
                    
                    //update the property in the correct pom
                    PluginProjectChangeset propChanges = new PluginProjectChangeset();
                    propChanges = propChanges.with(AmpsVersionUpdate.ampsVersionUpdate(currentVersion,AmpsVersionUpdate.PLUGIN,false,true));
                    ProjectRewriter propRewriter;
                    
                    if(StringUtils.isNotBlank(versionProp))
                    {
                        propRewriter = getRewriterForVersionProp(versionProp,project);
                    }
                    else
                    {
                        propRewriter = rewriter;
                    }
                    
                    propRewriter.applyChanges(propChanges);
                    
                    getLogger().info("AMPS version in pom updated to " + currentVersion);
                }
            }
            
        }
        catch (Throwable t)
        {
            getLogger().error("unable to check amps version in pom...", t);
        }
    }
    
    private Pair<Pair<DefaultArtifactVersion,PomVersionElement>,ProjectRewriter> getVersionAndRewriter(MavenProject project) throws IOException, DocumentException
    {
        ProjectRewriter rewriter;
        
        if(null == project.getFile() || !project.getFile().exists())
        {
            rewriter = new NOOPProjectRewriter();
            return Pair.pair(Pair.pair(new DefaultArtifactVersion(NO_VERSION_DEFINED),new PomVersionElement(PomVersionElement.MANAGEMENT,null)),rewriter);
        }

        rewriter = new MavenProjectRewriter(project.getFile());
        MavenProjectRewriter mavenRewriter = (MavenProjectRewriter) rewriter;
        
        String managementVersionInPom = mavenRewriter.getAmpsPluginManagementVersionInPom();
        String pluginVersionInPom = "";
        
        DefaultArtifactVersion ampsManagementVersionInPom = getPomVersion(managementVersionInPom,project);
        DefaultArtifactVersion ampsVersionInPom;
        try
        {
            pluginVersionInPom = mavenRewriter.getAmpsVersionInPom();
            ampsVersionInPom = getPomVersion(pluginVersionInPom,project);
        }
        catch (IllegalStateException e)
        {
            ampsVersionInPom = new DefaultArtifactVersion(NO_VERSION_DEFINED);
        }

        String versionProp = "";
        if(managementVersionInPom.startsWith("$"))
        {
            versionProp = managementVersionInPom;
        }
        if(pluginVersionInPom.startsWith("$"))
        {
            versionProp = pluginVersionInPom;
        }

        
        if(project.hasParent() && NO_VERSION_DEFINED.equalsIgnoreCase(ampsManagementVersionInPom.toString()) && NO_VERSION_DEFINED.equalsIgnoreCase(ampsVersionInPom.toString()))
        {
            return getVersionAndRewriter(project.getParent());
        }
        else if(!NO_VERSION_DEFINED.equalsIgnoreCase(ampsManagementVersionInPom.toString()))
        {
            return Pair.pair(Pair.pair(ampsManagementVersionInPom,new PomVersionElement(PomVersionElement.MANAGEMENT,versionProp)),rewriter);
        }
        else
        {
            return Pair.pair(Pair.pair(ampsVersionInPom,new PomVersionElement(PomVersionElement.PLUGIN,versionProp)),rewriter);
        }
    }

    private ProjectRewriter getRewriterForVersionProp(String versionProp, MavenProject project) throws IOException, DocumentException
    {
        if(null == project.getFile() || !project.getFile().exists())
        {
            return null;
        }
        
        MavenProjectRewriter rewriter = new MavenProjectRewriter(project.getFile());
        String propName = StringUtils.substringBetween(versionProp,"${","}");
        
        if(StringUtils.isNotBlank(propName) && rewriter.definesProperty(propName))
        {
            return rewriter;
        }
        else if(project.hasParent())
        {
            return getRewriterForVersionProp(versionProp,project.getParent());
        }
        else
        {
            return null;
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
                .append("You are running AMPS plugin version ")
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
        builder.append("You are running AMPS plugin version ")
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
        
        if(StringUtils.isNotBlank(ampsVersionOrProperty) && ampsVersionOrProperty.startsWith("$"))
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

    public class PomVersionElement
    {
        public static final String PLUGIN = "plugin";
        public static final String MANAGEMENT = "management";
        
        private final String type;
        private final String versionProperty;

        private PomVersionElement(String type, String versionProperty)
        {
            this.type = checkNotNull(type,"type");
            this.versionProperty = versionProperty;
        }

        public String getType()
        {
            return type;
        }

        public String getVersionProperty()
        {
            return versionProperty;
        }
    };
}
