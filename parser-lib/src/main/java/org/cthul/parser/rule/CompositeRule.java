package org.cthul.parser.rule;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.cthul.parser.api.Context;
import org.cthul.parser.grammar.api.ProductionMatch;
import org.cthul.parser.grammar.api.RuleEval;

public abstract class CompositeRule extends Rule {
    
    protected final Rule[] rules;

    public CompositeRule(Rule... rules) {
        this.rules = rules;
    }

    public CompositeRule(Collection<? extends Rule> rules) {
        this(rules.toArray(new Rule[rules.size()]));
    }

    public List<Rule> getRules() {
        return Arrays.asList(rules);
    }
    
}
