package org.cthul.matchers.fluent.gen.iface;

import com.thoughtworks.qdox.model.Type;

/**
 *
 * @author Arian Treffer
 */
public class FluentMethod implements Comparable<FluentMethod> {
    
    private String name;
    private String javadoc;
    private String args;
    private Type itemType;
    private String implementor;

    public FluentMethod(String name, String javadoc, String args, Type itemType, String implementor) {
        this.name = name;
        this.javadoc = javadoc;
        this.args = args;
        this.itemType = itemType;
        this.implementor = implementor;
    }

    public String getArgs() {
        return args;
    }

    public Type getItemType() {
        return itemType;
    }

    public String getJavadoc() {
        return javadoc;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(FluentMethod o) {
        int c = name.compareTo(o.name);
        if (c != 0) return c;
        return args.compareTo(o.args);
    }

    public String getImplementor() {
        return implementor;
    }
    
}
