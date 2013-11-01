package org.cthul.matchers.hamcrest;

import org.cthul.matchers.diagnose.result.MatchResult;
import org.cthul.matchers.diagnose.safe.TypesafeFeatureMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;

/**
 *
 */
public class MatchResultExpected extends TypesafeFeatureMatcher<MatchResult<?>, SelfDescribing> {

    @Factory
    public static MatchResultExpected expectedDescription(Matcher<? super SelfDescribing> messageMatcher) {
        return new MatchResultExpected(messageMatcher);
    }
    
    @Factory
    public static MatchResultExpected expectedMessage(Matcher<? super String> messageMatcher) {
        return new MatchResultExpected(HasDescription.message(messageMatcher));
    }
    
    @Factory
    public static MatchResultExpected expectedMessage(String message) {
        return new MatchResultExpected(HasDescription.message(message));
    }
    
    @Factory
    public static <T> IsMatchResult<T> expected(Matcher<? super String> messageMatcher) {
        return IsMatchResult.mismatch(expectedMessage(messageMatcher));
    }
    
    @Factory
    public static <T> IsMatchResult<T> expected(String message) {
        return IsMatchResult.mismatch(expectedMessage(message));
    }
    
    public MatchResultExpected(Matcher<? super SelfDescribing> featureMatcher) {
        super(featureMatcher, "expected", "expected");
    }

    @Override
    protected SelfDescribing getFeature(MatchResult<?> value) {
        MatchResult.Mismatch<?> mism = value.getMismatch();
        if (mism == null) return null;
        return mism.getExpectedDescription();
    }
}
