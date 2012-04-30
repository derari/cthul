package org.cthul.matchers.fluent.gen.iface;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.Type;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author derari
 */
public class ItemTypeFilter {
    
    private final JavaDocBuilder qdox;
    private final List<FluentMethod> methods;

    public ItemTypeFilter(JavaDocBuilder qdox, List<FluentMethod> methods) {
        this.qdox = qdox;
        this.methods = methods;
    }
    
    public List<FluentMethod> getMethodsForType(String clazz) {
        List<FluentMethod> result = new ArrayList<>();
        Type type = typeForString(clazz);
        for (FluentMethod m: methods) {
            if (type.isA(m.getItemType())) {
                result.add(m);
            }
        }
        return result;
    }
    
    private Type typeForString(String type) {
        return qdox.getClassByName(type).asType();
    }
    
}
