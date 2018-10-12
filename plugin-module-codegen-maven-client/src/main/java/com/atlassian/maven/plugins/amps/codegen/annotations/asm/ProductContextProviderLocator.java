package com.atlassian.maven.plugins.amps.codegen.annotations.asm;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.plugins.codegen.annotations.asm.AbstractAnnotationParser;
import com.atlassian.plugins.codegen.modules.PluginModuleCreatorRegistry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @since 3.6
 */
public class ProductContextProviderLocator extends AbstractAnnotationParser
{
    protected static final Map<String, String> productContextPackages = new HashMap<String, String>();

    static
    {
        productContextPackages.put(PluginModuleCreatorRegistry.JIRA, "com.atlassian.jira.plugin.webfragment.contextproviders");
        productContextPackages.put(PluginModuleCreatorRegistry.CONFLUENCE, "com.atlassian.jira.plugin.webfragment.contextproviders");
        productContextPackages.put(PluginModuleCreatorRegistry.BAMBOO, "com.atlassian.jira.plugin.webfragment.contextproviders");
        productContextPackages.put(PluginModuleCreatorRegistry.CROWD, "com.atlassian.jira.plugin.webfragment.contextproviders");
        productContextPackages.put(PluginModuleCreatorRegistry.FECRU, "com.atlassian.jira.plugin.webfragment.contextproviders");
        productContextPackages.put(PluginModuleCreatorRegistry.REFAPP, "com.atlassian.jira.plugin.webfragment.contextproviders");
    }

    private String productId;
    private Map<String, String> contextRegistry;

    public ProductContextProviderLocator(String productId, Map<String, String> contextRegistry)
    {
        this.productId = productId;
        this.contextRegistry = contextRegistry;
    }

    public void parse() throws Exception
    {
        String basePackage = productContextPackages.get(productId);
        if (StringUtils.isNotBlank(basePackage))
        {
            parse(basePackage, new ContextProviderClassVisitor());
        }

    }

    public void parse(String basePackage) throws Exception
    {
        parse(basePackage, new ContextProviderClassVisitor());
    }

    public class ContextProviderClassVisitor extends ClassVisitor
    {

        private String visitedClassname;
        private boolean isContextProvider;

        public ContextProviderClassVisitor()
        {
            super(Opcodes.ASM5);
        }

        @Override
        public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces)
        {
            this.visitedClassname = normalize(name);
            String iface = "com/atlassian/plugin/web/ContextProvider";

            this.isContextProvider = false;
            boolean isAbstract = ((access & Opcodes.ACC_ABSTRACT) > 0);

            if (!isAbstract)
            {
                this.isContextProvider = ArrayUtils.contains(interfaces, iface);
                if (!isContextProvider)
                {
                    this.isContextProvider = superHasInterface(superName, iface);
                }
            }

            if (isContextProvider)
            {
                String simpleName = StringUtils.substringAfterLast(visitedClassname, ".");
                contextRegistry.put(simpleName, visitedClassname);
            }

        }

        private boolean superHasInterface(String superName, String interfaceName)
        {
            boolean hasInterface = false;

            if (normalize(superName).equals("java.lang.Object"))
            {
                return false;
            }

            ClassLoader classLoader = Thread.currentThread()
                    .getContextClassLoader();
            String path = superName.replace('.', '/');

            InputStream is = null;
            try
            {
                is = classLoader.getResourceAsStream(path + ".class");
                if (null != is)
                {

                    ClassReader classReader = new ClassReader(is);
                    hasInterface = ArrayUtils.contains(classReader.getInterfaces(), interfaceName);
                    if (!hasInterface)
                    {
                        hasInterface = superHasInterface(classReader.getSuperName(), interfaceName);
                    }
                }
            } catch (Exception e)
            {
                //don't care
            } finally
            {
                IOUtils.closeQuietly(is);
            }

            return hasInterface;
        }

    }
}
