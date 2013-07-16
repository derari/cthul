package org.cthul.matchers.fluent.values;

import org.hamcrest.StringDescription;

/**
 *
 */
public abstract class AbstractMatchValue<Item> implements MatchValue<Item> {

    @Override
    public String toString() {
        StringDescription d = new StringDescription();
        describeTo(d);
        return d.toString();
    }

}
