package dev.cross.bingo;

import dev.cross.bingo.item.BingoCard;
import dev.cross.blissfulcore.BlissfulCore;
import org.bukkit.plugin.java.JavaPlugin;

public final class Bingo extends JavaPlugin {
    private static JavaPlugin plugin;

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        BlissfulCore.registerItem(new BingoCard());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
