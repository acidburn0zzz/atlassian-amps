package com.atlassian.plugins.codegen.modules;

import com.atlassian.plugins.codegen.ClassId;

import static com.atlassian.plugins.codegen.ClassId.fullyQualified;

/**
 * @since 3.6
 */
public class ClassWithInterfaceProperties extends BasicClassModuleProperties
{
    public static final String INTERFACE_CLASS = "INTERFACE_CLASS";
    public static final String FQ_INTERFACE = "FQ_INTERFACE";
    public static final String INTERFACE_PACKAGE = "INTERFACE_PACKAGE";

    private ClassId interfaceDescriptor;
    
    public ClassWithInterfaceProperties()
    {
        this("MyClass");
    }

    public ClassWithInterfaceProperties(String fqClassName)
    {
        super(fqClassName);
    }

    public void setFullyQualifiedInterface(String fqName)
    {
        interfaceDescriptor = fullyQualified(fqName);
        setProperty(FQ_INTERFACE, fqName);
        setProperty(INTERFACE_PACKAGE, interfaceDescriptor.getPackage());
        setProperty(INTERFACE_CLASS, interfaceDescriptor.getName());
    }

    public ClassId getInterfaceId()
    {
        return interfaceDescriptor;
    }
}
