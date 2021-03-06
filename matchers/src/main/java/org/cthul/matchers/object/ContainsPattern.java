package org.cthul.matchers.object;

import java.util.regex.Pattern;
import org.cthul.matchers.diagnose.safe.TypesafeQuickMatcher;
import org.hamcrest.*;

/**
 * Tests if a string contains a regex pattern.
 * <p>
 * Use the static factory methods to create instances.
 */
public class ContainsPattern extends TypesafeQuickMatcher<CharSequence> {

    private final Pattern p;
    private final boolean match;

    public ContainsPattern(Pattern p, boolean match) {
        super(String.class);
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
        description.appendText("a string ")
                   .appendText(match ? "matching" : "containing")
                   .appendText(" /")
                   .appendText(p.pattern())
                   .appendText("/");
    }

    @Override
    protected boolean matchesSafely(CharSequence item) {
        if (match) {
            if (p.matcher(item).matches()) return true;
        } else {
            if (p.matcher(item).find()) return true;
        }
        return false;
    }

    @Override
    protected void describeMismatchSafely(CharSequence item, Description mismatch) {
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
    public static Matcher<CharSequence> containsPattern(String regex) {
        return new ContainsPattern(regex, false);
    }

    /**
     * Can the given pattern be found in the string?
     * @param p
     * @return String-Matcher
     */
    @Factory
    public static Matcher<CharSequence> containsPattern(Pattern p) {
        return new ContainsPattern(p, false);
    }

    /**
     * Does the pattern match the entire string?
     * @param regex
     * @return String-Matcher
     */
    @Factory
    public static Matcher<CharSequence> matchesPattern(String regex) {
        return new ContainsPattern(regex, true);
    }

    /**
     * Does the pattern match the entire string?
     * @param p
     * @return String-Matcher
     */
    @Factory
    public static Matcher<CharSequence> matchesPattern(Pattern p) {
        return new ContainsPattern(p, true);
    }
}
