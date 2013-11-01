package org.cthul.matchers.hamcrest;

import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.safe.TypesafeFeatureMatcher;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

/**
 *
 */
public class HasMatchResult<T> extends TypesafeFeatureMatcher<Matcher<? super T>, MatchResult<T>> {

    @Factory
    public static <T> HasMatchResult<T> matchResult(T value, Matcher<? super MatchResult<T>> resultMatcher) {
        return new HasMatchResult<>(value, resultMatcher);
    }
    
    private final T value;
    
    public HasMatchResult(T value, Matcher<? super MatchResult<T>> featureMatcher) {
        super(featureMatcher, null, null, SelfDescribing.class);
        this.value = value;
    }

    @Override
    protected void describeFeature(Description description) {
        description.appendText("a matcher matching ")
                .appendValue(value)
                .appendText(" with result ");
    }

    @Override
    protected void describeMismatchedFeature(Matcher<? super T> matcher, Description description) {
        description
                .appendText("match result of ")
                .appendValue(value)
                .appendText(" ");
    }

    @Override
    protected MatchResult<T> getFeature(Matcher<? super T> matcher) {
        return quickMatchResult(matcher, value);
    }
}
