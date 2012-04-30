package org.cthul.proc;

public interface Proc2<A, B> extends Proc {
    
    Proc2 call(A a, B b);
    
}
