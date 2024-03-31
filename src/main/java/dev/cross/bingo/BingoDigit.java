package dev.cross.bingo;

import dev.cross.blissfulcore.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class BingoDigit {
    private final byte bingoDigit;
    private boolean isSelected;
    public static final byte middleIndicator = 101;
    public static final NamespacedKey NUMBER_KEY = NamespacedKey.fromString("bingo_number", Bingo.getPlugin());

    public BingoDigit(int digit) {
        this(digit, false);
    }

    public BingoDigit(int digit, boolean isSelected) {
        this.bingoDigit = (byte) digit;
        this.isSelected = isSelected;
    }

    public byte[] asSerialized() {
        byte[] bytes = new byte[2];
        bytes[0] = bingoDigit;
        bytes[1] = isSelected ? (byte) 1 : (byte) 0;
        return bytes;
    }

    public static BingoDigit from(byte digit, byte select) {
        return new BingoDigit(digit, select == 1);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getBingoDigit() {
        return bingoDigit;
    }

    public boolean isMiddle() {
        return this.getBingoDigit() == middleIndicator;
    }
    public ItemStack createItem() {
        Pair<Material, Integer> itemData = this.isSelected() ? BingoElements.SELECTED_BINGO : BingoElements.UNSELECTED_BINGO;

        ItemStack stack = new ItemStack(itemData.first());
        ItemMeta meta = stack.getItemMeta();
        assert meta != null;

        meta.setCustomModelData(itemData.second());
        meta.setDisplayName(ChatColor.RESET + " - " + this.bingoDigit);
        if (this.isMiddle()) {
            meta.setDisplayName(ChatColor.RESET + " - FREE SPACE");
        }
        meta.getPersistentDataContainer().set(NUMBER_KEY, PersistentDataType.INTEGER, this.getBingoDigit());
        stack.setItemMeta(meta);
        stack.setAmount(this.isMiddle() ? 1 : this.getBingoDigit());
        return stack;
    }

    public static Optional<Integer> from(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return Optional.empty();
        return Optional.ofNullable(meta.getPersistentDataContainer().get(NUMBER_KEY, PersistentDataType.INTEGER));
    }
}
