package org.cthul.proc;

public interface Proc4<A, B, C, D> extends Proc {
    
    Proc4 call(A a, B b, C c, D d);
    
}
