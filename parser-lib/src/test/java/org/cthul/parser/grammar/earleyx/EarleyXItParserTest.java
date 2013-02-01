package org.cthul.parser.grammar.earleyx;

import org.cthul.parser.api.Input;

public class EarleyXItParserTest extends EarleyXParserTestBase {

    @Override
    protected <I extends Input<?>> EarleyXGrammar<I> newGrammar() {
        return new EarleyXItGrammar<>();
    }

    
    
}
