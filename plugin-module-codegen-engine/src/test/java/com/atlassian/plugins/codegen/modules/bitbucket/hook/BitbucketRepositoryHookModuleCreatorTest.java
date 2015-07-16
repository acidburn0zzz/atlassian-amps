package com.atlassian.plugins.codegen.modules.bitbucket.hook;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;
import org.junit.Before;

public class BitbucketRepositoryHookModuleCreatorTest extends AbstractModuleCreatorTestCase<BitbucketRepositoryHookProperties>
{
    public BitbucketRepositoryHookModuleCreatorTest()
    {
        super("repository-hook", new BitbucketRepositoryHookModuleCreator());
    }


    @Before
    public void setupProps() throws Exception
    {
        setProps(new BitbucketRepositoryHookProperties("MyRepositoryHook", "pre"));
    }

}
