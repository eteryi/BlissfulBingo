package dev.cross.bingo.ui;

public class StringFormatter {
    private static final char BOX = '\uE213';
    private static final char LEFT = '\uE214';
    private static final char RIGHT = '\uE215';

    private static final char SPACING_BOX = '\uE216';
    private static final char SPACING_LETTER = '\uE217';
    public static String getBackgroundString(String string) {
        StringBuilder builder = new StringBuilder();
        builder.append(LEFT);
        builder.append(SPACING_BOX);
        builder.append(BOX);
        builder.append(SPACING_BOX);
        for (char c : string.toCharArray()) {
            builder.append(BOX);
            builder.append(SPACING_LETTER);
            builder.append(c);
            builder.append(SPACING_BOX);
        }
        builder.append(BOX);
        builder.append(SPACING_BOX);
        builder.append(RIGHT);
        return builder.toString();
    }
}
