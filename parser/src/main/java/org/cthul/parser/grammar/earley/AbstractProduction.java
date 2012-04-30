package org.cthul.parser.grammar.earley;

import org.cthul.parser.Priority;

/**
 *
 * @author Arian Treffer
 */
public abstract class AbstractProduction implements Production {
    
    private final String left;
    private final int priority;
    private final String[] right;
    private final int[] priorities;

    public AbstractProduction(String left, int priority, String[] right, int[] priorities) {
        this.left = left;
        this.priority = priority;
        if (right.length != priorities.length) {
            throw new IllegalArgumentException(
                    String.format("Symbol and priority array must have "
                                  + "same size, but was %d and %d",
                                  right.length, priorities.length));
        }
        this.right = right;
        this.priorities = priorities;
    }
    
    public AbstractProduction(String left, int priority, String right) {
        this.left = left;
        this.priority = priority;
        this.right = Priority.split(right);
        this.priorities = Priority.createPriorityArray(this.right);
        Priority.parse(this.right, priorities, left, priority);
    }

    @Override
    public String getLeft() {
        return left;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public int getRightSize() {
        return right.length;
    }

    @Override
    public String getRight(int i) {
        return right[i];
    }

    @Override
    public int getRightPriority(int i) {
        return priorities[i];
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('(').append(priority).append(')');
        sb.append(left).append(" ::=");
        for (int i = 0; i < right.length; i++) {
            sb.append(' ');
            sb.append('(').append(priorities[i]).append(')');
            sb.append(right[i]);
        }
        return sb.toString();
    }
    
}
