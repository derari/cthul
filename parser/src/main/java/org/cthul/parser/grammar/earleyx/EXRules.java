package org.cthul.parser.grammar.earleyx;

import java.util.ArrayList;
import java.util.List;
import org.cthul.parser.ProductionBuilder;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.Match;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.grammar.api.ProductionMatch;
import org.cthul.parser.grammar.api.ProxyRuleEval;
import org.cthul.parser.grammar.api.RuleEval;

public class EXRules {
    
    public static RuleKey createListRule(
                                ProductionBuilder mainRule,
                                RuleKey[] item,
                                RuleKey[] sep,
                                boolean allowEmpty) {
        ProductionBuilder listRule = mainRule.newSubRule("list");

        RuleKey itemKey = createSequence(mainRule, "item", item);        
        listRule.add(itemKey);
        RuleKey listKey = listRule.setEval(LIST_ORIGIN).createProduction();
        
        listRule.reset().add(listKey);
        if (sep != null && sep.length > 0) {
            RuleKey sepKey = createSequence(mainRule, "sep", sep);
            listRule.add(sepKey).setEval(LIST_SEP_STEP);
        } else {
            listRule.setEval(LIST_STEP);
        }
        listRule.add(itemKey).createProduction();
        
        if (allowEmpty) {
            mainRule.reset();
            mainRule.setEval(LIST_ORIGIN).createProduction();
        }
        mainRule.reset();
        mainRule.add(listKey).setEval(ProxyRuleEval.instance());
        return mainRule.createProduction();
    }

    public static RuleKey createSequence(ProductionBuilder owner, 
                                String name, RuleKey[] item) {
        ProductionBuilder seq = owner.newSubRule(name);
        for (int i = 0; i < item.length; i++) {
            seq.add(item[0]);
        }
        seq.setEval(ARGS_ARRAY);
        return seq.createProduction();
    }
    
    public static final RuleEval ARGS_ARRAY = new ArgsArrayEval();
    
    public static class ArgsArrayEval implements RuleEval {
        @Override
        public Object eval(Context<?> context, ProductionMatch match) {
            return match.matches();
        }
    }
    
    private static void addItemsToList(Match itemSeq, List<Object> list) {
        Match[] items = (Match[]) itemSeq.eval();
        for (Match item: items) {
            list.add(item.eval());
        }
    }
    
    private static final RuleEval LIST_ORIGIN = new ListOriginEval();
    private static final RuleEval LIST_STEP = new ListStepEval();
    private static final RuleEval LIST_SEP_STEP = new ListSepStepEval();

    public static class ListOriginEval implements RuleEval {
        @Override
        public Object eval(Context<?> context, ProductionMatch match) {
            assert match.matches().length == 1 : "should be 'item'";
            List<Object> list = new ArrayList<>();
            Match[] args = match.matches();
            if (args.length > 0) {
                addItemsToList(match.matches()[0], list);
            }
            return list;
        }

    }

    public static class ListStepEval implements RuleEval {
        @Override
        public Object eval(Context<?> context, ProductionMatch match) {
            List<Object> list = (List) match.matches()[0].eval();
            addItemsToList(match.matches()[1], list);
            return list;
        }
    }

    public static class ListSepStepEval implements RuleEval {
        @Override
        public Object eval(Context<?> context, ProductionMatch match) {
            List<Object> list = (List) match.matches()[0].eval();
            addItemsToList(match.matches()[2], list);
            return list;
        }
    }

}
