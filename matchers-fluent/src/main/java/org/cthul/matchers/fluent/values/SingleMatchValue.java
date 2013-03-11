package org.cthul.matchers.fluent.values;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 *
 * @author Arian Treffer
 */
public class SingleMatchValue<Item> extends MatchValuesBase<Item> {
    
    private final Item value;
    private boolean next = true;
    private boolean success = true;

    public SingleMatchValue(Item value) {
        this.value = value;
    }

    @Override
    public boolean hasNext() {
        return next;
    }

    @Override
    public Item next() {
        next = false;
        return value;
    }

    @Override
    public void success() {
    }

    @Override
    public void fail() {
        success = false;
    }

    @Override
    public boolean matched() {
        return success;
    }

    @Override
    public void describeMismatch(Matcher<? super Item> matcher, Description mismatch) {
        matcher.describeMismatch(value, mismatch);
    }
    
}
