package org.cthul.proc;

public interface Proc4<A, B, C, D> extends Proc {
    
    Proc4 call(A a, B b, C c, D d);
    
    Proc3<B, C, D> curry(A a);
    
    Proc2<C, D> curry(A a, B b);
    
    Proc1<D> curry(A a, B b, C c);
    
    Proc0 curry(A a, B b, C c, D d);

}
