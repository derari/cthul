package org.cthul.strings.plural;

import java.util.Locale;
import org.cthul.strings.Pluralizer;
import org.junit.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

/**
 *
 * @author Arian Treffer
 */
public class PluralizerRegistryTest {
    
    public PluralizerRegistryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test that english pluralizer is active by default.
     */
    @Test
    public void test_default_english() {
        Pluralizer pEn = PluralizerRegistry.INSTANCE.get(Locale.ENGLISH);
        assertThat(pEn.pluralize("sheep"), is("sheep"));
    }
}
