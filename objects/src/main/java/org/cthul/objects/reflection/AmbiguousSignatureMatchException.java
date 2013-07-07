package org.cthul.objects.reflection;

/**
 *
 * @author Arian Treffer
 */
public class AmbiguousSignatureMatchException extends RuntimeException {
    
    private final JavaSignatureComparator jsCmp;
    private final Class<?>[][] signatures;
    private final boolean[] varArgs;

    public AmbiguousSignatureMatchException(JavaSignatureComparator jsCmp, Class<?>[][] signatures, boolean[] varArgs) {
        this.jsCmp = jsCmp;
        this.signatures = signatures;
        this.varArgs = varArgs;
    }

    public Class<?>[][] getSignatures() {
        return signatures;
    }

    public boolean[] getVarArgs() {
        return varArgs;
    }
    
    
}
