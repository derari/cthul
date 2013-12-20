package org.cthul.objects.reflection;

import java.lang.reflect.Constructor;

public class AmbiguousConstructorMatchException extends AmbiguousSignatureMatchException {
    
    private final Constructor<?>[] allConstructors;
    private final Constructor<?>[] constructors;

    public AmbiguousConstructorMatchException(AmbiguousSignatureMatchException src, Constructor<?>[] allConstructors) {
        super(src);
        this.allConstructors = allConstructors;
        this.constructors = collectAmbiguousMethods(allConstructors);
    }

    public AmbiguousConstructorMatchException(JavaSignatureComparator jsCmp, Constructor<?>[] constructors) {
        super(jsCmp, Signatures.collectSignatures(constructors), Signatures.collectVarArgs(constructors));
        this.allConstructors = constructors;
        this.constructors = constructors;
    }

    public AmbiguousConstructorMatchException(JavaSignatureComparator jsCmp, Constructor<?>[] allConstructors, int[] ambiguousIndices) {
        super(jsCmp, Signatures.collectSignatures(allConstructors), Signatures.collectVarArgs(allConstructors), ambiguousIndices);
        this.allConstructors = allConstructors;
        this.constructors = collectAmbiguousMethods(allConstructors);
    }

    private Constructor<?>[] collectAmbiguousMethods(final Constructor<?>[] allConstructors) {
        final int[] indices = getAmbiguousIndices();
        Constructor<?>[] result = new Constructor[indices.length];
        for (int i = 0; i < indices.length; i++) {
            result[i] = allConstructors[indices[i]];
        }
        return result;
    }

    public Constructor<?>[] getAllConstructors() {
        return allConstructors;
    }

    public Constructor<?>[] getConstructors() {
        return constructors;
    }
}
