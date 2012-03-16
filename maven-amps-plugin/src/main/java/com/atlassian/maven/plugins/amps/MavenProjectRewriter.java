package com.atlassian.maven.plugins.amps;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import com.atlassian.plugins.codegen.AmpsSystemPropertyVariable;
import com.atlassian.plugins.codegen.ArtifactDependency;
import com.atlassian.plugins.codegen.ArtifactId;
import com.atlassian.plugins.codegen.BundleInstruction;
import com.atlassian.plugins.codegen.MavenPlugin;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.ProjectRewriter;
import com.atlassian.plugins.codegen.VersionId;

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
import org.apache.maven.model.PluginExecution;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;
import org.apache.maven.plugins.shade.pom.PomWriter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;

import static com.atlassian.fugue.Option.none;
import static com.atlassian.fugue.Option.option;
import static com.atlassian.fugue.Option.some;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.concat;

/**
 * Applies any changes from a {@link PluginProjectChangeset} that affect the POM of a Maven project.
 * These include dependencies, bundle instructions and bundled artifacts in the AMPS configuration,
 * and arbitrary build plugin configurations.
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

        modifyPom |= applyDependencyChanges(changes.getItems(ArtifactDependency.class));
        modifyPom |= applyMavenPluginChanges(changes.getItems(MavenPlugin.class));
        modifyPom |= applyBundleInstructionChanges(changes.getItems(BundleInstruction.class));
        modifyPom |= applyPluginArtifactChanges(changes.getItems(com.atlassian.plugins.codegen.PluginArtifact.class));
        modifyPom |= applyAmpsSystemPropertyChanges(changes.getItems(AmpsSystemPropertyVariable.class));

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
            boolean alreadyExists = any(originalDependencies, dependencyArtifactId(descriptor.getGroupAndArtifactId()));
            if (!alreadyExists)
            {
                modified = true;

                Dependency newDependency = new Dependency();
                newDependency.setGroupId(descriptor.getGroupAndArtifactId().getGroupId().get());
                newDependency.setArtifactId(descriptor.getGroupAndArtifactId().getArtifactId());
                newDependency.setVersion(descriptor.getVersionId().getVersionOrPropertyPlaceholder().get());
                createVersionPropertyIfNecessary(descriptor.getVersionId());
                newDependency.setScope(descriptor.getScope().name().toLowerCase());

                model.addDependency(newDependency);
            }
        }
        return modified;
    }

    private void createVersionPropertyIfNecessary(VersionId versionId)
    {
        for (String p : versionId.getPropertyName())
        {
            if (!model.getProperties().containsKey(p))
            {
                model.addProperty(p, versionId.getVersion().getOrElse(""));
            }
        }
    }
    
    private boolean applyMavenPluginChanges(Iterable<MavenPlugin> mavenPlugins) throws Exception
    {
        boolean modified = false;
        @SuppressWarnings("unchecked")
        List<Plugin> originalPlugins = model.getBuild().getPlugins();
        for (MavenPlugin descriptor : mavenPlugins)
        {
            Document fragDoc = DocumentHelper.parseText("<root>" + descriptor.getXmlContent() + "</root>");
            Predicate<Plugin> match = pluginArtifactId(descriptor.getGroupAndArtifactId());
            if (Iterables.any(originalPlugins, match))
            {
                modified |= mergeMavenPluginConfig(Iterables.find(originalPlugins, match), fragDoc.getRootElement());
            }
            else
            {
                originalPlugins.add(toMavenPlugin(descriptor, fragDoc.getRootElement()));
                modified = true;
            }
        }
        return modified;
    }
    
    private static boolean mergeMavenPluginConfig(Plugin plugin, Element paramsDesc)
    {
        boolean modified = false;
        for (Object node : paramsDesc.selectNodes("executions/execution"))
        {
            Element executionDesc = (Element) node;
            if (!plugin.getExecutionsAsMap().containsKey(executionDesc.elementTextTrim("id")))
            {
                plugin.addExecution(toMavenPluginExecution(executionDesc));
                modified = true;
            }
        }
        return modified;
    }
    
    private Plugin toMavenPlugin(MavenPlugin descriptor, Element paramsDesc)
    {
        Plugin p = new Plugin();
        p.setGroupId(descriptor.getGroupAndArtifactId().getGroupId().getOrElse((String)null));
        p.setArtifactId(descriptor.getGroupAndArtifactId().getArtifactId());
        if (descriptor.getVersionId().isDefined())
        {
            p.setVersion(descriptor.getVersionId().getVersionOrPropertyPlaceholder().get());
            createVersionPropertyIfNecessary(descriptor.getVersionId());
        }
        p.setExtensions("true".equals(paramsDesc.elementText("extensions")));
        for (Object configNode : paramsDesc.selectNodes("configuration"))
        {
            p.setConfiguration(toXpp3Dom((Element)configNode));
        }
        for (Object execNode : paramsDesc.selectNodes("executions/execution"))
        {
            p.addExecution(toMavenPluginExecution((Element)execNode));
        }
        return p;
    }

    private static PluginExecution toMavenPluginExecution(Element executionDesc)
    {
        PluginExecution pe = new PluginExecution();
        pe.setId(executionDesc.elementTextTrim("id"));
        pe.setPhase(executionDesc.elementTextTrim("phase"));
        for (Object goalNode : executionDesc.selectNodes("goals/goal"))
        {
            pe.addGoal(((Node)goalNode).getText());
        }
        for (Object configNode : executionDesc.selectNodes("configuration"))
        {
            pe.setConfiguration(toXpp3Dom((Element)configNode));
        }
        return pe;
    }
    
    private boolean applyBundleInstructionChanges(Iterable<BundleInstruction> instructions)
    {
        Xpp3Dom configRoot = getAmpsPluginConfiguration();
        boolean modified = false;
        Xpp3Dom instructionsRoot = getOrCreateElement(configRoot, "instructions");
        for (BundleInstruction instruction : instructions)
        {
            String categoryName = instruction.getCategory().getElementName();
            Xpp3Dom categoryElement = getOrCreateElement(instructionsRoot, categoryName);
            String body = categoryElement.getValue();
            Iterable<BundleInstruction> instructionList = parseInstructions(instruction.getCategory(), (body == null) ? "" : body);
            if (any(instructionList, bundleInstructionPackageName(instruction.getPackageName())))
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
    
    private static Iterable<BundleInstruction> parseInstructions(BundleInstruction.Category category, String body)
    {
        ImmutableList.Builder<BundleInstruction> ret = ImmutableList.builder();
        for (String instructionLine : body.split(","))
        {
            String[] instructionParts = instructionLine.trim().split(";");
            if (instructionParts.length == 1)
            {
                ret.add(new BundleInstruction(category, instructionParts[0], none(String.class)));
            }
            else if (instructionParts.length == 2 && instructionParts[1].startsWith("version=\"") && instructionParts[1].endsWith("\""))
            {
                String version = instructionParts[1].substring(9, instructionParts[1].length() - 1);
                ret.add(new BundleInstruction(category, instructionParts[0], some(version)));
            }
        }
        return ret.build();
    }
    
    private static String writeInstructions(Iterable<BundleInstruction> instructions)
    {
        StringBuilder ret = new StringBuilder("\n");
        for (BundleInstruction instruction : instructions)
        {
            if (ret.length() > 1)
            {
                ret.append(",\n");
            }
            ret.append(instruction.getPackageName());
            for (String version : instruction.getVersion())
            {
                ret.append(";version=\"").append(version).append("\"");
            }
        }
        return ret.append("\n").toString();
    }
    
    private boolean applyPluginArtifactChanges(Iterable<com.atlassian.plugins.codegen.PluginArtifact> pluginArtifacts)
    {
        Xpp3Dom configRoot = getAmpsPluginConfiguration();
        boolean modified = false;
        for (com.atlassian.plugins.codegen.PluginArtifact p : pluginArtifacts)
        {
            Xpp3Dom artifactsRoot = getOrCreateElement(configRoot, p.getType().getElementName() + "s");
            List<Xpp3Dom> existingItems = ImmutableList.copyOf(artifactsRoot.getChildren(p.getType().getElementName()));
            if (!any(existingItems, artifactElement(p.getGroupAndArtifactId())))
            {
                artifactsRoot.addChild(toArtifactElement(p));
                modified = true;
            }
        }
        return modified;
    }

    private boolean applyAmpsSystemPropertyChanges(Iterable<AmpsSystemPropertyVariable> propertyVariables)
    {
        Xpp3Dom configRoot = getAmpsPluginConfiguration();
        boolean modified = false;
        for (AmpsSystemPropertyVariable propertyVariable : propertyVariables)
        {
            Xpp3Dom variablesRoot = getOrCreateElement(configRoot, "systemPropertyVariables");
            if (variablesRoot.getChild(propertyVariable.getName()) == null)
            {
                Xpp3Dom variableElement = new Xpp3Dom(propertyVariable.getName());
                variableElement.setValue(propertyVariable.getValue());
                variablesRoot.addChild(variableElement);
                modified = true;
            }
        }
        return modified;
    }
    
    private Xpp3Dom toArtifactElement(com.atlassian.plugins.codegen.PluginArtifact pluginArtifact)
    {
        Xpp3Dom ret = new Xpp3Dom(pluginArtifact.getType().getElementName());
        for (String groupId : pluginArtifact.getGroupAndArtifactId().getGroupId())
        {
            Xpp3Dom ge = new Xpp3Dom("groupId");
            ge.setValue(groupId);
            ret.addChild(ge);
        }
        Xpp3Dom ae = new Xpp3Dom("artifactId");
        ae.setValue(pluginArtifact.getGroupAndArtifactId().getArtifactId());
        ret.addChild(ae);
        if (pluginArtifact.getVersionId().isDefined())
        {
            Xpp3Dom ve = new Xpp3Dom("version");
            ve.setValue(pluginArtifact.getVersionId().getVersionOrPropertyPlaceholder().get());
            createVersionPropertyIfNecessary(pluginArtifact.getVersionId());
            ret.addChild(ve);
        }
        return ret;
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

    private Xpp3Dom getAmpsPluginConfiguration()
    {
        Plugin ampsPlugin = findAmpsPlugin();
        Xpp3Dom configRoot = (Xpp3Dom) ampsPlugin.getConfiguration();
        if (configRoot == null)
        {
            configRoot = new Xpp3Dom("configuration");
            ampsPlugin.setConfiguration(configRoot);
        }
        return configRoot;
    }
    
    private static Xpp3Dom getOrCreateElement(Xpp3Dom container, String name)
    {
        Xpp3Dom ret = container.getChild(name);
        if (ret == null)
        {
            ret = new Xpp3Dom(name);
            container.addChild(ret);
        }
        return ret;
    }
    
    private static Xpp3Dom toXpp3Dom(Element dom4JElement)
    {
        try
        {
            return Xpp3DomBuilder.build(new StringReader(dom4JElement.asXML()));
        }
        catch (Exception e)
        {
            // should never fail to parse XML that was already parsed by dom4J
            throw new IllegalStateException();
        }
    }
    
    private static Predicate<Dependency> dependencyArtifactId(final ArtifactId artifactId)
    {
        return new Predicate<Dependency>()
        {
            public boolean apply(Dependency d)
            {
                return (artifactId.getGroupId().equals(option(d.getGroupId()))
                        && artifactId.getArtifactId().equals(d.getArtifactId()));
            }
        };
    }

    private static Predicate<Plugin> pluginArtifactId(final ArtifactId artifactId)
    {
        return new Predicate<Plugin>()
        {
            public boolean apply(Plugin p)
            {
                return artifactId.getArtifactId().equals(p.getArtifactId())
                    && (artifactId.getGroupId().equals(option(p.getGroupId()))
                        || (!artifactId.getGroupId().isDefined() && "org.apache.maven.plugins".equals(p.getGroupId())));
            }
        };
    }

    private static Predicate<Xpp3Dom> artifactElement(final ArtifactId artifactId)
    {
        return new Predicate<Xpp3Dom>()
        {
            public boolean apply(Xpp3Dom e)
            {
                return (e.getChild("artifactId") != null)
                    && e.getChild("artifactId").getValue().equals(artifactId.getArtifactId())
                    && (((e.getChild("groupId") == null) && !artifactId.getGroupId().isDefined())
                        || (e.getChild("groupId") != null) && artifactId.getGroupId().equals(some(e.getChild("groupId").getValue())));
            }
        };
    }

    private static Predicate<BundleInstruction> bundleInstructionPackageName(final String packageName)
    {
        return new Predicate<BundleInstruction>()
        {
            public boolean apply(BundleInstruction i)
            {
                return i.getPackageName().equals(packageName);
            }
        };
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
