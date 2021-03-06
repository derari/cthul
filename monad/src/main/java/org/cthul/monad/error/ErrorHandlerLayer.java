package org.cthul.monad.error;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ErrorHandlerLayer {
    
    <State extends ErrorState<? extends State>> State handle(State state, ErrorHandler parent);
    
    default ErrorHandlingScope enable() {
        return ErrorHandlingScope.enable(this);
    }
    
    static <E extends ErrorState<?>> Builder handle(Class<? extends E> type, BiFunction<? super E, ? super ErrorHandler, ? extends ErrorState<?>> handler) {
        return new Builder().handle(type, handler);
    }
    
    static <E extends ErrorState<?>> Builder.Step<E> handle(Class<E> type) {
        return new Builder().handle(type);
    }
    
    static ErrorHandlerLayer doNothing() {
        return ErrorHandlingScope.DO_NOT_HANDLE;
    }
    
    class Builder implements ErrorHandlerLayer {

        private final List<Predicate<? super ErrorState<?>>> filters = new ArrayList<>();
        private final List<BiFunction<? super ErrorState<?>, ? super ErrorHandler, ? extends ErrorState<?>>> handlers = new ArrayList<>();
        
        @Override
        public <State extends ErrorState<? extends State>> State handle(State state, ErrorHandler parent) {
            for (int i = 0; i < filters.size(); i++) {
                if (filters.get(i).test(state)) {
                    ErrorState<?> result = handlers.get(i).apply(state, parent);
                    return state.cast(result);
                }
            }
            return state;
        }
        
        public Builder handle(Predicate<? super ErrorState<?>> filter, BiFunction<? super ErrorState<?>, ? super ErrorHandler, ? extends ErrorState<?>> handler) {
            handlers.add(handler);
            filters.add(filter);
            return this;
        }
        
        public <E extends ErrorState<?>> Builder handle(Class<? extends E> type, BiFunction<? super E, ? super ErrorHandler, ? extends ErrorState<?>> handler) {
            return handle(type::isInstance, (BiFunction) handler);
        }
        
        public <E extends ErrorState<?>> Step<E> handle(Class<E> type) {
            Step<E> step = (filter, handler) -> handle((Predicate) filter, (BiFunction) handler);
            return step.filter(type::isInstance);
        }

        public interface Step<E extends ErrorState<?>> {
            
            Builder handle(Predicate<? super E> filter, BiFunction<? super E, ? super ErrorHandler, ? extends ErrorState<?>> handler);

            default Builder apply(BiFunction<? super E, ? super ErrorHandler, ? extends ErrorState<?>> handler) {
                return handle(null, handler);
            }

            default Builder apply(Function<? super E, ? extends ErrorState<?>> handler) {
                return handle(null, (e, p) -> handler.apply(e));
            }

            default Builder run(Consumer<? super E> handler) {
                return handle(null, (e, p) -> { handler.accept(e); return e; });
            }
            
            default Step<E> filter(Predicate<? super E> filter) {
                return (filter2, handler) -> {
                    if (filter2 == null) {
                        return handle(filter, handler);
                    }
                    return handle(filter.and((Predicate) filter2), handler);
                };
            }
        }
    }
}
