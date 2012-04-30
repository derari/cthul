package org.cthul.proc;

public interface Proc1<A> extends Proc {
    
    Proc1 call(A a);
    
}
