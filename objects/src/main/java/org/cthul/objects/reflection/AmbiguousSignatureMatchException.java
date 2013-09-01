package org.cthul.objects.reflection;

/**
 * Thrown when multiple best-matching signature were found.
 */
public class AmbiguousSignatureMatchException extends RuntimeException {

    private final JavaSignatureComparator jsCmp;
    private final Class<?>[][] signatures;
    private final boolean[] varArgs;
    private String message;

    public AmbiguousSignatureMatchException(JavaSignatureComparator jsCmp, Class<?>[][] signatures, boolean[] varArgs) {
        this.jsCmp = jsCmp;
        this.signatures = signatures;
        this.varArgs = varArgs;
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
            message = sigList(getReferenceSignature(), signatures, varArgs);
        }
        return message;
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
    
    private static void printClass(StringBuilder sb, Class<?> aClass) {
        if (aClass.isArray()) {
            printClass(sb, aClass.getComponentType());
            sb.append("[]");
        } else {
            sb.append(aClass.getName());
        }
    }
    
}
