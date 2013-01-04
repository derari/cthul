package org.cthul.parser.earleyx.factory;

import java.util.ArrayList;
import java.util.List;
import org.cthul.parser.api.MatchEval;
import org.cthul.parser.api.XMatch;

public class EXRules {
    
    public static EXRuleKey createListRule(
                                EarleyXGrammarFactory f,
                                String symbol, int priority,
                                String[] item, int[] itemPrios,
                                String[] sep, int[] sepPrios,
                                boolean allowEmpty) {
        EXProductionFactory mainRule = f.newProduction(symbol, priority);
        
        EXProductionFactory listRule;
        if (allowEmpty) {
            listRule = mainRule.newSubRule("list");
        } else {
            listRule = mainRule;
        }
        EXRuleKey itemKey = createSequence(f, mainRule, "item", item, itemPrios);        
        listRule.add(itemKey);
        EXRuleKey listKey = listRule.setEval(LIST_ORIGIN).create();
        
        listRule.reset().add(listKey);
        if (sep != null && sep.length > 0) {
            EXRuleKey sepKey = createSequence(f, mainRule, "sep", sep, sepPrios);
            listRule.add(sepKey).setEval(LIST_SEP_STEP);
        } else {
            listRule.setEval(LIST_STEP);
        }
        listRule.add(itemKey).create();
        
        EXRuleKey mainKey;
        if (allowEmpty) {
            mainRule.add(listKey).setEval(SINGLE_PROXY);
            mainKey = mainRule.create();
            mainRule.reset().setEval(LIST_ORIGIN).create();
        } else {
            mainKey = listKey;
        }
        return mainKey;
    }

    public static EXRuleKey createSequence(EarleyXGrammarFactory f, 
                                EXProductionFactory owner, 
                                String name, String[] item, int[] itemPrios) {
        EXProductionFactory seq = owner.newSubRule(name);
        for (int i = 0; i < item.length; i++) {
            seq.add(item[0], itemPrios[0]);
        }
        seq.setEval(ARGS_ARRAY);
        return seq.create();
    }
    
    public static final MatchEval ARGS_ARRAY = new ArgsArrayEval();
    
    public static class ArgsArrayEval implements MatchEval {
        @Override
        public Object eval(XMatch match) {
            return match.getArgs();
        }
    }
    
    private static void addItemsToList(int first, XMatch itemSeq, List<Object> list) {
        XMatch[] items = (XMatch[]) itemSeq.eval();
        for (XMatch item: items) {
            list.add(item.eval());
        }
    }
    
    private static final MatchEval LIST_ORIGIN = new ListOriginEval();
    private static final MatchEval LIST_STEP = new ListStepEval();
    private static final MatchEval LIST_SEP_STEP = new ListSepStepEval();
    private static final MatchEval SINGLE_PROXY = new SingleProxyEval();

    public static class ListOriginEval implements MatchEval {
        @Override
        public Object eval(XMatch match) {
            List<Object> list = new ArrayList<>();
            XMatch[] args = match.getArgs();
            if (args.length > 0) {
                addItemsToList(0, match.getArgs()[0], list);
            }
            return list;
        }

    }

    public static class ListStepEval implements MatchEval {
        @Override
        public Object eval(XMatch match) {
            List<Object> list = (List) match.getArgs()[0].eval();
            addItemsToList(1, match.getArgs()[1], list);
            return list;
        }
    }

    public static class ListSepStepEval implements MatchEval {
        @Override
        public Object eval(XMatch match) {
            List<Object> list = (List) match.getArgs()[0].eval();
            addItemsToList(1, match.getArgs()[2], list);
            return list;
        }
    }
    
    public static class SingleProxyEval implements MatchEval {
        @Override
        public Object eval(XMatch match) {
            return match.getArgs()[0].eval();
        }
    }

}
