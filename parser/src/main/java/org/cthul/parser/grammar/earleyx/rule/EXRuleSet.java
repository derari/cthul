package org.cthul.parser.grammar.earleyx.rule;

import org.cthul.parser.api.Input;
import org.cthul.parser.util.Format;
import org.cthul.parser.util.PriorityList;

/**
 * Set of rules with same name.
 */
public class EXRuleSet<I extends Input<?>> extends PriorityList<EXRule<? super I>> {

    @Override
    protected int priorityOf(EXRule<? super I> e) {
        return e.getPriority();
    }
    
    @Override
    public String toString() {
        return Format.join("", ", ", "", 128, "...", this);
    }

}
