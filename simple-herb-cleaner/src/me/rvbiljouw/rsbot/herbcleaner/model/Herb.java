package me.rvbiljouw.rsbot.herbcleaner.model;

import java.util.StringJoiner;

/**
 * @author rvbiljouw
 */
public enum Herb {

    GUAM_LEAF(199, 249),
    ROGUES_PURSE(1533, 1534),
    SNAKE_WEED(1525, 1526),
    MARRENTILL(201, 251),
    TARROMIN(203, 253),
    HARRALANDER(205, 255),
    RANARR_WEED(207, 257),
    TOADFLAX(3049, 2998),
    IRIT_LEAF(209, 259),
    AVANTOE(211, 261),
    KWUARM(213, 263),
    SNAPDRAGON(3051, 3000),
    CADANTINE(215, 265),
    LANTADYME(2485, 2481),
    DWARF_WEED(217, 267),
    TORSTOL(219, 269);

    Herb(int grimyId, int cleanId) {
        this.grimyId = grimyId;
        this.cleanId = cleanId;
        this.displayName = formatName();
    }

    private String formatName() {
        final String[] lowercaseTokens = name().toLowerCase().split("_");
        final StringJoiner joiner = new StringJoiner(" ");
        for (String token : lowercaseTokens) {
            final char firstChar = token.charAt(0);
            final char firstCharUpper = Character.toUpperCase(firstChar);
            joiner.add(token.replace(firstChar, firstCharUpper));
        }
        return joiner.toString();
    }

    public final int grimyId;
    public final int cleanId;
    public final String displayName;

    @Override
    public String toString() {
        return displayName;
    }
}
