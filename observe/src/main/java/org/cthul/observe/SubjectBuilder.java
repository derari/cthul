package org.cthul.observe;

import java.util.ArrayList;
import java.util.List;

public class SubjectBuilder implements Subject.Builder {

    private final HeraldBuilder herald;
    private final List<Observer> observers = new ArrayList<>();

    public SubjectBuilder() {
        this.herald = new HeraldBuilder(this);
    }

    @SuppressWarnings("LeakingThisInConstructor")
    protected SubjectBuilder(SubjectBuilder source) {
        this.herald = source.herald.copyForSubject(this);
        this.observers.addAll(source.observers);
    }

    @Override
    public SubjectBuilder copy() {
        return new SubjectBuilder(this);
    }

    @Override
    public Subject build() {
        return copy();
    }

    @Override
    public HeraldBuilder getHerald() {
        return herald;
    }

    @Override
    public List<Observer> getObserverList() {
        return observers;
    }

    @Override
    public String toString() {
        return "Subject@" + Integer.toHexString(hashCode()) + "[" + observers.size() + "]";
    }
}
