package org.cthul.parser.earleyx;

import org.cthul.parser.earleyx.api.EXRule;
import org.cthul.parser.earleyx.api.EXMatch;
import java.util.*;
import org.cthul.parser.api.Context;
import org.cthul.parser.earleyx.api.EXAntiMatchRule;
import org.cthul.parser.util.Format;
import org.cthul.parser.util.IntMap;

public class EarleyXRkParser extends EarleyXParserBase {

    private final IntMap<InputPosition> inputPositions = new IntMap<>();

    public EarleyXRkParser(Context<?> input, EarleyXGrammar grammar) {
        super(input, grammar);
    }

    @Override
    public Iterable<EXMatch> parse(int start, int end, String rule, int priority) {
        InputPosition ip = inputPosition(start);
        ip.predict(rule, priority);
        return filter(ip.matches, start, end, rule, priority);
    }
    
    private Iterable<EXMatch> filter(List<EXMatch> matches, int start, int end, String rule, int priority) {
        List<EXMatch> result = new ArrayList<>();
        for (EXMatch m: matches) {
            if (m.getStart() == start && m.getEnd() == end &&
                    rule.equals(m.getSymbol()) &&
                    m.getPriority() >= priority) {
                result.add(m);
            }
        }
        return result;
    }
    
    private InputPosition inputPosition(int index) {
        InputPosition ip = inputPositions.get(index);
        if (ip == null) {
            ip = new InputPosition(index);
            inputPositions.put(index, ip);
        }
        return ip;
    }

    private class InputPosition {
        
        private final int index;
        private final Set<EXRule> predicted = new HashSet<>();
        
        private final List<RuleState> states = new ArrayList<>();
        private final List<EXMatch> matches = new ArrayList<>();
        
        private Set<EXAntiMatchRule> matchtedAntis = null; // lazy

        public InputPosition(int index) {
            this.index = index;
        }
        
        void predict(String rule, int priority) {
            for (EXRule prod: grammar.getRules(rule, priority)) {
                if (!predicted.add(prod)) continue;
                add(new RuleState(prod));
            }
        }
        
        private void add(RuleState ps) {
            if (ps.completed()) {
                EXMatch match = ps.createMatch(index);
                if (match == null) return;
                inputPosition(match.getStart()).complete(match);
            } else {
                for (int i = 0; i < matches.size(); i++) {
                    EXMatch match = matches.get(i);
                    if (ps.acceptNext(match)) {
                        inputPosition(match.getEnd()).add(ps.next(match));
                    }
                }
                states.add(ps);
                predict(ps.nextSymbol(), ps.nextPriority());
                if (ps.rule instanceof EXAntiMatchRule) {
                    if (matchtedAntis == null || !matchtedAntis.contains((EXAntiMatchRule) ps.rule)) {
                        complete(((EXAntiMatchRule) ps.rule).antiMatch(input, index));
                    }
                }
            }
        }
        
        private void complete(EXMatch match){
            if (match instanceof EXAntiMatchRule.Blocked) {
                if (matchtedAntis == null) matchtedAntis = new HashSet<>();
                matchtedAntis.add((EXAntiMatchRule) match.getRule());
                return;
            }
            InputPosition nextPositon = inputPosition(match.getEnd());
            for (int i = 0; i < states.size(); i++) {
                RuleState open = states.get(i);
                if (open.acceptNext(match)) {
                    nextPositon.add(open.next(match));
                }
            }
            matches.add(match);
        }

        @Override
        public String toString() {
            return Format.join("#" + index + " (", ", ", ")", 512, " ...)", states, matches);
        }
        
    }
    
}
