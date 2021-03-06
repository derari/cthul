package org.cthul.parser.test;

import org.cthul.parser.api.RuleKey;
import org.cthul.parser.token.Token;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import static org.hamcrest.CoreMatchers.*;

/**
 *
 * @author Arian Treffer
 */
public class TokenMatcher<V> extends TypeSafeMatcher<Token<V>> {
    
    @SuppressWarnings("unchecked")
    public static Matcher<Token<?>> matchesKey(String key) {
        return (Matcher) new TokenMatcher<>(key, null, null);
    }
    
    public static <V> Matcher<Token<V>> keyAndValue(String key, V value) {
        return new TokenMatcher<>(key, is(value), null);
    }
    
    public static Matcher<Token<String>> keyAndValue(String key) {
        return new TokenMatcher<>(key, is(key), null);
    }

    private final String key;
    private final Matcher<V> valueMatcher;
    private final Integer channel;

    public TokenMatcher(String key, Matcher<V> valueMatcher, Integer channel) {
        this.key = key;
        this.valueMatcher = valueMatcher;
        this.channel = channel;
    }
    
    @Override
    public boolean matchesSafely(Token<V> item) {
        if (key != null && !item.match(new RuleKey(key, 0))) {
            return false;
        }
        if (valueMatcher != null && !valueMatcher.matches(item.eval())) {
            return false;
        }
        if (channel != null && channel != item.getChannel()) {
            return false;
        }
        return true;
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("<");
        if (key != null) {
            description.appendText(key);
            if (valueMatcher != null) {
                description.appendText(":");
            }
        }
        if (valueMatcher != null) {
            valueMatcher.describeTo(description);
        }
        if (channel != null) {
            description.appendText(" #");
            description.appendText(channel.toString());
        }
        description.appendText(">");
    }
    
}
