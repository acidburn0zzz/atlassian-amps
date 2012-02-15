package com.atlassian.maven.plugins.amps;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.atlassian.plugins.codegen.ArtifactDependency;
import com.atlassian.plugins.codegen.BundleInstruction;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.ProjectRewriter;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.XmlStreamWriter;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugins.shade.pom.PomWriter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.concat;

/**
 * Applies any changes from a {@link PluginProjectChangeset} that affect the POM of a Maven project.
 * These include dependencies and bundle instructions.
 */
public class MavenProjectRewriter implements ProjectRewriter
{
    private static final ImmutableSet<String> AMPS_PLUGIN_IDS =
        ImmutableSet.of("maven-amps-plugin",
                        "maven-bamboo-plugin",
                        "maven-confluence-plugin",
                        "maven-crowd-plugin",
                        "maven-fecru-plugin",
                        "maven-jira-plugin",
                        "maven-refapp-plugin");
    
    private final Model model;
    private final File pom;
    private final Log log;
    
    public MavenProjectRewriter(MavenProject project, Log log)
    {
        this.model = checkNotNull(project, "project").getModel();
        this.pom = project.getFile();
        this.log = checkNotNull(log, "log");
    }

    public MavenProjectRewriter(Model model, File pom)
    {
        this.model = checkNotNull(model, "model");
        this.pom = checkNotNull(pom, "pom");
        this.log = new SystemStreamLog();
    }
    
    @Override
    public void applyChanges(PluginProjectChangeset changes) throws Exception
    {
        boolean modifyPom = false;

        modifyPom |= applyDependencyChanges(changes.getDependencies());
        modifyPom |= applyBundleInstructionChanges(changes.getBundleInstructions());

        if (modifyPom)
        {
            XmlStreamWriter writer = null;
            try
            {
                writer = new XmlStreamWriter(pom);
                PomWriter.write(writer, model, true);
            }
            catch (IOException e)
            {
                log.warn("Unable to write plugin-module dependencies to pom.xml", e);
            }
            finally
            {
                if (writer != null)
                {
                    IOUtils.closeQuietly(writer);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private boolean applyDependencyChanges(Iterable<ArtifactDependency> dependencies)
    {
        boolean modified = false;
        List<Dependency> originalDependencies = model.getDependencies();
        for (ArtifactDependency descriptor : dependencies)
        {
            boolean alreadyExists = any(originalDependencies, new DependencyPredicate(descriptor));
            if (!alreadyExists)
            {
                modified = true;

                Dependency newDependency = new Dependency();
                newDependency.setGroupId(descriptor.getGroupId());
                newDependency.setArtifactId(descriptor.getArtifactId());
                newDependency.setVersion(descriptor.getVersion());
                newDependency.setScope(descriptor.getScope().name().toLowerCase());

                model.addDependency(newDependency);
            }
        }
        return modified;
    }
    
    private boolean applyBundleInstructionChanges(Iterable<BundleInstruction> instructions)
    {
        Plugin ampsPlugin = findAmpsPlugin();
        boolean modified = false;
        Xpp3Dom configRoot = (Xpp3Dom) ampsPlugin.getConfiguration();
        if (configRoot == null)
        {
            configRoot = new Xpp3Dom("configuration");
            ampsPlugin.setConfiguration(configRoot);
        }
        Xpp3Dom instructionsRoot = configRoot.getChild("instructions");
        if (instructionsRoot == null)
        {
            instructionsRoot = new Xpp3Dom("instructions");
            configRoot.addChild(instructionsRoot);
        }
        for (BundleInstruction instruction : instructions)
        {
            String categoryName = instruction.getCategory().getElementName();
            Xpp3Dom categoryElement = instructionsRoot.getChild(categoryName);
            if (categoryElement == null)
            {
                categoryElement = new Xpp3Dom(categoryName);
                instructionsRoot.addChild(categoryElement);
            }
            String body = categoryElement.getValue();
            Iterable<BundleInstruction> instructionList = parseInstructions(instruction.getCategory(), (body == null) ? "" : body);
            if (Iterables.contains(instructionList, new InstructionPackagePredicate(instruction.getPackageName())))
            {
                continue;
            }
            Iterable<BundleInstruction> newList = new InstructionPackageOrdering().sortedCopy(
                concat(instructionList, ImmutableList.of(instruction)));
            categoryElement.setValue(writeInstructions(newList));
            modified = true;
        }
        return modified;
    }
    
    private Iterable<BundleInstruction> parseInstructions(BundleInstruction.Category category, String body)
    {
        ImmutableList.Builder<BundleInstruction> ret = ImmutableList.builder();
        for (String instructionLine : body.split(","))
        {
            String[] instructionParts = instructionLine.trim().split(";");
            if (instructionParts.length == 2 && instructionParts[1].startsWith("version=\"") && instructionParts[1].endsWith("\""))
            {
                String version = instructionParts[1].substring(9, instructionParts[1].length() - 1);
                ret.add(new BundleInstruction(category, instructionParts[0], version));
            }
        }
        return ret.build();
    }
    
    private String writeInstructions(Iterable<BundleInstruction> instructions)
    {
        StringBuilder ret = new StringBuilder("\n");
        for (BundleInstruction instruction : instructions)
        {
            if (ret.length() > 1)
            {
                ret.append(",\n");
            }
            ret.append(instruction.getPackageName());
            ret.append(";version=\"").append(instruction.getVersion()).append("\"");
        }
        return ret.append("\n").toString();
    }
    
    @SuppressWarnings("unchecked")
    private Plugin findAmpsPlugin()
    {
        for (Plugin p : (List<Plugin>) model.getBuild().getPlugins())
        {
            if (p.getGroupId().equals("com.atlassian.maven.plugins")
                && AMPS_PLUGIN_IDS.contains(p.getArtifactId()))
            {
                return p;
            }
        }
        throw new IllegalStateException("Could not find AMPS plugin element in POM");
    }
    
    private static class DependencyPredicate implements Predicate<Dependency>
    {
        private final ArtifactDependency depToCheck;
    
        private DependencyPredicate(ArtifactDependency depToCheck)
        {
            this.depToCheck = depToCheck;
        }
    
        @Override
        public boolean apply(Dependency d)
        {
            return (depToCheck.getGroupId().equals(d.getGroupId())
                    && depToCheck.getArtifactId().equals(d.getArtifactId()));
        }
    }
    
    private static class InstructionPackagePredicate implements Predicate<BundleInstruction>
    {
        private final String packageName;
        
        private InstructionPackagePredicate(String packageName)
        {
            this.packageName = packageName;
        }
        
        @Override
        public boolean apply(BundleInstruction i)
        {
            return i.getPackageName().equals(packageName);
        }
    }
    
    private static class InstructionPackageOrdering extends Ordering<BundleInstruction>
    {
        @Override
        public int compare(BundleInstruction first, BundleInstruction second)
        {
            return first.getPackageName().compareTo(second.getPackageName());
        }
    }
}
