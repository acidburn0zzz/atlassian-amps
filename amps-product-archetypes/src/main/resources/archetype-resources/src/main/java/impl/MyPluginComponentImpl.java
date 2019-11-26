package ${package}.impl;

#set( $useOsgiJavaConfigParsed = $useOsgiJavaConfig.equalsIgnoreCase('Y') )
#if (!$useOsgiJavaConfigParsed)
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
#end
import com.atlassian.sal.api.ApplicationProperties;
import ${package}.api.MyPluginComponent;

#if (!$useOsgiJavaConfigParsed)
import javax.inject.Inject;
import javax.inject.Named;
#end

#if (!$useOsgiJavaConfigParsed)
@ExportAsService ({MyPluginComponent.class})
@Named ("myPluginComponent")
#end
public class MyPluginComponentImpl implements MyPluginComponent
{
    #if (!$useOsgiJavaConfigParsed)
    @ComponentImport
    #end
    private final ApplicationProperties applicationProperties;

    #if (!$useOsgiJavaConfigParsed)
    @Inject
    #end
    public MyPluginComponentImpl(final ApplicationProperties applicationProperties)
    {
        this.applicationProperties = applicationProperties;
    }

    public String getName()
    {
        if(null != applicationProperties)
        {
            return "myComponent:" + applicationProperties.getDisplayName();
        }
        
        return "myComponent";
    }
}