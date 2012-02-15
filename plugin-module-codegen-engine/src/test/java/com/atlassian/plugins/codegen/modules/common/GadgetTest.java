package com.atlassian.plugins.codegen.modules.common;

import com.atlassian.plugins.codegen.AbstractModuleCreatorTestCase;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @since 3.6
 */
public class GadgetTest extends AbstractModuleCreatorTestCase<GadgetProperties>
{
    public GadgetTest()
    {
        super("gadget", new GadgetModuleCreator());
    }
    
    @Before
    public void setupProps() throws Exception
    {
        setProps(new GadgetProperties("My Gadget", "gadgets/mygadget/gadget.xml"));
        props.setIncludeExamples(false);
    }

    @Test
    public void gadgetFileIsGenerated() throws Exception
    {
        getResourceFile("gadgets/mygadget", "gadget.xml");
    }

    @Test
    public void moduleHasLocation() throws Exception
    {
        assertEquals("gadgets/mygadget/gadget.xml", getGeneratedModule().attributeValue("location"));
    }
}
