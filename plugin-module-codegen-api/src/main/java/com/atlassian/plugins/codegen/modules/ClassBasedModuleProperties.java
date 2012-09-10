package com.atlassian.plugins.codegen.modules;

import com.atlassian.plugins.codegen.ClassId;

/**
 * @since 3.6
 */
public interface ClassBasedModuleProperties extends NameBasedModuleProperties
{
    String FQ_CLASSNAME = "FQ_CLASSNAME";
    String CLASSNAME = "CLASSNAME";
    String PACKAGE = "PACKAGE";
    String CLASS_UNDER_TEST = "CLASS_UNDER_TEST";
    String PACKAGE_UNDER_TEST = "PACKAGE_UNDER_TEST";
    String FQ_CLASS_UNDER_TEST = "FQ_CLASS_UNDER_TEST";

    ClassId getClassId();
    
    ClassId getClassUnderTest();

    ClassBasedModuleProperties withClass(ClassId newClass);
}
