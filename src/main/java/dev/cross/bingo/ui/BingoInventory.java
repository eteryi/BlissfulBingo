package dev.cross.bingo.ui;

import dev.cross.bingo.BingoDigit;
import dev.cross.bingo.BingoElements;
import dev.cross.bingo.item.BingoCard;
import dev.cross.blissfulcore.Pair;
import dev.cross.blissfulcore.ui.inventory.GUIInventory;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BingoInventory extends GUIInventory {
    private final List<BingoDigit> bingoNumbers;
    private final ItemStack stack;

    public BingoInventory(Player player, ItemStack stack, List<BingoDigit> digits) {
        super(player, false);
        this.bingoNumbers = digits;
        this.stack = stack;
    }

    @Override
    protected Inventory inventorySupplier() {
        Inventory inventory = Bukkit.createInventory(this.getPlayer(), 54);
        for (int i = 0; i < bingoNumbers.size(); i++) {
            BingoDigit digit = bingoNumbers.get(i);
            Pair<Material, Integer> itemData = digit.isSelected() ? BingoElements.SELECTED_BINGO : BingoElements.UNSELECTED_BINGO;

            ItemStack stack = new ItemStack(itemData.first());
            ItemMeta meta = stack.getItemMeta();
            assert meta != null;

            meta.setCustomModelData(itemData.second());
            stack.setItemMeta(meta);
            stack.setAmount(digit.getBingoDigit());
            int x = i / 5;
            int y = 9 * x;
            inventory.setItem((2 + i % 5) + y, stack);
        }
        return inventory;
    }

    @Override
    protected void onInteract(InventoryClickEvent event) {
        ItemStack getClicked = Objects.requireNonNull(event.getClickedInventory()).getItem(event.getSlot());
        if (getClicked == null) return;

        bingoNumbers.stream().filter(it -> it.getBingoDigit() == getClicked.getAmount()).findAny().ifPresent(it -> {
            it.setSelected(!it.isSelected());

            Pair<Material, Integer> itemData = it.isSelected() ? BingoElements.SELECTED_BINGO : BingoElements.UNSELECTED_BINGO;

            ItemMeta meta = getClicked.getItemMeta();
            assert meta != null;

            meta.setCustomModelData(itemData.second());
            getClicked.setItemMeta(meta);
            getClicked.setAmount(it.getBingoDigit());
            event.getClickedInventory().setItem(event.getSlot(), getClicked);
        });
    }

    @Override
    protected void onClose(InventoryCloseEvent inventoryCloseEvent) {
        BingoCard.write(bingoNumbers, stack);
    }
}
