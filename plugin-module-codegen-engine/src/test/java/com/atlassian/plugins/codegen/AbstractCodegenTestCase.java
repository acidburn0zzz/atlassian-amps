package com.atlassian.plugins.codegen;

import java.util.LinkedList;
import java.util.List;

import com.atlassian.plugins.codegen.modules.PluginModuleCreator;
import com.atlassian.plugins.codegen.modules.PluginModuleProperties;

import com.google.common.base.Joiner;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import static junit.framework.Assert.assertFalse;
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
    
    protected Document getAllGeneratedModulesOfType(String name) throws Exception
    {
        PluginProjectChangeset changeset = getChangesetForModule();
        assertFalse("did not generate any module descriptors", changeset.getModuleDescriptors().isEmpty());
        boolean found = false;
        Document ret = DocumentFactory.getInstance().createDocument();
        List<String> foundTypes = new LinkedList<String>();
        for (ModuleDescriptor module : changeset.getModuleDescriptors())
        {
            Document parsed = DocumentHelper.parseText(module.getContent());
            Element root = parsed.getRootElement();
            String type = root.getName();
            foundTypes.add(type);
            if (type.equals(name))
            {
                root.detach();
                ret.add(root);
                found = true;
            }
        }
        if (!found)
        {
            fail("did not generate any module descriptor of type \"" + name + "\"; generated modules were "
                + Joiner.on(", ").join(foundTypes));
        }
        return ret;
    }
    
    protected Element getGeneratedModule(String name) throws Exception
    {
        Document results = getAllGeneratedModulesOfType(name);
        assertEquals("found too many modules of type \"" + name + "\"", 1, results.selectNodes("//" + name).size());
        return (Element) results.selectSingleNode("//" + name);
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
        PluginProjectChangeset changeset = getChangesetForModule();
        assertFalse("did not generate any source files", changeset.getSourceFiles().isEmpty());
        List<String> foundFiles = new LinkedList<String>();
        for (SourceFile sourceFile : changeset.getSourceFiles())
        {
            if (sourceFile.getClassId().equals(ClassId.packageAndClass(packageName, className)) && sourceFile.getSourceGroup().equals(group))
            {
                return sourceFile;
            }
            foundFiles.add(sourceFile.getClassId().getFullName() + " (" + sourceFile.getSourceGroup() + ")");
        }
        fail("did not generate a source file for " + packageName + "." + className + " in " + group + "; generated files were "
             + Joiner.on(", ").join(foundFiles));
        return null;
    }

    protected ResourceFile getResourceFile(String path, String filename) throws Exception
    {
        PluginProjectChangeset changeset = getChangesetForModule();
        assertFalse("did not generate any resource files", changeset.getResourceFiles().isEmpty());
        List<String> foundFiles = new LinkedList<String>();
        for (ResourceFile resourceFile : changeset.getResourceFiles())
        {
            if (resourceFile.getRelativePath().equals(path) && resourceFile.getName().equals(filename))
            {
                return resourceFile;
            }
            foundFiles.add(resourceFile.getRelativePath() + "/" + resourceFile.getName());
        }
        fail("did not generate resource file " + path + "/" + filename + "; generated files were "
             + Joiner.on(", ").join(foundFiles));
        return null;
    }
}
