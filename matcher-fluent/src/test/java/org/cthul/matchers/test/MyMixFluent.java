package org.cthul.matchers.test;

/**
 *
 * @author derari
 */
public interface MyMixFluent {
    
    public static interface Group1<Item, This extends Group1<Item, This>> 
                extends ColorFluent<Item, Group1<Item, This>> {
        
    }
    
    public static interface Group2<Item, This extends Group2<Item, This>> 
                extends AngleFluent<Item, Group2<Item, This>> {
    }
    
    public static interface Group3<Item, This extends Group3<Item, This>> 
                extends ColorFluent<Item, Group3<Item, This>>,
                        AngleFluent<Item, Group3<Item, This>> {
        
    }
    
}
