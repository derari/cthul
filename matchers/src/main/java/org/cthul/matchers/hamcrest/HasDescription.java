package org.cthul.matchers.hamcrest;

import org.cthul.matchers.diagnose.safe.TypesafeFeatureMatcher;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;
import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;
import org.hamcrest.core.IsEqual;

/**
 *
 */
public class HasDescription extends TypesafeFeatureMatcher<SelfDescribing, String> {

    @Factory
    public static HasDescription description(Matcher<? super String> stringMatcher) {
        return new HasDescription(stringMatcher);
    }
    
    @Factory
    public static HasDescription description(String string) {
        return new HasDescription(IsEqual.equalTo(string));
    }
    
    @Factory
    public static HasDescription message(Matcher<? super String> stringMatcher) {
        return new HasDescription(stringMatcher, "message");
    }
    
    @Factory
    public static HasDescription message(String string) {
        return new HasDescription(IsEqual.equalTo(string), "message");
    }
    
    public HasDescription(Matcher<? super String> featureMatcher) {
        this(featureMatcher, "description");
    }
    
    public HasDescription(Matcher<? super String> featureMatcher, String name) {
        super(featureMatcher, name, name, SelfDescribing.class);
    }

    @Override
    protected String getFeature(SelfDescribing value) {
        return StringDescription.toString(value);
    }
}
