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
    public SubjectBuilder(HeraldBuilder herald) {
        this.herald = herald.forSubject(this);
    }

    public SubjectBuilder(HeraldBuilder herald, List<Observer> observers) {
        this(herald);
        this.observers.addAll(observers);
    }

    @Override
    public SubjectBuilder copy() {
        return new SubjectBuilder(herald.copy(), observers);
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
}
