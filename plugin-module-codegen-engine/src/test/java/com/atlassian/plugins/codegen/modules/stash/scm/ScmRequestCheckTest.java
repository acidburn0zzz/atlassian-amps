package com.atlassian.plugins.codegen.modules.stash.scm;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class ScmRequestCheckTest extends AbstractModuleCreatorTestCase<ScmRequestCheckProperties> {

    public ScmRequestCheckTest() {
        super("scm-request-check", new ScmRequestCheckModuleCreator());
    }

    @Before
    public void setupProps() throws Exception
    {
        setProps(new ScmRequestCheckProperties(PACKAGE_NAME + ".MyScmRequestCheck"));
        props.setIncludeExamples(false);
    }

    @Test
    public void classFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyScmRequestCheck");
    }

    @Test
    public void unitTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(TEST_PACKAGE_NAME, "MyScmRequestCheckTest");
    }

    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals("my-scm-request-check",
                getGeneratedModule().attributeValue("key"));
    }

    @Test
    public void moduleHasDefaultWeight() throws Exception
    {
        assertEquals("150",
                getGeneratedModule().attributeValue("weight"));
    }

    @Test
    public void moduleHasClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyScmRequestCheck", getGeneratedModule().attributeValue("class"));
    }

}
