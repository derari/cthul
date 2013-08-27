package org.cthul.objects;

import java.util.*;

/**
 *
 */
public class Types {

    /**
     * Returns an ordered set of superclasses of {@code clazz}. 
     * If {@code clazz} is a class, it begins with {@code clazz}, followed by 
     * its superclasses and its interfaces in breadth-first order;
     * if {@code clazz} is an interface, it begins with {@code Object.class}, 
     * follows by {@code clazz} and its super interfaces.
     * @param clazz the class
     * @return ordered set
     */
    public static Set<Class<?>> getSuperclasses(Class<?> clazz) {
        Set<Class<?>> result = new LinkedHashSet<>();
        Queue<Class<?>> queue = new ArrayDeque<>();
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
     * This may include elements of {@code classes}.
     * @param classes the classes
     * @return set
     */
    public static Set<Class<?>> commonSuperclasses(Class<?>... classes) {
        return commonSuperclasses(Arrays.asList(classes));
    }
    
    /**
     * Returns common superclasses of all {@code classes}.
     * This may include elements of {@code classes}.
     * @param classes the classes
     * @return set
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
    
    public static List<Class<?>> lowestCommonSuperclasses(Class<?>... classes) {
        return lowestCommonSuperclasses(Arrays.asList(classes));
    }
    
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
    public static List<Class<?>> lowestClasses(Collection<Class<?>> classes) {
        Class[] array = classes.toArray(new Class[classes.size()]);
        ArrayList<Class<?>> result = new ArrayList<>(classes.size());
        collect: for (int i = 0; i < array.length; i++) {
            Class<?> c = array[i];
            for (int j = i+1; j < array.length; j++) {
                Class<?> c2 = array[j];
                if (c.isAssignableFrom(c2) && !c.equals(c2)) {
                    continue collect;
                }
            }
            for (Class<?> m: result) {
                if (c.isAssignableFrom(m)) {
                    continue collect;
                }
            }
            result.add(c);
        }
        result.trimToSize();
        return result;
    }
    
}
