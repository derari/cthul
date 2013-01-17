package org.cthul.parser.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.cthul.parser.annotation.Group;
import org.cthul.parser.api.Context;
import org.cthul.parser.reflect.MethodExecutor.ArgMap;
import org.cthul.parser.reflect.MethodExecutor.ArgMapFactory;
import org.cthul.parser.reflect.MethodExecutor.DirectIndexArgMap;
import org.cthul.parser.util.Inject;
import org.cthul.parser.util.InstanceMap;

public class MethodInputEval<Token> extends AbstractMethodInputEval<Token, MatchResult> {
    
    public MethodInputEval(Object impl, Method m, String pattern) {
        super(impl, m, MAPPER, pattern);
    }

    public static final EvalArgMapFactory<MatchResult> MAPPER = new EvalArgMapFactory<>();

    public static class EvalArgMapFactory<A extends MatchResult> implements ArgMapFactory<A, String> {

        @Override
        public ArgMap<? super A, ? super String> create(Class<?> paramType, Annotation[] annotations, String pattern) {
            for (Annotation a: annotations) {
                if (a instanceof Group) {
                    if (!paramType.isAssignableFrom(String.class)) {
                        throw new IllegalArgumentException(
                                "@Group annotated parameter must have type "
                                + "String, but was " + paramType.getName());
                    }
                    return GROUP_ARG;
                }
                if (a instanceof Inject) {
                    return injectArg(paramType, (Inject) a);
                }
            }
            if (InstanceMap.class.isAssignableFrom(paramType)) {
                return CONTEXT_ARG;
            }
            if (String.class.equals(paramType)) {
                return STRING_ARG;
            }
            if (MatchResult.class.equals(paramType)) {
                return MATCHRESULT_ARG;
            }
            return otherParameterType(paramType, annotations, pattern);
        }
        
        protected ArgMap<? super A, ? super String> otherParameterType(Class<?> paramType, Annotation[] annotations, String pattern) {
            return null;
        }
    }
    
    public static final ArgMap<MatchResult, Object> STRING_ARG = new DirectIndexArgMap<MatchResult>() {
        @Override
        public Object map(int i, Context<?> ctx, MatchResult mr) {
            return mr.group();
        }
    };
    
    public static final ArgMap<MatchResult, String> GROUP_ARG = new ArgMap<MatchResult, String>() {
        @Override
        public Object map(int i, Context<?> ctx, MatchResult mr) {
            return mr.group(i);
        }
        @Override
        public int mapIndex(int i, Annotation[] annotations, String pattern) {
            for (Annotation a: annotations) {
                if (a instanceof Group) {
                    int id = ((Group) a).value();
                    if (id < 0) {
                        int groupC = Pattern.compile(pattern).matcher("").groupCount();
                        id += groupC;
                    }
                    return id;
                }
            }
            return i;
        }
    };
    
    public static final ArgMap<MatchResult, Object> MATCHRESULT_ARG = new DirectIndexArgMap<MatchResult>() {
        @Override
        public Object map(int i, Context ctx, MatchResult mr) {
            return mr;
        }
    };
    
    public static final ArgMap<Object, Object> CONTEXT_ARG = new DirectIndexArgMap<Object>() {
        @Override
        public Object map(int i, Context ctx, Object o) {
            return ctx;
        }
    };
    
    public static ArgMap<Object, Object> injectArg(final Class<?> clazz, final Inject inject) {
        return new DirectIndexArgMap<Object>(){
            @Override
            public Object map(int i, Context<?> ctx, Object arg) {
                return ctx.get(clazz, inject);
            }
        };
    }
}
