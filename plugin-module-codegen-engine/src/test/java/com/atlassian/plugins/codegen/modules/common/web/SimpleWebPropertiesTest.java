package com.atlassian.plugins.codegen.modules.common.web;

import org.junit.Before;

/**
 * @since 3.6
 */
public class SimpleWebPropertiesTest extends AbstractWebFragmentTest<SimpleWebProperties>
{
    public SimpleWebPropertiesTest()
    {
        super("simple-web-module", new SimpleWebModuleCreator());
    }

    @Before
    public void setupProps() throws Exception
    {
        setProps(new SimpleWebProperties(MODULE_NAME));
        props.setIncludeExamples(false);
    }
}
