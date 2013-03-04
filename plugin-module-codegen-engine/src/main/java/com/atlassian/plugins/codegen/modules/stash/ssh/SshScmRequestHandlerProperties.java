package com.atlassian.plugins.codegen.modules.stash.ssh;

import com.atlassian.plugins.codegen.ClassId;
import com.atlassian.plugins.codegen.modules.BasicClassModuleProperties;

import static com.atlassian.plugins.codegen.ClassId.fullyQualified;

public class SshScmRequestHandlerProperties extends BasicClassModuleProperties
{

    public static final String FQ_HANDLER_CLASSNAME = "FQ_HANDLER_CLASSNAME";
    public static final String HANDLER_CLASSNAME = "HANDLER_CLASSNAME";
    public static final String FQ_REQUEST_CLASSNAME = "FQ_REQUEST_CLASSNAME";
    public static final String REQUEST_CLASSNAME = "REQUEST_CLASSNAME";

    private ClassId requestClassId;

    public SshScmRequestHandlerProperties(String fqRequestClassName)
    {
        this(fqRequestClassName, fqRequestClassName + "Handler");
    }

    private SshScmRequestHandlerProperties(String fqRequestClassName, String fqHandlerClassName)
    {
        super(fqHandlerClassName);
        setFullyQualifiedHandlerClassname(fqHandlerClassName);
        setFullyQualifiedRequestClassname(fqRequestClassName);
    }

    public void setFullyQualifiedRequestClassname(String fqName)
    {
        requestClassId = fullyQualified(fqName);
        setProperty(FQ_REQUEST_CLASSNAME, fqName);
        setProperty(REQUEST_CLASSNAME, requestClassId.getName());
    }

    public void setFullyQualifiedHandlerClassname(String fqName)
    {
        ClassId handlerClassId = fullyQualified(fqName);
        setProperty(FQ_HANDLER_CLASSNAME, fqName);
        setProperty(HANDLER_CLASSNAME, handlerClassId.getName());
    }

    public ClassId getRequestClassId()
    {
        return requestClassId;
    }

}
