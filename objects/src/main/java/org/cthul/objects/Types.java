package org.cthul.objects;

import java.util.*;

/**
 * Utility functions for class relations.
 */
public class Types {

    /**
     * Alias for {@link #superclasses(java.lang.Class)}.
     * @param clazz the class
     * @return ordered set
     */
    public static Set<Class<?>> getSuperclasses(Class<?> clazz) {
        return superclasses(clazz);
    }
    
    /**
     * Returns an ordered set of superclasses of {@code clazz}. 
     * If {@code clazz} is a class, it begins with {@code clazz}, followed by 
     * its superclasses and then its interfaces in breadth-first order;
     * if {@code clazz} is an interface, it begins with {@code Object.class}, 
     * followed by {@code clazz} and its super interfaces.
     * @param clazz the class
     * @return ordered set
     */
    public static Set<Class<?>> superclasses(Class<?> clazz) {
        final Set<Class<?>> result = new LinkedHashSet<>();
        final Queue<Class<?>> queue = new ArrayDeque<>();
        if (clazz.isInterface()) {
            queue.add(Object.class);
        }
        while (clazz != null) {
            queue.add(clazz);
            clazz = clazz.getSuperclass();
        }
        while (!queue.isEmpty()) {
            Class<?> c = queue.remove();
            if (result.add(c)) {
                queue.addAll(Arrays.asList(c.getInterfaces()));
            }
        }
        return result;
    }
    
    /**
     * Returns common superclasses of all {@code classes}.
     * This may include elements of {@code classes} itself.
     * @param classes the classes
     * @return set of superclasses
     */
    public static Set<Class<?>> commonSuperclasses(Class<?>... classes) {
        return commonSuperclasses(Arrays.asList(classes));
    }
    
    /**
     * Returns common superclasses of all {@code classes}.
     * This may include elements of {@code classes} itself.
     * @param classes the classes
     * @return set of superclasses
     */
    public static Set<Class<?>> commonSuperclasses(Iterable<Class<?>> classes) {
        Iterator<Class<?>> it = classes.iterator();
        if (!it.hasNext()) {
            return Collections.emptySet();
        }
        // begin with set from first hierarchy
        Set<Class<?>> result = getSuperclasses(it.next());
        // remove non-superclasses of remaining
        while (it.hasNext()) {
            Class<?> c = it.next();
            Iterator<Class<?>> resultIt = result.iterator();
            while (resultIt.hasNext()) {
                Class<?> sup = resultIt.next();
                if (!sup.isAssignableFrom(c)) {
                    resultIt.remove();
                }
            }
        }
        return result;
    }
    
    /**
     * Returns the lowest common superclasses of {@code classes}.
     * @param classes
     * @return list of lowest common superclasses
     */
    public static List<Class<?>> lowestCommonSuperclasses(Class<?>... classes) {
        return lowestCommonSuperclasses(Arrays.asList(classes));
    }
    
    /**
     * Returns the lowest common superclasses of {@code classes}.
     * @param classes
     * @return list of lowest common superclasses
     */
    public static List<Class<?>> lowestCommonSuperclasses(Iterable<Class<?>> classes) {
        Collection<Class<?>> commonSupers = commonSuperclasses(classes);
        return lowestClasses(commonSupers);
    }
    
    /**
     * Returns a subset of {@code classes}, containing all elements
     * that do not have a subclass in {@code classes}.
     * @param classes the classes
     * @return list of lowest classes
     */
    public static List<Class<?>> lowestClasses(Class<?>... classes) {
        return lowestClasses(Arrays.asList(classes));
    }
    
    /**
     * Returns a subset of {@code classes}, containing all elements
     * that do not have a subclass in {@code classes}.
     * @param classes the classes
     * @return list of lowest classes
     */
    public static List<Class<?>> lowestClasses(Collection<Class<?>> classes) {
        final LinkedList<Class<?>> source = new LinkedList<>(classes);
        final ArrayList<Class<?>> result = new ArrayList<>(classes.size());
        while (!source.isEmpty()) {
            Iterator<Class<?>> srcIt = source.iterator();
            Class<?> c = Object.class;
            while (srcIt.hasNext()) {
                Class<?> c2 = srcIt.next();
                if (c2.isAssignableFrom(c)) {
                    srcIt.remove();
                } else if (c.isAssignableFrom(c2)) {
                    c = c2;
                    srcIt.remove();
                }
            }
            // in diamond hierarchies, result may already contain a subclass
            boolean diamond = false;
            for (Class<?> c2: result) {
                if (c.isAssignableFrom(c2)) {
                    diamond = true;
                    break;
                }
            }
            if (!diamond) {
                result.add(c);
            }
        }
        result.trimToSize();
        return result;
    }
}
