package dev.cross.bingo.ui;

import net.md_5.bungee.api.ChatColor;

public class BingoTextUtils {
    private static final char BOX = '\uE213';
    private static final char LEFT = '\uE214';
    private static final char RIGHT = '\uE215';

    private static final char SOLID_BOX = '\uE218';
    private static final char SOLID_LEFT = '\uE219';
    private static final char SOLID_RIGHT = '\uE220';

    private static final char SPACING_BOX = '\uE216';
    private static final char SPACING_LETTER = '\uE217';
    public static String getGoldenString(String string) {
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
    public static String getBackground(String string, ChatColor backgroundColor, ChatColor textColor) {
        StringBuilder builder = new StringBuilder();
        builder.append(backgroundColor);
        builder.append(SOLID_LEFT);
        builder.append(SPACING_BOX);
        builder.append(SOLID_BOX);
        builder.append(SPACING_BOX);
        for (char c : string.toCharArray()) {
            builder.append(SOLID_BOX);
            builder.append(SPACING_LETTER);
            builder.append(textColor);
            builder.append(c);
            builder.append(backgroundColor);
            builder.append(SPACING_BOX);
        }
        builder.append(SOLID_BOX);
        builder.append(SPACING_BOX);
        builder.append(SOLID_RIGHT);
        return builder.toString();
    }
}
