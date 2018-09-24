package com.atlassian.plugins.codegen.modules.jira;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;
import com.atlassian.plugins.codegen.ComponentDeclaration;

import org.junit.Before;
import org.junit.Test;

import static com.atlassian.plugins.codegen.ClassId.fullyQualified;
import static io.atlassian.fugue.Option.some;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * @since 3.8
 */
public abstract class AbstractRPCTest extends AbstractModuleCreatorTestCase<RPCProperties>
{
    protected boolean isSoap;
    
    public AbstractRPCTest(String type, boolean isSoap)
    {
        super(type, new RPCModuleCreator());
        this.isSoap = isSoap;
    }

    @Before
    public void setupProps()
    {
        setProps(new RPCProperties(PACKAGE_NAME + ".MyEndpoint"));
        props.setSoap(isSoap);
    }

    @Test
    public void interfaceFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyEndpoint");
    }

    @Test
    public void classFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyEndpointImpl");
    }

    @Test
    public void unitTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(TEST_PACKAGE_NAME, "MyEndpointImplTest");
    }

    @Test
    public void moduleHasClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyEndpointImpl", getGeneratedModule().attributeValue("class"));
    }

    @Test
    public void moduleHasServicePath() throws Exception
    {
        assertEquals("myendpoint-v1", getGeneratedModule().selectSingleNode("service-path").getText());
    }
    
    @Test
    public void componentAdded() throws Exception
    {
        assertFalse(getChangesetForModule(ComponentDeclaration.class).isEmpty());
    }
    
    @Test
    public void componentHasClass() throws Exception
    {
        assertEquals(fullyQualified(PACKAGE_NAME + ".MyEndpointImpl"), getChangesetForModule(ComponentDeclaration.class).get(0).getClassId());
    }

    @Test
    public void componentHasInterface() throws Exception
    {
        assertEquals(some(fullyQualified(PACKAGE_NAME + ".MyEndpoint")), getChangesetForModule(ComponentDeclaration.class).get(0).getInterfaceId());
    }
}
