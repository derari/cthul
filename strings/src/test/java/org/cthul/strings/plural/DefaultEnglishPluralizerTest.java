package org.cthul.strings.plural;

import org.cthul.strings.PluralizerTestBase;
import org.cthul.strings.Pluralizer;
import org.junit.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Arian Treffer
 */
public class DefaultEnglishPluralizerTest extends PluralizerTestBase<DefaultEnglishPluralizer> {
    
    public DefaultEnglishPluralizerTest() {
        instance = new DefaultEnglishPluralizer();
    }
    
    /**
     * Test of initUncountables method, of class DefaultEnglishPluralizer.
     */
    @Test
    public void test_uncountables() {
        assertThat(pluralOf("sheep"), is("sheep"));
        assertThat(singularOf("sheep"), is("sheep"));
    }

    /**
     * Test of initIrregulars method, of class DefaultEnglishPluralizer.
     */
    @Test
    public void test_irregulars() {
        assertThat(pluralOf("octopus"), is("octopi"));
        assertThat(singularOf("octopi"), is("octopus"));
    }

    /**
     * Test of initRules method, of class DefaultEnglishPluralizer.
     */
    @Test
    public void test_rules() {
        assertThat(pluralOf("wish"), is("wishes"));
        assertThat(singularOf("wishes"), is("wish"));
        
        assertThat(pluralOf("dummy"), is("dummies"));
        assertThat(singularOf("dummies"), is("dummy"));
        
        assertThat(pluralOf("test"), is("tests"));
        assertThat(singularOf("tests"), is("test"));
        
        // if a word ending with 's' is unknown,
        // we cannot convert it correctly, but we try anyway
        assertThat(pluralOf("foos"), is("foos"));
        assertThat(singularOf("foos"), is("foo"));
    }
    
}
