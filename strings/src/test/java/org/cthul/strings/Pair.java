package org.cthul.strings;

/**
 *
 * @author Arian Treffer
 */
public class Pair<A, B> {
    
    public static <A, B> Pair<A, B>[] merge(final A[] a, final B[] b) {
        if (a.length != b.length)
            throw new IllegalArgumentException("Arrays must have same length.");
        final Pair[] result = new Pair[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = new Pair<>(a[i], b[i]);
        }
        return result;
    }
    
    public static <A, B> Pair<A, B> p(A a, B b) {
        return new Pair(a, b);
    }
    
    public final A a;
    public final B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return "(" + a + ", " + b + ")";
    }
    
}
