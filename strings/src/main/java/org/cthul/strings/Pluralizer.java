package org.cthul.strings;

import org.cthul.strings.plural.PluralizerRegistry;
import org.cthul.strings.plural.RegexPluralizer;

/**
 * Implements pluralization rules of a language.
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
    public String pluralOf(String word);

    /**
     * Converts a noun to singular.
     * <p>
     * If {@code word} is singular, the result is the same.
     * @param word
     * @return singular of {@code word}
     */
    public String singularOf(String word);

}
