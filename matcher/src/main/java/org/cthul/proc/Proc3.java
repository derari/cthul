package org.cthul.proc;

public interface Proc3<A, B, C> extends Proc {
    
    Proc3 call(A a, B b, C c);
    
}
