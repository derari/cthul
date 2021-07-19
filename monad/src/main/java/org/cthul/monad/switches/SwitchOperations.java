package org.cthul.monad.switches;

import java.util.function.Function;
import org.cthul.monad.function.CheckedPredicate;

public class SwitchOperations {
    
    public static <K, K2, T, U, R> BasicSwitch<K2, T, U, R> mapKey(Switch<K, T, U, R, ?, ?, ?> instance, CheckedPredicate.Adapter<? super K, ? extends K2> conditionAdapter) {
        return instance.wrapWith(delegate -> new ConditionAdaptingSwitch<>(delegate, conditionAdapter));
    }

    public static <K, K2, T, U, R> BasicSwitch<K2, T, U, R> mapKey(Switch<K, T, U, R, ?, ?, ?> instance, Function<? super K, ? extends K2> keyMapping) {
        return mapKey(instance, CheckedPredicate.Adapter.map(keyMapping));
    }

    public static <K, T, T2, U, R> BasicSwitch<K, T2, U, R> mapSource(Switch<K, T, U, R, ?, ?, ?> instance, Function<? super T, ? extends T2> valueMapping) {
        return instance.wrapWith(delegate -> new SourceMappingSwitch<>(delegate, valueMapping));
    }

    public static <K, T, U, U2, R> Switch<K, T, U2, R, ?, ?, ?> mapTarget(Switch<K, T, U, R, ?, ?, ?> instance, Function<? super U2, ? extends U> valueMapping) {
        return instance.wrapWith(delegate -> new TargetMappingSwitch<>(delegate, valueMapping));
    }

    public static <K, T, U, R, R2> Switch<K, T, U, R2, ?, ?, ?> mapResult(Switch<K, T, U, R, ?, ?, ?> instance, Function<? super R, ? extends R2> resultMapping) {
        return instance.wrapWith(delegate -> new ResultMappingSwitch<>(delegate, resultMapping));
    }
}
