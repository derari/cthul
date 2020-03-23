package org.cthul.monad;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public enum DefaultStatus implements Status {
    
    OK(200),
    ACCEPTED(202),
    NO_VALUE(204),
    
    BAD_REQUEST(400),
    FORBIDDEN(403),
    NOT_FOUND(404),
    UNPROCESSABLE(422),
    
    INTERNAL(500),
    GATEWAY(502),
    TIMEOUT(504),
    UNKNOWN(591),
    
    ;
    
    final int code;

    private DefaultStatus(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return getCode() + " " + toString();
    }

    @Override
    public DefaultStatus normalize() {
        return this;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
    
    public static DefaultStatus fromStatus(Status status) {
        if (status == null) {
            throw new NullPointerException("status");
        }
        if (status instanceof DefaultStatus) {
            return (DefaultStatus) status;
        }
        return fromCode(status.getCode());
    }
    
    public static DefaultStatus fromCode(int code) {
        DefaultStatus value = BY_CODE.get(code);
        if (value != null) return value;
        value = BY_CODE.get(100 * (code/100));
        if (value != null) return value;
        return UNKNOWN;
    }
    
    private static final Map<Integer, DefaultStatus> BY_CODE = new HashMap<>();
    
    static {
        Stream.of(values()).forEach(status -> BY_CODE.put(status.code, status));
    }
            
}
