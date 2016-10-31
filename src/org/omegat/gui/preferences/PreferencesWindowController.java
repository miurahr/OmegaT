package org.omegat.gui.preferences;

import java.awt.Dimension;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.omegat.gui.preferences.view.AppearanceView;
import org.omegat.gui.preferences.view.AutoCompleterView;
import org.omegat.gui.preferences.view.AutotextAutoCompleterOptionsView;
import org.omegat.gui.preferences.view.CharTableAutoCompleterOptionsView;
import org.omegat.gui.preferences.view.CustomColorSelectionView;
import org.omegat.gui.preferences.view.DictionaryView;
import org.omegat.gui.preferences.view.ExternalTMXMatchesView;
import org.omegat.gui.preferences.view.FontSelectionView;
import org.omegat.gui.preferences.view.GeneralOptionsView;
import org.omegat.gui.preferences.view.GlossaryAutoCompleterOptionsView;
import org.omegat.gui.preferences.view.GlossaryView;
import org.omegat.gui.preferences.view.HistoryAutoCompleterOptionsView;
import org.omegat.gui.preferences.view.LanguageToolConfigurationView;
import org.omegat.gui.preferences.view.MachineTranslationView;
import org.omegat.gui.preferences.view.SaveOptionsView;
import org.omegat.gui.preferences.view.SpellcheckerConfigurationView;
import org.omegat.gui.preferences.view.TagProcessingOptionsView;
import org.omegat.gui.preferences.view.TeamOptionsView;
import org.omegat.gui.preferences.view.UserPassView;
import org.omegat.gui.preferences.view.ViewOptionsView;
import org.omegat.gui.preferences.view.WorkflowOptionsView;
import org.omegat.util.gui.StaticUIUtils;

public class PreferencesWindowController {

    private PreferencesView currentView;
    private final Map<String, Runnable> persistenceRunnables = new HashMap<>();

    public void show(Window parent) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Preferences");
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        // TODO: Think about whether we really want to be modal here
        dialog.setModal(true);
        StaticUIUtils.setEscapeClosable(dialog);
        
        PreferencesPanel panel = new PreferencesPanel();
        dialog.add(panel);

        panel.availablePrefsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        panel.availablePrefsTree.setModel(new DefaultTreeModel(getRootNode()));
        panel.availablePrefsTree.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) panel.availablePrefsTree
                    .getLastSelectedPathComponent();
            if (node != null) {
                PreferencesView oldView = currentView;
                Object obj = node.getUserObject();
                if (!(obj instanceof PreferencesView)) {
                    return;
                }
                PreferencesView newView = (PreferencesView) obj;
                if (oldView == newView || newView.equals(oldView)) {
                    return;
                }
                if (oldView != null) {
                    if (!oldView.validate()) {
                        panel.availablePrefsTree.getSelectionModel().setSelectionPath(e.getOldLeadSelectionPath());
                        return;
                    }
                    if (!persistenceRunnables.containsKey(oldView.getClass().getName())) {
                        persistenceRunnables.put(oldView.getClass().getName(), () -> oldView.persist());
                    }
                }
                panel.selectedPrefsScrollPane.setViewportView(newView.getGui());
                currentView = newView;
            }
        });
        DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) panel.availablePrefsTree.getCellRenderer();
        renderer.setIcon(null);
        renderer.setLeafIcon(null);
        renderer.setOpenIcon(null);
        renderer.setClosedIcon(null);
        renderer.setDisabledIcon(null);

        panel.okButton.addActionListener(e -> {
            doSave();
            StaticUIUtils.closeWindowByEvent(dialog);
        });
        panel.cancelButton.addActionListener(e -> StaticUIUtils.closeWindowByEvent(dialog));
        panel.resetButton.addActionListener(e -> currentView.initDefaults());

        dialog.getRootPane().setDefaultButton(panel.okButton);

        dialog.setPreferredSize(new Dimension(800, 500));
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public TreeNode getRootNode() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode();
        root.add(new DefaultMutableTreeNode(new GeneralOptionsView()));
        root.add(new DefaultMutableTreeNode(new MachineTranslationView()));
        root.add(new DefaultMutableTreeNode(new GlossaryView()));
        root.add(new DefaultMutableTreeNode(new DictionaryView()));
        DefaultMutableTreeNode appearanceNode = new DefaultMutableTreeNode(new AppearanceView());
        appearanceNode.add(new DefaultMutableTreeNode(new FontSelectionView()));
        appearanceNode.add(new DefaultMutableTreeNode(new CustomColorSelectionView()));
        root.add(appearanceNode);
        DefaultMutableTreeNode acNode = new DefaultMutableTreeNode(new AutoCompleterView());
        acNode.add(new DefaultMutableTreeNode(new GlossaryAutoCompleterOptionsView()));
        acNode.add(new DefaultMutableTreeNode(new AutotextAutoCompleterOptionsView()));
        acNode.add(new DefaultMutableTreeNode(new CharTableAutoCompleterOptionsView()));
        acNode.add(new DefaultMutableTreeNode(new HistoryAutoCompleterOptionsView()));
        root.add(acNode);
        root.add(new DefaultMutableTreeNode(new SpellcheckerConfigurationView()));
        root.add(new DefaultMutableTreeNode(new LanguageToolConfigurationView()));
        root.add(new DefaultMutableTreeNode(new WorkflowOptionsView()));
        root.add(new DefaultMutableTreeNode(new TagProcessingOptionsView()));
        root.add(new DefaultMutableTreeNode(new TeamOptionsView()));
        root.add(new DefaultMutableTreeNode(new ExternalTMXMatchesView()));
        root.add(new DefaultMutableTreeNode(new ViewOptionsView()));
        root.add(new DefaultMutableTreeNode(new SaveOptionsView()));
        root.add(new DefaultMutableTreeNode(new UserPassView()));
        return root;
    }

    private void doSave() {
        persistenceRunnables.values().forEach(Runnable::run);
    }
}
