package org.cthul.strings.plural;

import java.util.*;
import org.cthul.strings.Pluralizer;

/**
 * Provides a default implementation for a rule based conversion.
 * <p/>
 * Instances should be immutable. To set up the rules, subclasses should
 * overwrite {@code initUncountables, initIrregulars} and {@code initRules}.
 * @author Arian Treffer
 * @see #initUncountables()
 * @see #initIrregulars() 
 * @see #initRules()
 */
public class RulePluralizer implements Pluralizer {

    protected final List<Rule> plurals;
    protected final List<Rule> singulars;

    protected final Locale locale;

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public RulePluralizer() {
        plurals = newRuleList();
        singulars = newRuleList();
        locale = locale();
        initialize();
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
    
    protected void initialize() {
        initUncountables();
        initIrregulars();
        initRules();
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
     * This method will be called afterRem {@code initIrregulars}
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
     *
     * @param words
     * @see RulePluralizer#uncountable(java.lang.String)
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
        Rule r = new UncountableRule(lower(word));
        plural(r);
        singular(r);
    }

    protected final void ensurePairs(String... words) {
        if (words.length % 2 > 0) {
            throw new IllegalArgumentException(
                    "Expected even number of arguments");
        }
    }

    /**
     * @param words
     * @see #irregular(java.lang.String, java.lang.String)
     */
    protected void irregular(String... words) {
        ensurePairs(words);
        for (int i = 0; i < words.length; i += 2) {
            irregular(words[i], words[i+1]);
        }
    }

    /**
     * Defines a word with irregular plural. Ensures that the case of
     * the first letter is taken over.
     * <pre> {@code
     * irregular("cow", "kine");
     * pluralOf("cow"); // -> "kine"
     * pluralOf("Cow"); // -> "Kine"
     * singularOf("kine"); // -> "cow"
     * singularOf("Kine"); // -> "Cow"
     * }</pre>
     * The implementation of this method relies on
     * {@code plural} and {@code singular}.
     * @param singular
     * @param plural
     * @see #plural(org.cthul.strings.plural.RulePluralizer.Rule) 
     * @see #singular(org.cthul.strings.plural.RulePluralizer.Rule) 
     */
    protected void irregular(String singular, String plural) {
        plural = lower(plural);
        singular = lower(singular);
        plural(newIrregularRule(singular, plural, locale));
        singular(newIrregularRule(plural, singular, locale));
    }
    
    /**
     * Defines a rule that is used by {@link #singularOf(java.lang.String)}.
     * Rules will be applied in the order they are defined.
     * @param rule
     */
    protected void singular(Rule rule) {
        singulars.add(rule);
    }

    /**
     * Defines a rule that is used by {@link #pluralOf(java.lang.String)}.
     * Rules will be applied in the order they are defined.
     * @param rule
     */
    protected void plural(Rule rule) {
        plurals.add(rule);
    }

    /**
     * Converts a word.
     * <p/>
     * The first rule that matches is applied to the word.
     * If no rule matches, returns result of {@link #noMatch(java.lang.String)}.
     * <p/>
     * Is used by {@code pluralOf} and {@code singularOf}
     * @param word
     * @param rules
     * @return tranformed word
     * @see RegexPluralizer#pluralOf(java.lang.String)
     * @see RegexPluralizer#singularOf(java.lang.String) 
     */
    protected String transform(String word, List<Rule> rules) {
        String wordLower = lower(word);
        for (Rule r: rules) {
            String s = r.apply(word, wordLower);
            if (s != null) return s;
        }
        return noMatch(word);
    }
    
    protected String noMatch(String word) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String pluralOf(String word) {
        return transform(word, plurals);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String singularOf(String word) {
        return transform(word, singulars);
    }

    /**
     * A conversion rule.
     */
    public static abstract class Rule {
        
        public abstract String apply(String word, String wLower);

    }
    
    public static class UncountableRule extends Rule {
        
        protected final String word;

        public UncountableRule(String word) {
            this.word = word;
        }

        @Override
        public String apply(String word, String wLower) {
            if (!wLower.endsWith(this.word)) return null;
            return word;
        }
        
    }
    
    public static Rule newIrregularRule(String before, String after, Locale l) {
        if (before.isEmpty() || after.isEmpty()) {
            throw new IllegalArgumentException("Unexpected empty string");
        }
        if (before.charAt(0) == after.charAt(0)) {
            return new PrefixedIrregularRule(before, after);
        }
        return new IrregularRule(before, after, l);
    }
    
    public static class PrefixedIrregularRule extends Rule {
        
        protected final String before;
        protected final int cut;
        protected final String afterRem;

        public PrefixedIrregularRule(String before, String after) {
            this.before = before;
            this.cut = before.length()-1;
            this.afterRem = after.substring(1);
        }

        @Override
        public String apply(String word, String wLower) {
            if (!wLower.endsWith(before)) return null;
            return word.substring(word.length()-cut) + afterRem;
        }
        
        @Override
        public String toString() {
            return before + " -> " + before.substring(0, 1) + afterRem;
        }
        
    }
    
    public static class IrregularRule extends Rule {
        
        protected final String before;
        protected final int cut;
        protected final int afterFirstLow;
        protected final int afterFirstUp;
        protected final String afterRem;

        public IrregularRule(String before, String after, Locale l) {
            this.before = before;
            this.cut = before.length();
            this.afterRem = after.substring(1);
            String afterFirst = after.substring(0, 1);
            this.afterFirstLow = afterFirst.toLowerCase(l).codePointAt(0);
            this.afterFirstUp = afterFirst.toUpperCase(l).codePointAt(0);
        }

        @Override
        public String apply(String word, String wLower) {
            if (!wLower.endsWith(before)) return null;
            StringBuilder sb = new StringBuilder();
            int c = word.length() - cut;
            sb.append(word, 0, c);
            sb.appendCodePoint(
                    Character.isUpperCase(word.codePointAt(c)) ?
                    afterFirstUp : afterFirstLow);
            sb.append(afterRem);
            return sb.toString();
        }

        @Override
        public String toString() {
            return before + " -> " + ((char) afterFirstLow) + afterRem;
        }
        
    }

}
