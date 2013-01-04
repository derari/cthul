package org.cthul.parser.packratx;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.cthul.parser.api.Input;
import org.cthul.parser.api.StringInput;
import org.cthul.parser.util.IntMap;

public class EarleyXParser {

    private final Input<?> input;
    private final EarleyXGrammar grammar;
    private final IntMap<InputPosition> inputPositions = new IntMap<>();

    public EarleyXParser(Input<?> input, EarleyXGrammar grammar) {
        this.input = input;
        this.grammar = grammar;
    }
    
    public EarleyXParser(String input, EarleyXGrammar grammar) {
        this.input = new StringInput(input);
        this.grammar = grammar;
    }
    
    public Iterable<EXMatch> parse(String startSymbol) {
        return parse(0, startSymbol, 0);
    }
    
    Iterable<EXMatch> parse(int index, String production, int priority) {
        InputPosition ip = inputPositions.get(index);
        if (ip == null) {
            ip = new InputPosition(index);
            inputPositions.put(index, ip);
        }
        return ip.iterable(production, priority);
    }
    
    private class InputPosition {
        
        private final int index;
        private final Map<String, MatchesForProduction> productionMatches = new HashMap<>();

        public InputPosition(int index) {
            this.index = index;
        }
        
        MatchesIterable iterable(String production, int priority) {
            MatchesForProduction matches = productionMatches.get(production);
            if (matches == null) {
                matches = new MatchesForProduction(index, production);
                productionMatches.put(production, matches);
            }
            return matches.iterable(priority);
        }
    }
    
    private class MatchesForProduction {
        
        private final int index;
        private final String production;
        private final IntMap<MatchesIterable> iterables = new IntMap<>();
        private EXMatch first = null;

        public MatchesForProduction(int index, String production) {
            this.index = index;
            this.production = production;
        }
        
        MatchesIterable iterable(int priority) {
            MatchesIterable it = iterables.get(priority);
            if (it == null) {
                it = new MatchesIterable(this, priority);
                iterables.put(priority, it);
            }
            return it;
        }
        
        MatchIterator iterator(int priority) {
            return new MatchIterator(this, priority);
        }
        
        EXMatch first() {
            if (first == null) first = findNext();
            return first;
        }
        
        EXMatch next(EXMatch m) {
            EXMatch next = m.next;
            if (next != null) return next;
            m.next = findNext();
            return m.next;
        }

        private EXMatch findNext() {
            return null;
        }
    }
    
    private static class MatchesIterable implements Iterable<EXMatch> {
        
        private final MatchesForProduction matches;
        private final int priority;

        public MatchesIterable(MatchesForProduction matches, int priority) {
            this.matches = matches;
            this.priority = priority;
        }

        @Override
        public Iterator<EXMatch> iterator() {
            return matches.iterator(priority);
        }
    }
    
    private static class MatchIterator implements Iterator<EXMatch> {

        private final MatchesForProduction matches;
        private final int priority;
        private EXMatch next;

        public MatchIterator(MatchesForProduction matches, int priority) {
            this.matches = matches;
            this.priority = priority;
            findNext(matches.first());
        }
        
        private void findNext(EXMatch m) {
            while (m != null && m.priority < priority) {
                m = matches.next(m);
            }
            next = m;
        }
        
        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public EXMatch next() {
            EXMatch m = next;
            findNext(m);
            return m;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
    
}
