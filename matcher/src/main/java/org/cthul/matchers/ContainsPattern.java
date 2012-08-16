package org.cthul.matchers;

import java.util.regex.Pattern;
import org.cthul.matchers.diagnose.TypesafeQuickMatcherBase;
import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.Matcher;

/**
 * Tests if a string contains a regex pattern.
 * <p/>
 * Use the static factory methods to create instances.
 * @author Arian Treffer
 */
public class ContainsPattern extends TypesafeQuickMatcherBase<String> {

    private final Pattern p;
    private final boolean match;

    public ContainsPattern(Pattern p, boolean match) {
        this.p = p;
        this.match = match;
    }

    public ContainsPattern(Pattern p) {
        this(p, false);
    }

    public ContainsPattern(String regex, boolean match) {
        this(Pattern.compile(regex), match);
    }

    public ContainsPattern(String regex) {
        this(Pattern.compile(regex));
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(match ? "matches" : "contains")
                   .appendText(" /")
                   .appendText(p.pattern())
                   .appendText("/");
    }

    @Override
    protected boolean matchesSafely(String item) {
        if (match) {
            if (p.matcher(item).matches()) return true;
        } else {
            if (p.matcher(item).find()) return true;
        }
        return false;
    }

    @Override
    protected void describeMismatchSafely(String item, Description mismatch) {
        mismatch.appendValue(item)
                .appendText(" did not ")
                .appendText(match ? "match" : "contain")
                .appendText(" /")
                .appendText(p.pattern())
                .appendText("/");
    }

    /**
     * Can the given pattern be found in the string?
     * @param regex
     * @return String-Matcher
     */
    @Factory
    public static Matcher<String> containsPattern(String regex) {
        return new ContainsPattern(regex, false);
    }

    /**
     * Can the given pattern be found in the string?
     * @param p
     * @return String-Matcher
     */
    @Factory
    public static Matcher<String> containsPattern(Pattern p) {
        return new ContainsPattern(p, false);
    }

    /**
     * Does the pattern match the entire string?
     * @param regex
     * @return String-Matcher
     */
    @Factory
    public static Matcher<String> matchesPattern(String regex) {
        return new ContainsPattern(regex, true);
    }

    /**
     * Does the pattern match the entire string?
     * @param p
     * @return String-Matcher
     */
    @Factory
    public static Matcher<String> matchesPattern(Pattern p) {
        return new ContainsPattern(p, true);
    }

}
