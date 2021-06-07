package org.cthul.monad.switches;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.cthul.monad.function.CheckedBiFunction;
import org.cthul.monad.function.CheckedFunction;
import org.cthul.monad.function.CheckedPredicate;

public class SwitchFunctionBuilder<K, T, U, R> 
        implements Switch<K, T, U, R,
                        SwitchFunctionBuilder<K, T, U, R>,
                        Switch.Case<T, U, SwitchFunctionBuilder<K, T, U, R>>,
                        Switch.Case<T, U, R>> {

    public static <T, U> SwitchFunctionBuilder<T, T, U, CheckedFunction<T, U, ?>> functionBuilder() {
        return new SwitchFunctionBuilder<>(biFunction -> t -> biFunction.apply(t, t));
    }

    public static <T, U, R> SwitchFunctionBuilder<T, T, U, R> functionBuilder(Function<? super CheckedFunction<T, U, ?>, ? extends R> functionWrapper) {
        return new SwitchFunctionBuilder<>(biFunction -> {
            CheckedFunction<T, U, ?> f = t -> biFunction.apply(t, t); 
            return functionWrapper.apply(f);
        });
    }

    public static <K, T, U> SwitchFunctionBuilder<K, T, U, CheckedBiFunction<K, T, U, ?>> biFunctionBuilder() {
        return new SwitchFunctionBuilder<>(f -> (CheckedBiFunction) f);
    }
    
    private final Function<? super CheckedBiFunction<? super K, ? super T, ? extends U, ?>, R> resultBuilder;
    private final CheckedBiFunction<K, T, U, Exception> switchFunction = this::apply;
    private final List<CaseStep> steps = new ArrayList<>();
    private CheckedFunction<T, U, ?> defaultMapping;

    public SwitchFunctionBuilder(Function<? super CheckedBiFunction<? super K, ? super T, ? extends U, ?>, R> resultBuilder) {
        this.resultBuilder = resultBuilder;
    }
    
    @Override
    public <X extends Exception> Case<T, U, SwitchFunctionBuilder<K, T, U, R>> ifTrue(CheckedPredicate<? super K, X> condition) throws X {
        return new CaseStep(condition);
    }

    @Override
    public Case<T, U, R> orElse() {
        return new DefaultStep();
    }
    
    protected U apply(K key, T value) throws Exception {
        for (CaseStep step: steps) {
            if (step.matches(key)) {
                return step.map(value);
            }
        }
        return defaultMapping.apply(value);
    }
    
    protected R build() {
        if (defaultMapping == null) {
            throw new IllegalStateException("default mapping not set");
        }
        return resultBuilder.apply(switchFunction);
    }
    
    protected class CaseStep implements Switch.Case<T, U, SwitchFunctionBuilder<K, T, U, R>> {
        
        private final CheckedPredicate<? super K, ?> condition;
        private CheckedFunction<T, U, ?> mapping;

        public CaseStep(CheckedPredicate<? super K, ?> condition) {
            this.condition = condition;
        }

        @Override
        public <X extends Exception> SwitchFunctionBuilder<K, T, U, R> map(CheckedFunction<T, U, X> mapping) throws X {
            if (this.mapping != null) {
                throw new IllegalStateException("case mapping already set");
            }
            this.mapping = mapping;
            steps.add(this);
            return SwitchFunctionBuilder.this;
        }
        
        protected boolean matches(K key) throws Exception {
            return condition.test(key);
        }
        
        protected U map(T value) throws Exception {
            return mapping.apply(value);
        }
    }
    
    protected class DefaultStep implements Switch.Case<T, U, R> {

        @Override
        public <X extends Exception> R map(CheckedFunction<T, U, X> mapping) throws X {
            if (defaultMapping != null) {
                throw new IllegalStateException("defeault mapping already set");
            }
            defaultMapping = mapping;
            return build();
        }
    }
}
