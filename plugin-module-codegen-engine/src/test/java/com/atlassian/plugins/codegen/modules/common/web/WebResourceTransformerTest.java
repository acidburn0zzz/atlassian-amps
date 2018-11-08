package com.atlassian.plugins.codegen.modules.common.web;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @since 3.6
 */
public class WebResourceTransformerTest extends AbstractModuleCreatorTestCase<WebResourceTransformer>
{
    public static final String PACKAGE_NAME = "com.atlassian.plugin.webresource";
    public static final String TEST_PACKAGE_NAME = "ut.com.atlassian.plugin.webresource";

    public WebResourceTransformerTest()
    {
        super("web-resource-transformer", new WebResourceTransformerModuleCreator());
    }
    
    @Before
    public void setupProps() throws Exception
    {
        setProps(new WebResourceTransformer(PACKAGE_NAME + ".MyWebResourceTransformer"));
        props.setIncludeExamples(false);
    }

    @Test
    public void classFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyWebResourceTransformer");
    }

    @Test
    public void unitTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(TEST_PACKAGE_NAME, "MyWebResourceTransformerTest");
    }

    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals("my-web-resource-transformer",
                     getGeneratedModule().attributeValue("key"));
    }
    
    @Test
    public void moduleHasClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyWebResourceTransformer",
                     getGeneratedModule().attributeValue("class"));
    }
}
