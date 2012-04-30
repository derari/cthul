package org.cthul.matchers.fluent.gen;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.Type;
import com.thoughtworks.qdox.model.TypeVariable;
import com.thoughtworks.qdox.parser.structs.TypeDef;
import com.thoughtworks.qdox.parser.structs.TypeVariableDef;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Arian Treffer
 */
public class QDoxUtils {
    
    public static boolean isGenericArg(Type argument) {
        String fullName = argument.getFullyQualifiedName();
        return ! fullName.contains(".");
    }

    public static Type resolveGenericTypeBound(JavaDocBuilder doc, Type argument, List<TypeVariable> parameters) {
        TypeDef def = resolveGenericTypeDefBound(doc, argument, parameters);
        return new Type(null, def, 0, argument.getJavaClassParent());
    }
    
    public static TypeDef resolveGenericTypeDefBound(JavaDocBuilder doc, Type argument, List<TypeVariable> parameters) {
        String name;
        if (isGenericArg(argument)) {
            name = resolveGenericClassName(parameters, argument.getFullyQualifiedName());
        } else {
            name = argument.getFullyQualifiedName();
        }
        if (argument.getActualTypeArguments() == null) {
            return new  TypeDef(name);
        }
        List<TypeDef> genericArgs = new ArrayList<>();
        for (Type a: argument.getActualTypeArguments()) {
            genericArgs.add(resolveGenericTypeDefBound(doc, a, parameters));
        }
        return newTypeDef(name, genericArgs);
    }

    private static String resolveGenericClassName(List<TypeVariable> parameters, String name) {
        for (Type p: parameters) {
            if (p.getFullyQualifiedName().equals(name)) {
                if (p.getValue().isEmpty()) {
                    return "java.lang.Object";
                }
                return p.getValue();
            }
        }
        return null;
    }
    
    public static Type getGenericSuperType(JavaClass jClass) {
        Type qSuper = jClass.getSuperClass();
        if (qSuper.getActualTypeArguments() != null) {
            // generic args are included
            return qSuper;
        }
        Class clazz = getReflectionClass(jClass);
        java.lang.reflect.Type tSuper = clazz.getGenericSuperclass();
        if (tSuper instanceof Class) {
            // no generic args
            return qSuper;
        }
        ParameterizedType pSuper = (ParameterizedType) tSuper;
        return addGenericArguments(pSuper, qSuper);
    }
    
    public static Type getGenericInterface(Type iface, JavaClass implementor) {
        if (iface.getActualTypeArguments() != null) {
            return iface;
        }
        Class clazz = getReflectionClass(implementor);
        ParameterizedType pIface = selectInterface(clazz, iface);
        return addGenericArguments(pIface, iface);
    }
    
    public static List<TypeVariable> getTypeParameters(JavaClass qClass) {
        List<TypeVariable> params =  qClass.getTypeParameters();
        if (params != null && !params.isEmpty()) {
            return params;
        }
        Class clazz = getReflectionClass(qClass);
        params =  new ArrayList<>();
        for (java.lang.reflect.TypeVariable vParam: clazz.getTypeParameters()) {
            //System.out.println("<<" + vParam);
            params.add(new TypeVariable(vParam.getName(), new TypeVariableDef(""), qClass.getParent()));
        }
        return params;
    }
    
    private static ParameterizedType selectInterface(Class clazz, Type qIface) {
        for (java.lang.reflect.Type tIface: clazz.getGenericInterfaces()) {
            ParameterizedType pIface = (ParameterizedType) tIface;
            Class ifaceClass = (Class) pIface.getRawType();
            if (ifaceClass.getCanonicalName().equals(qIface.getFullyQualifiedName())) {
                return pIface;
            }
        }
        return null;
    }
    
    private static Class getReflectionClass(JavaClass jClass) throws RuntimeException {
        Class clazz;
        try {
            clazz = Class.forName(jClass.getFullyQualifiedName());
        } catch (Exception e) { throw new RuntimeException(e); }
        return clazz;
    }
    
    private static Type addGenericArguments(ParameterizedType pType, Type original) {
        List<TypeDef> params = new ArrayList<>();
        for (java.lang.reflect.Type tParam: pType.getActualTypeArguments()) {
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
    
    private static TypeDef newTypeDef(String name, List<TypeDef> params) {
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
