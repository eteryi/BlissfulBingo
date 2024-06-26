package dev.cross.bingo.ui;

import dev.cross.bingo.Bingo;
import dev.cross.blissfulcore.ui.BSounds;
import dev.cross.blissfulcore.ui.display.InteractWindowDisplay;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class RedButton extends InteractWindowDisplay {
    private boolean active;

    public RedButton() {
        super(12);
        this.active = false;
        this.setInteractionOffset(new Vector(0.0, -0.5, 0.0));
    }

    private static ItemStack getItemWithModel(int customModelData) {
        ItemStack stack = new ItemStack(Material.PAPER);
        ItemMeta meta = stack.getItemMeta();
        assert meta != null;
        meta.setCustomModelData(customModelData);
        stack.setItemMeta(meta);
        return stack;
    }
    @Override
    public void onInteraction(Player player) {
        if (!player.isOp()) return;
        if (this.active) return;

        player.playSound(player, BSounds.BUTTON_CLICK, 1.5f, 0.9f);
        if (Bingo.getState().isPresent()) Bingo.getState().get().roll();

        this.getDisplayEntity().setItemStack(getItemWithModel(13));
        active = true;
        new BukkitRunnable() {
            @Override
            public void run() {
                getDisplayEntity().setItemStack(getItemWithModel(12));
                active = false;
            }
        }.runTaskLater(Bingo.getPlugin(), 20L);
    }

    @Override
    protected void onSeen() {
        this.getDisplayEntity().setGlowing(true);
    }

    @Override
    protected void unSeen() {
        this.getDisplayEntity().setGlowing(false);
    }
}
