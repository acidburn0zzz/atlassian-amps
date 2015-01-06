package ut.${package};

import org.junit.Test;
import ${package}.api.MyPluginComponent;
import ${package}.impl.MyPluginComponentImpl;

import static org.junit.Assert.assertEquals;

public class MyComponentUnitTest
{
    @Test
    public void testMyName()
    {
        MyPluginComponent component = new MyPluginComponentImpl();
        assertEquals("names do not match!", "myComponent",component.getName());
    }
}