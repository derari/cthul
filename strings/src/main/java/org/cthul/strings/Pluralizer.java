package org.cthul.strings;

import org.cthul.strings.plural.RegexPluralizer;

/**
 * Implements pluralization rules.
 * <p>
 * Implementations can extend {@link RegexPluralizer}
 * 
 * @author Arian Treffer
 * @see PluralizerRegistry
 */
public interface Pluralizer {

    /**
     * Converts a noun to plural.
     * <p>
     * If {@code word} is plural, the result is the same.
     * @param word
     * @return plural of {@code word}
     */
    public String pluralize(String word);

    /**
     * Converts a noun to singular.
     * <p>
     * If {@code word} is singular, the result is the same.
     * @param word
     * @return singular of {@code word}
     */
    public String singularize(String word);

}
