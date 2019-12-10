package com.atlassian.maven.plugins.amps.code;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the contents of a file to perform transformations on, along with
 * any metadata about the contents that would be relevant to the transformation step.
 */
public class Sources {
    private final String content;
    private final String sourceMapContent;

    public Sources(String content) {
        this(content, null);
    }

    public Sources(String content, String sourceMapContent) {
        this.content = content;
        this.sourceMapContent = sourceMapContent;
    }

    @Nonnull
    public String getContent() {
        return StringUtils.isNotBlank(content) ? content : "";
    }

    @Nullable
    public String getSourceMapContent() {
        return sourceMapContent;
    }

    public boolean hasSourceMap() {
        return StringUtils.isNotBlank(sourceMapContent);
    }
}
