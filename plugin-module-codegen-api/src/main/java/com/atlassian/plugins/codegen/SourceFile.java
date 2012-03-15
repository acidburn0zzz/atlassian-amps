package com.atlassian.plugins.codegen;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Describes a source file that should be added to the project.
 */
public class SourceFile
{
    /**
     * Specifies whether to add the file to {@code src/main} or {@code src/test}.
     */
    public enum SourceGroup
    {
        MAIN,
        TESTS
    };

    private final ClassId classId;
    private final SourceGroup sourceGroup;
    private final String content;
    
    public static SourceFile sourceFile(ClassId classId, SourceGroup sourceGroup, String content)
    {
        return new SourceFile(classId, sourceGroup, content);
    }
    
    private SourceFile(ClassId classId, SourceGroup sourceGroup, String content)
    {
        this.classId = checkNotNull(classId, "classId");
        this.sourceGroup = checkNotNull(sourceGroup, "sourceGroup");
        this.content = checkNotNull(content, "content");
    }

    public ClassId getClassId()
    {
        return classId;
    }

    public SourceGroup getSourceGroup()
    {
        return sourceGroup;
    }

    public String getContent()
    {
        return content;
    }
}
