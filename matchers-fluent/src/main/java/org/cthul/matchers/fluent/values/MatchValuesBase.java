package org.cthul.matchers.fluent.values;

/**
 *
 * @author Arian Treffer
 */
public abstract class MatchValuesBase<Item> implements MatchValues<Item> {

    @Override
    public void result(boolean success) {
        if (success) success();
        else fail();
    }
    
}
