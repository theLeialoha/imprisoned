package dev.leialoha.imprisoned.task;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskManager<T> {
    
    public static final TaskManager<Task> INSTANCE = new TaskManager<>(Task.class); 

    Class<? extends T> target;

    public TaskManager(Class<? extends T> target) {
        this.target = target;
    }

    private final Map<Class<? extends T>, List<TaskData>> REGISTRY_MAP = new HashMap<>();

    protected void sortHandlers(Class<? extends T> clazz) {
        final List<TaskData> sorted = REGISTRY_MAP.getOrDefault(clazz, List.of())
            .stream().sorted((e1, e2) -> e1.priority.compareTo(e2.priority)).toList();

        REGISTRY_MAP.put(clazz, sorted);
    }

    protected boolean methodHasRequiredParams(Method method) {
        if (method.getParameterTypes().length != 1) return false;
        return target.isAssignableFrom(method.getParameterTypes()[0]);
    }

    protected TaskHandler methodGetTaskTarget(Method method) {
        return (method.isAnnotationPresent(TaskHandler.class))
            ? method.getAnnotation(TaskHandler.class)
            : null;
    }

    protected Class<? extends T> methodGetTask(Method method) {
        return (methodHasRequiredParams(method))
            ? (Class<? extends T>) method.getParameterTypes()[0]
            : null;
    }

    public final List<TaskData> get(Class<? extends T> clazz) {
        sortHandlers(clazz);
        return REGISTRY_MAP.get(clazz);
    }

    protected final void cleanMap(boolean removeOnlyEmpty) {
        Map.copyOf(REGISTRY_MAP).forEach((k, v) -> {
            if (!removeOnlyEmpty || v.isEmpty())
                REGISTRY_MAP.remove(k);
        });
    }

    public void register(Object listener) {
        Class<?> clazz = listener.getClass();
        List.of(clazz.getMethods()).forEach(m -> register(listener, m));
    }

    public void register(Object listener, Method method, TaskPriority priority) {
        if (!methodHasRequiredParams(method)) return;
        method.setAccessible(true);

        Class<? extends T> task = methodGetTask(method);
        TaskData data = new TaskData(listener, method, priority);
        
        REGISTRY_MAP.computeIfAbsent(task, p -> new ArrayList<>())
            .add(data);
    }

    protected void register(Object listener, Method method) {
        TaskHandler target = methodGetTaskTarget(method);
        if (target != null) register(listener, method, target.priority());
    }

    public void unregister(Object listener) {
        Map.copyOf(REGISTRY_MAP).values()
            .forEach(list -> list.removeIf(v -> v.source.equals(listener)));

        cleanMap(true);
    }

    public void unregister(Object listener, Class<? extends T> task) {
        if (!REGISTRY_MAP.containsKey(task)) return;

        REGISTRY_MAP.get(task)
            .removeIf(v -> v.source.equals(listener));

        cleanMap(true);
    }

}
