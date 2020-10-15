package me.rvbiljouw.rsbot.pickpocket.util;

import java.text.DecimalFormat;

/**
 * @author rvbiljouw
 */
public final class TimeFormatting {

    private TimeFormatting() {
    }

    public static String millisToTimestamp(long millis) {
        DecimalFormat decimalFormat = new DecimalFormat("00");
        long hours = millis / (1000 * 60 * 60);
        millis -= hours * (1000 * 60 * 60);
        long minutes = millis / (1000 * 60);
        millis -= minutes * (1000 * 60);
        long seconds = millis / 1000;
        return String.format("%s:%s:%s", decimalFormat.format(hours), decimalFormat.format(minutes), decimalFormat.format(seconds));
    }

}
