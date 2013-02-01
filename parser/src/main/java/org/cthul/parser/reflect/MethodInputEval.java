package org.cthul.parser.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import org.cthul.parser.annotation.Group;
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
        public ArgMap<? super A, ? super String> create(Class<?> paramType, Annotation[] annotations, String pattern, boolean isVarArgs) {
            for (Annotation a: annotations) {
                if (a instanceof Group) {
                    if (!paramType.isAssignableFrom(String.class)) {
                        throw new IllegalArgumentException(
                                "@Group annotated parameter must have type "
                                + "String, but was " + paramType.getName());
                    }
                    return GROUP_ARG;
                }
            }
            if (String.class.equals(paramType)) {
                return STRING_ARG;
            }
            if (MatchResult.class.equals(paramType)) {
                return MATCHRESULT_ARG;
            }
            return otherParameterType(paramType, annotations, pattern, isVarArgs);
        }
        
        protected ArgMap<? super A, ? super String> otherParameterType(Class<?> paramType, Annotation[] annotations, String pattern, boolean isVarArgs) {
            return MethodExecutor.DEFAULT_MAPPER.create(paramType, annotations, this, isVarArgs);
        }
    }
    
    public static final ArgMap<MatchResult, Object> STRING_ARG = new DirectIndexArgMap<MatchResult>() {
        @Override
        public Object map(int i, InstanceMap ctx, MatchResult mr, Object par3) {
            return mr.group();
        }
    };
    
    public static final ArgMap<MatchResult, String> GROUP_ARG = new ArgMap<MatchResult, String>() {
        @Override
        public Object map(int i, InstanceMap ctx, MatchResult mr, Object par3) {
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
        public Object map(int i, InstanceMap ctx, MatchResult mr, Object par3) {
            return mr;
        }
    };
    
}
