package org.somuga.aspect;

import java.util.Date;


public class Error {
    private String message;
    private String path;
    private int status;
    private String method;
    private Date timestamp;

    public Error() {
    }

    public Error(String message, String path, int status, String method, Date timestamp) {
        this.message = message;
        this.path = path;
        this.status = status;
        this.method = method;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
