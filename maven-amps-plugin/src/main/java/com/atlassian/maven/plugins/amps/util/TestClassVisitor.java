package com.atlassian.maven.plugins.amps.util;

import com.atlassian.plugins.osgi.test.AtlassianPluginsTestRunner;

import org.junit.runner.RunWith;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.EmptyVisitor;

/**
 * @since version
 */
public class TestClassVisitor extends EmptyVisitor
{
    private boolean isWiredTestClass;
    private boolean inITPackage;
    private String normalClassName;

    public TestClassVisitor()
    {
        this.isWiredTestClass = false;
        this.inITPackage = false;
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces)
    {
        this.normalClassName = normalize(name);

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

    private class RunWithAnnotationVisitor extends EmptyVisitor
    {

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
}
