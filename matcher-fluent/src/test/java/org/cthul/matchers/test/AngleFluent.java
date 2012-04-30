package org.cthul.matchers.test;

import org.cthul.matchers.fluent.custom.CustomFluent;

/**
 *
 * @author Arian Treffer
 */
public interface AngleFluent<Item, This extends AngleFluent<Item, This>>
                extends CustomFluent<Item, This> {
    
    public This between(float f1, float f2);

}
