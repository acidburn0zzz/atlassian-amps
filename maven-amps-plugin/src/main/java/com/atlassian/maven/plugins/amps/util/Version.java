package com.atlassian.maven.plugins.amps.util;

import com.google.common.base.Strings;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Ints;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Arrays.asList;

/**
 * Maven versioning compatible Version of the form {@code MAJOR.MINOR.PATCH(-|.)QUALIFIER}.
 * <p/>
 * Note: In addition to the
 * <a href="http://books.sonatype.com/mvnref-book/reference/pom-relationships-sect-pom-syntax.html">Maven versioning scheme</a>
 * this version supports the OSGi compatible scheme that separates the {@code QUALIFIER} with a {@code dot}.
 */
public final class Version implements Comparable<Version>, Serializable {
    private static final long serialVersionUID = 0L;

    private final static Ordering<Version> versionOrdering = new Ordering<Version>() {
        @Override
        public int compare(Version left, Version right) {
            int major = Ints.compare(left.major, right.major);
            if (major != 0) {
                return major;
            }
            int minor = Ints.compare(left.minor, right.minor);
            if (minor != 0) {
                return minor;
            }
            int patch = Ints.compare(left.patch, right.patch);
            if (patch != 0) {
                return patch;
            }
            return Ordering.explicit(asList("", "RELEASE", "BETA", "ALPHA", "SNAPSHOT")).compare(right.qualifier, left.qualifier);
        }
    };


    /**
     * The version number parts. Used for FSM style parsing of version numbers
     */
    private enum Part {
        FINISHED,
        MAYBE_QUALIFIER {
            @Override
            Part parse(VersionBuilder v, String input) {
                Matcher m = HAS_QUALIFIER.matcher(input);
                if (m.matches() && m.groupCount() > 1) {
                    return next(this.next).parse(v, m.group(1)).parse(v, m.group(2));
                }
                m = IS_QUALIFIER.matcher(input);
                if (m.matches()) {
                    return QUALIFIER.parse(v, input);
                }
                return next(this.next).parse(v, input);
            }
        },
        QUALIFIER {
            @Override
            Part parse(VersionBuilder v, String input) {
                v.qualifier = Strings.nullToEmpty(input);
                return FINISHED;
            }
        },
        PATCH {
            @Override
            Part parse(VersionBuilder v, String input) {
                v.patch = Integer.parseInt(input);
                return QUALIFIER;
            }
        },
        MINOR {
            @Override
            Part parse(VersionBuilder v, String input) {
                v.minor = Integer.parseInt(input);
                return MAYBE_QUALIFIER.then(PATCH);
            }
        },
        MAJOR {
            @Override
            Part parse(VersionBuilder v, String input) {
                v.major = Integer.parseInt(input);
                return MAYBE_QUALIFIER.then(MINOR);
            }
        };

        final static Pattern HAS_QUALIFIER = Pattern.compile("(\\d+)[\\._-](\\S+)");
        final static Pattern IS_QUALIFIER = Pattern.compile("([a-zA-Z][a-zA-Z0-9]*)");
        Part next;

        Part then(Part after) {
            this.next = after;
            return this;
        }

        Part parse(VersionBuilder v, String input) {
            return FINISHED;
        }

        static Part next(Part after) {
            if (null == after) {
                return FINISHED;
            }
            return after;
        }
    }

    private static class VersionBuilder {
        private int major;
        private int minor;
        private int patch;

        @Override
        public String toString() {
            return "VersionBuilder{" +
                    "major=" + major +
                    ", minor=" + minor +
                    ", patch=" + patch +
                    ", qualifier='" + qualifier + '\'' +
                    '}';
        }

        private String qualifier;
    }

    /**
     * Parses the given String which should be a valid version number string.
     * <p/>
     * Examples:
     * <p/>
     * <pre>
     * &quot;1.2.3&quot;
     * &quot;1.2.3.RELEASE&quot;
     * &quot;1.2.3-SNAPSHOT&quot;
     * &quot;1.2&quot;
     * &quot;3&quot;
     * </pre>
     *
     * @param str A version number string
     * @return The Version instance corresponding to the given version string
     * argument.
     * @throws IllegalArgumentException if the argument cannot be parsed into a valid Version
     */
    public static Version valueOf(final String str) {
        checkArgument(!isNullOrEmpty(str), "Input String cannot be null or empty.");

        VersionBuilder builder = new VersionBuilder();
        String[] elements = str.split("(\\.)");
        Part part = Part.MAJOR;
        for (String elem : elements) {
            try {
                part = part.parse(builder, elem);
            } catch (NumberFormatException nfe) {
                throw new IllegalArgumentException(String.format("Failed to parse %s part for input '%s'", part, elem));
            }
            if (Part.FINISHED.equals(part)) {
                break;
            }
        }
        return new Version(builder);
    }

    private final int major;
    private final int minor;
    private final int patch;
    private final String qualifier;

    private Version(VersionBuilder builder) {
        this.major = builder.major;
        this.minor = builder.minor;
        this.patch = builder.patch;
        this.qualifier = Strings.nullToEmpty(builder.qualifier);
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    public int getPatch() {
        return patch;
    }

    public String getQualifier() {
        return qualifier;
    }

    public int compareTo(Version other) {
        return versionOrdering.compare(this, other);
    }

    public boolean isGreaterThan(Version other) {
        return this.compareTo(other) > 0;
    }

    public boolean isGreaterOrEqualTo(Version other) {
        return this.compareTo(other) >= 0;
    }

    public boolean isLessThan(Version other) {
        return this.compareTo(other) < 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Version version = (Version) o;

        if (major != version.major) return false;
        if (minor != version.minor) return false;
        if (patch != version.patch) return false;
        return qualifier.equals(version.qualifier);
    }

    @Override
    public int hashCode() {
        int result = major;
        result = 31 * result + minor;
        result = 31 * result + patch;
        result = 31 * result + qualifier.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Version{" +
                "major=" + major +
                ", minor=" + minor +
                ", patch=" + patch +
                ", suffix='" + qualifier + '\'' +
                '}';
    }
}