package org.cthul.parser.earleyx;

import org.cthul.parser.api.Context;

public class EarleyXItParserTest extends EarleyXParserTestBase {

    @Override
    protected EarleyXParserBase parser(Context c, EarleyXGrammar g) {
        return new EarleyXItParser(c, g);
    }
    
}
