package dev.cross.bingo.ui;

import dev.cross.bingo.Bingo;
import dev.cross.blissfulcore.api.BlissfulTeams;
import dev.cross.blissfulcore.ui.BColors;
import dev.cross.blissfulcore.ui.utils.StringFormatter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BingoHUD implements Runnable {
    private final Bingo.State state;

    public BingoHUD(Bingo.State state) {
        this.state = state;
    }

    private String getMessage(BlissfulTeams team, boolean reversed) {
        int ts = state.getScore(team);

        StringBuilder message = new StringBuilder(reversed ? ChatColor.GRAY.toString() : team.getColor().toString());

        int firstLoop = reversed ? Bingo.State.MAX_SCORE - ts : ts;
        int secondLoop = reversed ? ts : Bingo.State.MAX_SCORE - ts;

        message.append("⏺ ".repeat(Math.max(0, firstLoop)));

        message.append(reversed ? team.getColor() : ChatColor.GRAY);

        message.append("⏺ ".repeat(Math.max(0, secondLoop)));

        return message.toString();
    }
    @Override
    public void run() {
        // ⏺ ⏺ ⏺  BINGO!  ⏺ ⏺ ⏺
        // R R G           G  G  B
        // R = RED
        // G = GRAY
        // B = BLUE
        BlissfulTeams teamOne = state.getTeams()[0];
        BlissfulTeams teamTwo = state.getTeams()[1];

        String message = getMessage(teamOne, false) + ChatColor.RESET + StringFormatter.customString("  BINGO!  ") + getMessage(teamTwo, true);

        for (Player p : Bukkit.getOnlinePlayers()) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
        }
    }
}
