package org.cthul.log;

import java.util.AbstractList;

/**
 *
 * @author Arian Treffer
 */
public class UnmodifiableArrayList<E> extends AbstractList<E> {
    
    private final E[] array;

    public UnmodifiableArrayList(E[] array) {
        this.array = array;
    }

    @Override
    public E get(int index) {
        return array[index];
    }

    @Override
    public int size() {
        return array.length;
    }
    
}
