package org.cthul.matchers.diagnose.nested;

import org.hamcrest.SelfDescribing;

/**
 * A {@link SelfDescribing} with a precedence that indicates when to set 
 * parenthesis when nesting messages.
 */
public interface PrecedencedSelfDescribing extends SelfDescribing {
    
    public static final int P_ATOMIC = 0x0100_0000;
    public static final int P_UNARY =  0x0010_0000;
    public static final int P_COMPLEX =     0x9000;
    public static final int P_AND =         0x6000;
    public static final int P_OR =          0x3000;
    
    public static final int P_NONE =          0x0;
    
    int getDescriptionPrecedence();
    
}
