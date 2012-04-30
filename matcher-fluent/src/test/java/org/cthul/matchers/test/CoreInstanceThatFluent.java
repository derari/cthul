package org.cthul.matchers.test;

/**
 *
 * @author derari
 */
public interface CoreInstanceThatFluent<FString, FInteger, FLong> {
    
    public FString isStringThat();
    
    public FInteger isIntegerThat();
    
    public FLong isLongThat();
    
}
