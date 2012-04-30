package org.cthul.parser.grammar.earley;

import org.cthul.parser.NoMatchException;

/**
 *
 * @author derari
 */
public abstract class AbstractResultIterator<T> implements ResultIterator<T> {

    private boolean nextFetched = false;
    
    protected T next;
    protected boolean hasNext;
    
    @Override
    public boolean hasNext() {
        ensureNextFetched();
        return hasNext;
    }

    private void ensureNextFetched() throws NoMatchException {
        if (!nextFetched) {
            fetchNext();
            nextFetched = true;
        }
    }

    @Override
    public T next() {
        if (hasNext()) {
            nextFetched = false;
            return next;
        }
        throw new IllegalStateException("no next element");
    }
    
    protected abstract void fetchNext() throws NoMatchException;

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }
    
}
