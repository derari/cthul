package org.cthul.parser.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.cthul.parser.annotation.Argument;
import org.cthul.parser.annotation.Step;
import org.cthul.parser.api.*;
import org.cthul.parser.grammar.api.ProductionMatch;
import org.cthul.parser.reflect.MethodExecutor.ArgMap;
import org.cthul.parser.reflect.MethodExecutor.ArgMapFactory;
import org.cthul.parser.reflect.MethodExecutor.DirectIndexArgMap;
import org.cthul.parser.util.InstanceMap;

public class MethodRuleEval extends AbstractMethodRuleEval {

    public MethodRuleEval(MethodExecutor<? super ProductionMatch> exec) {
        super(exec);
    }

    public MethodRuleEval(Object impl, Method m, List<RuleKey> keys) {
        super(impl, m, MAPPER, new Hint(keys));
    }
    
    public static final EvalArgMapFactory MAPPER = new EvalArgMapFactory();

    public static class EvalArgMapFactory implements ArgMapFactory<ProductionMatch, Hint> {

        @Override
        public ArgMap<? super ProductionMatch, ? super Hint> create(Class<?> paramType, Annotation[] annotations, Hint hint, boolean isVarArgs) {
            for (Annotation at: annotations) {
                if (at instanceof Argument) {
                    return ARGUMENT_ARG;
                } else if (at instanceof Step) {
                    Step s = (Step) at;
                    if (s.all()) {
                        if (isList(paramType)) {
                            return MULTI_STEP_VALUES_LIST;
                        } else if (isMatches(paramType)) {
                            return MULTI_STEP_MATCHES;
                        } else {
                            return MULTI_STEP_VALUES(paramType);
                        }
                    } else if (s.allMatches()) {
                        if (isList(paramType)) {
                            return MULTI_STEP_MATCHES_LIST;
                        } else {
                            return MULTI_STEP_MATCHES;
                        }
                    }
                }
            }
            if (Match[].class.isAssignableFrom(paramType)) {
                return MATCHES_ARG;
            }
            
            ArgMap<Object, Object> a = MethodExecutor.DEFAULT_MAPPER.create(paramType, annotations, null, isVarArgs);
            if (a != null) {
                if (a == MethodExecutor.VOID_ARG) {
                   return SINGLE_STEP_VOID;
                }
                return a;
            }
            
            if (Value.class.isAssignableFrom(paramType)) {
                if (isVarArgs) {
                    return MULTI_STEP_MATCHES;
                } else {
                    return SINGLE_STEP_MATCH;
                }
            } else {
                if (isVarArgs) {
                    return MULTI_STEP_VALUES(paramType);
                } else {
                    return SINGLE_STEP_VALUE;
                }
            }
        }
        
        protected boolean isList(Class<?> paramType) {
            return paramType != Object.class && paramType.isAssignableFrom(ArrayList.class);
        }
        
        protected boolean isMatches(Class<?> paramType) {
            return Value[].class.isAssignableFrom(paramType);
        }
    }
    
    public static abstract class SelectStepArg implements ArgMap<ProductionMatch, Hint> {
        
        @Override
        public int mapIndex(int i, Annotation[] annotations, Hint hint) {
            Step step = null;
            for (Annotation at: annotations) {
                if (at instanceof Step) step = (Step) at;
            }
            if (step == null) return ++hint.index;
            String sym = step.value();
            i = step.i();
            if (i < 0) i = hint.getLastIndex(sym)+1;
            hint.setLastIndex(sym, i);
            int keyIndex = 0;
            for (RuleKey key: hint.getKeys()) {
                if (sym.isEmpty() || sym.equals(key.getSymbol())) {
                    if (i == 0) {
                        return keyIndex;
                    }
                    i--;
                }
                keyIndex++;
            }
            throw new IllegalArgumentException(
                    "Cannot find step " + sym + "#" + 
                    hint.getLastIndex(sym));
        }
    }
    
    public static final SelectStepArg SINGLE_STEP_VALUE = new SelectStepArg() {
        @Override
        public Object map(int i, InstanceMap ctx, ProductionMatch arg, Object arg2) {
            return arg.matches()[i].eval();
        }
    };
    
    public static final SelectStepArg SINGLE_STEP_MATCH = new SelectStepArg() {
        @Override
        public Object map(int i, InstanceMap ctx, ProductionMatch arg, Object arg2) {
            return arg.matches()[i];
        }
    };
    
    public static final SelectStepArg SINGLE_STEP_VOID = new SelectStepArg() {
        @Override
        public Object map(int i, InstanceMap ctx, ProductionMatch arg, Object arg2) {
            return null;
        }
    };
    
    public static final SelectStepArg MULTI_STEP_VALUES_LIST = new SelectStepArg() {
        @Override
        public Object map(int i, InstanceMap ctx, ProductionMatch match, Object arg) {
            Match<?>[] matches = match.matches();
            final List<Object> result = new ArrayList<>(matches.length);
            for (Match<?> m: matches) result.add(m.eval(arg));
            return result;
        }
    };
    
    private static final ConcurrentMap<Class<?>, SelectStepArg> MULTI_STEP_VALUES = new ConcurrentHashMap<>();
    
    public static SelectStepArg MULTI_STEP_VALUES(Class<?> arrayType) {
        SelectStepArg arg = MULTI_STEP_VALUES.get(arrayType);
        if (arg == null) {
            final Class<?> compType = arrayType.getComponentType();
            if (compType == null) {
                throw new IllegalArgumentException("Expected array type, got: " + arrayType);
            }
            arg = new SelectStepArg() {
                @Override
                public Object map(final int i, InstanceMap ctx, ProductionMatch match, Object arg) {
                    Match<?>[] matches = match.matches();
                    final int len = matches.length-i;
                    Object[] values = (Object[]) Array.newInstance(compType, len);
                    for (int n = 0; n < len; n++) {
                        values[n] = matches[n+i].eval(arg);
                    }
                    return values;
                }
            };
            MULTI_STEP_VALUES.put(arrayType, arg);
        }
        return arg;
    }
    
    public static final SelectStepArg MULTI_STEP_MATCHES = new SelectStepArg() {
        @Override
        public Object map(int i, InstanceMap ctx, ProductionMatch arg, Object arg2) {
            Match<?>[] matches = arg.matches();
            return Arrays.copyOfRange(matches, i, matches.length);
        }
    };
    
    public static final SelectStepArg MULTI_STEP_MATCHES_LIST = new SelectStepArg() {
        @Override
        public Object map(int i, InstanceMap ctx, ProductionMatch arg, Object arg2) {
            Match<?>[] matches = arg.matches();
            return Arrays.asList(matches);
        }
    };
    
    public static final ArgMap<ProductionMatch, Object> MATCHES_ARG = new DirectIndexArgMap<ProductionMatch>() {
        @Override
        public Object map(int i, InstanceMap ctx, ProductionMatch pm, Object arg2) {
            return pm.matches();
        }
    };
    
    public static final ArgMap<Object, Object> ARGUMENT_ARG = new DirectIndexArgMap<Object>() {
        @Override
        public Object map(int i, InstanceMap ctx, Object o, Object arg2) {
            return arg2;
        }
    };
    
    public static class Hint {
        private final List<RuleKey> keys;
        private Map<String, Integer> keyIndices;
        public int index = -1;
        
        public Hint(List<RuleKey> keys) {
            this.keys = keys;
        }

        public List<RuleKey> getKeys() {
            return keys;
        }
        
        public int getLastIndex(String s) {
            if (s.isEmpty()) return index;
            if (keyIndices == null) keyIndices = new HashMap<>();
            Integer i = keyIndices.get(s);
            if (i == null) return -1;
            return i;
        }
        
        public void setLastIndex(String s, int i) {
            if (s.isEmpty()) {
                index = i;
            } else {
                if (keyIndices == null) keyIndices = new HashMap<>();
                keyIndices.put(s, i);
            }
        }
    }
}
