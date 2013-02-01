package org.cthul.parser.grammar.earleyx.algorithm;

import java.util.*;
import org.cthul.parser.api.Match;
import org.cthul.parser.api.RuleKey;
import org.cthul.parser.grammar.ParseException;
import org.cthul.parser.grammar.earleyx.algorithm.EarleyXParser.RuleState;

public class EXParseException extends ParseException {

    public EXParseException(Collection<?> actual, Collection<?> expected, String input, int position, String message, Throwable cause) {
        super(actual, expected, input, position, message, cause);
    }
    
    public static EXParseException forStates(Collection<? extends RuleState> states, Collection<? extends Match<?>> matches, Object actual, String input, int position, String message, Throwable cause) {
        Collection<?> expected = filterExpected(states, matches);
        Collection<?> actuals = filterActual(actual, matches);
        return new EXParseException(actuals, expected, input, position, message, cause);
    }
    
    protected static Collection<?> filterExpected(Collection<? extends RuleState> states, Collection<? extends Match<?>> matches) {
        final Map<String, Integer> expectedKeys = new HashMap<>();
        final Map<String, Integer> foundKeys = new HashMap<>();
        for (RuleState rs: states) {
            if (rs.ruleIndex == 0) {
                found(foundKeys, rs.rule.getKey());
            }
            if (!rs.completed()) {
                expected(expectedKeys, rs.nextSymbol(), rs.nextPriority());
            }
        }
        for (Match<?> m: matches) {
            found(foundKeys, m.getKey());
        }
        return combine(expectedKeys, foundKeys);
    }
    
    protected static void expected(Map<String, Integer> expectedKeys, String sym, int prio) {
        Integer oldPrio = expectedKeys.get(sym);
        if (oldPrio == null || oldPrio > prio) {
            expectedKeys.put(sym, prio);
        }
    }

    protected static void found(Map<String, Integer> foundKeys, RuleKey key) {
        String sym = key.getSymbol();
        int newPrio = key.getPriority();
        Integer prio = foundKeys.get(sym);
        if (prio == null || prio < newPrio) {
            foundKeys.put(sym, newPrio);
        }
    }
    
    protected static Collection<RuleKey> combine(Map<String, Integer> expectedKeys, Map<String, Integer> foundKeys) {
        final SortedSet<RuleKey> keys = new TreeSet<>();
        for (Map.Entry<String, Integer> expected: expectedKeys.entrySet()) {
            String sym = expected.getKey();
            int prio = expected.getValue();
            Integer found = foundKeys.get(sym);
            if (found == null || found < prio) {
                keys.add(new RuleKey(sym, prio));
            }
        }
        return keys;
    }
    
    protected static Collection<?> filterActual(Object actual, Collection<? extends Match<?>> matches) {
        if (matches.isEmpty()) {
            return Arrays.asList(actual);
        }
        final SortedSet<Match<?>> keys = new TreeSet<>(MATCH_COMP);
        keys.addAll(matches);
        if (actual == null) {
            return keys;
        } else {
            List<Object> list = new ArrayList<>();
            list.add(actual);
            list.addAll(keys);
            return list;
        }
    }
    
    protected static final Comparator<Match<?>> MATCH_COMP = new Comparator<Match<?>>() {
        @Override
        public int compare(Match<?> o1, Match<?> o2) {
            return o1.getKey().compareTo(o2.getKey());
        }
    };
}
