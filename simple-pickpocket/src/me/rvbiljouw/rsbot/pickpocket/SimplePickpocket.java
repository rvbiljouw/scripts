package me.rvbiljouw.rsbot.pickpocket;

import me.rvbiljouw.rsbot.pickpocket.gui.PickpocketSettingsDialog;
import me.rvbiljouw.rsbot.pickpocket.model.PickpocketSettings;
import me.rvbiljouw.rsbot.pickpocket.model.PickpocketState;
import me.rvbiljouw.rsbot.pickpocket.paint.StatusBoxPaint;
import me.rvbiljouw.rsbot.pickpocket.util.ProgressTracker;
import org.powerbot.script.*;
import org.powerbot.script.rt4.*;
import org.powerbot.script.rt4.ClientContext;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author rvbiljouw
 */
@Script.Manifest(name = "SimplePickpocket", description = "A simple script that can pickpocket any NPC and eat.")
public final class SimplePickpocket extends PollingScript<ClientContext> implements PaintListener, PickpocketSettingsDialog.SettingsUpdatedListener, MessageListener {
    private final PickpocketSettingsDialog dialog = new PickpocketSettingsDialog(this);
    private final StatusBoxPaint statusBoxPaint = new StatusBoxPaint(this);
    private final ProgressTracker progressTracker = new ProgressTracker(ctx);

    private PickpocketSettings settings;
    private PickpocketState lastState;

    @Override
    public void repaint(Graphics graphics) {
        statusBoxPaint.repaint(graphics);
    }

    @Override
    public void onSettingsChanged(PickpocketSettings settings) {
        this.settings = settings;
    }

    @Override
    public void start() {
        dialog.show();
    }

    @Override
    public void stop() {
        dialog.ensureClosed();
    }

    @Override
    public void poll() {
        if (ctx.camera.pitch() < 100) {
            ctx.camera.pitch(true);
        }

        progressTracker.update();

        switch ((lastState = getState())) {
            case AWAITING_SETTINGS:
                Condition.wait(() -> settings != null);
                break;

            case DROP:
                final Item[] items = ctx.inventory.items();
                final List<String> noDrop = Arrays.asList(settings.getDropAllExcept());
                for (Item item : items) {
                    if (noDrop.contains(item.name().toLowerCase()) || item.name().equalsIgnoreCase(settings.getFoodName())) {
                        continue;
                    }
                    ctx.inventory.drop(item, ctx.inventory.shiftDroppingEnabled());
                }
                break;

            case EAT:
                final Item item = ctx.inventory.select().name(settings.getFoodName()).poll();
                final int healthPreEating = getHealth();
                if (item != null) {
                    item.interact("Eat");
                    Condition.wait(() -> healthPreEating < getHealth(), 300, 3);
                }
                break;

            case OPEN_POUCHES:
                final Item pouches = ctx.inventory.select().name("Coin pouch").poll();
                if (pouches != null) {
                    pouches.interact("Open-all");
                    Condition.wait(() -> !mustOpenPouches(), 300, 3);
                }
                break;

            case PICKPOCKET:
                final Npc target = ctx.npcs.select().name(settings.getNpcName()).nearest().poll();
                if (target != null) {
                    final Tile location = target.tile();
                    if (target.inViewport()) {
                        target.interact("Pickpocket");
                    } else if (ctx.movement.reachable(ctx.players.local(), location)) {
                        final LocalPath path = ctx.movement.findPath(location);
                        path.traverse();
                    }
                    Condition.wait(this::isBusy, 300, 3);
                }
                break;
        }
    }

    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    public PickpocketSettings getSettings() {
        return settings;
    }

    public PickpocketState getLastState() {
        return lastState;
    }

    private boolean mustOpenPouches() {
        final int pouches = ctx.inventory.select().name("Coin pouch").count(true);
        return pouches >= 27;
    }

    private int getHealth() {
        final int hp = ctx.skills.realLevel(Constants.SKILLS_HITPOINTS);
        return (int) ((ctx.players.local().healthPercent() / 100.0) * hp);
    }

    private boolean isLowHealth() {
        return getHealth() <= settings.getEatAt();
    }

    private boolean isBusy() {
        final Player local = ctx.players.local();
        return local.inMotion() || local.animation() > 0 || local.interacting().valid() || local.healthBarVisible();
    }

    private PickpocketState getState() {
        if (settings == null) {
            return PickpocketState.AWAITING_SETTINGS;
        } else if (mustOpenPouches()) {
            return PickpocketState.OPEN_POUCHES;
        } else if (ctx.inventory.isFull() && settings.getDropAllExcept().length > 0) {
            return PickpocketState.DROP;
        } else if (isLowHealth() || ctx.inventory.isFull()) {
            return PickpocketState.EAT;
        } else if (!isBusy()) {
            return PickpocketState.PICKPOCKET;
        } else {
            return PickpocketState.BUSY;
        }
    }

    @Override
    public void messaged(MessageEvent messageEvent) {
        if (messageEvent.text().contains("You pick")) {
            progressTracker.incrementActions();
        }
    }
}
