package org.cthul.matchers.diagnose;

public interface PrecedencedMatcher {
    
    public static final int P_ATOMIC = 0x0100_0000;
    public static final int P_UNARY =  0x0010_0000;
    public static final int P_COMPLEX =     0x9000;
    public static final int P_AND =         0x6000;
    public static final int P_OR =          0x3000;
    
    int getPrecedence();
    
    int getMismatchPrecedence();
    
}
