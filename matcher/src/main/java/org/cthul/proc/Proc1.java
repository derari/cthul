package org.cthul.proc;

public interface Proc1<A> extends Proc {
    
    Proc1<A> call(A a);
    
    Proc1<A> with(A a);
    
    Proc0 curry(A a);
    
}
