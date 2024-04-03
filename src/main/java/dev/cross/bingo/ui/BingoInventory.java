package dev.cross.bingo.ui;

import dev.cross.bingo.BingoBoard;
import dev.cross.bingo.BingoDigit;
import dev.cross.bingo.item.BingoCard;
import dev.cross.blissfulcore.ui.inventory.GUIInventory;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class BingoInventory extends GUIInventory {
    public static final Component BINGO_SCREEN = Component.text("\uE201\uE206").color(NamedTextColor.WHITE);
    private final BingoBoard board;
    private final ItemStack bingoCard;

    public BingoInventory(Player player, ItemStack bingoCard, List<BingoDigit> digits) {
        super(player, false);
        this.bingoCard = bingoCard;
        this.board = new BingoBoard(digits);
        addButton(new BingoButton(board), 54 - 6);
    }

    @Override
    protected Inventory inventorySupplier() {
        Inventory inventory = Bukkit.createInventory(this.getPlayer(), 54, BukkitComponentSerializer.legacy().serialize(BINGO_SCREEN));
        List<BingoDigit> digits = board.getDigits();
        for (int i = 0; i < digits.size(); i++) {
            BingoDigit digit = digits.get(i);
            ItemStack digitStack = digit.createItem();
            int x = i / 5;
            int y = 9 * x;
            inventory.setItem((2 + i % 5) + y, digitStack);
        }
        return inventory;
    }

    @Override
    protected void onInteract(InventoryClickEvent event) {
        ItemStack getClicked = Objects.requireNonNull(event.getClickedInventory()).getItem(event.getSlot());
        if (getClicked == null) return;
        Optional<Integer> bingoDigit = BingoDigit.from(getClicked);
        if (bingoDigit.isEmpty()) return;
        List<BingoDigit> digits = board.getDigits();

        digits.stream().filter(it -> it.getBingoDigit() == bingoDigit.get()).findAny().ifPresent(it -> {
            it.setSelected(!it.isSelected());

            event.getClickedInventory().setItem(event.getSlot(), it.createItem());
        });
    }

    @Override
    protected void onClose(InventoryCloseEvent inventoryCloseEvent) {
        BingoCard.write(board.getDigits(), bingoCard);
    }
}
