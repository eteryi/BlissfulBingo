package dev.cross.bingo.command;

import dev.cross.bingo.ui.RedButton;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RedButtonCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player p) {
            RedButton button = new RedButton();
            Location location = p.getLocation();
            location.setPitch(0);
            location.setX(Math.ceil(location.getX()) - 0.5);
            location.setZ(Math.ceil(location.getZ()) - 0.5);
            location.setY(Math.ceil(location.getY()));
            location.setYaw(0);
            button.spawn(location);
        }
        return true;
    }
}
