package org.cthul.objects;

import java.util.*;

/**
 *
 */
public class Types {

    private static Set<Class<?>> getClassesBfs(Class<?> clazz) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        Set<Class<?>> nextLevel = new LinkedHashSet<>();
        nextLevel.add(clazz);
        while (!nextLevel.isEmpty()) {
            classes.addAll(nextLevel);
            Set<Class<?>> thisLevel = nextLevel;
            nextLevel = new LinkedHashSet<>();
            for (Class<?> each : thisLevel) {
                Class<?> superClass = each.getSuperclass();
                if (superClass != null /*&& superClass != Object.class*/) {
                    nextLevel.add(superClass);
                }
                nextLevel.addAll(Arrays.asList(each.getInterfaces()));
            }
        }
        return classes;
    }

    public static List<Class<?>> commonSuperClasses(Class<?>... classes) {
        // start off with set from first hierarchy
        Set<Class<?>> rollingIntersect = new LinkedHashSet<>(
                getClassesBfs(classes[0]));
        // intersect with next
        for (int i = 1; i < classes.length; i++) {
            rollingIntersect.retainAll(getClassesBfs(classes[i]));
        }
        return new LinkedList<>(rollingIntersect);
    }
    
    public static List<Class<?>> lowestCommonSuperClasses(Class<?>... classes) {
        List<Class<?>> commonSupers = commonSuperClasses(classes);
        return lowestClasses(commonSupers);
    }
    
    public static List<Class<?>> commonSuperClasses(Iterable<Class<?>> classes) {
        Iterator<Class<?>> it = classes.iterator();
        // start off with set from first hierarchy
        Set<Class<?>> rollingIntersect = new LinkedHashSet<>(
                getClassesBfs(it.next()));
        // intersect with next
        while (it.hasNext()) {
            rollingIntersect.retainAll(getClassesBfs(it.next()));
        }
        return new ArrayList<>(rollingIntersect);
    }
    
    public static List<Class<?>> lowestCommonSuperClasses(Iterable<Class<?>> classes) {
        List<Class<?>> commonSupers = commonSuperClasses(classes);
        return lowestClasses(commonSupers);
    }
    
    public static List<Class<?>> lowestClasses(List<Class<?>> classes) {
        Set<Class<?>> result = new LinkedHashSet<>(classes);
        for (Class<?> c: classes) {
            Set<Class<?>> cSupers = getClassesBfs(c);
            cSupers.remove(c);
            result.removeAll(cSupers);
        }
        return new ArrayList<>(result);
    }
    
}
