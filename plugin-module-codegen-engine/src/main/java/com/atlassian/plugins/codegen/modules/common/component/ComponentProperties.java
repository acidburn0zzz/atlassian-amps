package com.atlassian.plugins.codegen.modules.common.component;

import com.atlassian.plugins.codegen.modules.BasicClassModuleProperties;
import com.atlassian.plugins.codegen.modules.PluginModuleProperties;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @since version
 */
public class ComponentProperties extends BasicClassModuleProperties implements PluginModuleProperties {
    public static final String INTERFACE_CLASS = "INTERFACE_CLASS";
    public static final String FQ_INTERFACE = "FQ_INTERFACE";
    public static final String INTERFACE_PACKAGE = "INTERFACE_PACKAGE";
    public static final String ALIAS = "ALIAS";
    public static final String PUBLIC = "PUBLIC";
    public static final String SERVICE_PROPS = "SERVICE_PROPS";

    private boolean generateInterface;
    private boolean generateClass;

    public ComponentProperties() {
        this("MyComponentProperties");
    }

    public ComponentProperties(String fqClassName) {
        super(fqClassName);
        put(SERVICE_PROPS, new HashMap<String, String>());

        setAlias("");
        setPublic(false);
    }

    public void setAlias(String alias) {
        setProperty(ALIAS,alias);
    }

    public void setPublic(boolean isPublic) {
        setProperty(PUBLIC,Boolean.toString(isPublic));
    }

    public void setFullyQualifiedInterface(String fqName) {
        String classname;
        if (StringUtils.isNotBlank(fqName)) {

            if (fqName.lastIndexOf(".") > 0) {
                classname = StringUtils.substringAfterLast(fqName, ".");
                String packageName = StringUtils.substringBeforeLast(fqName, ".");
                setProperty(INTERFACE_CLASS, classname);
                setProperty(INTERFACE_PACKAGE, packageName);
            } else {
                classname = fqName;
                setProperty(INTERFACE_CLASS, classname);
                setProperty(INTERFACE_PACKAGE, "");
            }

            setProperty(FQ_INTERFACE, fqName);
        }
    }

    public void setServiceProps(Map<String, String> params) {
        put(SERVICE_PROPS, params);
    }

    @SuppressWarnings(value = "unchecked")
    public void addServiceProp(String name, String value) {
        Map<String, String> params = (Map<String, String>) get(SERVICE_PROPS);
        if (params == null) {
            params = new HashMap<String, String>();
            setServiceProps(params);
        }

        params.put(name, value);
    }

    public boolean generateInterface() {
        return generateInterface;
    }

    public void setGenerateInterface(boolean generateInterface) {
        this.generateInterface = generateInterface;
    }

    public boolean generateClass() {
        return generateClass;
    }

    public void setGenerateClass(boolean generateClass) {
        this.generateClass = generateClass;
    }
}
