package com.atlassian.plugins.codegen.modules.jira;

import com.atlassian.plugins.codegen.ClassId;
import com.atlassian.plugins.codegen.modules.BasicClassModuleProperties;

import org.apache.commons.lang.StringUtils;

import static com.atlassian.plugins.codegen.ClassId.fullyQualified;

/**
 * @since 3.6
 */
public class WorkflowElementProperties extends BasicClassModuleProperties
{
    public static final String FQ_FACTORY_NAME = "FQ_FACTORY_NAME";
    public static final String FACTORY_NAME = "FACTORY_NAME";

    private ClassId factoryClassId;
    
    public WorkflowElementProperties()
    {
        this("MyWorkflowElement");
    }

    public WorkflowElementProperties(String fqClassName)
    {
        super(fqClassName);
    }

    @Override
    public void setFullyQualifiedClassname(String fqName)
    {
        super.setFullyQualifiedClassname(fqName);
        setFullyQualifiedFactoryName(fqName + "Factory");
    }

    public void setFullyQualifiedFactoryName(String fqName)
    {
        factoryClassId = fullyQualified(fqName);
        setProperty(FQ_FACTORY_NAME, fqName);
        setProperty(FACTORY_NAME, factoryClassId.getName());
    }

    public ClassId getFactoryClassId()
    {
        return factoryClassId;
    }
    
    public String getFullyQualifiedFactoryName()
    {
        return factoryClassId.getFullName();
    }

    public String getFactoryName()
    {
        return factoryClassId.getName();
    }
}
