package org.cthul.parser.rule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.cthul.parser.grammar.api.ProxyRuleEval;
import org.cthul.parser.grammar.api.RuleEval;
import org.cthul.parser.sequence.SequenceBuilder;

public class SequenceRule extends Rule {
    
    protected final SequenceBuilder<?, ?> sequence;
    protected final Rule[] itemProduction;
    protected final Rule[] sepProduction;
    protected final boolean includeSeparator;
    protected final boolean flatten;
    protected final boolean allowEmpty;
    protected final int minSize;
    protected final int stepSize;

    public SequenceRule(SequenceBuilder<?, ?> sequence, Rule[] itemProduction, Rule[] sepProduction, boolean includeSeparator, boolean flatten, boolean allowEmpty, int minSize, int stepSize) {
        this.sequence = sequence;
        this.itemProduction = itemProduction;
        this.sepProduction = sepProduction;
        this.includeSeparator = includeSeparator;
        this.flatten = flatten;
        this.allowEmpty = allowEmpty;
        this.minSize = minSize;
        this.stepSize = stepSize;
    }

    public SequenceBuilder<?, ?> getSequenceBuilder() {
        return sequence;
    }

    public List<Rule> getItemProduction() {
        return itemProduction == null ? Collections.<Rule>emptyList() : Arrays.asList(itemProduction);
    }

    public List<Rule> getSepProduction() {
        return sepProduction == null ? Collections.<Rule>emptyList() : Arrays.asList(sepProduction);
    }

    public boolean isFlatten() {
        return flatten;
    }

    public boolean isIncludeSeparator() {
        return includeSeparator;
    }

    public boolean isAllowEmpty() {
        return allowEmpty;
    }

    public int getMinSize() {
        return minSize;
    }

    public int getSetSize() {
        return stepSize;
    }
    
    public RuleEval getEval() {
        return ProxyRuleEval.instance();
    }
    
}
