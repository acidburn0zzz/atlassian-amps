package com.atlassian.plugins.codegen.util;

import java.io.File;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;

import com.atlassian.plugins.codegen.modules.BasicClassModuleProperties;
import com.atlassian.plugins.codegen.modules.ClassBasedModuleProperties;
import com.atlassian.plugins.codegen.modules.PluginModuleProperties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 *
 */
public class CodeTemplateHelper
{

    private static Logger LOG = Logger.getLogger(CodeTemplateHelper.class);
    public static final String UTF8 = "UTF-8";

    static
    {
        Velocity.setProperty(RuntimeConstants.INPUT_ENCODING, UTF8);
        Velocity.setProperty(RuntimeConstants.PARSER_POOL_SIZE, 3);
        Velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        Velocity.setProperty("classpath." + RuntimeConstants.RESOURCE_LOADER + ".class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.setProperty(Velocity.VM_LIBRARY, "templates/macros.vm");
        Velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, "org.apache.velocity.runtime.log.NullLogSystem");
        try
        {
            Velocity.init();
        } catch (Exception e)
        {
            LOG.error("Unable to init velocity", e);
        }
    }

    public String parseTemplate(String templatePath, Map<Object, Object> props) throws Exception
    {
        VelocityContext ctx = new VelocityContext();
        ctx.put("parseCheck", new TemplateChecker());

        for (Map.Entry<Object, Object> entry: props.entrySet())
        {
            ctx.put(entry.getKey().toString(), entry.getValue());
        }

        final StringWriter stringWriter = new StringWriter();
        Template template = Velocity.getTemplate(templatePath);

        template.merge(ctx, stringWriter);

        return stringWriter.toString();
    }

    public void writeJavaClassFromTemplate(String templatePath, String className, File sourceDirectory, String packageName, ClassBasedModuleProperties props) throws Exception
    {
        String originalClass = props.getClassId().getFullName();
        PluginModuleProperties overrideProps = new BasicClassModuleProperties(originalClass);

        overrideProps.putAll(props);
        overrideProps.setProperty("CLASSNAME", className);
        overrideProps.setProperty("PACKAGE", packageName);

        String content = parseTemplate(templatePath, overrideProps);
        String packagePath = packageName.length() == 0 ? "" : packageName.replaceAll("\\.", Matcher.quoteReplacement(File.separator));

        File packageFile = sourceDirectory;
        if (!packagePath.equals(""))
        {
            packageFile = new File(sourceDirectory, packagePath);
        }
        packageFile.mkdirs();

        File javaFile = new File(packageFile, className + ".java");
        FileUtils.writeStringToFile(javaFile, content, UTF8);

    }

    public void writeFileFromTemplate(String templatePath, String fileName, File directory, PluginModuleProperties props) throws Exception
    {
        String content = parseTemplate(templatePath, props);
        File newFile = new File(directory, fileName);

        FileUtils.writeStringToFile(newFile, content, UTF8);

    }

    public String getStringFromTemplate(String templatePath, Map<Object, Object> props) throws Exception
    {
        return parseTemplate(templatePath, props);
    }

    public class TemplateChecker
    {
        public synchronized boolean templateExists(String templatePath)
        {
            URL resourceUrl = ClasspathResourceLoader.class.getResource(templatePath);

            return resourceUrl != null;
        }
    }
}
