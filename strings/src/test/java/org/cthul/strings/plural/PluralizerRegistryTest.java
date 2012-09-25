package org.cthul.strings.plural;

import java.util.Locale;
import org.cthul.strings.Pluralizer;
import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 *
 * @author Arian Treffer
 */
public class PluralizerRegistryTest {

    /**
     * Test that english pluralizer is active by default.
     */
    @Test
    public void test_default_english() {
        Pluralizer pEn = PluralizerRegistry.INSTANCE.find(Locale.UK);
        assertThat(pEn.pluralOf("sheep"), is("sheep"));
    }
}
