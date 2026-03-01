package dev.leialoha.imprisoned.task;

import java.lang.reflect.Method;

public class TaskData {
    
    public final Object source;
    public final Method target;
    public final TaskPriority priority;

    public TaskData(Object source, Method target, TaskPriority priority) {
        this.source = source;
        this.target = target;
        this.priority = priority;
    }

}
