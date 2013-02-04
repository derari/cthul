package org.cthul.parser.grammar.earleyx.algorithm;

import org.cthul.parser.grammar.earleyx.EarleyXParserTestBase;
import org.cthul.parser.api.Input;
import org.cthul.parser.grammar.earleyx.EarleyXRkGrammar;
import org.cthul.parser.grammar.earleyx.EarleyXRkGrammar;

public class EarleyXRkParserTest extends EarleyXParserTestBase {

    @Override
    protected <I extends Input<?>> EarleyXRkGrammar<I> newGrammar() {
        return new EarleyXRkGrammar<>();
    }

}
