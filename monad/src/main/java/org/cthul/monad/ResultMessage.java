package org.cthul.monad;

import org.cthul.monad.Status;

public class ResultMessage {
    
    private String scope;
    private int code;
    private String status;
    private String message;

    public ResultMessage() {
    }

    public ResultMessage(String scope, int code, String status, String message) {
        this.scope = scope;
        this.code = code;
        this.status = status;
        this.message = message;
    }

    @SuppressWarnings("OverridableMethodCallInConstructor")
    public ResultMessage(String scope, Status status, String message) {
        this.scope = scope;
        this.message = message;
        setStatus(status);
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStatus(Status status) {
        this.status = status.getDescription();
        this.code = status.getCode();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
