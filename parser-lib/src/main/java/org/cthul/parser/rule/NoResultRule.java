package org.cthul.parser.rule;

import java.util.Collection;

public class NoResultRule extends CompositeRule {

    public NoResultRule(Rule... rules) {
        super(rules);
    }

    public NoResultRule(Collection<? extends Rule> rules) {
        super(rules);
    }

}
