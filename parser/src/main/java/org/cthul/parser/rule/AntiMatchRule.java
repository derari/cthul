package org.cthul.parser.rule;

import java.util.Collection;

public class AntiMatchRule extends CompositeRule {

    public AntiMatchRule(Rule... rules) {
        super(rules);
    }

    public AntiMatchRule(Collection<? extends Rule> rules) {
        super(rules);
    }

}
