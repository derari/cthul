package org.cthul.matchers.fluent.generator;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.model.TypeVariable;
import com.thoughtworks.qdox.parser.structs.TypeDef;
import com.thoughtworks.qdox.parser.structs.TypeVariableDef;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Test;

/**
 *
 * @author derari
 */
public class QDoxTest {
    
    private Type hamcrestMatcher;
    private JavaClass hamcrestMatcherClass;
    
    @Test
    public void test() {
        if (true) return;
        JavaDocBuilder doc = new JavaDocBuilder();
        hamcrestMatcherClass = doc.getClassByName("org.hamcrest.Matcher");
        hamcrestMatcher = hamcrestMatcherClass.asType();
        
        doc.addSourceTree(new File("src/test/java"));
        JavaClass cls = doc.getClassByName("org.cthul.matchers.test.ColorMatchers");
        System.out.println("------------------------------");
        System.out.println(cls);
        for (JavaMethod m: cls.getMethods()) {
            Type ret = m.getReturnType();
            System.out.println(ret.getGenericValue());
            System.out.println("-> " + findMatcherItemType(m, ret));
        }
        assert false : "asd";
    }

    private Type createQdoxType(ParameterizedType pIface, Type original) {
        List<TypeDef> params = new ArrayList<>();
        for (java.lang.reflect.Type tParam: pIface.getActualTypeArguments()) {
            if (tParam instanceof java.lang.reflect.TypeVariable) {
                java.lang.reflect.TypeVariable vParam = (java.lang.reflect.TypeVariable) tParam;
                params.add(new TypeDef(vParam.getName()));
            } else if(tParam instanceof Class) {
                Class cParam = (Class) tParam;
                Type qParam = original.getJavaClass().getJavaClassLibrary().getJavaClass(cParam.getCanonicalName()).asType();
                params.add(new TypeDef(qParam.getFullyQualifiedName()));
            } else {
                throw new IllegalArgumentException(tParam + " " + tParam.getClass());
            }
        }
        return new Type(null, newTypeDef(original.getFullyQualifiedName(), params), 0, original.getJavaClassParent());
    }
    
    private Type findMatcherItemType(JavaMethod m, Type matcherClass) {
        if (!matcherClass.isA(hamcrestMatcher)) {
            return null;
        }
        if (matcherClass.getJavaClass() == hamcrestMatcherClass) {
            Type mType = matcherClass.getActualTypeArguments().get(0);
            
            if (!isGenericArg(mType)) {
                return mType;
            }
            
            
            return resolveGenericType(mType, m.getTypeParameters());
        }
        
        Stack<Type> superTypes = new Stack();
        
        while (matcherClass.isA(hamcrestMatcher)) {
            System.out.println("^ "+ matcherClass.getGenericValue());
            superTypes.push(matcherClass);
            matcherClass = matcherClass.getJavaClass().getSuperJavaClass().asType();
        }
        
        matcherClass = superTypes.pop();
        System.out.println("v "+ matcherClass);
        JavaClass matcherCls = matcherClass.getJavaClass();
        
        Type iMatcher = null;
        int argIndex = 0;
        for (Type i: matcherCls.getImplements()) {
            if (i.getJavaClass() == hamcrestMatcherClass) {
                iMatcher = i;
                break;
            }
        }
        iMatcher = getGenericInterface(iMatcher, matcherCls);
        
        while (true) {
            Type itemTypeArg = iMatcher.getActualTypeArguments().get(argIndex);
            System.out.println(" <" + itemTypeArg);
            if (!isGenericArg(itemTypeArg)) {
                return itemTypeArg;
            }
            argIndex = findGenericType(itemTypeArg, getTypeParameters(matcherCls));
            System.out.println(argIndex);
            
            if (superTypes.isEmpty()) break;
            matcherClass = superTypes.pop();
            System.out.println("v "+ matcherClass);
            matcherCls = matcherClass.getJavaClass();
            iMatcher = getGenericSuperType(matcherCls);
        }
       
        return null;
    }

    private List<TypeVariable> getTypeParameters(JavaClass matcherCls) {
        List<TypeVariable> params =  matcherCls.getTypeParameters();
        if (params != null && !params.isEmpty()) {
            return params;
        }
        Class clazz = getReflectionClass(matcherCls);
        params =  new ArrayList<>();
        for (java.lang.reflect.TypeVariable vParam: clazz.getTypeParameters()) {
            System.out.println("<<" + vParam);
            params.add(new TypeVariable(vParam.getName(), new TypeVariableDef(""), matcherCls.getParent()));
        }
        return params;
    }

    private Class getReflectionClass(JavaClass jClass) throws RuntimeException {
        Class clazz;
        try {
            clazz = Class.forName(jClass.getFullyQualifiedName());
        } catch (Exception e) { throw new RuntimeException(e); }
        return clazz;
    }

    private Type resolveGenericType(Type argument, List<TypeVariable> parameters) {
        for (Type p: parameters) {
            if (p.getFullyQualifiedName().equals(argument.getFullyQualifiedName())) {
                if (p.getValue().isEmpty()) {
                    return null; // todo return object
                }
                return p.getJavaClass().getJavaClassLibrary().getJavaClass(p.getValue()).asType();
            }
        }
//                    Type pType = m.getTypeParameters()[0];
//            
//            System.out.println("  " + pType.getGenericValue());
//            System.out.println("  " + pType.getFullyQualifiedName());
//            System.out.println("  " + pType.getValue());
//            
//            return mType;
        
        return null;
    }
    
    private int findGenericType(Type argument, final List<TypeVariable> parameters) {
        for (int i = 0; i < parameters.size(); i++) {
            final Type p = parameters.get(i);
            if (p.getFullyQualifiedName().equals(argument.getFullyQualifiedName())) {
                return i;
            }
        }
        return -1;
    }
    
    private boolean isGenericArg(Type argument) {
        String fullName = argument.getFullyQualifiedName();
        return ! fullName.contains(".");
    }
    
    private Type getGenericSuperType(JavaClass jClass) {
        Type sup = jClass.getSuperClass();
        return getGenericType(sup, jClass);
    }
    
    private Type getGenericType(Type sup, JavaClass subclass) throws RuntimeException {
        if (sup.getActualTypeArguments() != null) {
            return sup;
        }
        Class clazz = getReflectionClass(subclass);
        ParameterizedType pSup = (ParameterizedType) clazz.getGenericSuperclass();
        return createQdoxType(pSup, sup);
    }

    private Type getGenericInterface(Type iface, JavaClass implementor) {
        if (iface.getActualTypeArguments() != null) {
            return iface;
        }
        Class clazz = getReflectionClass(implementor);
        ParameterizedType pIface = selectInterface(clazz, iface);
        return createQdoxType(pIface, iface);
    }

    private ParameterizedType selectInterface(Class clazz, Type iface) {
        ParameterizedType pIfaceX = null;
        for (java.lang.reflect.Type tIface: clazz.getGenericInterfaces()) {
            ParameterizedType pIface = (ParameterizedType) tIface;
            Class ifaceClass = (Class) pIface.getRawType();
            System.out.println(ifaceClass.getCanonicalName() + "-" + iface.getFullyQualifiedName());
            if (ifaceClass.getCanonicalName().equals(iface.getFullyQualifiedName())) {
                pIfaceX = pIface;
                break;
            }
        }
        return pIfaceX;
    }
    
    private TypeDef newTypeDef(String name, List<TypeDef> params) {
        TypeDef type = new TypeDef(name);
        try {
            fActualArgumentTypes.set(type, params);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return type;
    }
    
    private static final Field fActualArgumentTypes;
    static {
        try {
            fActualArgumentTypes = TypeDef.class.getField("actualArgumentTypes");
            fActualArgumentTypes.setAccessible(true);
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
    
}
