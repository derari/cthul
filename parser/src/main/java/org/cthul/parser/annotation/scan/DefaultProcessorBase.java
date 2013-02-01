package org.cthul.parser.annotation.scan;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.cthul.parser.annotation.Key;
import org.cthul.parser.annotation.Priority;

public abstract class DefaultProcessorBase<Token, Match> implements AnnotationProcessor<Token, Match> {

    protected String selectName(Key name, Object ao, String s, boolean useDefaultName) {
        int i = s.indexOf(" ::=");
        if (i < 0) {
            if (name != null) {
                String n = name.value();
                return n.isEmpty() ? nameOf(ao) : n;
            } else {
                return useDefaultName ? nameOf(ao) : null;
            }
        } else {
            return s.substring(0, i).trim();
        }
    }
    
    protected String nameOf(Object ao) {
        if (ao instanceof Method) {
            return ((Method) ao).getName();
        } else if (ao instanceof Field) {
            return ((Field) ao).getName();
        } else {
            throw new IllegalArgumentException(
                    "Method or field expected: " + ao);
        }
    }
    
    protected String selectRule(String s) {
        int i = s.indexOf(" ::=");
        if (i < 0) {
            return s.trim();
        } else {
            return s.substring(i + " ::=".length()).trim();
        }
    }

    protected int selectPriority(AnnotatedElement m) {
        Priority priority = m.getAnnotation(Priority.class);
        if (priority == null) {
            return Priority.Default;
        } else {
            return priority.value();
        }
    }
    
}
