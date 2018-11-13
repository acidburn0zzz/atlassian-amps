package com.atlassian.plugins.codegen.modules.common.web;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @since 3.6
 */
public class WebPanelRendererTest extends AbstractModuleCreatorTestCase<WebPanelRendererProperties>
{
    public WebPanelRendererTest()
    {
        super("web-panel-renderer", new WebPanelRendererModuleCreator());
    }

    @Before
    public void setupProps() throws Exception
    {
        setProps(new WebPanelRendererProperties(PACKAGE_NAME + ".MyWebPanelRenderer"));
        props.setIncludeExamples(false);
    }

    @Test
    public void classFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyWebPanelRenderer");
    }

    @Test
    public void unitTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(TEST_PACKAGE_NAME, "MyWebPanelRendererTest");
    }
    
    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals("my-web-panel-renderer",
                     getGeneratedModule().attributeValue("key"));
    }
    
    @Test
    public void moduleHasClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyWebPanelRenderer",
                     getGeneratedModule().attributeValue("class"));
    }
}
