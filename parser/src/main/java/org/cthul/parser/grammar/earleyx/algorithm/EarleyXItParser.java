package org.cthul.parser.grammar.earleyx.algorithm;

import java.util.*;
import org.cthul.parser.api.Context;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.Match;
import org.cthul.parser.grammar.earleyx.EarleyXGrammar;
import org.cthul.parser.grammar.earleyx.rule.EXAntiMatch;
import org.cthul.parser.grammar.earleyx.rule.EXRule;
import org.cthul.parser.grammar.earleyx.rule.EXRuleSet;
import org.cthul.parser.util.Format;
import org.cthul.parser.util.IntMap;
import org.cthul.parser.util.LinkedQueue;


public class EarleyXItParser<I extends Input<?>> extends EarleyXParser<I> {

    private final IntMap<InputPosition> inputPositions = new IntMap<>();
    private LinkedQueue<InputPosition> updatedPositions = new LinkedQueue<>();

    public EarleyXItParser(Context<? extends I> input, EarleyXGrammar<I> grammar) {
        super(input, grammar);
    }
    
    @Override
    public Iterable<Match<?>> parse(int start, int end, String rule, int priority) {
        InputPosition ip = inputPosition(start);
        ip.initial(rule, priority);
        earley();
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
        return result;
    }
    
    private void earley() {
        InputPosition ip;
        while ((ip = updatedPositions.poll()) != null) {
            ip.update();
        }
    }
    
    private InputPosition inputPosition(int index) {
        InputPosition ip = inputPositions.get(index);
        if (ip == null) {
            ip = new InputPosition(index);
            inputPositions.put(index, ip);
        }
        return ip;
    }

    private class InputPosition extends LinkedQueue.Link {
        
        private static final int PREDICTING =   1;
        private static final int SCANNING =     2;
        private static final int IDLE =         3;
        
        private final int index;
        
        private int progress = IDLE;
        
        private final Map<String, Integer> predicted = new HashMap<>();
        private final List<RuleState> states = new ArrayList<>();
        private int processedStates = 0;
        
        private final List<Match<?>> matches = new ArrayList<>();
        private int scannedMatches = 0;
        
        private List<EXAntiMatch> predictedAntis = null;
        private Set<EXAntiMatch.Blocked> matchtedAntis = null;
        private int processedAntis = 0;        

        public InputPosition(int index) {
            this.index = index;
        }
        
        void initial(String rule, int priority) {
            predict(rule, priority);
        }
        
        private void enqueueForUpdate() {
            updatedPositions.enqueue(this);
        }
        
        void update() {
            progress = PREDICTING;
            predict();
            progress = SCANNING;
            scan();
            progress = IDLE;
            evaluateAntiMatches();
        }
        
        private void predict() {
            final int matchesSize = scannedMatches;
            for (int i = processedStates; i < states.size(); i++) {
                RuleState rs = states.get(i);
                predict(rs.nextSymbol(), rs.nextPriority());
                for (int j = 0; j < matchesSize; j++) {
                    Match<?> match = matches.get(j);
                    if (rs.acceptNext(match)) {
                        inputPosition(match.getInputEnd()).add(rs.next(match));
                    }
                }
            }
            processedStates = states.size();
        }
        
        private void predict(String symbol, int priority) {
            EXRuleSet<I> rs = grammar.getRules(symbol);
            if (rs == null) return;
            final int size = rs.size();
            Integer nextRule = predicted.get(symbol);
            int i = nextRule == null ? 0 : nextRule;
            if (i == size) return;
            for (;i < size; i++) {
                EXRule<? super I> rule = rs.get(i);
                if (rule.getPriority() < priority) break;
                if (rule instanceof EXAntiMatch) {
                    if (predictedAntis == null) predictedAntis = new ArrayList<>();
                    predictedAntis.add((EXAntiMatch) rule);
                }
                add(new RuleState(rule));
            }
            predicted.put(symbol, i);
        }
        
        private void complete(RuleState rs) {
            Match match = rs.createMatch(index);
            if (match != null) {
                inputPosition(match.getStartIndex()).matched(match);
            }
        }
        
        private void matched(Match match) {
            if (match instanceof EXAntiMatch.Blocked) {
                if (matchtedAntis == null) matchtedAntis = new HashSet<>();
                matchtedAntis.add((EXAntiMatch.Blocked) match);
                return;
            }
            matches.add(match);
            if (progress > SCANNING) enqueueForUpdate();
        }
        
        private void scan() {
            final int statesSize = processedStates;
            for (int i = scannedMatches; i < matches.size(); i++) {
                Match<?> match = matches.get(i);
                InputPosition nextPosition = inputPosition(match.getEndIndex());
                for (int j = 0; j < statesSize; j++) {
                    RuleState rs = states.get(j);
                    if (rs.acceptNext(match)) {
                        nextPosition.add(rs.next(match));
                    }
                }
            }
            scannedMatches = matches.size();
        }
        
        private void add(RuleState rs) {
            if (rs.completed()) {
                complete(rs);
            } else {
                states.add(rs);
                if (progress > PREDICTING) enqueueForUpdate();
            }
        }
        
        @SuppressWarnings("element-type-mismatch") // EXAntiMatch equals its own Blockeds
        private void evaluateAntiMatches() {
            if (predictedAntis == null) return;
            while (processedAntis < predictedAntis.size()) {
                EXAntiMatch antiMatch = predictedAntis.get(processedAntis);
                processedAntis++;
                earley();
                if (matchtedAntis == null || !matchtedAntis.contains(antiMatch)) {
                    matched(antiMatch.antiMatch(context, index));
                }
            }
        }

        @Override
        public String toString() {
            return Format.join("#" + index + " (", ", ", ")", 512, " ...)", states, matches);
        }
        
    }
    
}
