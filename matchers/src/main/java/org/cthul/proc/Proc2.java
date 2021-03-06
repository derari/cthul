package org.cthul.proc;

public interface Proc2<A, B> extends Proc {
    
    Proc2<A, B> call(A a, B b);
    
    Proc2<A, B> with(A a, B b);
    
    Proc1<B> curry(A a);
    
    Proc0 curry(A a, B b);
    
}
