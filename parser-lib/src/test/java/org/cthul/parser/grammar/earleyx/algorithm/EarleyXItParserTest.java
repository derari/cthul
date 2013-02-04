package org.cthul.parser.grammar.earleyx.algorithm;

import org.cthul.parser.grammar.earleyx.EarleyXParserTestBase;
import org.cthul.parser.api.Input;
import org.cthul.parser.grammar.earleyx.EarleyXGrammar;
import org.cthul.parser.grammar.earleyx.EarleyXItGrammar;

public class EarleyXItParserTest extends EarleyXParserTestBase {

    @Override
    protected <I extends Input<?>> EarleyXGrammar<I> newGrammar() {
        return new EarleyXItGrammar<>();
    }

    
    
}
