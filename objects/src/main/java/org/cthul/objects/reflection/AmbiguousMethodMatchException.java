package org.cthul.objects.reflection;

import java.lang.reflect.Method;

public class AmbiguousMethodMatchException extends AmbiguousSignatureMatchException {
    
    private final Method[] allMethods;
    private final Method[] methods;

    public AmbiguousMethodMatchException(AmbiguousSignatureMatchException src, Method[] allMethods) {
        super(src);
        this.allMethods = allMethods;
        this.methods = collectAmbiguousMethods(allMethods);
    }

    public AmbiguousMethodMatchException(JavaSignatureComparator jsCmp, Method[] methods) {
        super(jsCmp, Signatures.collectSignatures(methods), Signatures.collectVarArgs(methods));
        this.allMethods = methods;
        this.methods = methods;
    }

    public AmbiguousMethodMatchException(JavaSignatureComparator jsCmp, Method[] allMethods, int[] ambiguousIndices) {
        super(jsCmp, Signatures.collectSignatures(allMethods), Signatures.collectVarArgs(allMethods), ambiguousIndices);
        this.allMethods = allMethods;
        this.methods = collectAmbiguousMethods(allMethods);
    }

    private Method[] collectAmbiguousMethods(final Method[] allMethods) {
        final int[] indices = getAmbiguousIndices();
        Method[] result = new Method[indices.length];
        for (int i = 0; i < indices.length; i++) {
            result[i] = allMethods[indices[i]];
        }
        return result;
    }

    public Method[] getAllMethods() {
        return allMethods;
    }

    public Method[] getMethods() {
        return methods;
    }

    @Override
    protected String buildMessage() {
        return methodList(getReferenceSignature(), getMethods());
    }
    
    protected static String methodList(Class<?>[] match, Method[] methods) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        printSig(sb, match, false);
        sb.append("): ");
        for (int i = 0; i < methods.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            Method m = methods[i];
            sb.append(m.getName());
            sb.append("(");
            printSig(sb, m.getParameterTypes(), m.isVarArgs());
            sb.append(")");
        }
        String result = sb.toString();
        if (result.length() > 70) {
            result = result.replace(" (", "\n    (");
        }
        return result;
    }
}
