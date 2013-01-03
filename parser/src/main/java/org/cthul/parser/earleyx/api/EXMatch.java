package org.cthul.parser.earleyx.api;

import org.cthul.parser.api.Match;
import org.cthul.parser.api.Rule;

public abstract class EXMatch extends Match {

    public EXMatch(Rule rule, int inputStart, int inputEnd, EXMatch[] args) {
        super(rule, inputStart, inputEnd, args);
    }

}
