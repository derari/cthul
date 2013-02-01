package org.cthul.parser.rule;

import java.util.Collection;
import org.cthul.parser.grammar.api.RuleEval;

public class ChoiceRule extends CompositeRule {

    public ChoiceRule(Rule... rules) {
        super(rules);
    }

    public ChoiceRule(Collection<? extends Rule> rules) {
        super(rules);
    }
    
}
