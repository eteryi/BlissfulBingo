package dev.cross.bingo;

import dev.cross.bingo.command.RedButtonCommand;
import dev.cross.bingo.item.BingoCard;
import dev.cross.blissfulcore.BlissfulCore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;

public final class Bingo extends JavaPlugin {
    public static class State {
        HashSet<Integer> validDigits;

        private State() {
            this.validDigits = new HashSet<>();
        }

        public boolean isValid(int i) {
            return this.validDigits.contains(i);
        }

        private void reset() {
            this.validDigits = new HashSet<>();
        }

        public void roll() {
            Random random = new Random(System.currentTimeMillis());
            int rand = random.nextInt(1, 65);
            while (validDigits.contains(rand)) {
                rand = random.nextInt(1, 65);
            }
            validDigits.add(rand);

            for (Player p : Bukkit.getOnlinePlayers()) {
                p.sendTitle("The number is...", "", 10, 20, 0);
            }
            int finalRand = rand;
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendTitle(finalRand + "", "", 0, 100, 20);
                    }
                }
            }.runTaskLater(Bingo.getPlugin(), 20L);
        }

        public void declareWinner(Player player) {
            player.sendMessage("You have just won at Bingo! Congratz!");
            this.reset();
        }
    }

    private static JavaPlugin plugin;
    public static final State STATE = new State();

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        BlissfulCore.registerItem(new BingoCard());
        Objects.requireNonNull(getCommand("red-button")).setExecutor(new RedButtonCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
