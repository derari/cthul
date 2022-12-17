package org.cthul.prepost;

public class PpNode<T> {

    private final PpNode<T> parent;
    private final long pre;
    private final long post;
    private T value;

    public PpNode(T value) {
        this(value, null, Long.MIN_VALUE >>> 8, Long.MAX_VALUE);
    }

    PpNode(T value, PpNode<T> parent, long pre, long post) {
        this.value = value;
        this.parent = parent;
        this.pre = pre;
        this.post = post;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public PpNode<T> addChild(T value) {
        return new PpNode<>(value, this, pre + 1, post);
    }

    public boolean isChild(PpNode<?> other) {
        return other.parent == this;
    }

    public boolean isParent(PpNode<?> other) {
        return parent == other;
    }
}
