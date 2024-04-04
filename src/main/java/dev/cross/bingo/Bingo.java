package dev.cross.bingo;

import dev.cross.bingo.command.RedButtonCommand;
import dev.cross.bingo.item.BingoCard;
import dev.cross.bingo.ui.StringFormatter;
import dev.cross.blissfulcore.BlissfulCore;
import dev.cross.blissfulcore.api.BlissfulAPI;
import dev.cross.blissfulcore.ui.BColors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;

public final class Bingo extends JavaPlugin {

    private static String getOrdinal(int i) {
        String[] ordinals = {"th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th", "th"};
        int j = i % 100;
        return switch (j) {
            case 11 -> "11th";
            case 12 -> "12th";
            case 13 -> "13th";
            default -> i + ordinals[i % 10];
        };
    }

    public static class State {
        private HashSet<Integer> validDigits;
        private static final int baseTokensPerWin = 2000;
        private final HashSet<Player> winners;

        private State() {
            this.validDigits = new HashSet<>();
            this.winners = new HashSet<>();
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
                p.sendTitle(ChatColor.of(BColors.LIGHT_PURPLE.asHexString()) + "The number is...", "", 10, 20, 10);
            }
            int finalRand = rand;
            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendTitle(StringFormatter.getBackgroundString(finalRand + ""), "", 10, 80, 20);
                    }
                }
            }.runTaskLater(Bingo.getPlugin(), 20L);
        }

        public boolean hasWon(Player player) {
            return this.winners.contains(player);
        }

        public void declareWinner(Player player) {
            winners.add(player);

            ChatColor purple = ChatColor.of(BColors.LIGHT_PURPLE.asHexString());
            ChatColor red = ChatColor.of(BColors.RED.asHexString());
            ChatColor yellow = ChatColor.of(BColors.YELLOW.asHexString());

            String finish = yellow + "(" + getOrdinal(this.winners.size()) + ")";
            String message = yellow + " ⏺ " + red + player.getName() + purple + " has finished the Bingo Card  " + finish;

            Bukkit.getOnlinePlayers().forEach(it -> it.sendMessage(message));
            BlissfulAPI.getImpl().setTokensFor(player, BlissfulAPI.getImpl().getTokens(player) + (baseTokensPerWin * (1 / winners.size())));
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
