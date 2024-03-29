package dev.cross.bingo;

public class BingoDigit {
    private final byte bingoDigit;
    private boolean isSelected;

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
}
