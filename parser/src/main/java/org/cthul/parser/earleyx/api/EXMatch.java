package org.cthul.parser.earleyx.api;

import org.cthul.parser.api.Rule;
import org.cthul.parser.api.XMatch;

public abstract class EXMatch extends XMatch {

    public EXMatch(Rule rule, int inputStart, int inputEnd, EXMatch[] args) {
        super(rule, inputStart, inputEnd, args);
    }

}
