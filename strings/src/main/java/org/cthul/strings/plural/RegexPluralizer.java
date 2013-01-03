package org.cthul.strings.plural;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides a default implementation for a regex based conversion.
 * <p/>
 * Instances should be immutable. To set up the rules, subclasses should
 * overwrite {@code initUncountables, initIrregulars} and {@code initRules}.
 * @author Arian Treffer
 */
public class RegexPluralizer extends RulePluralizer {

    /**
     * Creates a case insensitive regex pattern. This method is used by
     * {@code irregular, plural} and {@code singular}.
     * @param regex
     * @return pattern
     * @see RegexPluralizer#irregular(java.lang.String, java.lang.String)
     * @see RegexPluralizer#plural(java.lang.String, java.lang.String)
     * @see RegexPluralizer#singular(java.lang.String, java.lang.String)
     */
    protected Pattern p(String regex) {
        return p(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }

    /**
     * An abbreviation for {@code Pattern.compile}, called by {@code p(String)}
     * @param regex
     * @param flags
     * @return pattern
     * @see RegexPluralizer#p(java.lang.String) 
     */
    protected Pattern p(String regex, int flags) {
        return Pattern.compile(regex, flags);
    }

    private static String q(char c) {
        return Pattern.quote(String.valueOf(c));
    }
    
    private static String q(String s) {
        return Pattern.quote(s);
    }

    /**
     * @param words
     * @see RegexPluralizer#singular(java.lang.String, java.lang.String)
     */
    protected void singular(String... words) {
        ensurePairs(words);
        for (int i = 0; i < words.length; i += 2) {
            singular(words[i], words[i+1]);
        }
    }

    /**
     * Defines a rule that is used by {@code singularOf}.
     * Rules will be applied in the order they are defined.
     * <p/>
     * Uses {@code p} to compile the regex.
     * @param pluralRegex
     * @param singularReplace
     * @see RegexPluralizer#p(java.lang.String) 
     * @see RegexPluralizer#singularOf(java.lang.String) 
     */
    protected void singular(String pluralRegex, String singularReplace) {
        singular(p(pluralRegex), singularReplace);
    }

    /**
     *
     * @param pluralRegex
     * @param singularReplace
     * @see RegexPluralizer#singular(java.lang.String, java.lang.String) 
     */
    protected void singular(Pattern pluralRegex, String singularReplace) {
        singular(new RegexRule(pluralRegex, singularReplace));
    }

    /**
     *
     * @param words
     * @see RegexPluralizer#plural(java.lang.String, java.lang.String) 
     */
    protected void plural(String... words) {
        ensurePairs(words);
        for (int i = 0; i < words.length; i += 2) {
            plural(words[i], words[i+1]);
        }
    }

    /**
     * Defines a rule that is used by {@code pluralOf}.
     * Rules will be applied in the order they are defined.
     * <p/>
     * Uses {@code p} to compile the regex.
     * @param singularRegex
     * @param pluralReplace
     * @see RegexPluralizer#p(java.lang.String)
     * @see RegexPluralizer#pluralOf(java.lang.String)
     */
    protected void plural(String singularRegex, String pluralReplace) {
        plural(p(singularRegex), pluralReplace);
    }

    /**
     *
     * @param singularRegex
     * @param pluralReplace
     * @see RegexPluralizer#plural(java.lang.String, java.lang.String) 
     */
    protected void plural(Pattern singularRegex, String pluralReplace) {
        plural(new RegexRule(singularRegex, pluralReplace));
    }

    /**
     * A rule is a regex pattern and a replacement
     */
    protected static class RegexRule extends Rule {
        private Pattern pattern;
        private String replacement;

        public RegexRule(Pattern pattern, String replacement) {
            this.pattern = pattern;
            this.replacement = replacement;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public String getReplacement() {
            return replacement;
        }

        @Override
        public String apply(String word, String wLower) {
            Matcher m = pattern.matcher(word);
            if (!m.find()) return null;
            StringBuffer sb = new StringBuffer();
            m.appendReplacement(sb, replacement);
            return m.appendTail(sb).toString();
        }

        @Override
        public String toString() {
            return "/" + pattern.toString() + "/ -> " + replacement;
        }

    }

}
