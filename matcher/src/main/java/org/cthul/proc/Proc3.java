package org.cthul.proc;

public interface Proc3<A, B, C> extends Proc {
    
    Proc3 call(A a, B b, C c);
    
    Proc2<B, C> curry(A a);
    
    Proc1<C> curry(A a, B b);
    
    Proc0 curry(A a, B b, C c);
    
}
