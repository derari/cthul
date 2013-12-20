package org.cthul.objects.reflection;

/**
 * Thrown when multiple best-matching signature were found.
 */
public class AmbiguousSignatureMatchException extends RuntimeException {

    private final JavaSignatureComparator jsCmp;
    private final Class<?>[][] allSignatures;
    private final boolean[] allVarArgs;
    private final Class<?>[][] signatures;
    private final boolean[] varArgs;
    private final int[] ambiguousIndices;
    private String message;
    
    protected AmbiguousSignatureMatchException(AmbiguousSignatureMatchException src) {
        this.jsCmp = src.jsCmp;
        this.allSignatures = src.allSignatures;
        this.allVarArgs = src.allVarArgs;
        this.signatures = src.signatures;
        this.varArgs = src.varArgs;
        this.ambiguousIndices = src.ambiguousIndices;
    }

    public AmbiguousSignatureMatchException(JavaSignatureComparator jsCmp, Class<?>[][] allSignatures, boolean[] allVarArgs, int[] ambiguousIndices) {
        this.jsCmp = jsCmp;
        this.allSignatures = allSignatures;
        this.allVarArgs = allVarArgs;
        this.ambiguousIndices = ambiguousIndices;
        signatures = new Class[ambiguousIndices.length][];
        varArgs = new boolean[ambiguousIndices.length];
        for (int i = 0; i < ambiguousIndices.length; i++) {
            signatures[i] = allSignatures[ambiguousIndices[i]];
            varArgs[i] = allVarArgs[ambiguousIndices[i]];
        }
    }

    public AmbiguousSignatureMatchException(JavaSignatureComparator jsCmp, Class<?>[][] signatures, boolean[] varArgs) {
        this.jsCmp = jsCmp;
        this.allSignatures = signatures;
        this.allVarArgs = varArgs;
        this.signatures = signatures;
        this.varArgs = varArgs;
        this.ambiguousIndices = new int[signatures.length];
        for (int i = 0; i < ambiguousIndices.length; i++) {
            ambiguousIndices[i] = i;
        }
    }

    /**
     * Returns all signatures.
     * @return signatures
     */
    public Class<?>[][] getAllSignatures() {
        return allSignatures;
    }

    /**
     * Indicates which signature were var-args.
     * @return booleans
     */
    public boolean[] getAllVarArgs() {
        return allVarArgs;
    }

    /**
     * Returns the signatures that matched equally against the reference signature.
     * @return signatures
     */
    public Class<?>[][] getSignatures() {
        return signatures;
    }

    /**
     * Indicates which signature were var-args.
     * @return booleans
     */
    public boolean[] getVarArgs() {
        return varArgs;
    }

    public int[] getAmbiguousIndices() {
        return ambiguousIndices;
    }
    
    /**
     * Returns the signature comparator that was used for matching.
     * @return comparator
     */
    public JavaSignatureComparator getSignatureComparator() {
        return jsCmp;
    }
    
    /**
     * Returns the signature that was matched against.
     * @return reference signature
     */
    public Class<?>[] getReferenceSignature() {
        return jsCmp.getReferenceSignature();
    }

    @Override
    public String getMessage() {
        if (message == null) {
            message = buildMessage();
        }
        return message;
    }
    
    protected String buildMessage() {
        return sigList(getReferenceSignature(), getSignatures(), getVarArgs());
    }
    
    protected static String sigList(Class<?>[] match, Class<?>[][] signatures, boolean[] varArgs) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        printSig(sb, match, false);
        sb.append("): ");
        for (int i = 0; i < signatures.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append("(");
            printSig(sb, signatures[i], varArgs[i]);
            sb.append(")");
        }
        String result = sb.toString();
        if (result.length() > 70) {
            result = result.replace(" (", "\n    (");
        }
        return result;
    }
    
    protected static void printSig(StringBuilder sb, Class<?>[] sig, boolean varArgs) {
        for (int i = 0; i < sig.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            printClass(sb, sig[i]);
        }
        if (varArgs) {
            sb.setLength(sb.length()-2);
            sb.append("...");
        }
    }
    
    protected static void printClass(StringBuilder sb, Class<?> aClass) {
        if (aClass.isArray()) {
            printClass(sb, aClass.getComponentType());
            sb.append("[]");
        } else {
            sb.append(aClass.getName());
        }
    }
    
}
