package com.atlassian.plugins.codegen;

import java.util.List;

import com.atlassian.fugue.Option;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import static com.atlassian.fugue.Option.some;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertEquals;

public class XmlMatchers
{
    private static final String DUMMY_NAMESPACE_PREFIX = "xmlMatchersDummyNamespace";
    
    public static class XmlWrapper
    {
        private final Element root;
        private final Option<String> defaultNamespacePrefix;
        
        private XmlWrapper(Element root)
        {
            this(root, Option.none(String.class));
        }
        
        private XmlWrapper(Element root, Option<String> defaultNamespacePrefix)
        {
            this.root = root;
            this.defaultNamespacePrefix = defaultNamespacePrefix;
        }
        
        private String xpath(String path)
        {
            for (String ns : defaultNamespacePrefix)
            {
                return path.replaceAll("/(?![/@])", "/" + ns + ":");
            }
            return path;
        }
        
        private String dump()
        {
            return "\nDocument being matched:\n" + root.asXML();
        }
    }
    
    public static XmlWrapper xml(Element root)
    {
        return new XmlWrapper(root);
    }
    
    public static XmlWrapper xml(Element root, String defaultNamespacePrefix)
    {
        return new XmlWrapper(root, some(defaultNamespacePrefix));
    }
    
    public static XmlWrapper xml(String content) throws DocumentException
    {
        Document doc = DocumentHelper.parseText(content);
        String defaultNamespace = doc.getRootElement().getNamespace().getURI();
        if ((defaultNamespace != null) && !defaultNamespace.equals(""))
        {
            // this is a workaround for the fact that default namespaces don't really work in dom4j
            doc.getRootElement().addNamespace(DUMMY_NAMESPACE_PREFIX, defaultNamespace);
            return xml(doc.getRootElement(), DUMMY_NAMESPACE_PREFIX);
        }
        else
        {
            return xml(doc.getRootElement());
        }
    }

    public static XmlWrapper xml(String content, String rootElementName) throws DocumentException
    {
        XmlWrapper xml = xml(content);
        assertEquals("Wrong root element type", rootElementName, xml.root.getName());
        return xml;
    }
    
    public static Matcher<XmlWrapper> node(final String xpath, final Matcher<? super Node> nodeMatcher)
    {
        return new TypeSafeDiagnosingMatcher<XmlWrapper>()
        {
            protected boolean matchesSafely(XmlWrapper xml, Description mismatchDescription)
            {
                Node node = xml.root.selectSingleNode(xml.xpath(xpath));
                if (!nodeMatcher.matches(node))
                {
                    nodeMatcher.describeMismatch(node, mismatchDescription);
                    mismatchDescription.appendText(xml.dump());
                    return false;
                }
                return true;
            }

            public void describeTo(Description description)
            {
                description.appendText("node at [" + xpath + "] ");
                nodeMatcher.describeTo(description);
            }
        };
    }

    public static Matcher<XmlWrapper> nodes(final String xpath, final Matcher<Iterable<Node>> nodesMatcher)
    {
        return new TypeSafeDiagnosingMatcher<XmlWrapper>()
        {
            @SuppressWarnings("unchecked")
            protected boolean matchesSafely(XmlWrapper xml, Description mismatchDescription)
            {
                List<Node> nodes = (List<Node>) xml.root.selectNodes(xml.xpath(xpath));
                if (!nodesMatcher.matches(nodes))
                {
                    nodesMatcher.describeMismatch(nodes, mismatchDescription);
                    mismatchDescription.appendText(xml.dump());
                    return false;
                }
                return true;
            }

            public void describeTo(Description description)
            {
                description.appendText("nodes at [" + xpath + "] ");
                nodesMatcher.describeTo(description);
            }
        };
    }
    
    public static Matcher<Node> nodeText(final Matcher<? super String> textMatcher)
    {
        return new TypeSafeDiagnosingMatcher<Node>()
        {
            protected boolean matchesSafely(Node node, Description mismatchDescription)
            {
                if (node == null)
                {
                    mismatchDescription.appendText("node did not exist");
                    return false;
                }
                else if (!textMatcher.matches(node.getText().trim()))
                {
                    textMatcher.describeMismatch(node.getText().trim(), mismatchDescription);
                    return false;
                }
                return true;
            }

            public void describeTo(Description description)
            {
                description.appendText("with text ");
                textMatcher.describeTo(description);
            }
        };
    }

    public static Matcher<Node> nodeName(final Matcher<? super String> nameMatcher)
    {
        return new TypeSafeDiagnosingMatcher<Node>()
        {
            protected boolean matchesSafely(Node node, Description mismatchDescription)
            {
                if (node == null)
                {
                    mismatchDescription.appendText("node did not exist");
                    return false;
                }
                else if (!nameMatcher.matches(node.getName()))
                {
                    nameMatcher.describeMismatch(node.getText().trim(), mismatchDescription);
                    return false;
                }
                return true;
            }

            public void describeTo(Description description)
            {
                description.appendText("with name ");
                nameMatcher.describeTo(description);
            }
        };
    }
    
    public static Matcher<Node> nodeTextEquals(final String text)
    {
        return nodeText(equalTo(text));
    }
    
    public static Matcher<Iterable<Node>> nodeCount(final int count)
    {
        return iterableWithSize(count);
    }
}
