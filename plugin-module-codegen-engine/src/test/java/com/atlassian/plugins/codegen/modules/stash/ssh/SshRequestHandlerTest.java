package com.atlassian.plugins.codegen.modules.stash.ssh;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class SshRequestHandlerTest extends AbstractModuleCreatorTestCase<SshScmRequestHandlerProperties> {

    public SshRequestHandlerTest() {
        super("ssh-request-handler", new SshScmRequestHandlerModuleCreator());
    }

    @Before
    public void setupProps() throws Exception
    {
        setProps(new SshScmRequestHandlerProperties(PACKAGE_NAME + ".MySshRequest"));
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
