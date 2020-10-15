package me.rvbiljouw.rsbot.herbcleaner.util;

import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Constants;

/**
 * @author rvbiljouw
 */
public final class ProgressTracker {
    private final ClientContext context;

    private int initialHerbloreLevel;
    private int initialHerbloreXP;
    private int levelsGained;
    private int xpGained;
    private int herbsCleaned;

    public ProgressTracker(ClientContext context) {
        this.context = context;
    }

    public void update() {
        if (initialHerbloreLevel == 0 || initialHerbloreXP == 0) {
            initialHerbloreLevel = context.skills.realLevel(Constants.SKILLS_HERBLORE);
            initialHerbloreXP = context.skills.experience(Constants.SKILLS_HERBLORE);
        } else {
            levelsGained = context.skills.realLevel(Constants.SKILLS_HERBLORE) - initialHerbloreLevel;
            xpGained = context.skills.experience(Constants.SKILLS_HERBLORE) - initialHerbloreXP;
        }
    }

    public void incrementHerbsCleaned() {
        herbsCleaned++;
    }

    public int getLevelsGained() {
        return levelsGained;
    }

    public int getXPGained() {
        return xpGained;
    }

    public int getHerbsCleaned() {
        return herbsCleaned;
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

        final int herbloreLevel = context.skills.realLevel(Constants.SKILLS_HERBLORE);
        final int herbloreXP = context.skills.experience(Constants.SKILLS_HERBLORE);
        final int remainingXP = context.skills.experienceAt(herbloreLevel + 1) - herbloreXP;
        final long ttl = (long)(remainingXP / (double)getXPPerHour(runtime) * 3600000);
        System.out.println(remainingXP + " " + getXPPerHour(runtime) + " " + (remainingXP / 3600000));
        return TimeFormatting.millisToTimestamp(ttl);
    }

}
