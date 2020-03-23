package org.cthul.monad;

public class CustomStatus implements Status {
    
    private final int code;
    private final String name;

    public CustomStatus(int code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return code + " " + toString();
    }

    @Override
    public DefaultStatus normalize() {
        return DefaultStatus.fromStatus(this);
    }

    @Override
    public String toString() {
        return name;
    }
}
