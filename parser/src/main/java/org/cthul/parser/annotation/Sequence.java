package org.cthul.parser.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;
import org.cthul.parser.grammar.sequence.*;

/**
 * Defines a production rule for a sequence of items. The annotated method
 * should expect a single parameter of type {@link List}
 * @author Arian Treffer
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Sequence {

    /**
     * May be ommitted if a {@link Name} annotation is used.
     * @return
     */
    public String name() default "";

    /**
     * Definition for an item of the sequence.
     * <p>
     * If it contains multiple elements, 
     * they all will be added to the result list.
     *
     * @return
     */
    public String item();

    /**
     * Definition for the separators between the items.
     * May be empty or null.
     * <p>
     * By default, separators are not added to the result list.
     * This can be changed with
     * {@link Sequence#includeSeparator() includeSeparator}.
     * If it contains multiple elements,
     * they all will be added to the result list.
     * @return
     */
    public String separator() default "";

    /**
     * If set to true, separators will be added to the result list.
     * @return
     */
    public boolean includeSeparator() default false;

    /**
     * If set to true, empty lists are allowed,
     * even if minSize is greater than zero.
     * @return
     * @see Sequence#minSize() 
     */
    public boolean optional() default false;

    /**
     * The minimum size of the sequence. If it is zero, the optional-flag will
     * be ignored.
     * @return
     * @see Sequence#optional() 
     */
    public int minSize() default 0;

    /**
     * Number of items that have to be added to the minimum size with each step.
     * @return
     */
    public int step() default 1;
    
    /**
     * Defines the sequence implementation.
     * @return 
     * @see AbstractSequenceStrategy
     * @see ArrayListStrategy
     * @see HashMapStrategy
     * @see HashSetStrategy
     * @see LinkedListStrategy
     * @see TreeMapStrategy
     * @see TreeSetStrategy
     */
    public Class<? extends SequenceStrategy> sequenceImpl() default ArrayListStrategy.class;

}
