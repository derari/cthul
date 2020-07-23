package org.cthul.monad;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public enum DefaultStatus implements Status {
    
    OK(200),
    CREATED(201),
    ACCEPTED(202),
    NO_VALUE(204),
    
    NOT_MODIFIED(304),
    
    BAD_REQUEST(400),
    FORBIDDEN(403),
    NOT_FOUND(404),
    UNPROCESSABLE(422),
    
    INTERNAL_ERROR(500),
    NOT_IMPLEMENTED(501),
    BAD_GATEWAY(502),
    UNAVAILABLE(503),
    TIMEOUT(504),
    UNKNOWN(591),
    ;
    
    final int code;
    final String description;

    private DefaultStatus(int code) {
        this.code = code;
        this.description = name().toLowerCase().replace('_', ' ');
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public DefaultStatus normalize() {
        return this;
    }

    @Override
    public String toString() {
        return getCode() + " " + getDescription();
    }
    
    public static DefaultStatus fromString(String message) {
        if (message == null) return UNKNOWN;
        String lower = message.toLowerCase();
        DefaultStatus[] all = values();
        for (int i = all.length - 2; i >= 0; i--) {
            DefaultStatus status = all[i];
            if (lower.contains(status.getDescription())) {
                return status;
            }
        }
        Matcher m = DIGITS.matcher(lower);
        if (m.find()) {
            try {
                int code = Integer.parseInt(m.group());
                return fromCode(code);
            } catch (NumberFormatException ex) { }
        }
        return UNKNOWN;
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
    private static final Pattern DIGITS = Pattern.compile("\\d+");
    
    static {
        Stream.of(values()).forEach(status -> BY_CODE.put(status.code, status));
    }
            
}
