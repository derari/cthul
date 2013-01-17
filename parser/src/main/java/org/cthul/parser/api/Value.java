package org.cthul.parser.api;

public interface Value<V> {
    
    V eval(Object arg);
    
    V eval();
}
