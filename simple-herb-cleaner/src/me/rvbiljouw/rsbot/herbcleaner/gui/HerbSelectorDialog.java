package me.rvbiljouw.rsbot.herbcleaner.gui;

import me.rvbiljouw.rsbot.herbcleaner.model.Herb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import static java.awt.GridBagConstraints.HORIZONTAL;

/**
 * @author rvbiljouw
 */
public final class HerbSelectorDialog {
    private final JFrame dialog;
    private final HerbSelectionListener listener;

    public HerbSelectorDialog(HerbSelectionListener listener) {
        this.listener = listener;
        this.dialog = buildDialog();
    }

    public void show() {
        dialog.pack();
        dialog.setVisible(true);
        dialog.setLocationRelativeTo(null);
    }

    public void ensureClosed() {
        dialog.setVisible(false);
    }

    private JFrame buildDialog() {
        final JFrame dialog = new JFrame();
        dialog.setTitle("Select a herb to clean");
        dialog.setLayout(new BorderLayout());
        dialog.setContentPane(buildContentPane());
        dialog.setResizable(false);
        return dialog;
    }

    private JPanel buildContentPane() {
        final JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(450, 100));
        panel.setLayout(new BorderLayout());

        panel.add(buildInfo(), BorderLayout.NORTH);
        panel.add(buildList(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildInfo() {
        final JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = HORIZONTAL;
        constraints.weightx = 1;
        constraints.gridx = 0;

        final JLabel instruction = new JLabel("<html><b>Select which herb you want to clean to start the script</b></html>", SwingConstants.CENTER);
        panel.add(instruction, constraints);

        final JLabel note = new JLabel("You can change this selection later, or close the dialog.", SwingConstants.CENTER);
        panel.add(note, constraints);

        return panel;
    }

    private JComboBox<Herb> buildList() {
        final JComboBox<Herb> herbs = new JComboBox<>();
        for (Herb herb : Herb.values()) {
            herbs.addItem(herb);
        }
        herbs.setSelectedIndex(-1);
        herbs.addItemListener(buildListSelectionListener());
        return herbs;
    }

    private ItemListener buildListSelectionListener() {
        return e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                listener.onHerbSelected((Herb) e.getItem());
            }
        };
    }

    public interface HerbSelectionListener {

        void onHerbSelected(Herb herb);

    }

}
