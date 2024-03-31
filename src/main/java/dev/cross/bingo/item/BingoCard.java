package dev.cross.bingo.item;

import dev.cross.bingo.Bingo;
import dev.cross.bingo.BingoDigit;
import dev.cross.bingo.ui.BingoInventory;
import dev.cross.blissfulcore.item.AbstractBlissfulItem;
import dev.cross.blissfulcore.ui.BColors;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BingoCard extends AbstractBlissfulItem {
    private static final ItemStack cardStack;
    private static final @NotNull NamespacedKey BINGO_KEY = Objects.requireNonNull(NamespacedKey.fromString("bingo_card", Bingo.getPlugin()));

    static {
        cardStack = new ItemStack(Material.PAPER);
        ItemMeta meta = Objects.requireNonNull(cardStack.getItemMeta());
        meta.setDisplayName(BukkitComponentSerializer.legacy().serialize(Component.text("Bingo Card").color(BColors.YELLOW).decoration(TextDecoration.ITALIC, false)));
        meta.setLore(Stream.of(Component.text(" "),
                Component.text("   ⏺ \"An elegant item, for a more civilized age.\"").decoration(TextDecoration.ITALIC, false).color(BColors.LIGHT_PURPLE),
                Component.text("       - ").decoration(TextDecoration.ITALIC, false).color(BColors.LIGHT_PURPLE).append(Component.text("BingoDabQueen").color(BColors.YELLOW)),
                Component.text("  ")
        ).map(it -> BukkitComponentSerializer.legacy().serialize(it)).collect(Collectors.toList()));
        cardStack.setItemMeta(meta);
    }

    public BingoCard() {
        super("bingo-card", cardStack);
    }

    public static final int AMOUNT = 24;

    public List<BingoDigit> generateDigits() {
        Random random = new Random();
        List<Integer> digits = new ArrayList<>();

        for (int i = 0; i < AMOUNT + 1; i++) {
            if (i == Math.floor((double) (AMOUNT + 1) / 2.0)) {
                // Adds the middle number, aka, 101, if it's in the middle of the amount.
                digits.add((int) BingoDigit.middleIndicator);
                continue;
            }
            int digit = random.nextInt(1, 65);

            while (digits.contains(digit)) {
                digit = random.nextInt(1, 65);
            }
            digits.add(digit);
        }

        return digits.stream().map(BingoDigit::new).toList();
    }
    @Override
    public ItemStack createItem() {
        ItemStack stack = super.createItem();
        List<BingoDigit> digits = generateDigits();
        write(digits, stack);
        return stack;
    }

    public static void write(List<BingoDigit> digits, ItemStack stack) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        for (BingoDigit digit : digits) {
            try {
                stream.write(digit.asSerialized());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        ItemMeta meta = stack.getItemMeta();
        if (meta == null) return;
        meta.getPersistentDataContainer().set(BINGO_KEY, PersistentDataType.BYTE_ARRAY, stream.toByteArray());
        stack.setItemMeta(meta);
    }

    public static List<BingoDigit> read(ItemStack stack) {
        if (stack.getItemMeta() == null) return null;
        byte[] toRead = stack.getItemMeta().getPersistentDataContainer().get(BINGO_KEY, PersistentDataType.BYTE_ARRAY);
        if (toRead == null) return null;

        List<BingoDigit> digits = new ArrayList<>();
        for (int i = 0; i < toRead.length - 1; i = i + 2) {
            digits.add(BingoDigit.from(toRead[i], toRead[i + 1]));
        }

        return digits;
    }

    @Override
    public void onClick(PlayerInteractEvent playerInteractEvent) {
        assert playerInteractEvent.getItem() != null;
        ItemMeta meta = playerInteractEvent.getItem().getItemMeta();
        if (meta == null) return;
        byte[] arr = meta.getPersistentDataContainer().get(BINGO_KEY, PersistentDataType.BYTE_ARRAY);
        if (arr == null) return;

        List<BingoDigit> digits = read(playerInteractEvent.getItem());
        if (digits == null) return;

        BingoInventory inventory = new BingoInventory(playerInteractEvent.getPlayer(), playerInteractEvent.getItem(), digits);
        inventory.open();
    }
}
