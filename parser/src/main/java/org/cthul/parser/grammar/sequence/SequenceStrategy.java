package org.cthul.parser.grammar.sequence;

/**
 *
 * @author Arian Treffer
 */
public interface SequenceStrategy<E, S> {
    
    public S newInstance();
    
    public void add(S sequence, E element);
    
}
