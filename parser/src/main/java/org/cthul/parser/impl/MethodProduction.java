package org.cthul.parser.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import org.cthul.parser.Context;
import org.cthul.parser.Value;
import org.cthul.parser.annotation.Arg;
import org.cthul.parser.annotation.Symbol;
import org.cthul.parser.grammar.earley.AbstractProduction;
import org.cthul.parser.grammar.earley.Production;
import static org.cthul.parser.impl.Executor.*;

/**
 *
 * @author Arian Treffer
 */
public class MethodProduction extends AbstractProduction {
    
    private final Executor<Value<?>[]> exec;

    public MethodProduction(String left, int priority, String[] right, int[] priorities, Object impl, Method m) {
        super(left, priority, right, priorities);
        this.exec = new Executor<Value<?>[]>(impl, m, MAPPER, this);
    }
    
    public MethodProduction(String left, int priority, String right, Object impl, Method m) {
        super(left, priority, right);
        this.exec = new Executor<Value<?>[]>(impl, m, MAPPER, this);
    }
    
    @Override
    public Object invoke(Context context, Value<?>... args) {
        return exec.invoke(context, args);
    }
    
    private static ArgMapFactory<Value<?>[], Production> MAPPER = new ArgMapFactory<Value<?>[], Production>() {
        @Override
        public ArgMap<Value<?>[]> create(Class<?> paramType, Annotation[] annotations) {
            if (paramType == Void.class || paramType == void.class ) {
                return VOID;
            }
            if (Value.class.isAssignableFrom(paramType)) {
                return VALUE;
            }
            if (paramType == Context.class) {
                return CONTEXT;
            }
            return EVALUATE;
        }
        
        @Override
        public int mapIndex(int i, Annotation[] annotations, Production p) {
            for (Annotation a: annotations) {
                if (a instanceof Arg) {
                    int id = ((Arg) a).value();
                    if (id < 0) id += p.getRightSize();
                    if (id < 0 || id >= p.getRightSize()) return -1;
                    return id;
                }
                if (a instanceof Symbol) {
                    String symbol = ((Symbol) a).value();
                    int n = ((Symbol) a).n();
                    int pLen = p.getRightSize();
                    int r = n > 0 ? 0 : pLen-1;
                    int step = n > 0 ? 1 : -1;
                    while (r >= 0 && r < pLen) {
                        String s = p.getRight(r);
                        if (s.equals(symbol)) {
                            if (n == 0) return r;
                            n -= step;
                        }
                        r += step;
                    }
                    return -1;
                }
            }
            return i;
        }
    };

    private static final ArgMap<Value<?>[]> VALUE = new ArgMap<Value<?>[]>() {
        @Override
        public Object map(int i, Context ctx, Value<?>[] arg) {
            if (i == -1) return null;
            return arg[i];
        }
    };
    
    private static final ArgMap<Value<?>[]> EVALUATE = new ArgMap<Value<?>[]>() {
        @Override
        public Object map(int i, Context ctx, Value<?>[] arg) {
            if (i == -1) return null;
            return arg[i].getValue();
        }
    };
    
    private static final ArgMap<Value<?>[]> CONTEXT = new ArgMap<Value<?>[]>() {
        @Override
        public Object map(int i, Context ctx, Value<?>[] arg) {
            return ctx;
        }
    };
    
    private static final ArgMap<Value<?>[]> VOID = new ArgMap<Value<?>[]>() {
        @Override
        public Object map(int i, Context ctx, Value<?>[] arg) {
            return null;
        }
    };
    
}
