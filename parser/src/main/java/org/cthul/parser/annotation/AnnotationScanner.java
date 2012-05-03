package org.cthul.parser.annotation;

import java.lang.reflect.*;
import java.util.*;
import org.cthul.parser.*;
import org.cthul.parser.grammar.earley.EarleyGrammar;
import org.cthul.parser.grammar.earley.GrammarFactory;
import org.cthul.parser.grammar.sequence.LeftDeepSequenceFactory;
import org.cthul.parser.impl.*;
import org.cthul.parser.lexer.LexerFactory;
import org.cthul.parser.lexer.RegexLexer;

/**
 *
 * @author Arian Treffer
 */
public class AnnotationScanner {
    
    public static LexerFactory DefaultLexerFactory = RegexLexer.FACTORY;
    public static GrammarFactory DefaultGrammarFactory = EarleyGrammar.FACTORY;
    
    private final LexerFactory lexerFactory;
    private final GrammarFactory grammarFactory;
    
    private final List<PriorityMatch> matches = new LinkedList<>();
    private final List<org.cthul.parser.grammar.earley.Production> productions = new ArrayList<>();
    
    private final LeftDeepSequenceFactory<?, ?> sequenceFactory = new LeftDeepSequenceFactory<>();

    public AnnotationScanner() {
        this(DefaultLexerFactory, DefaultGrammarFactory);
    }

    public AnnotationScanner(LexerFactory lexerFactory, GrammarFactory grammarFactory) {
        this.lexerFactory = lexerFactory;
        this.grammarFactory = grammarFactory;
    }
    
    public void addAll(Iterable<?> collection) {
        for (Object o: collection) {
            add(o);
        }
    }
    
    public void addAll(Object... collection) {
        for (Object o: collection) {
            add(o);
        }
    }
    
    public void add(Object impl) {
        Class<?> clazz = impl instanceof Class ? (Class<?>) impl : impl.getClass();
        scan(impl, clazz);
    }
    
    @SuppressWarnings("unchecked")
    public List<org.cthul.parser.lexer.Match> getMatches() {
        return Collections.unmodifiableList((List)matches);
    }
    
    public Lexer createLexer() {
        return lexerFactory.create(matches);
    }
    
    public List<org.cthul.parser.grammar.earley.Production> getProductions() {
        return Collections.unmodifiableList(productions);
    }
    
    public Grammar createGrammar() {
        return grammarFactory.create(productions);
    }
    
    public <R> Parser<R> createParser() {
        return new Parser<>(createLexer(), createGrammar());
    }
    
    private void scan(Object impl, Class<?> clazz) {
        if (clazz == null) return;
        // methods from superclass have priority
        scan(impl, clazz.getSuperclass());
        for (Method m: clazz.getDeclaredMethods()) {
            if ((m.getModifiers() & Modifier.PUBLIC) != 0) {
                Match match = m.getAnnotation(Match.class);
                if (match != null) {
                    addMatch(impl, m, match);
                }
                Production production = m.getAnnotation(Production.class);
                if (production != null) {
                    addProduction(impl, m, production);
                }
                Sequence sequence = m.getAnnotation(Sequence.class);
                if (sequence != null) {
                    addSequence(impl, m, sequence);
                }
                Sequences sequences = m.getAnnotation(Sequences.class);
                if (sequences != null) {
                    for (Sequence s: sequences.value()) {
                        addSequence(impl, m, s);
                    }
                }
            }
        }
    }

    private void addMatch(Object impl, Method m, Match match) {
        Key name = m.getAnnotation(Key.class);
        for (String s : match.value()) {
            String key = selectName(name, m, s, false);
            String pattern = selectRule(s);
            int channel = selectChannel(m);
            int priority = selectPriority(m);
            TokenFactory<?, ?> f = selectFactory(m);
            PriorityMethodMatch pMatch = new PriorityMethodMatch(
                    pattern, impl, m, priority, key, channel, f);
            insertMatch(pMatch);
        }
    }

    private void insertMatch(PriorityMatch match) {
        final int p = match.getPriority();
        final ListIterator<PriorityMatch> it = matches.listIterator();
        while (it.hasNext()) {
            if (it.next().getPriority() < p) {
                it.previous();
                it.add(match);
                return;
            }
        }
        it.add(match);
    }
    
    private void addProduction(Object impl, Method m, Production production) {
        Key name = m.getAnnotation(Key.class);
        for (String s : production.value()) {
            String left = selectName(name, m, s, true);
            String right = selectRule(s);
            int priority = selectPriority(m);
            org.cthul.parser.grammar.earley.Production p;
            if (noEvaluation(m)) {
                p = new DirectProduction(left, priority, right);
            } else {
                p = new MethodProduction(left, priority, right, impl, m);
            }
            productions.add(p);
        }
    }
    
    private void addSequence(Object impl, Method m, Sequence sequence) {
        String name; 
        if (sequence.name().isEmpty()) {
            Key n = m.getAnnotation(Key.class);
            name = selectName(n, m, "", true);
        } else {
            name = sequence.name();
        }
        int priority = selectPriority(m);
        sequenceFactory.setConfiguration(sequence);
        sequenceFactory.setPriority(priority);
        org.cthul.parser.grammar.earley.Production p;
        if (noEvaluation(m)) {
            sequenceFactory.setName(name);
        } else {
            String seqName = name + "_SEQUENCE";
            sequenceFactory.setName(seqName);
            productions.add(new MethodProduction(name, priority, seqName, impl, m));
        }
        sequenceFactory.addTo(productions);
    }
    
    private String selectName(Key name, Method m, String s, boolean isProduction) {
        int i = s.indexOf(" ::=");
        if (i < 0) {
            if (name != null) {
                String n = name.value();
                return n.isEmpty() ? m.getName() : n;
            } else {
                return isProduction ? m.getName() : null;
            }
        } else {
            return s.substring(0, i).trim();
        }
    }
    
    private String selectRule(String s) {
        int i = s.indexOf(" ::=");
        if (i < 0) {
            return s.trim();
        } else {
            return s.substring(i + " ::=".length()).trim();
        }
    }

    private int selectChannel(Method m) {
        Channel channel = m.getAnnotation(Channel.class);
        if (channel == null) {
            return Channel.Undefined;
        } else {
            return channel.value();
        }
    }

    private int selectPriority(Method m) {
        Priority priority = m.getAnnotation(Priority.class);
        if (priority == null) {
            return Priority.Default;
        } else {
            return priority.value();
        }
    }

    private TokenFactory<?, ?> selectFactory(Method m) {
        TokenClass tokenClass = m.getAnnotation(TokenClass.class);
        if (tokenClass == null) return null;
        Class<? extends Token<?>> clazz = tokenClass.value();
        try {
            Field f = clazz.getField(tokenClass.factoryField());
            if ((f.getModifiers() & Modifier.PUBLIC) == 0) {
                throw new IllegalArgumentException(
                        tokenClass.factoryField() + " is not public");
            }
            if ((f.getModifiers() & Modifier.STATIC) == 0) {
                throw new IllegalArgumentException(
                        tokenClass.factoryField() + " is not static");
            }
            return (TokenFactory<?, ?>) f.get(null);
        } catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(
                    "Invalid annotation of " + m + ":" +
                    clazz + " has no field " + tokenClass.factoryField());
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(
                    "Error accessing " + tokenClass.factoryField(), e);
        }
    }
    
    private boolean noEvaluation(Method m) {
        return m.getAnnotation(Redirect.class) != null;
    }
    
    private static interface PriorityMatch extends org.cthul.parser.lexer.Match {
        int getPriority();
    }
    
    private static class PriorityMethodMatch extends MethodMatch implements PriorityMatch {
        private final int priority;
        private final String defaultKey;
        private final int defaultChannel;
        private final TokenFactory<?, ?> defaultFactory;

        public PriorityMethodMatch(String pattern, Object impl, Method m, int priority, String defaultKey, int defaultChannel, TokenFactory<?, ?> factory) {
            super(pattern, impl, m);
            this.priority = priority;
            this.defaultKey = defaultKey;
            this.defaultChannel = defaultChannel;
            this.defaultFactory = factory;
        }

        @Override
        protected String defaultKey() {
            return defaultKey;
        }

        @Override
        protected int defaultChannel() {
            return defaultChannel;
        }

        @Override
        protected TokenFactory<?, ?> defaultFactory() {
            return defaultFactory;
        }
        
        @Override
        public int getPriority() {
            return priority;
        }
    }
}
