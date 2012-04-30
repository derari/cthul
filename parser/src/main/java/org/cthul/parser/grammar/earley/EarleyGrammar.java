package org.cthul.parser.grammar.earley;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.cthul.parser.Context;
import org.cthul.parser.Grammar;
import org.cthul.parser.NoMatchException;
import org.cthul.parser.Token;
import org.cthul.parser.TokenStream;
import org.cthul.parser.Value;

/**
 *
 * @author Arian Treffer
 */
public final class EarleyGrammar implements Grammar {
    
    public static final GrammarFactory FACTORY = new AbstractGrammarFactory() {
        @Override
        public Grammar create(Iterable<? extends Production> rules) {
            return new EarleyGrammar(rules).asSingleResultGrammar();
        }
    };
    
    private final Map<String, List<EarleyProduction>> productionsByName;

    @SuppressWarnings("LeakingThisInConstructor")
    public EarleyGrammar(Iterable<? extends Production> rules) {
        productionsByName = new HashMap<>();
        for (Production p: rules) {
            productions(p.getLeft()).add(new EarleyProduction(p));
        }
        for (List<EarleyProduction> pp: productionsByName.values()) {
            for (EarleyProduction p: pp) {
                p.finish(this);
            }
        }
    }
    
    private List<EarleyProduction> productions(String name) {
        List<EarleyProduction> result = productionsByName.get(name);
        if (result == null) {
            result = new ArrayList<>();
            productionsByName.put(name, result);
        }
        return result;
    }
    
    private boolean isProduction(String name) {
        List<EarleyProduction> result = productionsByName.get(name);
        return result != null && result.size() > 0;
    }

    @Override
    public ResultIterator<?> parse(String startSymbol, TokenStream tokenStream, Context context) {
        try {
            StateSet result = earley(startSymbol, tokenStream, context);
            return new FinishResult<>(result, startSymbol, tokenStream, context);
        } catch (NoMatchException ex) {
            return new ErrorResult<>(ex);
        }
    }
    
    public Grammar asSingleResultGrammar() {
        return new SingleResultEarleyGrammar(this);
    }
    
    private StateSet earley(final String startSymbol, final TokenStream ts, final Context context) throws NoMatchException {
        final int length = ts.getLength()+1;
        final StateSet[] sets = new StateSet[length];
        StateSet current = initial(startSymbol, context);
        sets[0] = current;
        complete(current, sets);
        predict(current, sets, context);
        ts.start();
        for (int i = 1; i < length; i++) {
            current = scan(ts.next(), current);
            if (current.isEmpty()) {
                throw noMatch(ts, sets[i-1], startSymbol, ts.previous(), null);
            }
            sets[i] = current;
            complete(current, sets);
            predict(current, sets, context);
        }
        return current;
    }
    
    private StateSet initial(final String startSymbol,
                             final Context context) throws NoMatchException {
        final StateSet initial = new StateSet(0);
        for (EarleyProduction p: productions(startSymbol)) {
            initial.add(new State(p, 0, context));
        }
        if (initial.isEmpty()) {
            throw new NoMatchException("Unknown start symbol '%s'", startSymbol);
        }
        return initial;
    }

    /**
     * Complete all states that are origins of states in {@code currentSet}
     * @param currentSet
     * @param sets 
     */
    private void complete(final StateSet currentSet, final StateSet[] sets) {
        // states that are inserted to currentSet are completed immediately,
        // here we have to complete only the states that were already there
        final int len = currentSet.states.size();
        for (int i = 0; i < len; i++) {
            State s = currentSet.states.get(i);
            if (s.complete()) {
                complete(s, currentSet, sets);
            }
        }
    }

    /**
     * Match all states that are origins of {@code state}
     * @param state
     * @param currentSet {@code state}'s state set
     * @param sets 
     */
    private void complete(final State state, final StateSet currentSet, 
                          final StateSet[] sets) {
        final StateSet originSet = sets[state.origin];
        final int len = originSet.states.size();
        for (int i = 0; i < len; i++) {
            State originState = originSet.states.get(i);
            if (originState.acceptMatch(state)) {
                State next = originState.matchNext(state);
                addAndTryComplete(next, currentSet, sets);
            }
        }
    }
    
    /**
     * Adds a state to the current set. To be used when some states of the
     * set are already completed, as there might occurs some special cases
     * with epsilon productions.
     * * a predicted state represents an epsilon production
     * 
     * @param state
     * @param currentSet
     * @param sets 
     */
    private void addAndTryComplete(final State state, final StateSet currentSet, 
                          final StateSet[] sets) {
        currentSet.add(state);
        if (state.complete()) {
            if (state.isEpsilon()) {
                currentSet.epsilons.add(state);
            }
            complete(state, currentSet, sets);
        } else {
            completeEpsilon(state, currentSet, sets);
        }
    }

    /**
     * Checks matching with all epsilons that were already completede
     * by the time of adding {@code state} to the current set.
     */
    private void completeEpsilon(final State state, final StateSet currentSet, 
                          final StateSet[] sets) {
        final int len = currentSet.epsilons.size();
        for (int i = 0; i < len; i++) {
            State e = currentSet.epsilons.get(i);
            if (state.acceptMatch(e)) {
                State next = state.matchNext(e);
                addAndTryComplete(next, currentSet, sets);
            }
        }
    }
    
    private void predict(final StateSet current, final StateSet[] sets, final Context context) {
        for (int i = 0; i < current.states.size(); i++) {
            final State state = current.states.get(i);
            if (state.nextIsProduction()) {
                for (EarleyProduction p: productions(state.nextSymbol())) {
                    if (state.acceptPredicted(p)) {
                        predict(p, current, sets, context);
                    }
                }
            }
        }
    }

    private void predict(final EarleyProduction p, final StateSet current, 
                         final StateSet[] sets, final Context context) {
        final State predicted = new State(p, current.position, context);
        if (!current.contains(predicted)) {
            addAndTryComplete(predicted, current, sets);
        }
    }
    
    private StateSet scan(final Token<?> token, final StateSet current) {
        final StateSet next = current.next();
        for (State state: current.states) {
            if (state.acceptToken(token)) {
                next.add(state.matchNext(token));
            }
        }
        return next;
    }
    
    /**
     * Searches final state set for states matching the start symbol.
     * @param <R> 
     */
    private static class FinishResult<R> extends AbstractResultIterator<R> {
        
        private final StateSet resultSet;
        private final Iterator<State> stateIterator;
        private final String startSymbol;
        private final TokenStream tokenStream;
        private final Context context;
        
        private boolean foundResult = false;

        public FinishResult(StateSet resultSet, String startSymbol, 
                            TokenStream tokenStream, Context context) {
            this.resultSet = resultSet;
            this.stateIterator = resultSet.states.iterator();
            this.startSymbol = startSymbol;
            this.tokenStream = tokenStream;
            this.context = context;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void fetchNext() throws NoMatchException {
            while (stateIterator.hasNext()) {
                State state = stateIterator.next();
                if (state.origin == 0 
                        && state.complete()
                        && state.match(startSymbol)) {
                    next = (R) state.eval();
                    foundResult = true;
                    hasNext = true;
                    return;
                }
            }
            if (!foundResult) {
                throw noMatch(tokenStream, resultSet, startSymbol, 
                                  tokenStream.last(), null);
            }
            hasNext = false;
        }
        
    }
    
    private static class ErrorResult<R> extends AbstractResultIterator<R> {

        private final NoMatchException ex;

        public ErrorResult(NoMatchException ex) {
            this.ex = ex;
        }

        @Override
        protected void fetchNext() throws NoMatchException {
            throw ex;
        }
        
    }
    
    private static NoMatchException noMatch(TokenStream tokenStream, 
                            StateSet current, String start, Token<?> token, 
                            List<NoMatchException> errors) {
        List<String> expected = getExpected(current, start);
        return NoMatchException.productionFailed(
                tokenStream, token, errors, expected);
    }
    
    private static List<String> getExpected(StateSet stateSet, String startSymbol) {
        List<String> result = new ArrayList<>();
        boolean eoi = false; // one of the states represented a valid input
        for (State s: stateSet.states) {
            if (!s.complete() && !s.nextIsProduction()) {
                // s expected a token
                result.add(s.nextSymbol());
            }
            eoi |= s.complete() && s.match(startSymbol);
        }
        if (eoi) result.add("<EOI>");
        return result;
    }
    
    /**
     * The set of all possible interpretations of the input so far.
     */
    private static class StateSet {
        
        /** position in token stream */
        final int position;
        
        final List<State> states = new ArrayList<>();
        
        final List<State> epsilons = new ArrayList<>();

        StateSet(int position) {
            this.position = position;
        }

        boolean contains(State state) {
            for (State o: states) {
                if (o.equals(state)) {
                    return true;
                }
            }
            return false;
        }
        
        void add(State state) {
            assert !contains(state);
            states.add(state);
        }

        StateSet next() {
            return new StateSet(position+1);
        }

        boolean isEmpty() {
           return states.isEmpty();
        }

        @Override
        public String toString() {
            return position + ": " + states.toString();
        }
        
    }
    
    /**
     * A partially processed production. Tries to complete the next symbol.
     */
    private static final class State implements Value<Object> {

        /** the production in question */
		private final EarleyProduction production;
        
        /** position in production; 0 == before first, production.size == after last */
		private final int position;
        
        /** position of the production in the token stream */
		private final int origin;
        
        private int end = -1;

        /** {@code null} if before first */
        private final State previous;
        
        /** represents the symbol last matched, {@code null} if before first */
        private final Value<?> arg;
        
        /** the evaluation context */
        private final Context context;
        
//        /** arg as {@link Value} */
//        private Value<?> argValue = null;

        /**
         * Creates a predicted state.
         */
        State(EarleyProduction production, int origin, Context context) {
            this(production, 0, origin, null, null, context);
        }

        private State(EarleyProduction production, int position, int origin, State previous, Value<?> arg, Context context) {
            this.production = production;
            this.position = position;
            this.origin = origin;
            this.previous = previous;
            this.arg = arg;
            this.context = context;
        }
        
        boolean complete() {
            return position == production.size;
        }
        
        boolean acceptMatch(State state) {
            if (complete()) return false;
            assert state.complete();
            return state.match(nextSymbol()) && 
                   state.hasPriority(nextSymbolPriority());
        }
        
        State matchNext(State state) {
            assert !complete();
            return new State(production, position+1, origin, this, state, context);
        }
        
        boolean acceptToken(Token<?> token) {
            if (complete()) return false;
            return token.match(nextSymbol());
        }
        
        State matchNext(Token<?> token) {
            assert !complete();
            return new State(production, position+1, origin, this, token, context);
        }
        
        boolean nextIsProduction() {
            return !complete() && nextSymbolPriority() > -1;
        }
        
        boolean acceptPredicted(EarleyProduction p) {
            assert p.key.equals(nextSymbol());
            return p.priority >= nextSymbolPriority();
        }
        
        private int getPriority() {
            return production.priority;
        }
        
        private String nextSymbol() {
            assert !complete();
            return production.symbols[position];
        }
        
        private int nextSymbolPriority() {
            assert !complete();
            return production.priorities[position];
        }
        
        private boolean isEpsilon() {
            return production.size == 0;
        }

        private boolean match(String key) {
            return production.match(key);
        }
        
        private boolean hasPriority(int priority) {
            return getPriority() >= priority;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof State)) return false;
            return equals((State) obj);
        }
        
        public boolean equals(State other) {
            if (this.production != other.production) {
                return false;
            }
            if (this.position != other.position) {
                return false;
            }
            if (this.origin != other.origin) {
                return false;
            }
            if (this.previous != other.previous) {
                return false;
            }
            if (this.arg != other.arg) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 83 * hash + (this.production != null ? this.production.hashCode() : 0);
            hash = 83 * hash + this.position;
            hash = 83 * hash + this.origin;
            hash = 83 * hash + (this.arg != null ? this.arg.hashCode() : 0);
            return hash;
        }
        
        private Object eval() {
            final Value<?>[] args = new Value<?>[production.size];
            fillArgs(args);
            return production.invoke(context, args);
        }
        
        private void fillArgs(final Value<?>[] args) {
            if (previous != null) {
                previous.fillArgs(args);
                args[position-1] = arg; //argValue(ctx);
            }
        }
        
//        private Value<?> argValue(Context ctx) {
//            if (argValue == null) {
//                if (arg instanceof Token<?>) {
//                    argValue = (Value<?>) arg;
//                } else if (arg instanceof State) {
//                    argValue = new ProductionValue((State) arg, ctx);
//                } else {
//                    assert arg == null;
//                    throw new AssertionError(
//                            "Requested argument of empty state.");
//                }
//            }
//            return argValue;
//        }
        
        @Override
        public Object getValue() {
            return eval();
        }
        
        @Override
        public int getStart() {
            return origin;
        }
        
        @Override
        public int getEnd() {
            if (end < 0) {
                if (arg != null) {
                    end = arg.getEnd();
                } else {
                    end = getStart();
                }
            }
            return end;
        }
        
        @Override
		public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append(origin).append(": ");
            sb.append('(').append(getPriority()).append(')');
            sb.append(production.key).append(" ::=");
			for (int i = 0; i < production.size; i++) {
				if (i == position) sb.append(" • ");
				else sb.append(' ');
                sb.append('(').append(production.priorities[i]).append(')');
				sb.append(production.symbols[i]);
			}
			if (complete()) sb.append(" •");
			return sb.toString();
		}

	}
    
//    private static final class ProductionValue implements Value<Object> {
//        
//        private final State state;
//        private final Context context;
//
//        public ProductionValue(State state, Context context) {
//            this.state = state;
//            this.context = context;
//        }
//
//        @Override
//        public Object getValue() throws NoMatchException {
//            return state.eval(context);
//        }
//
//        @Override
//        public int getStart() {
//            return state.getStart();
//        }
//
//        @Override
//        public int getEnd() {
//            return state.getEnd();
//        }
//        
//    }
    
    private static final class EarleyProduction {
        
        final Production production;
        final String key;
        final int priority;
        final int size;
        final String[] symbols;
        final int[] priorities;

        EarleyProduction(Production production) {
            this.production = production;
            key = production.getLeft();
            priority = production.getPriority();
            size = production.getRightSize();
            symbols = new String[size];
            priorities = new int[size];
            for (int i = 0; i < size; i++) {
                symbols[i] = production.getRight(i);
                priorities[i] = production.getRightPriority(i);
            }
        }
        
        void finish(EarleyGrammar earley) {
            for (int i = 0; i < size; i++) {
                if (!earley.isProduction(symbols[i])) {
                    priorities[i] = -1;
                }
            }
        }

        boolean match(String symbol) {
            return key.equals(symbol);
        }
        
        public Object invoke(Context context, Value[] args) {
            return production.invoke(context, args);
        }

        @Override
        public String toString() {
            return production.toString();
        }
        
    }
    
}
