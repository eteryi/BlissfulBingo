package dev.cross.bingo.ui;

import dev.cross.bingo.Bingo;
import dev.cross.bingo.BingoBoard;
import dev.cross.bingo.BingoElements;
import dev.cross.blissfulcore.ui.BColors;
import dev.cross.blissfulcore.ui.inventory.button.AbstractClickableButton;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;

public class BingoButton extends AbstractClickableButton {
    private final BingoBoard board;
    private boolean isSelected;

    public BingoButton(BingoBoard board) {
        super(3, new Hitbox(Component.text("BINGO!").color(BColors.YELLOW).decorate(TextDecoration.BOLD), List.of(Component.text(""))), new Texture(BingoElements.BINGO_BUTTON.first(), BingoElements.BINGO_BUTTON.second()));
        this.board = board;
        this.isSelected = false;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (this.isSelected) return;

        this.isSelected = true;
        boolean valid = board.isValid();
        boolean bingo = board.isBingo();

        ItemStack stack = this.getTexture();
        stack.setType(BingoElements.SELECTED_BINGO_BUTTON.first());
        ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(BingoElements.SELECTED_BINGO_BUTTON.second());
        stack.setItemMeta(meta);
        int textureSlot = this.getTextureSlot();
        Objects.requireNonNull(event.getClickedInventory()).setItem(textureSlot, stack);

        new BukkitRunnable() {
            @Override
            public void run() {
                isSelected = false;
                ItemStack stack = getTexture();
                event.getClickedInventory().setItem(textureSlot, stack);
            }
        }.runTaskLater(Bingo.getPlugin(), 10L);


        /*
        Logic
         */
        Player p = (Player) event.getWhoClicked();
        if (Bingo.STATE.hasWon(p)) {
            p.sendMessage(ChatColor.of(BColors.RED.asHexString()) + " ⏺ You have already finished this round!");
            return;
        }

        if (!valid) {
            p.sendMessage(ChatColor.of(BColors.RED.asHexString()) + " ⏺ You have selected unrolled numbers in your Bingo card!");
            return;
        }

        if (!bingo) {
            p.sendMessage(ChatColor.of(BColors.RED.asHexString()) + " ⏺ You don't have a valid bingo in your Bingo card!");
            return;
        }

        Bingo.STATE.declareWinner(p);
    }
}
