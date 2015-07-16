package com.atlassian.plugins.codegen.modules.bitbucket.idx;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class BitbucketCommitIndexerTest extends AbstractModuleCreatorTestCase<BitbucketCommitIndexerProperties>
{

    public BitbucketCommitIndexerTest()
    {
        super("commit-indexer", new BitbucketCommitIndexerModuleCreator());
    }

    @Before
    public void setupProps() throws Exception
    {
        setProps(new BitbucketCommitIndexerProperties(PACKAGE_NAME + ".MyCommitIndexer"));
        props.setIncludeExamples(false);
    }

    @Test
    public void classFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyCommitIndexer");
    }

    @Test
    public void unitTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(TEST_PACKAGE_NAME, "MyCommitIndexerTest");
    }

    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals("my-commit-indexer",
                getGeneratedModule().attributeValue("key"));
    }

    @Test
    public void moduleHasClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyCommitIndexer", getGeneratedModule().attributeValue("class"));
    }

}
