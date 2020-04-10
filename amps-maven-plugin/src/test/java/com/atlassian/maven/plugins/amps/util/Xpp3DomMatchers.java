package com.atlassian.maven.plugins.amps.util;

import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;
import org.hamcrest.TypeSafeMatcher;

import java.util.Arrays;

public final class Xpp3DomMatchers {

    private Xpp3DomMatchers() {}

    /**
     * Creates a matcher for {@link Xpp3Dom} that matches if the child of the examined {@link Xpp3Dom} with the
     * specified name satisfies the specified matcher
     * @param childName the name of the Xpp3Dom element child to match against
     * @param childMatcher the matcher that should be applied against the child
     */
    public static Matcher<Xpp3Dom> childMatching(String childName, Matcher<Xpp3Dom> childMatcher) {
        return new Xpp3DomChildMatcher(childName, childMatcher);
    }

    /**
     * Creates a matcher for {@link Xpp3Dom} that matches if the children of the examined {@link Xpp3Dom}
     * satisfy the specified matcher
     * @param childrenMatcher the matcher that should be applied against the children
     */
    public static Matcher<Xpp3Dom> childrenMatching(Matcher<Iterable<? extends Xpp3Dom>> childrenMatcher) {
        return new Xpp3DomChildrenMatcher(childrenMatcher);
    }

    /**
     * Creates a matcher for {@link Xpp3Dom} that matches if the value of the examined {@link Xpp3Dom}
     * is equal to the specified value
     * @param expectedValue the value that the examined {@link Xpp3Dom}'s value will be compared to
     */
    public static Matcher<Xpp3Dom> valueMatching(String expectedValue) {
        return new Xpp3DomValueMatcher(expectedValue);
    }

    /**
     * Creates a matcher for {@link Xpp3Dom} that matches if the value of the examined {@link Xpp3Dom}
     * satisfies the specified matcher
     * @param valueMatcher the matcher that should be applied against the {@link Xpp3Dom}'s value
     */
    public static Matcher<Xpp3Dom> valueMatching(Matcher<String> valueMatcher) {
        return new Xpp3DomValueMatcher(valueMatcher);
    }

    private static class Xpp3DomChildMatcher extends TypeSafeMatcher<Xpp3Dom> {

        private final String childName;
        private final Matcher<Xpp3Dom> childMatcher;

        Xpp3DomChildMatcher(String childName, Matcher<Xpp3Dom> childMatcher) {
            this.childName = childName;
            this.childMatcher = childMatcher;
        }

        @Override
        protected boolean matchesSafely(Xpp3Dom item) {
            return childMatcher.matches(item.getChild(childName));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Matcher for Xpp3Dom child with name: ")
                    .appendValue(childName)
                    .appendText(" and value: ")
                    .appendDescriptionOf(childMatcher);
        }

        @Override
        protected void describeMismatchSafely(Xpp3Dom item, Description mismatchDescription) {
            childMatcher.describeMismatch(item.getChild(childName), mismatchDescription);
        }
    }

    private static class Xpp3DomChildrenMatcher extends TypeSafeMatcher<Xpp3Dom> {

        private final Matcher<Iterable<? extends Xpp3Dom>> childrenMatcher;

        Xpp3DomChildrenMatcher(Matcher<Iterable<? extends Xpp3Dom>> childrenMatcher) {
            this.childrenMatcher = childrenMatcher;
        }

        @Override
        protected boolean matchesSafely(Xpp3Dom item) {
            return childrenMatcher.matches(Arrays.asList(item.getChildren()));
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("Matcher for children of Xpp3Dom element executing ")
                    .appendDescriptionOf(childrenMatcher);
        }

        @Override
        protected void describeMismatchSafely(Xpp3Dom item, Description mismatchDescription) {
            childrenMatcher.describeMismatch(item.getChildren(), mismatchDescription);
        }
    }

    private static class Xpp3DomValueMatcher extends TypeSafeMatcher<Xpp3Dom> {

        private Matcher<String> valueMatcher;

        Xpp3DomValueMatcher(Matcher<String> valueMatcher) {
            this.valueMatcher = valueMatcher;
        }

        Xpp3DomValueMatcher(String expectedValue) {
            valueMatcher = Matchers.is(expectedValue);
        }

        @Override
        protected boolean matchesSafely(Xpp3Dom item) {
            return valueMatcher.matches(item.getValue());
        }

        @Override
        public void describeTo(Description description) {
            valueMatcher.describeTo(description);
        }

        @Override
        protected void describeMismatchSafely(Xpp3Dom item, Description mismatchDescription) {
            valueMatcher.describeMismatch(item.getValue(), mismatchDescription);
        }
    }
}
