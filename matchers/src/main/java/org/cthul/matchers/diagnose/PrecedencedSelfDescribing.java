package org.cthul.matchers.diagnose;

public interface PrecedencedSelfDescribing {
    
    public static final int P_AND =        0x4000;
    public static final int P_XOR =        0x3000;
    public static final int P_NOR =        0x2000;
    public static final int P_OR =         0x1000;
    public static final int P_ATOMIC = 0x01000000;
    
    int getPrecedence();
    
}
