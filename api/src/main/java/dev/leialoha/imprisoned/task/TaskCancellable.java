package dev.leialoha.imprisoned.task;

public class TaskCancellable extends Task {
    
    private boolean cancelled = false;

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return cancelled;
    }

}
