package org.cthul.matchers.fluent.values;

import org.cthul.matchers.fluent.values.MatchValue.Element;
import org.cthul.matchers.fluent.values.MatchValue.ExpectationDescription;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 *
 */
public class ElementMatcher<Item> extends TypeSafeMatcher<Element<Item>> 
                implements MatchValue.ElementMatcher<Item> {
    
    private final Matcher<? super Item> matcher;

    public ElementMatcher(Matcher<? super Item> matcher) {
        this.matcher = matcher;
    }

    @Override
    protected boolean matchesSafely(Element<Item> item) {
        return matcher.matches(item.value());
    }

    @Override
    public void describeTo(Description description) {
        matcher.describeTo(description);
    }

    @Override
    public void describeExpected(Element<Item> e, ExpectationDescription description) {
        matcher.describeTo(description);
        description.addedExpectation();
    }

    @Override
    protected void describeMismatchSafely(Element<Item> item, Description mismatchDescription) {
        matcher.describeMismatch(item.value(), mismatchDescription);
    }
    
}
