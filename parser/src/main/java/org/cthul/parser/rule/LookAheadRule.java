package org.cthul.parser.rule;

import java.util.Collection;

public class LookAheadRule extends CompositeRule {

    public LookAheadRule(Rule... rules) {
        super(rules);
    }

    public LookAheadRule(Collection<? extends Rule> rules) {
        super(rules);
    }

}
