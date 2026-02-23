package dev.leialoha.imprisoned.mines;

public interface Tickable {

    default void startTicking() {
        TickableHandler.add(this);
    };

    default void stopTicking() {
        TickableHandler.remove(this);
    };

    default boolean isTicking() {
        return TickableHandler.has(this);
    }

    public void onTick();

}
