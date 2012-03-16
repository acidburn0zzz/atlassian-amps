package com.atlassian.plugins.codegen;

import com.atlassian.plugins.codegen.modules.PluginModuleCreator;
import com.atlassian.plugins.codegen.modules.PluginModuleProperties;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;

import static com.atlassian.fugue.Option.some;
import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;

/**
 *
 */
public abstract class AbstractCodegenTestCase<T extends PluginModuleProperties>
{
    public static final String PACKAGE_NAME = "com.atlassian.plugins.test";
    public static final String FUNC_TEST_PACKAGE_NAME = "it.com.atlassian.plugins.test";

    protected T props;
    protected PluginModuleCreator creator;
    protected PluginProjectChangeset changeset;
    
    public void setProps(T props)
    {
        this.props = props;
    }

    public void setCreator(PluginModuleCreator creator)
    {
        this.creator = creator;
    }
    
    protected PluginProjectChangeset getChangesetForModule() throws Exception
    {
        return creator.createModule(props);
    }
    
    protected <T extends PluginProjectChange> ImmutableList<T> getChangesetForModule(Class<T> itemClass) throws Exception
    {
        return ImmutableList.copyOf(getChangesetForModule().getItems(itemClass));
    }

    protected void failWithChangeset(PluginProjectChangeset changeset, String message)
    {
        fail(message + "; generated changeset was " + changeset.toString());
    }
    
    protected void assertChangesetContains(PluginProjectChange... changes) throws Exception
    {
        PluginProjectChangeset changeset = getChangesetForModule();
        for (PluginProjectChange change : changes)
        {
            if (!Iterables.contains(changeset.getItems(), change))
            {
                failWithChangeset(changeset, "did not generate expected change: " + change);
            }
        }
    }
    
    protected boolean hasGeneratedModulesOfType(String name) throws Exception
    {
        PluginProjectChangeset changeset = getChangesetForModule();
        for (ModuleDescriptor module : changeset.getItems(ModuleDescriptor.class))
        {
            if (name.equals(module.getType()))
            {
                return true;
            }
        }
        return false;
    }

    protected Document getAllGeneratedModulesOfType(String name) throws Exception
    {
        PluginProjectChangeset changeset = getChangesetForModule();
        boolean found = false;
        Document ret = DocumentFactory.getInstance().createDocument();
        ret.addElement("modules");
        for (ModuleDescriptor module : changeset.getItems(ModuleDescriptor.class))
        {
            if (module.getType().equals(name))
            {
                ret.getRootElement().add(module.getContent());
                found = true;
            }
        }
        if (!found)
        {
            failWithChangeset(changeset, "did not generate any module descriptor of type \"" + name + "\"");
        }
        return ret;
    }

    protected ComponentDeclaration getComponentOfClass(ClassId classId) throws Exception
    {
        PluginProjectChangeset changeset = getChangesetForModule();
        for (ComponentDeclaration component : changeset.getItems(ComponentDeclaration.class))
        {
            if (component.getClassId().equals(classId))
            {
                return component;
            }
        }
        failWithChangeset(changeset, "did not generate any component declaration of type \"" + classId + "\"");
        return null;
    }

    protected ComponentImport getComponentImportOfInterface(ClassId interfaceId) throws Exception
    {
        PluginProjectChangeset changeset = getChangesetForModule();
        for (ComponentImport component : changeset.getItems(ComponentImport.class))
        {
            if (component.getInterfaceClass().equals(interfaceId))
            {
                return component;
            }
        }
        failWithChangeset(changeset, "did not generate any component import for interface \"" + interfaceId + "\"");
        return null;
    }
    
    protected ArtifactDependency getDependency(String groupId, String artifactId) throws Exception
    {
        ArtifactId searchFor = ArtifactId.artifactId(some(groupId), artifactId);
        PluginProjectChangeset changeset = getChangesetForModule();
        for (ArtifactDependency dependency : changeset.getItems(ArtifactDependency.class))
        {
            if (searchFor.equals(dependency.getGroupAndArtifactId()))
            {
                return dependency;
            }
        }
        failWithChangeset(changeset, "did not generate any dependency for " + searchFor);
        return null;
    }
    
    protected Element getGeneratedModule(String name) throws Exception
    {
        Document results = getAllGeneratedModulesOfType(name);
        assertEquals("found too many modules of type \"" + name + "\"", 1, results.selectNodes("//" + name).size());
        return (Element) results.selectSingleNode("//modules/" + name);
    }

    protected I18nString getI18nString(String name, String value) throws Exception
    {
        I18nString searchFor = I18nString.i18nString(name, value);
        PluginProjectChangeset changeset = getChangesetForModule();
        for (I18nString i : changeset.getItems(I18nString.class))
        {
            if (searchFor.equals(i))
            {
                return i;
            }
        }
        failWithChangeset(changeset, "did not generate i18n string " + searchFor);
        return null;
    }
    
    protected SourceFile getSourceFile(String packageName, String className) throws Exception
    {
        return getSourceFile(SourceFile.SourceGroup.MAIN, packageName, className);
    }

    protected SourceFile getTestSourceFile(String packageName, String className) throws Exception
    {
        return getSourceFile(SourceFile.SourceGroup.TESTS, packageName, className);
    }

    protected SourceFile getSourceFile(SourceFile.SourceGroup group, String packageName, String className) throws Exception
    {
        ClassId searchFor = ClassId.packageAndClass(packageName, className);
        PluginProjectChangeset changeset = getChangesetForModule();
        for (SourceFile sourceFile : changeset.getItems(SourceFile.class))
        {
            if (sourceFile.getClassId().equals(searchFor) && sourceFile.getSourceGroup().equals(group))
            {
                return sourceFile;
            }
        }
        failWithChangeset(changeset, "did not generate a source file for " + searchFor + " in " + group);
        return null;
    }

    protected ResourceFile getResourceFile(String path, String filename) throws Exception
    {
        PluginProjectChangeset changeset = getChangesetForModule();
        for (ResourceFile resourceFile : changeset.getItems(ResourceFile.class))
        {
            if (resourceFile.getRelativePath().equals(path) && resourceFile.getName().equals(filename))
            {
                return resourceFile;
            }
        }
        failWithChangeset(changeset, "did not generate resource file " + path + "/" + filename);
        return null;
    }
}
