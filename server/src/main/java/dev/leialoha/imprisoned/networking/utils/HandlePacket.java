package dev.leialoha.imprisoned.networking.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import dev.leialoha.imprisoned.task.TaskPriority;
import net.minecraft.network.protocol.Packet;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface HandlePacket {

    Class<? extends Packet<?>> value();
    TaskPriority priority() default TaskPriority.NORMAL;

}
