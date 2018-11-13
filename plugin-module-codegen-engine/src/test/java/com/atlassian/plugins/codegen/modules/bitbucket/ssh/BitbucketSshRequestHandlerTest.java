package com.atlassian.plugins.codegen.modules.bitbucket.ssh;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BitbucketSshRequestHandlerTest extends AbstractModuleCreatorTestCase<BitbucketSshScmRequestHandlerProperties>
{

    public BitbucketSshRequestHandlerTest()
    {
        super("ssh-request-handler", new BitbucketSshScmRequestHandlerModuleCreator());
    }

    @Before
    public void setupProps() throws Exception
    {
        setProps(new BitbucketSshScmRequestHandlerProperties(PACKAGE_NAME + ".MySshRequest"));
        props.setIncludeExamples(false);
    }

    @Test
    public void classFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MySshRequest");
        getSourceFile(PACKAGE_NAME, "MySshRequestHandler");
    }

    @Test
    public void unitTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(TEST_PACKAGE_NAME, "MySshRequestTest");
        getTestSourceFile(TEST_PACKAGE_NAME, "MySshRequestHandlerTest");
    }

    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals("my-ssh-request-handler",
                getGeneratedModule().attributeValue("key"));
    }

    @Test
    public void moduleHasClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MySshRequestHandler",
                getGeneratedModule().attributeValue("class"));
    }


}
