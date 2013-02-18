package com.atlassian.plugins.codegen.modules.stash;

import com.atlassian.plugins.codegen.ClassId;
import com.atlassian.plugins.codegen.modules.BasicClassModuleProperties;

import static com.atlassian.plugins.codegen.ClassId.fullyQualified;

public class SshScmRequestHandlerProperties extends BasicClassModuleProperties {

    public static final String FQ_HANDLER_CLASSNAME = "FQ_HANDLER_CLASSNAME";
    public static final String HANDLER_CLASSNAME = "HANDLER_CLASSNAME";
    public static final String REQUEST_CLASSNAME = "REQUEST_CLASSNAME";

    private ClassId handlerClassId;

    public SshScmRequestHandlerProperties(String fqClassName)
    {
        super(fqClassName);
    }

    @Override
    public void setFullyQualifiedClassname(String fqName)
    {
        super.setFullyQualifiedClassname(fqName);
        setFullyQualifiedHandlerClassname(fqName + "Handler");
        setRequestClassname(getClassId().getName());
    }

    public void setRequestClassname(String name) {
        setProperty(REQUEST_CLASSNAME, name);
    }

    public void setFullyQualifiedHandlerClassname(String fqName)
    {
        handlerClassId = fullyQualified(fqName);
        setProperty(FQ_HANDLER_CLASSNAME, fqName);
        setProperty(HANDLER_CLASSNAME, handlerClassId.getName());
    }

    public ClassId getHandlerClassId()
    {
        return handlerClassId;
    }

    public String getFullyQualifiedHandlerClassname()
    {
        return handlerClassId.getFullName();
    }

    public String getHandlerClassname()
    {
        return handlerClassId.getName();
    }

}
