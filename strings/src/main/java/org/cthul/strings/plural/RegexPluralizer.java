package org.cthul.strings.plural;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cthul.strings.Pluralizer;

/**
 * Provides a default implementation for a regex based conversion.
 * <p/>
 * Instances should be immutable. To set up the rules, subclasses should
 * overwrite {@code initUncountables, initIrregulars} and {@code initRules}.
 * @author derari
 */
public class RegexPluralizer implements Pluralizer {

    protected final Set<String> uncountables;

    protected final List<Rule> plurals;
    protected final List<Rule> singulars;

    protected final Locale locale;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public RegexPluralizer() {
        uncountables = newSet();
        plurals = newRuleList();
        singulars = newRuleList();
        locale = locale();
        initUncountables();
        initIrregulars();
        initRules();
    }

    /**
     * @return empty string set
     */
    protected Set<String> newSet() {
        return new HashSet<>();
    }

    /**
     * @return empty list of rules
     */
    protected List<Rule> newRuleList() {
        return new ArrayList<>();
    }

    /**
     * @return locale that is used for conversion between
     * upper and lower case.
     */
    protected Locale locale() {
        return Locale.ROOT;
    }

    /**
     * Initialize uncountable nouns here.
     */
    protected void initUncountables() {
    }

    /**
     * Initialize irregular nouns here.
     * This method will be called before {@code initRules}.
     */
    protected void initIrregulars() {
    }

    /**
     * Initialize regular rules here.
     * This method will be called after {@code initIrregulars}
     */
    protected void initRules() {
    }

    protected String lower(String s) {
        return s.toLowerCase(locale);
    }

    protected String lower(char... c) {
        return new String(c).toLowerCase(locale);
    }

    protected String upper(String s) {
        return s.toUpperCase(locale);
    }

    protected String upper(char... c) {
        return new String(c).toUpperCase(locale);
    }

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

    /**
     *
     * @param words
     * @see RegexPluralizer#uncountable(java.lang.String)
     */
    protected void uncountable(String... words) {
        for (String w: words) uncountable(w);
    }

    /**
     * Declares a word as uncountable, which means that singular and plural
     * are identical.
     * @param word
     */
    protected void uncountable(String word) {
        uncountables.add(word);
    }

    private void ensurePairs(String... words) {
        if (words.length % 2 > 0) {
            throw new IllegalArgumentException(
                    "Expected even number of arguments");
        }
    }

    /**
     *
     * @param words
     * @see RegexPluralizer#irregular(java.lang.String, java.lang.String)
     */
    protected void irregular(String... words) {
        ensurePairs(words);
        for (int i = 0; i < words.length; i += 2) {
            irregular(words[i], words[i+1]);
        }
    }

    /**
     * Defines a word with irregular plural. Makes sure that the case of
     * the first letter is taken over.
     * <pre> {@code
     * irregular("cow", "kine");
     * pluralize("cow"); // -> "kine"
     * pluralize("Cow"); // -> "Kine"
     * singularize("kine"); // -> "cow"
     * singularize("Kine"); // -> "Cow"
     * }</pre>
     * The implementation of this method relies on
     * {@code plural} and {@code singular}.
     * @param singular
     * @param plural
     * @see RegexPluralizer#plural(java.lang.String, java.lang.String)
     * @see RegexPluralizer#singular(java.lang.String, java.lang.String) 
     */
    protected void irregular(String singular, String plural) {
        if (singular.isEmpty() || plural.isEmpty()) {
            throw new IllegalArgumentException("Unexpected empty string");
        }
        char plurFirst = plural.charAt(0);
        String plurRem = plural.length() == 1 ? "" :
                plural.substring(1, plural.length());
        char singFirst = singular.charAt(0);
        String singRem = singular.length() == 1 ? "" :
                singular.substring(1, singular.length());


        if (upper(singFirst).equals(upper(plurFirst))) {
            // first character is the same, copy it for correct case
            singular("(" + q(plurFirst) + ")" + q(plurRem) + "$",
                     "$1" + singRem);
            plural("(" + singFirst + ")" + singRem + "$",
                     "$1" + plurRem);
        } else {
            // create 2 rules for each direction,
            // one for upper and one for lower case
            String matchSingRem = "((?i)" + q(singRem) + ")$";
            String matchPlurRem = "((?i)" + q(plurRem) + ")$";
            plural(p(q(upper(singFirst)) + matchSingRem, 0),
                   upper(plurFirst) + plurRem);
            plural(p(q(lower(singFirst)) + matchSingRem, 0),
                   lower(plurFirst) + plurRem);
            singular(p(q(upper(plurFirst)) + matchPlurRem, 0),
                     upper(singFirst) + singRem);
            singular(p(q(lower(plurFirst)) + matchPlurRem, 0),
                     lower(singFirst) + singRem);
        }
    }
    
    private static String q(char c) {
        return Pattern.quote(String.valueOf(c));
    }
    
    private static String q(String s) {
        return Pattern.quote(s);
    }

    /**
     *
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
     * Defines a rule that is used by {@code singularize}.
     * Rules will be applied in the order they are defined.
     * <p/>
     * Uses {@code p} to compile the regex.
     * @param pluralRegex
     * @param singularReplace
     * @see RegexPluralizer#p(java.lang.String) 
     * @see RegexPluralizer#singularize(java.lang.String) 
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
        singulars.add(new Rule(pluralRegex, singularReplace));
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
     * Defines a rule that is used by {@code pluralize}.
     * Rules will be applied in the order they are defined.
     * <p/>
     * Uses {@code p} to compile the regex.
     * @param singularRegex
     * @param pluralReplace
     * @see RegexPluralizer#p(java.lang.String)
     * @see RegexPluralizer#pluralize(java.lang.String)
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
        plurals.add(new Rule(singularRegex, pluralReplace));
    }

    /**
     * Converts a word.
     * <p/>
     * First checks if the word is uncountable.
     * If it is not, the first rule that matches is applied to the word.
     * If no rule matches, returns {@code word}
     * <p/>
     * Is used by {@code pluralize} and {@code singularize}
     * @param word
     * @param rules
     * @return tranformed word
     * @see RegexPluralizer#pluralize(java.lang.String)
     * @see RegexPluralizer#singularize(java.lang.String) 
     */
    protected String transform(String word, List<Rule> rules) {
        int i = word.lastIndexOf(' ');
        if (i > -1) {
            return word.substring(0, i+1) +
                   transform(word.substring(i+1), rules);
        }
        if (uncountables.contains(word.toLowerCase(locale))) return word;
        for (Rule r: rules) {
            Matcher m = r.pattern.matcher(word);
            if (m.find()) return m.replaceAll(r.replacement);
        }
        return word;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String pluralize(String word) {
        return transform(word, plurals);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String singularize(String word) {
        return transform(word, singulars);
    }

    /**
     * A rule is a regex pattern and a replacement
     */
    protected static class Rule {
        private Pattern pattern;
        private String replacement;

        public Rule(Pattern pattern, String replacement) {
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
        public String toString() {
            return pattern.toString() + " -> " + replacement;
        }

    }

}
