package com.atlassian.plugins.codegen.modules.bitbucket.scm;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BitbucketScmRequestCheckTest extends AbstractModuleCreatorTestCase<BitbucketScmRequestCheckProperties>
{

    public BitbucketScmRequestCheckTest()
    {
        super("scm-request-check", new BitbucketScmRequestCheckModuleCreator());
    }

    @Before
    public void setupProps() throws Exception
    {
        setProps(new BitbucketScmRequestCheckProperties(PACKAGE_NAME + ".MyScmRequestCheck"));
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
