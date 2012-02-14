package com.atlassian.plugins.codegen.modules;

import com.atlassian.plugins.codegen.ClassId;

import static com.atlassian.plugins.codegen.ClassId.fullyQualified;

/**
 * @since 3.6
 */
public abstract class AbstractClassBasedModuleProperties extends AbstractNameBasedModuleProperties implements ClassBasedModuleProperties
{
    private ClassId classId;
    private ClassId classUnderTestId;
    
    protected AbstractClassBasedModuleProperties()
    {
        super();
    }

    public void setFullyQualifiedClassname(String fqName)
    {
        classId = fullyQualified(fqName);
        setProperty(FQ_CLASSNAME, fqName);
        setProperty(PACKAGE, classId.getPackage());
        setProperty(CLASSNAME, classId.getName());
        setClassUnderTest(fqName);
    }

    public void setClassUnderTest(String fqName)
    {
        classUnderTestId = fullyQualified(fqName);
        setProperty(FQ_CLASS_UNDER_TEST, fqName);
        setProperty(PACKAGE_UNDER_TEST, classUnderTestId.getPackage());
        setProperty(CLASS_UNDER_TEST, classUnderTestId.getName());
    }

    public ClassId getClassId()
    {
        return classId;
    }
    
    public ClassId getClassUnderTest()
    {
        return classUnderTestId;
    }
}
