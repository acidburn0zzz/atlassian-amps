package com.atlassian.plugins.codegen.annotations.asm;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.atlassian.plugins.codegen.annotations.*;
import com.atlassian.plugins.codegen.modules.PluginModuleCreator;
import com.atlassian.plugins.codegen.modules.PluginModuleCreatorRegistry;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.EmptyVisitor;

/**
 * @since 3.6
 */
public class ModuleCreatorAnnotationParser extends AbstractAnnotationParser
{

    public static final String MODULE_PACKAGE = "com.atlassian.plugins.codegen.modules";
    protected static final Map<String, String> annotationProductMap = new HashMap<String, String>();

    static
    {
        annotationProductMap.put(JiraPluginModuleCreator.class.getName(), PluginModuleCreatorRegistry.JIRA);
        annotationProductMap.put(ConfluencePluginModuleCreator.class.getName(), PluginModuleCreatorRegistry.CONFLUENCE);
        annotationProductMap.put(BambooPluginModuleCreator.class.getName(), PluginModuleCreatorRegistry.BAMBOO);
        annotationProductMap.put(CrowdPluginModuleCreator.class.getName(), PluginModuleCreatorRegistry.CROWD);
        annotationProductMap.put(FeCruPluginModuleCreator.class.getName(), PluginModuleCreatorRegistry.FECRU);
        annotationProductMap.put(StashPluginModuleCreator.class.getName(), PluginModuleCreatorRegistry.STASH);
        annotationProductMap.put(RefAppPluginModuleCreator.class.getName(), PluginModuleCreatorRegistry.REFAPP);
    }

    private final PluginModuleCreatorRegistry pluginModuleCreatorRegistry;

    public ModuleCreatorAnnotationParser(PluginModuleCreatorRegistry pluginModuleCreatorRegistry)
    {
        super();
        this.pluginModuleCreatorRegistry = pluginModuleCreatorRegistry;
    }

    public void parse() throws Exception
    {
        ClassLoader oldLoader = Thread.currentThread()
                .getContextClassLoader();
        Thread.currentThread()
                .setContextClassLoader(getClass().getClassLoader());
        parse(MODULE_PACKAGE, new ModuleClassVisitor());
        Thread.currentThread()
                .setContextClassLoader(oldLoader);
    }

    public void parse(String basePackage) throws Exception
    {
        ClassLoader oldLoader = Thread.currentThread()
                .getContextClassLoader();
        Thread.currentThread()
                .setContextClassLoader(getClass().getClassLoader());
        parse(basePackage, new ModuleClassVisitor());
        Thread.currentThread()
                .setContextClassLoader(oldLoader);
    }

    public class ModuleClassVisitor extends EmptyVisitor
    {

        private String visitedClassname;
        private boolean isModuleCreator;

        @Override
        public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces)
        {
            this.visitedClassname = normalize(name);
            String iface = PluginModuleCreator.class.getName()
                    .replace('.', '/');
            this.isModuleCreator = ArrayUtils.contains(interfaces, iface);
            if (!isModuleCreator)
            {
                this.isModuleCreator = superHasInterface(superName, iface);
            }
        }

        private boolean superHasInterface(String superName, String interfaceName)
        {
            boolean hasInterface = false;

            if (normalize(superName).equals("java.lang.Object"))
            {
                return hasInterface;
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

        @Override
        public AnnotationVisitor visitAnnotation(String annotationName, boolean isVisible)
        {
            String normalizedName = normalize(annotationName);

            if (isModuleCreator && annotationProductMap.containsKey(normalizedName))
            {
                return new ProductCreatorAnnotationVisitor(normalizedName);
            }

            return null;
        }


        @Override
        public MethodVisitor visitMethod(int i, String s, String s1, String s2, String[] strings)
        {
            return null;
        }

        @Override
        public FieldVisitor visitField(int i, String s, String s1, String s2, Object o)
        {
            return null;
        }

        private class ProductCreatorAnnotationVisitor extends EmptyVisitor
        {

            private String annotationName;

            private ProductCreatorAnnotationVisitor(String annotationName)
            {
                this.annotationName = annotationName;
            }

            @Override
            public void visitEnd()
            {

                super.visitEnd();

                String productId = annotationProductMap.get(annotationName);
                if (StringUtils.isNotBlank(productId))
                {
                    try
                    {
                        PluginModuleCreator creator = (PluginModuleCreator) Class.forName(visitedClassname)
                                .newInstance();
                        pluginModuleCreatorRegistry.registerModuleCreator(productId, creator);
                    } catch (Exception e)
                    {
                        e.printStackTrace();
                        //just don't register
                    }
                }
            }
        }
    }
}
