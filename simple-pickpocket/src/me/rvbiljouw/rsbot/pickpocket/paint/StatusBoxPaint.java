package me.rvbiljouw.rsbot.pickpocket.paint;

import me.rvbiljouw.rsbot.pickpocket.SimplePickpocket;
import me.rvbiljouw.rsbot.pickpocket.util.TimeFormatting;
import org.powerbot.script.PaintListener;

import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author rvbiljouw
 */
public final class StatusBoxPaint implements PaintListener {
    private static final Color BOX_COLOR = new Color(33, 33, 33, 127);
    private static final int BOX_X = 10;
    private static final int BOX_Y = 30;

    private final Logger logger = Logger.getLogger(getClass().getName());
    private final SimplePickpocket script;

    private Font roboto;

    public StatusBoxPaint(SimplePickpocket script) {
        this.script = script;
        tryLoadFonts();

    }

    private void tryLoadFonts() {
        try {
            this.roboto = Font.createFont(Font.TRUETYPE_FONT, getClass().getResourceAsStream("/Roboto-Regular.ttf"));
        } catch (FontFormatException | IOException e) {
            logger.log(Level.WARNING, "Failed to load custom fonts - using defaults instead.", e);
        }
    }

    @Override
    public void repaint(Graphics graphics) {
        graphics.setColor(Color.WHITE);
        graphics.drawRect(BOX_X, BOX_Y, 200, 260);

        graphics.setColor(BOX_COLOR);
        graphics.fillRect(BOX_X, BOX_Y, 200, 260);

        graphics.setColor(Color.WHITE);
        graphics.setFont(getFont(graphics).deriveFont(Font.BOLD, 16f));
        graphics.drawString("Simple Pickpocket", BOX_X + 10, BOX_Y + 30);

        graphics.setColor(Color.ORANGE);
        graphics.setFont(getFont(graphics).deriveFont(12f));
        graphics.drawString("Runtime: " + TimeFormatting.millisToTimestamp(script.getRuntime()), BOX_X + 10, BOX_Y + 60);
        graphics.drawString("Status: " + script.getLastState(), BOX_X + 10, BOX_Y + 90);
        graphics.drawString("Total actions: " + script.getProgressTracker().getActions(), BOX_X + 10, BOX_Y + 120);
        graphics.drawString("Levels gained: " + script.getProgressTracker().getLevelsGained(), BOX_X + 10, BOX_Y + 150);
        graphics.drawString("XP gained: " + script.getProgressTracker().getXPGained(), BOX_X + 10, BOX_Y + 180);
        graphics.drawString("XP per hour: " + script.getProgressTracker().getXPPerHour(script.getRuntime()), BOX_X + 10, BOX_Y + 210);
        graphics.drawString("TTL: " + script.getProgressTracker().getTimeToLevel(script.getRuntime()), BOX_X + 10, BOX_Y + 240);
    }

    private String getTargetNpcName() {
        if (script.getSettings() != null) {
            return script.getSettings().getNpcName();
        }
        return "Not selected";
    }

    private Font getFont(Graphics graphics) {
        return roboto != null ? roboto : graphics.getFont();
    }

}
