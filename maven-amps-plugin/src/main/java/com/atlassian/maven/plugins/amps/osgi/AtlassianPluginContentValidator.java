package com.atlassian.maven.plugins.amps.osgi;

import com.atlassian.plugins.codegen.util.PluginXmlHelper;
import com.google.common.collect.ImmutableList;
import org.apache.maven.plugin.MojoFailureException;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Validates the content of atlassian-plugin.xml when the AMPS instructions contain Atlassian-Plugin-Key
 *
 * @since 6.2
 */
public class AtlassianPluginContentValidator {

    private final List<String> forbiddenElements = ImmutableList.of("component-import", "component", "module-type");

    /**
     * Validates the content of atlassian-plugin.xml. Fails the validation if atlassian-plugin.xml contains any of
     * the following elements:
     * <p>
     *      &lt;component&gt;<br>
     *      &lt;component-import&gt;<br>
     *      &lt;module-type&gt;
     *
     * @param pluginXml the  atlassian-plugin.xml
     * @throws MojoFailureException if the content is invalid
     */
    public void validate(File pluginXml) throws MojoFailureException {
        try {
            PluginXmlHelper xmlHelper = new PluginXmlHelper(pluginXml);
            Document document = xmlHelper.getDocument();
            for (String forbidden : forbiddenElements) {
                if (!document.getRootElement().elements(forbidden).isEmpty()) {
                    throw new MojoFailureException("\n\natlassian-plugin.xml contains a definition of " + forbidden
                            + ". This is not allowed when Atlassian-Plugin-Key is set.\n\n"
                            + "Please check the documentation of https://bitbucket.org/atlassian/atlassian-spring-scanner for further details.\n\n");
                }
            }
        }
        catch (DocumentException | IOException e) {
            throw new MojoFailureException("unable to read atlassian-plugin.xml, " + e);
        }
    }
}
