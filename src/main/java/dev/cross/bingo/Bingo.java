package dev.cross.bingo;

import dev.cross.bingo.command.BingoStateCommand;
import dev.cross.bingo.command.RedButtonCommand;
import dev.cross.bingo.item.BingoCard;
import dev.cross.bingo.ui.BingoHUD;
import dev.cross.bingo.ui.BingoTextUtils;
import dev.cross.blissfulcore.BlissfulCore;
import dev.cross.blissfulcore.api.BlissfulAPI;
import dev.cross.blissfulcore.api.BlissfulTeams;
import dev.cross.blissfulcore.item.BlissfulItem;
import dev.cross.blissfulcore.ui.BColors;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public final class Bingo extends JavaPlugin {

    public static class State {
        public static final int MAX_SCORE = 2;

        private HashSet<Integer> validDigits;

        private final BlissfulTeams teamOne;
        private final BlissfulTeams teamTwo;

        private final HashMap<BlissfulTeams, Integer> score;

        private final BukkitTask hudTask;

        private State(BlissfulTeams teamOne, BlissfulTeams teamTwo) {
            this.validDigits = new HashSet<>();

            this.teamOne = teamOne;
            this.teamTwo = teamTwo;

            this.score = new HashMap<>();

            this.hudTask = Bukkit.getScheduler().runTaskTimer(Bingo.getPlugin(), new BingoHUD(this), 0L, 20L);
            this.startRound();
        }

        public BlissfulTeams[] getTeams() {
            return new BlissfulTeams[]{teamOne, teamTwo};
        }

        public int getScore(BlissfulTeams team) {
            return score.getOrDefault(team, 0);
        }

        public boolean isValid(int i) {
            return this.validDigits.contains(i);
        }

        private void startRound() {
            this.validDigits = new HashSet<>();

            for (Player p : Bukkit.getOnlinePlayers()) {
                HashSet<ItemStack> stackToRemove = new HashSet<>();
                p.getInventory().forEach(it -> {
                    if (BlissfulItem.isCustom(it)) {
                        stackToRemove.add(it);
                    }
                });
                stackToRemove.forEach(it -> p.getInventory().remove(it));
            }

            new BukkitRunnable() {
                @Override
                public void run() {
                    for (Player p : teamOne.getPlayers()) {
                        p.getInventory().addItem(BINGO_CARD.createItem());
                    }

                    for (Player p : teamTwo.getPlayers()) {
                        p.getInventory().addItem(BINGO_CARD.createItem());
                    }
                    String yellow = ChatColor.of(BColors.YELLOW.asHexString()).toString();
                    Bukkit.getOnlinePlayers().forEach(it -> it.sendMessage(yellow + " ⏺  New round starting now!"));
                }
            }.runTaskLater(getPlugin(), 60L);
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
                    String yellow = ChatColor.of(BColors.YELLOW.asHexString()).toString();
                    String purple = ChatColor.of(BColors.LIGHT_PURPLE.asHexString()).toString();
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.sendTitle(BingoTextUtils.getGoldenString(finalRand + ""), "", 0, 80, 0);
                        p.sendMessage(purple + " ⏺  " + "The number was " + yellow + org.bukkit.ChatColor.BOLD +  finalRand);
                    }
                }
            }.runTaskLater(Bingo.getPlugin(), 20L);
        }


        public void declareWinner(Player player) {
            Optional<BlissfulTeams> winnerTeam = BlissfulAPI.getImpl().getTeamFrom(player);
            if (winnerTeam.isEmpty()) return;
            if (Arrays.stream(getTeams()).noneMatch(it -> it == winnerTeam.get())) return;

            score.put(winnerTeam.get(), getScore(winnerTeam.get()) + 1);

            String yellow = ChatColor.of(BColors.YELLOW.asHexString()).toString();
            String teamName = winnerTeam.get().getDisplayName();
            Bukkit.getOnlinePlayers().forEach(it -> { it.sendTitle(teamName, yellow + "has won this round!", 0, 100, 0); it.playSound(it, Sound.ENTITY_WARDEN_SONIC_BOOM, 1.0f, 2.0f); });

            if (getScore(winnerTeam.get()) >= MAX_SCORE) {
                Bingo.stopState();
                return;
            }
            this.startRound();
        }

        public void stop() {
            this.hudTask.cancel();
        }
    }

    private static JavaPlugin plugin;
    public static final BingoCard BINGO_CARD = new BingoCard();
    private static State STATE = null;

    public static Optional<State> getState() {
        if (STATE == null) {
            return Optional.empty();
        }
        return Optional.of(STATE);
    }

    public static void startWith(BlissfulTeams teamOne, BlissfulTeams teamTwo) {
        STATE = new State(teamOne, teamTwo);
    }

    public static void stopState() {
        if (STATE == null) return;
        STATE.stop();
        STATE = null;
    }

    public static JavaPlugin getPlugin() {
        return plugin;
    }

    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        BlissfulCore.registerItem(BINGO_CARD);
        Objects.requireNonNull(getCommand("red-button")).setExecutor(new RedButtonCommand());
        Objects.requireNonNull(getCommand("bingo")).setExecutor(new BingoStateCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
