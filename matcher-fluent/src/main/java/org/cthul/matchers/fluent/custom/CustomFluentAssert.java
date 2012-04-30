package org.cthul.matchers.fluent.custom;

import org.cthul.matchers.fluent.FluentAssert;

public interface CustomFluentAssert<Item, This extends CustomFluentAssert<Item, This>> 
                extends FluentAssert<Item>, CustomFluent<Item, This>  {

}
