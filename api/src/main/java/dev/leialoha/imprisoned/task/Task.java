package dev.leialoha.imprisoned.task;

import java.lang.reflect.InvocationTargetException;

public class Task {

    public Task call() {
        TaskManager.INSTANCE.get(getClass()).forEach(
            data -> {
                try {
                    data.target.invoke(data.source, this);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        );

        return this;
    }

}
