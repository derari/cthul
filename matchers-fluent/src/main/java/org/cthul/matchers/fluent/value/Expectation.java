package org.cthul.matchers.fluent.value;

import java.util.LinkedHashSet;
import java.util.Set;
import org.hamcrest.Description;
import org.hamcrest.SelfDescribing;
import org.hamcrest.StringDescription;

/**
 *
 */
public class Expectation implements MatchValue.ExpectationDescription, SelfDescribing {
    
    private final Set<String> expected = new LinkedHashSet<>();
    private StringDescription current = null;

    protected StringDescription current() {
        if (current == null) {
            current = new StringDescription();
        }
        return current;
    }

    @Override
    public void addedExpectation() {
        expected.add(current().toString());
        current = null;
    }

    @Override
    public Description appendText(String text) {
        return current().appendText(text);
    }

    @Override
    public Description appendDescriptionOf(SelfDescribing value) {
        return current().appendDescriptionOf(value);
    }

    @Override
    public Description appendValue(Object value) {
        return current().appendValue(value);
    }

    @Override
    public <T> Description appendValueList(String start, String separator, String end, T... values) {
        return current().appendValueList(start, separator, end, values);
    }

    @Override
    public <T> Description appendValueList(String start, String separator, String end, Iterable<T> values) {
        return current().appendValueList(start, separator, end, values);
    }

    @Override
    public Description appendList(String start, String separator, String end, Iterable<? extends SelfDescribing> values) {
        return current().appendList(start, separator, end, values);
    }

    @Override
    public void describeTo(Description description) {
        final int last = expected.size() - 1;
        int i = 0;
        for (String s : expected) {
            if (i > 0) {
                if (i == last) {
                    description.appendText(", and ");
                } else {
                    description.appendText(", ");
                }
            }
            description.appendText(s);
            i++;
        }
    }

    @Override
    public String toString() {
        StringDescription d = new StringDescription();
        describeTo(d);
        return d.toString();
    }
    
}
