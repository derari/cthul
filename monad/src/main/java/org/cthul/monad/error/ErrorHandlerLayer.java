package org.cthul.monad.error;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public interface ErrorHandlerLayer {
    
    <State extends ErrorState<? extends State>> State handle(State state, ErrorHandler parent);
    
    default ErrorContext enable() {
        return ErrorContext.enable(this);
    }
    
    default ErrorHandler withCurrentAsParent() {
        return new ErrorHandler() {
            @Override
            public <State extends ErrorState<? extends State>> State handle(State state) {
                return ErrorHandlerLayer.this.handle(state, ErrorHandler.current());
            }
            @Override
            public ErrorContext enable() {
                return ErrorContext.enable(ErrorHandlerLayer.this);
            }
        };
    }
    
    static ErrorHandlerLayer doNotHandle() {
        return Builder.DO_NOT_HANDLE;
    }
    
    static ErrorHandlerLayer delegateToParent() {
        return Builder.DELEGATE_TO_PARENT;
    }
    
    static <E extends ErrorState<?>> Builder handle(Class<? extends E> type, BiFunction<? super E, ? super ErrorHandler, ? extends ErrorState<?>> handler) {
        return new Builder().handle(type, handler);
    }
    
    static <E extends ErrorState<?>> Builder.Step<E> handle(Class<E> type) {
        return new Builder().handle(type);
    }
    
    class Builder implements ErrorHandlerLayer {

        private static final ErrorHandlerLayer DO_NOT_HANDLE = new ErrorHandlerLayer() {
            @Override
            public <State extends ErrorState<? extends State>> State handle(State state, ErrorHandler parent) {
                return state;
            }
            @Override
            public ErrorHandler withCurrentAsParent() {
                return ErrorHandler.doNotHandle();
            }
            @Override
            public ErrorContext enable() {
                return ErrorHandler.doNotHandle().enable();
            }
            @Override
            public String toString() {
                return "NO ERROR HANDLER";
            }
        };

        private static final  ErrorHandlerLayer DELEGATE_TO_PARENT = new ErrorHandlerLayer() {
            @Override
            public <State extends ErrorState<? extends State>> State handle(State state, ErrorHandler parent) {
                return parent.handle(state);
            }
            @Override
            public ErrorHandler withCurrentAsParent() {
                return ErrorHandler.delegateCurrent();
            }
            @Override
            public ErrorContext enable() {
                return ErrorHandler.delegateCurrent().enable();
            }
            @Override
            public String toString() {
                return "DELEGATE TO PARENT";
            }
        };

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
