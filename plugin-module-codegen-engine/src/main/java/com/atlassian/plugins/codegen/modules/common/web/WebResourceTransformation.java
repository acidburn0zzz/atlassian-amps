package com.atlassian.plugins.codegen.modules.common.web;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @since 3.6
 */
public class WebResourceTransformation
{
    private String extension;
    private List<WebResourceTransformer> transformers;

    public WebResourceTransformation(String extension)
    {
        this.extension = extension;
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

    public void addTransformer(WebResourceTransformer transformer)
    {
        transformers.add(transformer);
    }

    public List<WebResourceTransformer> getTransformers()
    {
        return Collections.unmodifiableList(transformers);
    }

    public void setTransformers(List<WebResourceTransformer> transformers)
    {
        this.transformers = transformers;
    }
}
