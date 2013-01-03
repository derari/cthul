package org.cthul.parser.earleyx;

import org.cthul.parser.earleyx.api.EXRuleSet;
import org.cthul.parser.earleyx.api.EXRule;
import org.cthul.parser.earleyx.api.EXMatch;
import java.util.*;
import org.cthul.parser.api.Context;
import org.cthul.parser.earleyx.api.EXAntiMatchRule;
import org.cthul.parser.util.*;

public class EarleyXItParser extends EarleyXParserBase {

    private final IntMap<InputPosition> inputPositions = new IntMap<>();
    private LinkedQueue<InputPosition> updatedPositions = new LinkedQueue();

    public EarleyXItParser(Context<?> input, EarleyXGrammar grammar) {
        super(input, grammar);
    }
    
    @Override
    public Iterable<EXMatch> parse(int start, int end, String rule, int priority) {
        InputPosition ip = inputPosition(start);
        ip.initial(rule, priority);
        earley();
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
        
        private final List<EXMatch> matches = new ArrayList<>();
        private int scannedMatches = 0;
        
        private List<EXAntiMatchRule> predictedAntis = null;
        private Set<EXAntiMatchRule.Blocked> matchtedAntis = null;
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
                    EXMatch match = matches.get(j);
                    if (rs.acceptNext(match)) {
                        inputPosition(match.getEnd()).add(rs.next(match));
                    }
                }
            }
            processedStates = states.size();
        }
        
        private void predict(String symbol, int priority) {
            EXRuleSet rs = grammar.getRules(symbol);
            final int size = rs.size();
            if (rs == null) return;
            Integer nextRule = predicted.get(symbol);
            int i = nextRule == null ? 0 : nextRule;
            for (;i < size; i++) {
                EXRule rule = rs.get(i);
                if (rule.getPriority() < priority) break;
                if (rule instanceof EXAntiMatchRule) {
                    if (predictedAntis == null) predictedAntis = new ArrayList<>();
                    predictedAntis.add((EXAntiMatchRule) rule);
                }
                add(new RuleState(rule));
            }
            predicted.put(symbol, i);
        }
        
        private void complete(RuleState rs) {
            EXMatch match = rs.createMatch(index);
            if (match != null) {
                inputPosition(match.getStart()).matched(match);
            }
        }
        
        private void matched(EXMatch match) {
            if (match instanceof EXAntiMatchRule.Blocked) {
                if (matchtedAntis == null) matchtedAntis = new HashSet<>();
                matchtedAntis.add((EXAntiMatchRule.Blocked) match);
                return;
            }
            matches.add(match);
            if (progress > SCANNING) enqueueForUpdate();
        }
        
        private void scan() {
            final int statesSize = processedStates;
            for (int i = scannedMatches; i < matches.size(); i++) {
                EXMatch match = matches.get(i);
                InputPosition nextPosition = inputPosition(match.getEnd());
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
        
        private void evaluateAntiMatches() {
            if (predictedAntis == null) return;
            while (processedAntis < predictedAntis.size()) {
                EXAntiMatchRule antiMatch = predictedAntis.get(processedAntis);
                processedAntis++;
                earley();
                if (matchtedAntis == null || !matchtedAntis.contains(antiMatch)) {
                    matched(antiMatch.antiMatch(input, index));
                }
            }
        }

        @Override
        public String toString() {
            return Format.join("#" + index + " (", ", ", ")", 512, " ...)", states, matches);
        }
        
    }
    
}
