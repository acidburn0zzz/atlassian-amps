package com.atlassian.maven.plugins.amps.code;

import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents multiple related content sources, such as source code along with source maps.
 *
 * Is typically used to encapsulate the input to or result of code transformations,
 * such as minification of front-end code.
 *
 * Should be used to encapsulate co-dependent contents, along with any metadata
 * about the contents that would be relevant to or a by-product of a code transformation.
 *
 * @since 8.1.0
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
