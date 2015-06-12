package com.atlassian.plugins.codegen.modules.bitbucket.idx;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class BitbucketCommitIndexerTest extends AbstractModuleCreatorTestCase<BitbucketCommitIndexerProperties>
{

    public BitbucketCommitIndexerTest()
    {
        super("changeset-indexer", new BitbucketCommitIndexerModuleCreator());
    }

    @Before
    public void setupProps() throws Exception
    {
        setProps(new BitbucketCommitIndexerProperties(PACKAGE_NAME + ".MyChangesetIndexer"));
        props.setIncludeExamples(false);
    }

    @Test
    public void classFileIsGenerated() throws Exception
    {
        getSourceFile(PACKAGE_NAME, "MyChangesetIndexer");
    }

    @Test
    public void unitTestFileIsGenerated() throws Exception
    {
        getTestSourceFile(TEST_PACKAGE_NAME, "MyChangesetIndexerTest");
    }

    @Test
    public void moduleHasDefaultKey() throws Exception
    {
        assertEquals("my-changeset-indexer",
                getGeneratedModule().attributeValue("key"));
    }

    @Test
    public void moduleHasClass() throws Exception
    {
        assertEquals(PACKAGE_NAME + ".MyChangesetIndexer", getGeneratedModule().attributeValue("class"));
    }

}
