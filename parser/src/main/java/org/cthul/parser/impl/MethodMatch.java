package org.cthul.parser.impl;

import java.lang.annotation.Annotation;
import org.cthul.parser.Token;
import java.lang.reflect.Method;
import org.cthul.parser.Context;
import org.cthul.parser.ExecutionException;
import org.cthul.parser.NoMatchException;
import org.cthul.parser.TokenBuilder;
import org.cthul.parser.Value;
import org.cthul.parser.annotation.Group;
import org.cthul.parser.lexer.AbstractMatch;
import static org.cthul.parser.impl.Executor.*;

/**
 *
 * @author Arian Treffer
 */
public class MethodMatch extends AbstractMatch {

    private final Executor<TokenBuilder> exec;
    
    private final boolean voidResult;

    public MethodMatch(String pattern, Object impl, Method m) {
        super(pattern);
        exec = new Executor<TokenBuilder>(impl, m, MAPPER, pattern);
        voidResult = m.getReturnType() == void.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object eval(final TokenBuilder tokenBuilder, final Context context) throws NoMatchException {
        try {
            final Object result = exec.invoke(context, tokenBuilder);
            return voidResult ? Boolean.TRUE : result;
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof NoMatchException) {
                throw (NoMatchException) cause;
            }
            throw e;
        }
    }

    private static ArgMapFactory<TokenBuilder, String> MAPPER = new ArgMapFactory<TokenBuilder, String>() {
        @Override
        public ArgMap<TokenBuilder> create(Class<?> paramType, Annotation[] annotations) {
            for (Annotation a: annotations) {
                if (a instanceof Group) {
                    if (!paramType.isAssignableFrom(String.class)) {
                        throw new IllegalArgumentException(
                                "@Group annotated parameter must have type "
                                + "String, but was " + paramType.getName());
                    }
                    return GROUP;
                }
            }
            if (paramType.isAssignableFrom(Value.class)) {
                return TOKEN;
            }
            if (paramType.isAssignableFrom(TokenBuilder.class)) {
                return TOKENBUILDER;
            }
            if (paramType.isAssignableFrom(Context.class)) {
                return CONTEXT;
            }
            if (paramType.isAssignableFrom(String.class)) {
                return STRING;
            }
            return TOKEN_VALUE;
        }

        @Override
        public int mapIndex(int i, Annotation[] annotations, String pattern) {
            for (Annotation a: annotations) {
                if (a instanceof Group) {
                    int id = ((Group) a).value();
                    if (id < 0) {
                        int groupC = pattern.replaceAll("[^(]", "").length();
                        id += groupC;
                    }
                    return id;
                }
            }
            return i;
        }
    };
    
    private static final ArgMap<TokenBuilder> STRING = new ArgMap<TokenBuilder>() {
        @Override
        public Object map(int i, Context ctx, TokenBuilder tb) {
            return tb.getMatch();
        }
    };
    
    private static final ArgMap<TokenBuilder> GROUP = new ArgMap<TokenBuilder>() {
        @Override
        public Object map(int i, Context ctx, TokenBuilder tb) {
            return tb.getGroup(i);
        }
    };
    
    private static final ArgMap<TokenBuilder> TOKEN = new ArgMap<TokenBuilder>() {
        @Override
        public Token<?> map(int i, Context ctx, TokenBuilder tb) {
            return tb.newToken();
        }
    };
    
    private static final ArgMap<TokenBuilder> TOKENBUILDER = new ArgMap<TokenBuilder>() {
        @Override
        public Object map(int i, Context ctx, TokenBuilder tb) {
            return tb;
        }
    };
    
    private static final ArgMap<TokenBuilder> TOKEN_VALUE = new ArgMap<TokenBuilder>() {
        @Override
        public Object map(int i, Context ctx, TokenBuilder tb) {
            return tb.newToken().getValue();
        }
    };
    
    private static final ArgMap<TokenBuilder> CONTEXT = new ArgMap<TokenBuilder>() {
        @Override
        public Object map(int i, Context ctx, TokenBuilder tb) {
            return ctx;
        }
    };
     
}
