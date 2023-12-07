package org.cthul.observe;

public interface Subject extends ObserverList {

    static Builder builder() {
        return new SubjectBuilder();
    }

    Herald getHerald();

    Subject.Builder copy();

    interface Builder extends Subject {
        
        @Override
        Herald.Builder getHerald();

        Subject build();
    }
}
