package me.rvbiljouw.rsbot.pickpocket.util;

import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

/**
 * @author rvbiljouw
 */
public final class ProgressTracker {
    private final ClientContext context;

    private int initialLevel;
    private int initialXP;
    private int levelsGained;
    private int xpGained;
    private int actions;

    public ProgressTracker(ClientContext context) {
        this.context = context;
    }

    public void update() {
        if (initialLevel == 0 || initialXP == 0) {
            initialLevel = context.skills.realLevel(Constants.SKILLS_THIEVING);
            initialXP = context.skills.experience(Constants.SKILLS_THIEVING);
        } else {
            levelsGained = context.skills.realLevel(Constants.SKILLS_THIEVING) - initialLevel;
            xpGained = context.skills.experience(Constants.SKILLS_THIEVING) - initialXP;
        }
    }

    public void incrementActions() {
        actions++;
    }

    public int getLevelsGained() {
        return levelsGained;
    }

    public int getXPGained() {
        return xpGained;
    }

    public int getActions() {
        return actions;
    }

    public int getXPPerHour(long runtime) {
        if (runtime > 0 && xpGained > 0) {
            return (int) (3600000 / runtime) * xpGained;
        } else {
            return 0;
        }
    }

    public String getTimeToLevel(long runtime) {
        if (xpGained == 0) {
            return "Unknown";
        }

        final int level = context.skills.realLevel(Constants.SKILLS_THIEVING);
        final int xp = context.skills.experience(Constants.SKILLS_THIEVING);
        final int remainingXP = context.skills.experienceAt(level + 1) - xp;
        final long ttl = (long)(remainingXP / (double)getXPPerHour(runtime) * 3600000);
        return TimeFormatting.millisToTimestamp(ttl);
    }

}
