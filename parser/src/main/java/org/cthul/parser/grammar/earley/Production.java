package org.cthul.parser.grammar.earley;

import org.cthul.parser.Context;
import org.cthul.parser.Value;

/**
 *
 * @author Arian Treffer
 */
public interface Production {
    
    public String getLeft();
    
    public int getPriority();
    
    public int getRightSize();
    
    public String getRight(int i);
    
    public int getRightPriority(int i);
    
    public Object invoke(Context context, Value<?>... args);
    
}
