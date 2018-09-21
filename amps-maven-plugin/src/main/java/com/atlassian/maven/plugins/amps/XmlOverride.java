package com.atlassian.maven.plugins.amps;

/**
 * Class to represent an XmlOverride element that will be passed to cargo on start goal to override it's default configuration.
 * More details see https://codehaus-cargo.github.io/cargo/XML+replacements.html
 *
 * @since 6.3
 */
public class XmlOverride {
    private final String file;
    private final String xPathExpression;
    private final String attributeName;
    private final String value;

    public XmlOverride(final String file, final String xPathExpression, final String attributeName, final String value) {
        this.file = file;
        this.xPathExpression = xPathExpression;
        this.attributeName = attributeName;
        this.value = value;
    }

    public String getFile() {
        return file;
    }

    public String getxPathExpression() {
        return xPathExpression;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public String getValue() {
        return value;
    }
}
