package dev.leialoha.imprisoned.commands;

import java.util.List;
import java.util.Map;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import dev.leialoha.imprisoned.changelogs.Changelog;
import dev.leialoha.imprisoned.changelogs.ChangelogHandler;
import net.kyori.adventure.text.Component;
import net.minecraft.resources.Identifier;

public class ChangelogCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Map<Identifier, Changelog> changelogs = ChangelogHandler.getChangelogs();

        sender.sendMessage(
            Component.empty()
                .append(Component.translatable(
                    () -> "imprisoned.changelog.list", 
                    "Changelogs (%s/%s):", 
                    List.of(
                        Component.text(1),
                        Component.text(1)
                    )
                ))
        );

        changelogs.forEach((identifier, changelog) -> {
            sender.sendMessage(
                Component.empty()
                    .append(Component.translatable(
                        () -> "imprisoned.changelog.name", 
                        "%s (%s)", 
                        List.of(
                            Component.text(identifier.getPath()),
                            Component.text(identifier.getNamespace())
                        )
                    ))
                    .append(
                        changelog.asComponent()
                    )
            );
        });

        return true;
    }

}
