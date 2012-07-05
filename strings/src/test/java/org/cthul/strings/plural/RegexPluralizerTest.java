package org.cthul.strings.plural;

import org.cthul.strings.PluralizerTestBase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 *
 * @author Arian Treffer
 */
public class RegexPluralizerTest extends PluralizerTestBase<RegexPluralizerTest.TestRegexPluralizer>{
    
    public RegexPluralizerTest() {
    }
    
    @Before
    public void setUp() {
        instance = new TestRegexPluralizer();
    }
    
    @After
    public void tearDown() {
    }
    
    private void setUpDefaultRule() {
        instance.plural("$", "s");
        instance.singular("s$", "");
    }
    
    @Test
    public void test_simple_convert() {
        setUpDefaultRule();
        assertThat(pluralOf("test"), is("tests"));
        assertThat(singularOf("tests"), is("test"));    }
    
    @Test
    public void test_convert_ignoring_case() {
        setUpDefaultRule();
        assertThat(pluralOf("ITEM"), is("ITEMs"));
        assertThat(singularOf("ITEMS"), is("ITEM"));
    }

    /**
     * Test of irregular method, of class RegexPluralizer.
     */
    @Test
    public void test_irregular_character_case_switch() {
        instance.irregular("cow", "kine");
        setUpDefaultRule();
        
        assertThat(pluralOf("cow"), is("kine"));
        assertThat(singularOf("kine"), is("cow"));
        
        assertThat(pluralOf("Cow"), is("Kine"));
        assertThat(singularOf("Kine"), is("Cow"));
    }

    public class TestRegexPluralizer extends RegexPluralizer {

        @Override
        public void irregular(String... words) {
            super.irregular(words);
        }

        @Override
        public void plural(String... words) {
            super.plural(words);
        }

        @Override
        public void singular(String... words) {
            super.singular(words);
        }

        @Override
        protected void uncountable(String... words) {
            super.uncountable(words);
        }
        
    }
}
