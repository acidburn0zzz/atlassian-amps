package com.atlassian.plugins.codegen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.atlassian.fugue.Option;
import com.atlassian.plugins.codegen.AmpsSystemPropertyVariable;
import com.atlassian.plugins.codegen.ArtifactDependency;
import com.atlassian.plugins.codegen.BundleInstruction;
import com.atlassian.plugins.codegen.MavenPlugin;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.ProjectRewriter;
import com.atlassian.plugins.codegen.VersionId;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.and;
import static com.google.common.collect.Iterables.any;
import static org.apache.commons.io.IOUtils.closeQuietly;

/**
 * Applies any changes from a {@link PluginProjectChangeset} that affect the POM of a Maven project.
 * These include dependencies, bundle instructions and bundled artifacts in the AMPS configuration,
 * and arbitrary build plugin configurations.
 */
public class MavenProjectRewriter implements ProjectRewriter
{
    private static final int POM_INDENTATION = 4;
    
    private final File pomFile;
    private final Document document;
    private final Element root;
    
    private static final ImmutableSet<String> AMPS_PLUGIN_IDS =
        ImmutableSet.of("maven-amps-plugin",
                        "maven-bamboo-plugin",
                        "maven-confluence-plugin",
                        "maven-crowd-plugin",
                        "maven-fecru-plugin",
                        "maven-jira-plugin",
                        "maven-refapp-plugin");
    
    public MavenProjectRewriter(File pom) throws DocumentException, IOException
    {
        this.pomFile = checkNotNull(pom, "pom");
        document = readPom(pom);
        root = document.getRootElement();
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
            writePom(document, pomFile);
        }
    }

    @SuppressWarnings("unchecked")
    private boolean applyDependencyChanges(Iterable<ArtifactDependency> dependencies)
    {
        boolean modified = false;
        Element eDependencies = getOrCreateElement(root, "dependencies");
        for (ArtifactDependency descriptor : dependencies)
        {
            boolean alreadyExists = any(eDependencies.elements("dependency"),
                                        and(childElementValue("groupId", descriptor.getGroupAndArtifactId().getGroupId().getOrElse("")),
                                            childElementValue("artifactId", descriptor.getGroupAndArtifactId().getArtifactId())));
            if (!alreadyExists)
            {
                modified = true;

                Element eNewDep = eDependencies.addElement("dependency");
                eNewDep.addElement("groupId").setText(descriptor.getGroupAndArtifactId().getGroupId().get());
                eNewDep.addElement("artifactId").setText(descriptor.getGroupAndArtifactId().getArtifactId());
                eNewDep.addElement("version").setText(descriptor.getVersionId().getVersionOrPropertyPlaceholder().get());
                createVersionPropertyIfNecessary(descriptor.getVersionId());
                eNewDep.addElement("scope").setText(descriptor.getScope().name().toLowerCase());
            }
        }
        return modified;
    }

    private void createVersionPropertyIfNecessary(VersionId versionId)
    {
        for (String p : versionId.getPropertyName())
        {
            Element eProperties = getOrCreateElement(root, "properties");
            if (eProperties.element(p) == null)
            {
                eProperties.addElement(p).setText(versionId.getVersion().getOrElse(""));
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    private boolean applyMavenPluginChanges(Iterable<MavenPlugin> mavenPlugins) throws Exception
    {
        boolean modified = false;
        Element ePlugins = getOrCreateElement(root, "build/plugins");
        for (MavenPlugin descriptor : mavenPlugins)
        {
            Document fragDoc = DocumentHelper.parseText("<root>" + descriptor.getXmlContent() + "</root>");
            Option<String> groupId = descriptor.getGroupAndArtifactId().getGroupId();
            String artifactId = descriptor.getGroupAndArtifactId().getArtifactId();
            Predicate<Element> matchGroup = (Predicate<Element>) (groupId.isDefined() ?
                childElementValue("groupId", groupId.get()) :
                Predicates.or(childElementValue("groupId", ""), childElementValue("groupId", "org.apache.maven.plugins")));
            Predicate<Element> match = Predicates.and(matchGroup, childElementValue("artifactId", artifactId));
            if (Iterables.any(ePlugins.elements("plugin"), match))
            {
                modified |= mergeMavenPluginConfig(Iterables.find((List<Element>) ePlugins.elements("plugin"), match), fragDoc.getRootElement());
            }
            else
            {
                ePlugins.add(toMavenPluginElement(descriptor, fragDoc.getRootElement()));
                modified = true;
            }
        }
        return modified;
    }
    
    @SuppressWarnings("unchecked")
    private boolean mergeMavenPluginConfig(Element ePlugin, Element paramsDesc)
    {
        boolean modified = false;
        Element eExecutions = getOrCreateElement(ePlugin, "executions");
        for (Object node : paramsDesc.selectNodes("executions/execution"))
        {
            Element eExecution = (Element) node;
            String id = eExecution.elementTextTrim("id");
            if (!Iterables.any(eExecutions.elements("execution"), childElementValue("id", id)))
            {
                detachAndAdd(eExecution, eExecutions);
                modified = true;
            }
        }
        return modified;
    }
    
    private Element toMavenPluginElement(MavenPlugin descriptor, Element paramsDesc)
    {
        Element p = createElement("plugin");
        for (String groupId : descriptor.getGroupAndArtifactId().getGroupId())
        {
            p.addElement("groupId").setText(groupId);
        }
        p.addElement("artifactId").setText(descriptor.getGroupAndArtifactId().getArtifactId());
        if (descriptor.getVersionId().isDefined())
        {
            p.addElement("version").setText(descriptor.getVersionId().getVersionOrPropertyPlaceholder().get());
            createVersionPropertyIfNecessary(descriptor.getVersionId());
        }
        if ("true".equals(paramsDesc.elementText("extensions")))
        {
            p.addElement("extensions").setText("true");
        }
        for (Object oParam : paramsDesc.elements())
        {
            detachAndAdd((Element) oParam, p);
        }
        return p;
    }

    private boolean applyBundleInstructionChanges(Iterable<BundleInstruction> instructions)
    {
        Element configRoot = getAmpsPluginConfiguration();
        boolean modified = false;
        Element instructionsRoot = getOrCreateElement(configRoot, "instructions");
        for (BundleInstruction instruction : instructions)
        {
            String categoryName = instruction.getCategory().getElementName();
            Element categoryElement = getOrCreateElement(instructionsRoot, categoryName);
            String body = categoryElement.getText();
            String[] instructionLines = (body == null) ? new String[0] : body.split(",");
            if (any(ImmutableList.copyOf(instructionLines), bundleInstructionLineWithPackageName(instruction.getPackageName())))
            {
                continue;
            }
            categoryElement.setText(addInstructionLine(instructionLines, instruction));
            modified = true;
        }
        return modified;
    }
    
    private static String addInstructionLine(String[] instructionLines, BundleInstruction instruction)
    {
        String newLine = instruction.getPackageName();
        for (String version : instruction.getVersion())
        {
            newLine = newLine + ";version=\"" + version + "\"";
        }
        if ((instructionLines.length == 0) || instructionLines[0].trim().equals(""))
        {
            return newLine;
        }
        StringBuilder buf = new StringBuilder();
        boolean inserted = false;
        String indent = "";
        Pattern indentRegex = Pattern.compile("^\\n*([ \\t]*).*");
        for (String oldLine : instructionLines)
        {
            if (buf.length() > 0)
            {
                buf.append(",");
            }
            if (!inserted && (oldLine.trim().compareTo(newLine) > 0))
            {
                buf.append("\n").append(indent).append(newLine).append(",\n");
                inserted = true;
            }
            if (indent.equals(""))
            {
                Matcher m = indentRegex.matcher(oldLine);
                if (m.matches())
                {
                    indent = m.group(1);
                }
            }
            buf.append(oldLine);
        }
        if (!inserted)
        {
            buf.append(",\n").append(newLine);
        }
        return buf.toString();
    }
    
    @SuppressWarnings("unchecked")
    private boolean applyPluginArtifactChanges(Iterable<com.atlassian.plugins.codegen.PluginArtifact> pluginArtifacts)
    {
        Element configRoot = getAmpsPluginConfiguration();
        boolean modified = false;
        for (com.atlassian.plugins.codegen.PluginArtifact p : pluginArtifacts)
        {
            String elementName = p.getType().getElementName();
            Element artifactsRoot = getOrCreateElement(configRoot, elementName + "s");
            if (!any(artifactsRoot.elements(elementName),
                     and(childElementValue("groupId", p.getGroupAndArtifactId().getGroupId().getOrElse("")),
                         childElementValue("artifactId", p.getGroupAndArtifactId().getArtifactId()))))
            {
                artifactsRoot.add(toArtifactElement(p));
                modified = true;
            }
        }
        return modified;
    }

    private boolean applyAmpsSystemPropertyChanges(Iterable<AmpsSystemPropertyVariable> propertyVariables)
    {
        Element configRoot = getAmpsPluginConfiguration();
        boolean modified = false;
        for (AmpsSystemPropertyVariable propertyVariable : propertyVariables)
        {
            Element variablesRoot = getOrCreateElement(configRoot, "systemPropertyVariables");
            if (variablesRoot.element(propertyVariable.getName()) == null)
            {
                variablesRoot.addElement(propertyVariable.getName()).setText(propertyVariable.getValue());
                modified = true;
            }
        }
        return modified;
    }
    
    private Element toArtifactElement(com.atlassian.plugins.codegen.PluginArtifact pluginArtifact)
    {
        Element ret = createElement(pluginArtifact.getType().getElementName());
        for (String groupId : pluginArtifact.getGroupAndArtifactId().getGroupId())
        {
            ret.addElement("groupId").setText(groupId);
        }
        ret.addElement("artifactId").setText(pluginArtifact.getGroupAndArtifactId().getArtifactId());
        if (pluginArtifact.getVersionId().isDefined())
        {
            ret.addElement("version").setText(pluginArtifact.getVersionId().getVersionOrPropertyPlaceholder().get());
            createVersionPropertyIfNecessary(pluginArtifact.getVersionId());
        }
        return ret;
    }
    
    @SuppressWarnings("unchecked")
    private Element findAmpsPlugin()
    {
        for (Element p : (List<Element>) getOrCreateElement(root, "build/plugins").elements("plugin"))
        {
            if (p.elementTextTrim("groupId").equals("com.atlassian.maven.plugins")
                && AMPS_PLUGIN_IDS.contains(p.elementTextTrim("artifactId")))
            {
                return p;
            }
        }
        throw new IllegalStateException("Could not find AMPS plugin element in POM");
    }

    private Element getAmpsPluginConfiguration()
    {
        return getOrCreateElement(findAmpsPlugin(), "configuration");
    }
    
    private static Element getOrCreateElement(Element container, String path)
    {
        Element last = container;
        for (String pathName : path.split("/"))
        {
            last = container.element(pathName);
            if (last == null)
            {
                last = container.addElement(pathName);
            }
            container = last;
        }
        return last;
    }
    
    private Document readPom(File f) throws DocumentException, IOException
    {
        final SAXReader reader = new SAXReader();
        reader.setMergeAdjacentText(true);
        reader.setStripWhitespaceText(true);
        return reader.read(new FileInputStream(f));
    }
    
    private void writePom(Document doc, File f) throws IOException
    {
        FileOutputStream fos = new FileOutputStream(f);
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setIndentSize(POM_INDENTATION);
        XMLWriter writer = new XMLWriter(fos, format);
        try
        {
            writer.write(doc);
        }
        finally
        {
            closeQuietly(fos);
        }
    }

    private Element createElement(String name)
    {
        return DocumentHelper.createElement(new QName(name, root.getNamespace()));
    }
    
    private void fixNamespace(Element e)
    {
        e.setQName(new QName(e.getName(), root.getNamespace()));
        for (Object child : e.elements())
        {
            fixNamespace((Element) child);
        }
    }
    
    private void detachAndAdd(Element e, Element container)
    {
        e.detach();
        fixNamespace(e);
        container.add(e);
    }
    
    private static Predicate<? super Element> childElementValue(final String name, final String value)
    {
        return new Predicate<Element>()
        {
            public boolean apply(Element input)
            {
                Element child = input.element(name);
                return (child == null) ? value.equals("") : value.equals(child.getText());
            }
        };
    }
    
    private static Predicate<String> bundleInstructionLineWithPackageName(final String packageName)
    {
        return new Predicate<String>()
        {
            public boolean apply(String input)
            {
                String s = input.trim();
                return s.equals(packageName) || s.startsWith(packageName + ";");
            }
        };
    }
}
