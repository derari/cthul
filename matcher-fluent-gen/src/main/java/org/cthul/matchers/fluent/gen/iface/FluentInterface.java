package org.cthul.matchers.fluent.gen.iface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author Arian Treffer
 */
public class FluentInterface implements Comparable<FluentInterface> {
    
    private final Map<String, FluentInterface> interfaces;
    private final String name;
    private final String pkg;
    
    private final String base;
    private final String itemType;

    private final SortedSet<FluentMethod> methods = new TreeSet<>();
    private final List<FluentInterface> subInterfaces = new ArrayList<>();
    private SortedSet<FluentMethod> effectiveMethods = null;

    public FluentInterface(Map<String, FluentInterface> interfaces, String name, String pkg, String base, String itemType) {
        this.interfaces = interfaces;
        this.name = name;
        this.pkg = pkg;
        this.base = base;
        this.itemType = itemType;
    }

    public String getItemType() {
        return itemType;
    }

    public String getFullName() {
        return pkg + "." + name;
    }

    public String getName() {
        return name;
    }

    public void addMethods(List<FluentMethod> myMethods) {
        methods.addAll(myMethods);
    }

    @Override
    public int compareTo(FluentInterface o) {
        int c = compareBase(o);
        if (c == 0) c = name.compareTo(o.name);
        if (c == 0) c = getFullName().compareTo(o.getFullName());
        return c;
    }

    private int compareBase(FluentInterface o) {
        // todo: if an interface extends the other, have the other first
        return 0;
    }

    public String getPackage() {
        return pkg;
    }

    public String getBase() {
        return base;
    }

    public SortedSet<FluentMethod> getMethods() {
        return methods;
    }
    
    public SortedSet<FluentMethod> getEffectiveMethods() {
        if (effectiveMethods == null) {
            effectiveMethods = new TreeSet<>(methods);
            removeBaseMethods(effectiveMethods);
        }
        return effectiveMethods;
    }

    public List<FluentInterface> getSubinterfaces() {
        return subInterfaces;
    }
    
    private FluentInterface getBaseInterface() {
        return interfaces.get(base);
    }

    private void removeBaseMethods(SortedSet<FluentMethod> effectiveMethods) {
        FluentInterface iBase = getBaseInterface();
        if (iBase != null) {
            iBase.filterEffectiveMethods(effectiveMethods);
        }
    }

    private void filterEffectiveMethods(SortedSet<FluentMethod> effectiveMethods) {
        effectiveMethods.removeAll(getMethods());
        removeBaseMethods(effectiveMethods);
    }
    
}
