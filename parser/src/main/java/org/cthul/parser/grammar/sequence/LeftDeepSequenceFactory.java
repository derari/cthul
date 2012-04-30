package org.cthul.parser.grammar.sequence;

import java.util.Arrays;
import java.util.Collection;
import org.cthul.parser.Context;
import org.cthul.parser.Priority;
import org.cthul.parser.Value;
import org.cthul.parser.annotation.Sequence;
import org.cthul.parser.grammar.earley.AbstractProduction;
import org.cthul.parser.grammar.earley.EmptyProduction;
import org.cthul.parser.grammar.earley.Production;

/**
 *
 * @author Arian Treffer
 */
public class LeftDeepSequenceFactory<E, S> {
    
    private String name;
    private String itemProduction;
    private String separatorProduction;
    private boolean includeSeparator;
    private boolean optional;
    private int minSize;
    private int stepSize;
    private int priority;
    private SequenceStrategy<E, S> strategy;
    
    private String stepSuffix = "_STEP";

    public LeftDeepSequenceFactory() {
    }

    public LeftDeepSequenceFactory(String name, String itemProduction, 
                    String separatorProduction, boolean includeSeparator, 
                    boolean optional, int minSize, int step, int priority,
                    SequenceStrategy<E, S> strategy, String stepSuffix) {
        this.name = name;
        this.itemProduction = itemProduction;
        this.separatorProduction = separatorProduction;
        this.includeSeparator = includeSeparator;
        this.optional = optional;
        this.minSize = minSize;
        this.stepSize = step;
        this.priority = priority;
        this.strategy = strategy;
        this.stepSuffix = stepSuffix;
    }

    public LeftDeepSequenceFactory(String name, String itemProduction, 
                    String separatorProduction, boolean includeSeparator, 
                    boolean optional, int minSize, int step, int priority,
                    SequenceStrategy<E, S> strategy) {
        this(name, itemProduction, separatorProduction, includeSeparator, 
                optional, minSize, step, priority, strategy, "_STEP");
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public LeftDeepSequenceFactory(Sequence sequence, int priority,
                    String stepSuffix) {
        setConfiguration(sequence);
        this.priority = priority;
        this.stepSuffix = stepSuffix;
    }
    
    public LeftDeepSequenceFactory(Sequence sequence, int priority) {
        this(sequence, priority, "_STEP");
    }
    
    @SuppressWarnings("unchecked")
    public void setConfiguration(Sequence sequence) {
        this.name = sequence.name();
        this.itemProduction = sequence.item();
        this.separatorProduction = sequence.separator();
        this.includeSeparator = sequence.includeSeparator();
        this.optional = sequence.optional();
        this.minSize = sequence.minSize();
        this.stepSize = sequence.step();
        this.strategy = SequenceStrategies.get(sequence.sequenceImpl());
    }

    public boolean isIncludeSeparator() {
        return includeSeparator;
    }

    public void setIncludeSeparator(boolean includeSeparator) {
        this.includeSeparator = includeSeparator;
    }

    public String getItemProduction() {
        return itemProduction;
    }

    public void setItemProduction(String itemProduction) {
        this.itemProduction = itemProduction;
    }

    public int getMinSize() {
        return minSize;
    }

    public void setMinSize(int minSize) {
        this.minSize = minSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getSeparatorProduction() {
        return separatorProduction;
    }

    public void setSeparatorProduction(String separatorProduction) {
        this.separatorProduction = separatorProduction;
    }

    public int getStep() {
        return stepSize;
    }

    public void setStep(int step) {
        this.stepSize = step;
    }

    public String getStepSuffix() {
        return stepSuffix;
    }

    public void setStepSuffix(String stepSuffix) {
        this.stepSuffix = stepSuffix;
    }

    public SequenceStrategy<E, S> getStrategy() {
        return strategy;
    }

    public void setStrategy(SequenceStrategy<E, S> strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(Class<? extends SequenceStrategy<E, S>> strategy) {
        this.strategy = SequenceStrategies.get(strategy);
    }
    
    private int nonEmptyMinSize() {
        if (minSize < 0) {
            throw new IllegalArgumentException("negative minSize");
        }
        if (minSize == 0) return 1;
        return minSize;
    }
    
    private boolean emptyAllowed() {
        return optional || minSize == 0;
    }
        
    private String stepName() {
        return name + stepSuffix;
    }
    
    public void addTo(Collection<? super Production> target) {
        target.add(createRoot());
        if (emptyAllowed()) {
            target.add(createEmpty());
        }
        
        String[] item = Priority.split(itemProduction);
        int[] itemPrio = Priority.createPriorityArray(item);
        Priority.parse(item, itemPrio, name, priority);
        
        String[] sep = Priority.split(separatorProduction);
        int[] sepPrio = Priority.createPriorityArray(sep);
        Priority.parse(sep, sepPrio, name, priority);
        
        target.add(createInit(item, itemPrio, sep, sepPrio));
        target.add(createStep(item, itemPrio, sep, sepPrio));
    }
    
    private Empty<E, S> createEmpty() {
        return new Empty<E, S>(name, priority, strategy);
    }
    
    private Init<E, S> createInit(String[] item, int[] itemPrio,
                        String[] sep, int[] sepPrio) {
        final int min = nonEmptyMinSize();
        final int initLen = item.length * min + sep.length * (min-1);
        String[] init = Arrays.copyOf(item, initLen);
        int[] initPrio = Arrays.copyOf(itemPrio, initLen);
        for (int i = item.length; i < initLen;) {
            System.arraycopy(item, 0, init, i, item.length);
            System.arraycopy(itemPrio, 0, initPrio, i, item.length);
            i += item.length;
            System.arraycopy(sep, 0, init, i, sep.length);
            System.arraycopy(sepPrio, 0, initPrio, i, sep.length);
            i += sep.length;
        }
        return new Init<E, S>(stepName(), priority, init, initPrio, strategy, 
                              item.length, sep.length, includeSeparator);
    }
    
    private Step<E, S> createStep(String[] item, int[] itemPrio,
                        String[] sep, int[] sepPrio) {
        final int stepLen = 1 + stepSize*(item.length  + sep.length);
        String[] step = new String[stepLen];
        step[0] = stepName();
        int[] stepPrio = new int[stepLen];
        stepPrio[0] = priority;
        for (int i = 1; i < stepLen;) {
            System.arraycopy(sep, 0, step, i, sep.length);
            System.arraycopy(sepPrio, 0, stepPrio, i, sep.length);
            i += sep.length;
            System.arraycopy(item, 0, step, i, item.length);
            System.arraycopy(itemPrio, 0, stepPrio, i, item.length);
            i += item.length;
        }
        return new Step<E, S>(stepName(), priority, step, stepPrio, strategy, 
                              item.length, sep.length, includeSeparator);
    }
    
    private Root<S> createRoot() {
        return new Root<S>(name, priority, stepName());
    }
    
    private static <E, S> S addAllToSequence(
                        final S seq, final Value<?>[] args, int firstItem,
                        final SequenceStrategy<E, S> strategy) {
        for (int i = firstItem; i < args.length; i++) {
            @SuppressWarnings("unchecked")
            E e = (E) args[i].getValue();
            strategy.add(seq, e);
        }
        return seq;
    }
    
    private static <S, E> S addItemsToSequence(
                        final S seq, final Value<?>[] args, int firstItem,
                        final SequenceStrategy<E, S> strategy, 
                        final int itemCount, final int sepCount) {
        int i = firstItem;
        while (i < args.length) {
            for (int j = 0; j < itemCount; j++, i++) {
                @SuppressWarnings("unchecked")
                E e = (E) args[i].getValue();
                strategy.add(seq, e);
            }
            i += sepCount;
        }
        return seq;
    }
    
    private static class Empty<E, S> extends EmptyProduction {
        
        private final SequenceStrategy<E, S> strategy;

        public Empty(String name, int priority, SequenceStrategy<E, S> strategy) {
            super(name, priority);
            this.strategy = strategy;
        }

        @Override
        protected Object defaultValue(Context context) {
            return strategy.newInstance();
        }
        
    }
    
    private static class Init<E, S> extends AbstractProduction {
        
        private final SequenceStrategy<E, S> strategy;
        private final int itemCount;
        private final int sepCount;
        private final boolean includeSep;

        public Init(String left, int priority, String[] right, int[] priorities, SequenceStrategy<E, S> strategy, int itemCount, int sepCount, boolean includeSep) {
            super(left, priority, right, priorities);
            this.strategy = strategy;
            this.itemCount = itemCount;
            this.sepCount = sepCount;
            this.includeSep = includeSep;
        }

        @Override
        public Object invoke(Context context, final Value<?>... args) {
            final S seq = strategy.newInstance();
            if (includeSep) {
                return addAllToSequence(seq, args, 0, strategy);
            } else {
                return addItemsToSequence(seq, args, 0, strategy, itemCount, sepCount);
            }
        }
        
    }
    
    private static class Step<E, S> extends AbstractProduction {
        
        private final SequenceStrategy<E, S> strategy;
        private final int itemCount;
        private final int sepCount;
        private final boolean includeSep;

        public Step(String left, int priority, String[] right, int[] priorities, SequenceStrategy<E, S> strategy, int itemCount, int sepCount, boolean includeSep) {
            super(left, priority, right, priorities);
            this.strategy = strategy;
            this.itemCount = itemCount;
            this.sepCount = sepCount;
            this.includeSep = includeSep;
        }

        @Override
        public Object invoke(Context context, final Value<?>... args) {
            @SuppressWarnings("unchecked")
            final S seq = (S) args[0].getValue();
            if (includeSep) {
                return addAllToSequence(seq, args, 1, strategy);
            } else {
                return addItemsToSequence(seq, args, 1+sepCount, strategy, itemCount, sepCount);
            }
        }
        
    }
    
    private static class Root<S> extends AbstractProduction {

        public Root(String left, int priority, String right) {
            super(left, priority, right);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object invoke(Context context, Value<?>... args) {
            return (S) args[0].getValue();
        }
        
    }
    
}
