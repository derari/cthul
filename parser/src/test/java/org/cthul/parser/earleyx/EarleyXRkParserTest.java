package org.cthul.parser.earleyx;

import org.cthul.parser.api.Context;

public class EarleyXRkParserTest extends EarleyXParserTestBase {

    @Override
    protected EarleyXParserBase parser(Context c, EarleyXGrammar g) {
        return new EarleyXRkParser(c, g);
    }
    
}
