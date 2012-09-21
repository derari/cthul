package org.cthul.matchers.testold;

import java.util.List;
import org.cthul.matchers.fluent.custom.CustomFluent;
import org.hamcrest.Matcher;

/**
 *
 * @author derari
 */
public interface ListFluent<T, This extends ListFluent<T, This>>
                extends CustomFluent<List<T>, This> {
    
    This hasSize(int size);
    
    This each(Matcher<? super T> matcher);
    
}
