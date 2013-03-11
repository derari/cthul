package org.cthul.matchers.fluent.strategy;

/**
 *
 * @author Arian Treffer
 */
public interface MatchStrategy<T, M> {

    MatchingObject<M> apply(T t);
    
}
