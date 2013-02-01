package org.cthul.parser.sequence;

/**
 *
 * @author Arian Treffer
 */
public interface SequenceBuilder<E, S> {
    
    public S newInstance();
    
    public void add(S sequence, E element);
    
}
