package org.cthul.parser.grammar.earleyx.algorithm;

import java.util.*;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.Match;
import org.cthul.parser.grammar.earleyx.EarleyXGrammar;
import org.cthul.parser.grammar.earleyx.rule.EXAntiMatch;
import org.cthul.parser.grammar.earleyx.rule.EXRule;
import org.cthul.parser.util.Format;
import org.cthul.parser.util.IntMap;

public class EarleyXRkParser<I extends Input<?>> extends EarleyXParser<I> {

    private final IntMap<InputPosition> inputPositions = new IntMap<>();
    private InputPosition last = inputPosition(0);

    public EarleyXRkParser(Context<? extends I> input, EarleyXGrammar<I> grammar) {
        super(input, grammar);
    }

    @Override
    public Iterable<Match<?>> parse(int start, int end, String rule, int priority) {
        InputPosition ip = last = inputPosition(start);
        ip.predict(rule, priority);
        return filter(ip.matches, start, end, rule, priority);
    }
    
    private Iterable<Match<?>> filter(List<Match<?>> matches, int start, int end, String rule, int priority) {
        List<Match<?>> result = new ArrayList<>();
        for (Match<?> m: matches) {
            if (m.getStartIndex() == start && m.getEndIndex() == end &&
                    rule.equals(m.getSymbol()) &&
                    m.getPriority() >= priority) {
                result.add(m);
            }
        }
        if (result.isEmpty()) {
            String msg = null;
            Throwable cause = null;
            throw parseException(last, msg, cause);
        }
        return result;
    }
    
    protected EXParseException parseException(InputPosition last, String msg, Throwable cause) {
        Input<?> input = context.getInput();
        Object actual = null;
        if (last.index >= input.getLength()) {
            actual = "<END>";
        } else if (last.matches.isEmpty()) {
            actual = input.get(last.index);
        }
        String strInput = input.asString();
        int position = input.startPosition(last.index);
        EXParseException ex = EXParseException.forStates(last.states, last.matches, actual, strInput, position, msg, cause);
        return ex;
    }
    
    private InputPosition inputPosition(int index) {
        InputPosition ip = inputPositions.get(index);
        if (ip == null) {
            ip = new InputPosition(index);
            inputPositions.put(index, ip);
        }
        return ip;
    }

    protected class InputPosition {
        
        private final int index;
        private final Set<EXRule<? super I>> predicted = new HashSet<>();
        
        private final List<RuleState> states = new ArrayList<>();
        private final List<Match<?>> matches = new ArrayList<>();
        
        private Set<EXAntiMatch> matchtedAntis = null; // lazy

        public InputPosition(int index) {
            this.index = index;
        }
        
        void predict(String rule, int priority) {
            Iterable<EXRule<? super I>> it = grammar.getRules(rule, priority);
            for (EXRule<? super I> prod: it) {
                if (!predicted.add(prod)) continue;
                add(new RuleState(prod));
            }
        }
        
        private void add(RuleState ps) {
            if (ps.completed()) {
                Match<?> match = ps.createMatch(index);
                if (match == null) return;
                inputPosition(match.getStartIndex()).complete(match);
            } else {
                for (int i = 0; i < matches.size(); i++) {
                    Match<?> match = matches.get(i);
                    if (ps.acceptNext(match)) {
                        inputPosition(match.getEndIndex()).add(ps.next(match));
                    }
                }
                states.add(ps);
                predict(ps.nextSymbol(), ps.nextPriority());
                if (ps.ruleIndex == 0 && ps.rule instanceof EXAntiMatch) {
                    if (matchtedAntis == null || !matchtedAntis.contains((EXAntiMatch) ps.rule)) {
                        complete(((EXAntiMatch) ps.rule).antiMatch(context, index));
                    }
                }
            }
        }
        
        private void complete(Match<?> match){
            if (match instanceof EXAntiMatch.Blocked) {
                if (matchtedAntis == null) matchtedAntis = new HashSet<>();
                matchtedAntis.add(((EXAntiMatch.Blocked) match).getRule());
                return;
            }
            InputPosition nextPositon = inputPosition(match.getEndIndex());
            for (int i = 0; i < states.size(); i++) {
                RuleState open = states.get(i);
                if (open.acceptNext(match)) {
                    nextPositon.add(open.next(match));
                }
            }
            if (last.index < index) {
                last = this;
            }
            matches.add(match);
        }

        @Override
        public String toString() {
            return Format.join("#" + index + " (", ", ", ")", 512, " ...)", states, matches);
        }
        
    }
    
}
