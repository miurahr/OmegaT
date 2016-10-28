package org.omegat.gui.preferences.view;

import java.awt.Color;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.Map;

import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.ListModel;
import javax.swing.colorchooser.AbstractColorChooserPanel;

import org.omegat.gui.preferences.PreferencesView;
import org.omegat.util.OStrings;
import org.omegat.util.Preferences;
import org.omegat.util.gui.DelegatingComboBoxRenderer;
import org.omegat.util.gui.Styles;
import org.omegat.util.gui.Styles.EditorColor;

public class CustomColorSelectionView implements PreferencesView {

    private final Map<EditorColor, Color> temporaryPreferences = new EnumMap<>(EditorColor.class);
    private CustomColorSelectionPanel panel;
    private boolean listenerEnabled = true;

    @Override
    public JComponent getGui() {
        if (panel == null) {
            initGui();
            initDefaults();
        }
        return panel;
    }

    @Override
    public String toString() {
        return "Colors";
    }

    private void initGui() {
        panel = new CustomColorSelectionPanel();
        try {
            removeTransparencySlider(panel.colorChooser);
        } catch (Exception e) {
            // Ignore
        }
        panel.colorChooser.getSelectionModel().addChangeListener(e -> {
            if (listenerEnabled) {
                recordTemporaryPreference();
            }
        });
        panel.colorStylesList.setCellRenderer(new DelegatingComboBoxRenderer<EditorColor, String>() {
            @Override
            protected String getDisplayText(EditorColor value) {
                return value.getDisplayName();
            }
        });
        panel.resetCurrentColorButton.addActionListener(e -> resetCurrentColor());
        panel.resetAllColorsButton.addActionListener(e -> resetAllColors());
    }

    private void recordTemporaryPreference() {
        EditorColor selectedStyle = (EditorColor) panel.colorStylesList.getSelectedValue();
        if (selectedStyle == null) {
            return;
        }
        temporaryPreferences.put(selectedStyle, panel.colorChooser.getColor());
    }

    // Hide the Transparency Slider.
    // From: http://stackoverflow.com/a/22608885
    private static void removeTransparencySlider(JColorChooser jc) throws Exception {

        AbstractColorChooserPanel[] colorPanels = jc.getChooserPanels();
        for (int i = 1; i < colorPanels.length; i++) {
            AbstractColorChooserPanel cp = colorPanels[i];

            Field f = cp.getClass().getDeclaredField("panel");
            f.setAccessible(true);

            Object colorPanel = f.get(cp);
            Field f2 = colorPanel.getClass().getDeclaredField("spinners");
            f2.setAccessible(true);
            Object spinners = f2.get(colorPanel);

            Object transpSlispinner = Array.get(spinners, 3);
            if (i == colorPanels.length - 1) {
                transpSlispinner = Array.get(spinners, 4);
            }
            Field f3 = transpSlispinner.getClass().getDeclaredField("slider");
            f3.setAccessible(true);
            JSlider slider = (JSlider) f3.get(transpSlispinner);
            slider.setEnabled(false);
            slider.setVisible(false);
            Field f4 = transpSlispinner.getClass().getDeclaredField("spinner");
            f4.setAccessible(true);
            JSpinner spinner = (JSpinner) f4.get(transpSlispinner);
            spinner.setEnabled(false);
            spinner.setVisible(false);

            Field f5 = transpSlispinner.getClass().getDeclaredField("label");
            f5.setAccessible(true);
            JLabel label = (JLabel) f5.get(transpSlispinner);
            label.setVisible(false);
        }
    }

    private void setColorChooserWithoutNotifying(Color color) {
        listenerEnabled = false;
        panel.colorChooser.setColor(color == null ? Color.BLACK : color);
        listenerEnabled = true;
    }

    private void updateSelection() {
        EditorColor selectedStyle = (EditorColor) panel.colorStylesList.getSelectedValue();
        if (selectedStyle == null) {
            panel.colorChooser.setEnabled(false);
            panel.resetCurrentColorButton.setEnabled(false);
            return;
        }
        panel.colorChooser.setEnabled(true);
        panel.resetCurrentColorButton.setEnabled(true);
        Color color = temporaryPreferences.containsKey(selectedStyle) ? temporaryPreferences.get(selectedStyle)
                : selectedStyle.getColor();
        setColorChooserWithoutNotifying(color);
    }

    private void resetCurrentColor() {
        EditorColor selectedStyle = (Styles.EditorColor) panel.colorStylesList.getSelectedValue();
        if (selectedStyle == null) {
            return;
        }
        Color defaultColor = selectedStyle.getDefault();
        if (defaultColor == null) {
            setColorChooserWithoutNotifying(Color.BLACK);
            temporaryPreferences.put(selectedStyle, null);
        } else {
            panel.colorChooser.setColor(defaultColor);
        }
    }

    private void resetAllColors() {
        int confirm = JOptionPane.showConfirmDialog(panel, OStrings.getString("GUI_COLORS_RESET_ALL_COLORS_CONFIRM"),
                OStrings.getString("GUI_COLORS_RESET_ALL_COLORS").replaceFirst("&", ""), JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        ListModel<EditorColor> model = panel.colorStylesList.getModel();
        for (int i = 0; i < model.getSize(); i++) {
            EditorColor style = (EditorColor) model.getElementAt(i);
            temporaryPreferences.put(style, style.getDefault());
        }
        resetCurrentColor();
    }

    @Override
    public void initDefaults() {
        updateSelection();
    }

    @Override
    public void persist() {
        temporaryPreferences.entrySet().forEach(e -> e.getKey().setColor(e.getValue()));
        Preferences.save();
        if (!temporaryPreferences.isEmpty()) {
            JOptionPane.showMessageDialog(panel, OStrings.getString("GUI_COLORS_CHANGED_RESTART"));
        }
    }
}
