package org.cthul.parser.rule;

import java.util.Collection;

public class ProductionRule extends CompositeRule {

    public ProductionRule(Rule... rules) {
        super(rules);
    }

    public ProductionRule(Collection<? extends Rule> rules) {
        super(rules);
    }

}
