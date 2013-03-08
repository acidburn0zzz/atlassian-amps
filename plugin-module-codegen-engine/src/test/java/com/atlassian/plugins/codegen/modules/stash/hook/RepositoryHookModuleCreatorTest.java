package com.atlassian.plugins.codegen.modules.stash.hook;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;
import org.junit.Before;

public class RepositoryHookModuleCreatorTest extends AbstractModuleCreatorTestCase<RepositoryHookProperties>
{
    public RepositoryHookModuleCreatorTest()
    {
        super("repository-hook", new RepositoryHookModuleCreator());
    }


    @Before
    public void setupProps() throws Exception
    {
        setProps(new RepositoryHookProperties("MyRepositoryHook", "pre"));
    }

}
