package me.rvbiljouw.rsbot.pickpocket.gui;

import me.rvbiljouw.rsbot.pickpocket.model.PickpocketSettings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.logging.Logger;

import static java.awt.GridBagConstraints.*;

/**
 * @author rvbiljouw
 */
public final class PickpocketSettingsDialog {
    private final Logger logger = Logger.getLogger(getClass().getName());

    private final JFrame dialog;
    private final JTextField npcField = new JTextField();
    private final JTextField foodField = new JTextField();
    private final JFormattedTextField healthField = new JFormattedTextField(NumberFormat.getIntegerInstance());
    private final JTextField dropField = new JTextField();

    private final PickpocketSettings settings = new PickpocketSettings();
    private final SettingsUpdatedListener listener;


    public PickpocketSettingsDialog(SettingsUpdatedListener listener) {
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
        dialog.setTitle("Simple Pickpocket - Settings");
        dialog.setLayout(new BorderLayout());
        dialog.setContentPane(buildContentPane());
        dialog.setResizable(false);
        return dialog;
    }

    private JPanel buildContentPane() {
        final JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(450, 175));
        panel.setLayout(new GridBagLayout());

        final GridBagConstraints labelConstraints = new GridBagConstraints();
        labelConstraints.fill = HORIZONTAL;
        labelConstraints.anchor = NORTHWEST;
        labelConstraints.weightx = 0;
        labelConstraints.gridwidth = 1;
        labelConstraints.insets = new Insets(10, 10, 10, 10);


        final GridBagConstraints fieldConstraints = new GridBagConstraints();
        fieldConstraints.fill = HORIZONTAL;
        fieldConstraints.anchor = NORTHWEST;
        fieldConstraints.weightx = 1.0;
        fieldConstraints.gridwidth = REMAINDER;
        fieldConstraints.insets = new Insets(10, 20, 10, 10);

        final JLabel npcLabel = new JLabel("Name of the NPC to pickpocket");
        panel.add(npcLabel, labelConstraints);
        panel.add(npcField, fieldConstraints);

        final JLabel foodLabel = new JLabel("Name of the food to eat");
        panel.add(foodLabel, labelConstraints);
        panel.add(foodField, fieldConstraints);

        final JLabel healthLabel = new JLabel("Health to eat at");
        panel.add(healthLabel, labelConstraints);

        healthField.setValue(20);
        panel.add(healthField, fieldConstraints);

        final JLabel dropLabel = new JLabel("Drop all items except (names, comma-separated)");
        panel.add(dropLabel, labelConstraints);
        panel.add(dropField, fieldConstraints);

        final GridBagConstraints actionConstraints = new GridBagConstraints();
        actionConstraints.fill = HORIZONTAL;
        actionConstraints.anchor = CENTER;
        actionConstraints.weightx = 1;
        actionConstraints.gridwidth = REMAINDER;
        actionConstraints.insets = new Insets(10, 10, 10, 10);

        final JButton applyButton = new JButton("Apply settings");
        applyButton.addActionListener(this::onApplyButtonPressed);
        panel.add(applyButton, actionConstraints);
        return panel;
    }

    private void onApplyButtonPressed(ActionEvent e) {
        settings.setNpcName(npcField.getText());
        settings.setFoodName(foodField.getText());
        settings.setEatAt(((Number) healthField.getValue()).intValue());
        settings.setDropAllExcept(dropField.getText().toLowerCase().split(","));
        listener.onSettingsChanged(settings);
    }

    public interface SettingsUpdatedListener {

        void onSettingsChanged(PickpocketSettings settings);

    }

    public static void main(String[] args) {
        new PickpocketSettingsDialog(System.out::println).show();
    }
}
