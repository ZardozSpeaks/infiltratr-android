package com.davidremington.infiltratr.utils;

import java.util.HashMap;
import java.util.Map;

public class RemoteLog {
    private String priority;
    private String tag;
    private String message;
    private String throwable;
    private String time;

    public RemoteLog(String priority, String tag, String message, String throwable, String time) {
        this.priority = priority;
        this.tag = tag;
        this.message = message;
        this.throwable = throwable;
        this.time = time;
    }

    public String getPriority() {
        return priority;
    }

    public String getTag() {
        return tag;
    }

    public String getMessage() {
        return message;
    }

    public String getThrowable() {
        return throwable;
    }

    public String getTime() {
        return time;
    }

    public Map<String, Object> map() {
        Map<String, Object> objectAsMap = new HashMap<>();
        objectAsMap.put("priority", priority);
        objectAsMap.put("tag", tag);
        objectAsMap.put("message", message);
        objectAsMap.put("throwable", throwable);
        objectAsMap.put("time", time);
        return objectAsMap;
    }
}
