package org.cthul.matchers.fluent.gen.iface;

import com.thoughtworks.qdox.model.TypeVariable;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import static org.cthul.matchers.fluent.gen.QDoxUtils.*;

/**
 *
 * @author Arian Treffer
 */
public final class FluentMethodCollector {
    
    private final List<FluentMethod> methods = new ArrayList<>();

    private final JavaDocBuilder qdox;
    private final JavaClass hamcrestMatcher;
    private final Type hamcrestMatcherType;
    
    private int itemTypeGenericParamIndex;
    
    public FluentMethodCollector(JavaDocBuilder qdox) {
        this.qdox = qdox;
        hamcrestMatcher = qdox.getClassByName("org.hamcrest.Matcher");
        hamcrestMatcherType = hamcrestMatcher.asType();
    }

    public List<FluentMethod> getMethods() {
        return methods;
    }
    
    public void addMethods(String className) {
        JavaClass qClass = qdox.getClassByName(className);
        for (JavaMethod m: qClass.getMethods(true)) {
            addMethod(m);            
        }
    }

    private synchronized void addMethod(JavaMethod m) {
        if (!m.isStatic() || 
                !m.getReturnType(true).isA(hamcrestMatcherType)) {
            return;
        }
        itemTypeGenericParamIndex = -1;
        Type itemType = findItemType(m);
        String params = getParameters(m);
        methods.add(new FluentMethod(m.getName(), "", params, 
                itemType, m.getParentClass().getFullyQualifiedName()));
    }

    private Type findItemType(JavaMethod m) {
        Type matcherType = m.getReturnType(true);
        Stack<Type> hierarchy = hierarchyToHamcrestMatcher(matcherType);
        assert hierarchy.peek().getJavaClass() == hamcrestMatcher : 
               "Hierarchy top should be org.hamcrest.Matcher<T>";
        Type itemType = new Type("T");
        
        int argIndex = -1;
        
        while (!hierarchy.isEmpty()) {
            matcherType = hierarchy.pop();
            argIndex = findParameterForArgument(getTypeParameters(matcherType.getJavaClass()), itemType);
            itemType = matcherType.getActualTypeArguments().get(argIndex);
            if (!isGenericArg(itemType)) {
                break;
            }
        }
        
        assert argIndex > -1 : 
               "Item type should be bound to generic parameter.";
        itemTypeGenericParamIndex = argIndex;
        return resolveGenericTypeBound(qdox, itemType, m.getTypeParameters());
    }
    
    private Stack<Type> hierarchyToHamcrestMatcher(Type type) {
        final Stack<Type> hierarchy = new Stack<>();
        
        JavaClass clazz = type.getJavaClass();
        while (clazz != hamcrestMatcher) {
            hierarchy.push(type);
            type = findHamcrestMatcherSupertype(clazz);
            clazz = type.getJavaClass();
        }
        
        hierarchy.push(type);
        return hierarchy;
    }
    
    private Type findHamcrestMatcherSupertype(JavaClass clazz) {
        Type sup = getGenericSuperType(clazz);
        if (sup.isA(hamcrestMatcherType)) {
            return sup;
        }
        for (Type iface: clazz.getImplements()) {
            if (iface.isA(hamcrestMatcherType)) {
                return getGenericInterface(iface, clazz);
            }
        }
        return null;
    }
    
    private int findParameterForArgument(final List<TypeVariable> parameters, Type argument) {
        for (int i = 0; i < parameters.size(); i++) {
            final Type p = parameters.get(i);
            if (p.getFullyQualifiedName().equals(argument.getFullyQualifiedName())) {
                return i;
            }
        }
        return -1;
    }

    private String getParameters(JavaMethod m) {
        int pCount = 0;
        StringBuilder sb = new StringBuilder();
        for (JavaParameter p: m.getParameters()) {
            sb.append(',');
            sb.append(paramType(p));
            sb.append(' ');
            sb.append(paramName(p, pCount));
            pCount++;
        }
        if (sb.length() == 0) return "";
        return sb.substring(1);
    }

    private String paramName(JavaParameter p, int pCount) {
        String name = p.getName();
        if (name == null || name.isEmpty()) {
            return "a" + pCount;
        }
        return name;
    }

    private String paramType(JavaParameter p) {
        Type pType = p.getType();
        if (isGenericArg(pType)) {
            pType = resolveGenericArg(p.getParentMethod(), pType);
        }
        return pType.getFullyQualifiedName() + genericArgs(p, pType);
    }

    private String genericArgs(JavaParameter p, Type pType) {
        List<Type> args = pType.getActualTypeArguments();
        if (args == null || args.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Type a: args) {
            sb.append(',');
            if (isGenericArg(a)) {
                a = resolveGenericArg(p.getParentMethod(), a);
            }
            sb.append(a.getFullyQualifiedName());
            sb.append(genericArgs(p, a));
        }
        return "<" + sb.substring(1)+ ">";
    }

    private Type resolveGenericArg(JavaMethod parentMethod, Type a) {
        int i = findParameterForArgument(parentMethod.getTypeParameters(), a);
        if (i == itemTypeGenericParamIndex) {
            return new Type("Item");
        }
        return null;
    }
    
}
