package com.atlassian.plugins.codegen.modules.common.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @since 3.6
 */
public class WebResourceTransformation
{
    private String extension;
    private List<String> transformerKeys;
    private List<WebResourceTransformerProperties> transformers;

    public WebResourceTransformation(String extension)
    {
        this.extension = extension;
        this.transformerKeys = new ArrayList<String>();
        this.transformers = newArrayList();
    }

    public String getExtension()
    {
        return extension;
    }

    public void setExtension(String extension)
    {
        this.extension = extension;
    }

    // TODO - should be replace with transformers?
    public void addTransformerKey(String key)
    {
        transformerKeys.add(key);
    }

    public List<String> getTransformerKeys()
    {
        return Collections.unmodifiableList(transformerKeys);
    }

    public void addTransformer(WebResourceTransformerProperties props)
    {
        transformers.add(props);
    }

    public List<WebResourceTransformerProperties> getTransformers()
    {
        return Collections.unmodifiableList(transformers);
    }

    public void setTransformerKeys(List<String> keys)
    {
        transformerKeys = keys;
    }
}
