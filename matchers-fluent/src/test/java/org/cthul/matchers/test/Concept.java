package org.cthul.matchers.test;

import static org.cthul.matchers.fluent.CoreFluents.*;
import static org.hamcrest.Matchers.is;

/**
 *
 * @author Arian Treffer
 */
public class Concept {
    
    {
        assertThat(2).is(either(is(3)));
    }
}
