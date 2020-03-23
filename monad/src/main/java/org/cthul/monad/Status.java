package org.cthul.monad;

public interface Status {
    
    int getCode();
    
    String getDescription();
    
    default DefaultStatus normalize() {
        return DefaultStatus.fromStatus(this);
    }
    
    default boolean isOk() {
        return getCode() / 100 == 2;
    }
    
    default boolean isIllegal() {
        return getCode() / 100 == 4;
    }
    
    default boolean isNotFound() {
        return getCode() == 404;
    }
    
    default boolean isInternal() {
        return getCode() / 100 == 5;
    }
    
    interface Delegate extends Status {
        
        Status getStatus();
        
        default int getCode() {
            return getStatus().getCode();
        }
    
        default String getDescription() {
            return getStatus().getDescription();
        }

        default DefaultStatus normalize() {
            return getStatus().normalize();
        }

        default boolean isOk() {
            return getStatus().isOk();
        }

        default boolean isIllegal() {
            return getStatus().isIllegal();
        }

        default boolean isNotFound() {
            return getStatus().isNotFound();
        }

        default boolean isInternal() {
            return getStatus().isInternal();
        }
        
    }
}
