package org.cthul.parser.grammar.earleyx;

import org.cthul.parser.api.Input;

public class EarleyXRkParserTest extends EarleyXParserTestBase {

    @Override
    protected <I extends Input<?>> EarleyXRkGrammar<I> newGrammar() {
        return new EarleyXRkGrammar<>();
    }

}
