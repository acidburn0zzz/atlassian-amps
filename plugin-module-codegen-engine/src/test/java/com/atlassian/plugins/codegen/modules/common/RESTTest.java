package com.atlassian.plugins.codegen.modules.common;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @since 3.6
 */
public class RESTTest extends AbstractModuleCreatorTestCase<RESTProperties>
{
    public static final String PACKAGE1 = "com.atlassian.plugins.rest.hello";
    public static final String PACKAGE2 = "com.atlassian.plugins.rest.message";
    public static final String DISPATCHER1 = "REQUEST";
    public static final String DISPATCHER2 = "FORWARD";
    
    public RESTTest()
    {
        super("rest", new RESTModuleCreator());
    }
    
    @Before
    public void setupProps() throws Exception
    {
        setProps(new RESTProperties(PACKAGE_NAME + ".MyRestResource"));
        props.setIncludeExamples(false);
        props.addPackageToScan(PACKAGE1);
        props.addPackageToScan(PACKAGE2);
        props.addDispatcher(DISPATCHER1);
        props.addDispatcher(DISPATCHER2);
    }

    @Test
    public void classFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyRestResource");
    }

    @Test
    public void modelClassFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyRestResourceModel");
    }
    
    @Test
    public void unitTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(PACKAGE_NAME, "MyRestResourceTest");
    }

    @Test
    public void functionalTestTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(FUNC_TEST_PACKAGE_NAME, "MyRestResourceFuncTest");
    }
    
    @Test
    public void moduleHasDefaultPath() throws Exception
    {
        assertEquals("/myrestresource", getGeneratedModule().attributeValue("path"));
    }

    @Test
    public void moduleHasSpecifiedPath() throws Exception
    {
        props.setPath("/helloworld");
        
        assertEquals("/helloworld", getGeneratedModule().attributeValue("path"));
    }

    @Test
    public void moduleHasDefaultVersion() throws Exception
    {
        assertEquals("1.0", getGeneratedModule().attributeValue("version"));
    }

    @Test
    public void moduleHasSpecifiedVersion() throws Exception
    {
        props.setVersion("1.1");
        
        assertEquals("1.1", getGeneratedModule().attributeValue("version"));
    }
    
    @Test
    public void package1IsAdded() throws Exception
    {
        assertEquals(PACKAGE1, getGeneratedModule().selectSingleNode("package[1]").getText());
    }

    @Test
    public void package2IsAdded() throws Exception
    {
        assertEquals(PACKAGE2, getGeneratedModule().selectSingleNode("package[2]").getText());
    }

    @Test
    public void dispatcher1IsAdded() throws Exception
    {
        assertEquals(DISPATCHER1, getGeneratedModule().selectSingleNode("dispatcher[1]").getText());
    }

    @Test
    public void dispatcher2IsAdded() throws Exception
    {
        assertEquals(DISPATCHER2, getGeneratedModule().selectSingleNode("dispatcher[2]").getText());
    }
}
