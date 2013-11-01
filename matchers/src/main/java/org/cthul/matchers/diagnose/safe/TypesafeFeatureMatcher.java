package org.cthul.matchers.diagnose.safe;

import org.cthul.matchers.diagnose.result.MatchResult;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.internal.ReflectiveTypeFinder;

/**
 *
 */
public abstract class TypesafeFeatureMatcher<V, F> extends TypesafeNestedResultMatcher<V> {

    private static final ReflectiveTypeFinder TYPE_FINDER = new ReflectiveTypeFinder("getFeature", 1, 0);

    private final Matcher<? super F> featureMatcher;
    private final String matchPrefix;
    private final String mismatchPrefix;

    public TypesafeFeatureMatcher(Matcher<? super F> featureMatcher, String matchPrefix, String mismatchPrefix) {
        super(TYPE_FINDER);
        this.featureMatcher = featureMatcher;
        this.matchPrefix = matchPrefix;
        this.mismatchPrefix = mismatchPrefix;
    }

    public TypesafeFeatureMatcher(Matcher<? super F> featureMatcher, String matchPrefix, String mismatchPrefix, Class<?> expectedType) {
        super(expectedType);
        this.featureMatcher = featureMatcher;
        this.matchPrefix = matchPrefix;
        this.mismatchPrefix = mismatchPrefix;
    }

    public TypesafeFeatureMatcher(Matcher<? super F> featureMatcher, String matchPrefix, String mismatchPrefix, ReflectiveTypeFinder typeFinder) {
        super(typeFinder);
        this.featureMatcher = featureMatcher;
        this.matchPrefix = matchPrefix;
        this.mismatchPrefix = mismatchPrefix;
    }
    
    protected abstract F getFeature(V value);

    @Override
    public void describeTo(Description description) {
        describeFeature(description);
        describeFeatureMatcher(description);
    }

    protected void describeFeature(Description description) {
        if (matchPrefix != null) {
            description.appendText(matchPrefix).appendText(" ");
        }
    }

    protected void describeFeatureMatcher(Description description) {
        nestedDescribeTo(featureMatcher, description);
    }

    protected void describeMatchedFeature(V item, Description description) {
        // sentence structure of mismatch applies better;
        // the feature description contains no actual matching information,
        // so no semantics are violated
        describeMismatchedFeature(item, description);
    }
    
    protected void describeExpectedFeature(V item, Description description) {
        describeFeature(description);
    }
    
    protected void describeMismatchedFeature(V item, Description description) {
        if (mismatchPrefix != null) {
            description.appendText(mismatchPrefix).appendText(" ");
        }
    }

    @Override
    public int getDescriptionPrecedence() {
        return P_UNARY;
    }
    
    @Override
    protected boolean matchesSafely(V item) {
        return featureMatcher.matches(getFeature(item));
    }
    
    @Override
    protected <I extends V> MatchResult<I> matchResultSafely(final I item) {
        final MatchResult<F> mr = quickMatchResult(featureMatcher, getFeature(item));
        return new NestedResult<I,Matcher<?>>(item, this, mr.matched()) {
            @Override
            public void describeMatch(Description d) {
                describeMatchedFeature(item, d);
                nestedDescribeTo(getMatchPrecedence(), mr, d);
            }
            @Override
            public void describeExpected(Description d) {
                describeExpectedFeature(item, d);
                nestedDescribeTo(getExpectedPrecedence(), mr.getMismatch().getExpectedDescription(), d);
            }
            @Override
            public void describeMismatch(Description d) {
                describeMismatchedFeature(item, d);
                nestedDescribeTo(getMismatchPrecedence(), mr, d);
            }
        };
    }
}
