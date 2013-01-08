package org.cthul.parser.grammar.earleyx.rule;

import org.cthul.parser.util.Format;
import org.cthul.parser.util.PriorityList;

/**
 * Set of rules with same name.
 */
public class EXRuleSet extends PriorityList<EXRule> {

    @Override
    protected int priorityOf(EXRule e) {
        return e.getPriority();
    }
    
    @Override
    public String toString() {
        return Format.join("", ", ", "", 128, "...", this);
    }

}
