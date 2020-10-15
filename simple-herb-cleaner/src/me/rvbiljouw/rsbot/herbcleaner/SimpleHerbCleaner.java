package me.rvbiljouw.rsbot.herbcleaner;

import me.rvbiljouw.rsbot.herbcleaner.gui.HerbSelectorDialog;
import me.rvbiljouw.rsbot.herbcleaner.model.Herb;
import me.rvbiljouw.rsbot.herbcleaner.model.HerbCleanerState;
import me.rvbiljouw.rsbot.herbcleaner.paint.StatusBoxPaint;
import me.rvbiljouw.rsbot.herbcleaner.util.ProgressTracker;
import org.powerbot.script.Condition;
import org.powerbot.script.PaintListener;
import org.powerbot.script.PollingScript;
import org.powerbot.script.Script;
import org.powerbot.script.rt4.Bank;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Item;
import org.powerbot.script.rt4.ItemQuery;

import java.awt.*;

/**
 * @author rvbiljouw
 */
@Script.Manifest(name = "SimpleHerbCleaner", description = "A simple script that can clean a variety of herbs")
public final class SimpleHerbCleaner extends PollingScript<ClientContext> implements PaintListener, HerbSelectorDialog.HerbSelectionListener {
    private final HerbSelectorDialog dialog = new HerbSelectorDialog(this);
    private final StatusBoxPaint statusBoxPaint = new StatusBoxPaint(this);
    private final ProgressTracker progressTracker = new ProgressTracker(ctx);

    private Herb currentHerb = null;

    @Override
    public void repaint(Graphics g) {
        statusBoxPaint.repaint(g);
    }

    @Override
    public void onHerbSelected(Herb herb) {
        this.currentHerb = herb;
    }

    public ProgressTracker getProgressTracker() {
        return progressTracker;
    }

    public Herb getCurrentHerb() {
        return currentHerb;
    }

    public ClientContext ctx() {
        return ctx;
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
        progressTracker.update();
        ctx.camera.pitch(true);

        switch (getState()) {
            case OPEN_BANK:
                ctx.bank.open();
                Condition.wait(ctx.bank::opened, 200, 3);
                break;

            case CLOSE_BANK:
                ctx.bank.close(true);
                Condition.wait(() -> !ctx.bank.opened(), 200, 3);
                break;

            case WITHDRAW_GRIMY_HERBS:
                if (ctx.bank.select().id(currentHerb.grimyId).count() > 0) {
                    ctx.bank.withdraw(currentHerb.grimyId, Bank.Amount.ALL);
                    Condition.wait(this::hasGrimyHerbs, 200, 3);
                } else {
                    log.severe("No herbs left to clean - logging out!");
                    ctx.game.logout();
                    ctx.controller.stop();
                }
                break;

            case DEPOSIT_CLEAN_HERBS:
                depositCleanHerbs();
                break;

            case CLEAN_GRIMY_HERBS:
                cleanGrimyHerbs();
                break;
        }
    }

    private void depositCleanHerbs() {
        final ItemQuery<Item> cleanHerbQuery = ctx.inventory.select().id(currentHerb.cleanId);
        final int cleanHerbCount = cleanHerbQuery.count();
        if (cleanHerbCount == 28) {
            ctx.bank.depositInventory();
        } else {
            ctx.bank.deposit(currentHerb.cleanId, Bank.Amount.ALL);
        }
        Condition.wait(this::hasCleanHerbs, 200, 3);
    }

    private void cleanGrimyHerbs() {
        final ItemQuery<Item> grimyHerbQuery = ctx.inventory.select().id(currentHerb.grimyId);
        final Item grimyHerb = grimyHerbQuery.poll();
        if (grimyHerb != null) {
            final int prevGrimyHerbCount = getHerbCount(currentHerb.grimyId);
            grimyHerb.click(true);
            if (Condition.wait(() -> getHerbCount(currentHerb.grimyId) < prevGrimyHerbCount, 200, 3)) {
                progressTracker.incrementHerbsCleaned();
            }
        }
    }

    private int getHerbCount(int itemId) {
        return ctx.inventory.select().id(itemId).count();
    }

    private boolean hasGrimyHerbs() {
        return getHerbCount(currentHerb.grimyId) > 0;
    }

    private boolean hasCleanHerbs() {
        return getHerbCount(currentHerb.cleanId) > 0;
    }

    private HerbCleanerState getState() {
        if (currentHerb == null) {
            return HerbCleanerState.UNKNOWN;
        } else if (!ctx.bank.opened() && !hasGrimyHerbs()) {
            return HerbCleanerState.OPEN_BANK;
        } else if (ctx.bank.opened() && hasCleanHerbs()) {
            return HerbCleanerState.DEPOSIT_CLEAN_HERBS;
        } else if (ctx.bank.opened() && !hasGrimyHerbs()) {
            return HerbCleanerState.WITHDRAW_GRIMY_HERBS;
        } else if (ctx.bank.opened() && hasGrimyHerbs()) {
            return HerbCleanerState.CLOSE_BANK;
        } else if (hasGrimyHerbs()) {
            return HerbCleanerState.CLEAN_GRIMY_HERBS;
        } else {
            return HerbCleanerState.UNKNOWN;
        }
    }

}
