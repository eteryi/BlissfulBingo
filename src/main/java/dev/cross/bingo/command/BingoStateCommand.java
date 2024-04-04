package dev.cross.bingo.command;

import dev.cross.bingo.Bingo;
import dev.cross.blissfulcore.api.BlissfulTeams;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class BingoStateCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        // /bingo start topaz ruby
        // /bingo stop
        if (!commandSender.isOp()) return false;

        String action = strings.length == 0 ? "" : strings[0];

        switch (action) {
            case "start": {
                String teamOneID = strings[1];
                String teamTwoID = strings[2];

                BlissfulTeams teamOne;
                BlissfulTeams teamTwo;
                try {
                    teamOne = BlissfulTeams.valueOf(teamOneID.toUpperCase());
                    teamTwo = BlissfulTeams.valueOf(teamTwoID.toUpperCase());
                } catch (IllegalArgumentException e) {
                    commandSender.sendMessage("Team not found!");
                    break;
                }

                Bingo.startWith(teamOne, teamTwo);
                commandSender.sendMessage("Bingo has begun!");

                break;
            }
            case "stop": {
                Bingo.stopState();
                commandSender.sendMessage("Bingo was cancelled!");
                break;
            }
            case "win": {
                if (Bingo.getState().isPresent() && commandSender instanceof Player p) {
                    Bingo.getState().get().declareWinner(p);
                }
            }
            default: {
                commandSender.sendMessage("/bingo start <team1> <team2>");
                commandSender.sendMessage("/bingo stop");
            }
        }
        return true;
    }
}
