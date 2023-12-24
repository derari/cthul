package org.cthul.observe;

public interface Subject extends ObserverList, Adaptive {

    static Builder builder() {
        return new SubjectBuilder();
    }

    Herald getHerald();

    Subject.Builder copy();

    @Override
    default <T> T as(Class<T> clazz) {
        return getHerald().as(clazz);
    }

    interface Builder extends Subject {
        
        @Override
        Herald.Builder getHerald();

        Subject build();
    }
}
