package com.atlassian.plugins.codegen.modules;

import java.io.File;
import java.io.StringReader;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import com.atlassian.plugins.codegen.ClassId;
import com.atlassian.plugins.codegen.I18nString;
import com.atlassian.plugins.codegen.PluginProjectChangeset;
import com.atlassian.plugins.codegen.modules.common.Resource;
import com.atlassian.plugins.codegen.util.CodeTemplateHelper;

import com.google.common.collect.ImmutableMap;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import static com.atlassian.plugins.codegen.I18nString.i18nStrings;
import static com.atlassian.plugins.codegen.ModuleDescriptor.moduleDescriptor;
import static com.atlassian.plugins.codegen.PluginProjectChangeset.changeset;
import static com.atlassian.plugins.codegen.ResourceFile.resourceFile;
import static com.atlassian.plugins.codegen.SourceFile.sourceFile;
import static com.atlassian.plugins.codegen.SourceFile.SourceGroup.MAIN;
import static com.atlassian.plugins.codegen.SourceFile.SourceGroup.TESTS;

/**
 * Abstract base class for implementations of {@link PluginModuleCreator} that provides some
 * helper methods for commonly used project modifications.
 */
public abstract class AbstractPluginModuleCreator<T extends PluginModuleProperties> implements PluginModuleCreator<T>
{
    public static final String DEFAULT_I18N_NAME = "atlassian-plugin";
    public static final String FUNC_TEST_PACKAGE = "it";
    public static final String UNIT_TEST_PACKAGE = "ut";
    public static final String TEST_SUFFIX = "Test";
    public static final String FUNCT_TEST_SUFFIX = "FuncTest";
    public static final String GENERIC_TEMPLATE_PREFIX = "templates/generic/";
    public static final String GENERIC_TEST_TEMPLATE = GENERIC_TEMPLATE_PREFIX + "GenericTest.java.vtl";
    public static final String TEMPLATES = "templates" + File.separator;
    
    protected CodeTemplateHelper templateHelper;

    protected AbstractPluginModuleCreator()
    {
        this(new CodeTemplateHelper());
    }

    protected AbstractPluginModuleCreator(CodeTemplateHelper templateHelper)
    {
        this.templateHelper = templateHelper;
    }

    @Override
    public abstract PluginProjectChangeset createModule(T props) throws Exception;

    /**
     * Returns a changeset that will add a source file to the project, within the main source directory.
     * @param props  property set whose {@link ClassBasedModuleProperties#getClassId()} method provides the class name;
     *   other properties may be used in the template
     * @param templateName  path to the template file for creating the source code
     * @return  a {@link PluginProjectChangeset} that describes the new file
     */
    protected PluginProjectChangeset createClass(ClassBasedModuleProperties props, String templateName) throws Exception
    {
        return createClass(props, props.getClassId(), templateName);
    }
    
    /**
     * Returns a changeset that will add a source file to the project, within the main source directory.
     * @param props  property set whose properties may be used in the template
     * @param classId  describes the class and package name
     * @param templateName  path to the template file for creating the source code
     * @return  a {@link PluginProjectChangeset} that describes the new file
     */
    protected PluginProjectChangeset createClass(ClassBasedModuleProperties props, ClassId classId, String templateName) throws Exception
    {
        return changeset().with(sourceFile(classId, MAIN, fromTemplate(templateName, props.withClass(classId))));
    }

    /**
     * Returns a changeset that will add a source file to the project, within the test source directory.
     * @param props  property set whose properties may be used in the template
     * @param classId  describes the class and package name
     * @param templateName  path to the template file for creating the source code
     * @return  a {@link PluginProjectChangeset} that describes the new file
     */
    protected PluginProjectChangeset createTestClass(ClassBasedModuleProperties props, ClassId classId, String templateName) throws Exception
    {
        return changeset().with(sourceFile(classId, TESTS, fromTemplate(templateName, props.withClass(classId))));
    }

    /**
     * Returns a changeset that will add a source file and also its corresponding unit test.
     * @param props  property set whose properties may be used in the template; the source file will be
     *   for the class described by {@link ClassBasedModuleProperties#getClassId()} and the unit test
     *   class will be determined by {@link #testClassFor(ClassId)}
     * @param mainTemplate  path to the template file for creating the main source code
     * @param unitTestTemplate  path to the template file for creating the unit test source code
     * @return  a {@link PluginProjectChangeset} that describes the new files
     */
    protected PluginProjectChangeset createClassAndTests(ClassBasedModuleProperties props,
                                                         String mainTemplate,
                                                         String unitTestTemplate) throws Exception
    {
        ClassId testClass = testClassFor(props.getClassId());
        return changeset()
            .with(createClass(props, mainTemplate))
            .with(createTestClass(props.withClass(testClass), testClass, unitTestTemplate));
    }
    
    /**
     * Returns a changeset that will add a source file and also its corresponding unit test and
     * functional test.
     * @param props  property set whose properties may be used in the template; the source file will be
     *   for the class described by {@link ClassBasedModuleProperties#getClassId()}, the unit test
     *   class will be determined by {@link #testClassFor(ClassId)}, and the functional test class
     *   will be determined by {@link #funcTestClassFor(ClassId)}
     * @param mainTemplate  path to the template file for creating the main source code
     * @param unitTestTemplate  path to the template file for creating the unit test source code
     * @return  a {@link PluginProjectChangeset} that describes the new files
     */
    protected PluginProjectChangeset createClassAndTests(ClassBasedModuleProperties props,
                                                         String mainTemplate,
                                                         String unitTestTemplate,
                                                         String funcTestTemplate) throws Exception
    {
        ClassId funcTestClass = funcTestClassFor(props.getClassId());
        return createClassAndTests(props, mainTemplate, unitTestTemplate)
            .with(createTestClass(props.withClass(funcTestClass), funcTestClass, funcTestTemplate));
    }
    
    /**
     * Returns a changeset that will add a plugin module to the project's plugin XML file.  Also
     * adds any i18n properties that are required for the module.
     * @param props  property set whose properties may be used in the template; also may provide
     *   i18n properties with {@link PluginModuleProperties#getI18nProperties()}
     * @param templateName  path to the template file for the module's XML fragment
     * @return  a {@link PluginProjectChangeset} that describes the new module and i18n changes
     */
    protected PluginProjectChangeset createModule(PluginModuleProperties props, String templateName) throws Exception
    {
        return changeset().with(moduleDescriptor(fromTemplate(templateName, props)))
            .with(i18nStrings(props.getI18nProperties()));
    }
    
    /**
     * Returns a changeset that will add a resource file to the project, within the main resource
     * directory or a subdirectory thereof.
     * @param props  property set whose properties may be used in the template
     * @param path  relative path within the resource directory, or "" for no subpath
     * @param fileName  base name for the new file
     * @param templateName  path to the template file for the resource content
     * @return  a {@link PluginProjectChangeset} that describes the new file
     */
    protected PluginProjectChangeset createResource(Map<Object, Object> props, String path, String fileName, String templateName) throws Exception
    {
        return changeset().with(resourceFile(path, fileName, fromTemplate(templateName, props)));
    }

    /**
     * Returns a changeset that will add a resource file to the project, within a subdirectory of
     * the main resource directory that is prefixed with "templates/".
     * @param props  property set whose properties may be used in the template
     * @param path  relative path within the resource directory, or "" for no subpath
     * @param fileName  base name for the new file
     * @param templateName  path to the template file for the resource content
     * @return  a {@link PluginProjectChangeset} that describes the new file
     */
    protected PluginProjectChangeset createTemplateResource(Map<Object, Object> props, String path, String fileName, String templateName) throws Exception
    {
        path = path.equals("") ? TEMPLATES : (path.startsWith(TEMPLATES) ? path : (TEMPLATES + path));
        return changeset().with(resourceFile(path, fileName, fromTemplate(templateName, props)));
    }

    /**
     * Wrapper for {@link #createTemplateResource(Map, String, String, String)} that derives the path
     * and filename from a {@link Resource} object.  Also sets the property "CURRENT_VIEW" to the
     * filename when generating the template.
     * @param props  property set whose properties may be used in the template
     * @param resource  a {@link Resource} describing the path and filename
     * @param templateName  path to the template file for the resource content
     * @return  a {@link PluginProjectChangeset} that describes the new file
     */
    protected PluginProjectChangeset createTemplateResource(Map<Object, Object> props, Resource resource, String templateName) throws Exception
    {
        String resourceFullPath = FilenameUtils.separatorsToSystem(resource.getLocation());
        String path = FilenameUtils.getPath(resourceFullPath);
        String fileName = FilenameUtils.getName(resourceFullPath);
        Map<Object, Object> tempProps = ImmutableMap.builder().putAll(props).put("CURRENT_VIEW", fileName).build();
        return createTemplateResource(tempProps, path, fileName, templateName);
    }
    
    /**
     * Returns a changeset that will add a set of I18n strings to the project, based on a properties file
     * that can contain template variables.
     * @param props  property set whose properties may be used in the template
     * @param templateName  path to the template file for the property list
     * @return  a {@link PluginProjectChangeset} that describes the new I18n strings
     */
    @SuppressWarnings("unchecked")
    protected PluginProjectChangeset createI18nStrings(Map<Object, Object> props, String templateName) throws Exception
    {
        String propListString = fromTemplate(templateName, props);
        Properties propList = new Properties();
        propList.load(new StringReader(propListString));
        return changeset().with(I18nString.i18nStrings(new TreeMap<String, String>((Map) propList)));
    }
    
    /**
     * Generates content using a template file.
     * @param templatePath  path to the template file
     * @param props  properties that may be used in the template
     * @return  the generated content
     * @throws Exception if the tmplate resource cannot be read
     */
    protected String fromTemplate(String templatePath, Map<Object, Object> props) throws Exception
    {
        return templateHelper.getStringFromTemplate(templatePath, props);
    }

    /**
     * Reads a file as-is from the classpath, with no template substitution.
     * @param filePath  path to the template file
     * @return  the file content
     * @throws Exception if the tmplate resource cannot be read
     */
    protected String fromFile(String filePath) throws Exception
    {
        return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(filePath));
    }
    
    /**
     * Returns the standard unit test class corresponding to the given class.  The test class
     * is in the same package but has {@link #TEST_SUFFIX} appended to its name.
     */
    protected ClassId testClassFor(ClassId mainClass)
    {
        return mainClass.packageNamePrefix(UNIT_TEST_PACKAGE).classNameSuffix(TEST_SUFFIX);
    }
    
    /**
     * Returns the standard functional test class corresponding to the given class.  The test
     * class has {@link #FUNCT_TEST_SUFFIX} appended to its name and {@link #FUNC_TEST_PACKAGE}
     * prepended to its package.
     */
    protected ClassId funcTestClassFor(ClassId mainClass)
    {
        return mainClass.packageNamePrefix(FUNC_TEST_PACKAGE).classNameSuffix(FUNCT_TEST_SUFFIX);
    }
}
