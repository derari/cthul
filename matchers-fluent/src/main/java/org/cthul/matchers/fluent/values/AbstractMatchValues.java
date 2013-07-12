package org.cthul.matchers.fluent.values;

/**
 *
 */
public abstract class AbstractMatchValues<Item> implements MatchValues<Item> {

    @Override
    public void result(boolean success) {
        if (success) success();
        else fail();
    }
    
}
