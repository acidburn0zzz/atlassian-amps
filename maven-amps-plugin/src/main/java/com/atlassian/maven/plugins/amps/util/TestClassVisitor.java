package com.atlassian.maven.plugins.amps.util;

import com.atlassian.plugins.osgi.test.Application;
import com.atlassian.plugins.osgi.test.AtlassianPluginsTestRunner;

import org.apache.commons.lang3.StringUtils;
import org.junit.runner.RunWith;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * @since version
 */
public class TestClassVisitor extends ClassVisitor
{
    private boolean isWiredTestClass;
    private boolean inITPackage;
    private String applicationFilter;

    public TestClassVisitor()
    {
        super(Opcodes.ASM5);
        this.isWiredTestClass = false;
        this.inITPackage = false;
        this.applicationFilter = "";
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces)
    {
        String normalClassName = normalize(name);

        if (normalClassName.startsWith("it."))
        {
            inITPackage = true;
        }
    }

    @Override
    public void visitEnd()
    {
        
    }

    @Override
    public AnnotationVisitor visitAnnotation(String annoName, boolean isVisible)
    {
        String normalName = normalize(annoName);
        if (RunWith.class.getName().equals(normalName))
        {
            return new RunWithAnnotationVisitor();
        }

        if (Application.class.getName().equals(normalName))
        {
            return new ApplicationAnnotationVisitor();
        }

        return null;
    }
    
    public boolean isWiredTest()
    {
        return (isWiredTestClass && inITPackage);
    }
    
    static String normalize(String name)
    {
        if (name == null)
        {
            return null;
        }

        if (name.startsWith("L") && name.endsWith(";"))
        {
            name = name.substring(1, name.length() - 1);
        }

        if (name.endsWith(".class"))
        {
            name = name.substring(0, name.length() - ".class".length());
        }

        return name.replace('/', '.');
    }

    public String getApplicationFilter()
    {
        return applicationFilter;
    }

    private class RunWithAnnotationVisitor extends AnnotationVisitor
    {

        public RunWithAnnotationVisitor()
        {
            super(Opcodes.ASM5);
        }

        @Override
        public void visit(String name, Object value)
        {
            if (value instanceof Type)
            {
                Type type = (Type) value;

                if (AtlassianPluginsTestRunner.class.getName().equals(normalize(type.getInternalName())))
                {
                    isWiredTestClass = true;
                }
            }
        }
    }

    private class ApplicationAnnotationVisitor extends AnnotationVisitor
    {

        public ApplicationAnnotationVisitor()
        {
            super(Opcodes.ASM5);
        }

        @Override
        public void visit(String name, Object value)
        {
            if (value instanceof String)
            {
                String app = (String) value;

                if (StringUtils.isNotBlank(app))
                {
                    applicationFilter = app;
                }
            }
        }
    }
}
