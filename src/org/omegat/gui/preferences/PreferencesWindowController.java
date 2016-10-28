package org.omegat.gui.preferences;

import java.awt.Dimension;
import java.awt.Window;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JDialog;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreeSelectionModel;

import org.omegat.core.Core;
import org.omegat.core.data.NotLoadedProject;
import org.omegat.gui.preferences.view.AutoCompleterView;
import org.omegat.gui.preferences.view.ExternalTMXMatchesView;
import org.omegat.gui.preferences.view.FontSelectionView;
import org.omegat.gui.preferences.view.GlossaryAutoCompleterOptionsView;
import org.omegat.gui.preferences.view.LanguageToolConfigurationView;
import org.omegat.gui.preferences.view.SaveOptionsView;
import org.omegat.gui.preferences.view.SpellcheckerConfigurationView;
import org.omegat.gui.preferences.view.TagProcessingOptionsView;
import org.omegat.gui.preferences.view.TeamOptionsView;
import org.omegat.gui.preferences.view.UserPassView;
import org.omegat.gui.preferences.view.ViewOptionsView;
import org.omegat.gui.preferences.view.WorkflowOptionsView;
import org.omegat.util.TestPreferencesInitializer;
import org.omegat.util.gui.StaticUIUtils;

public class PreferencesWindowController {

    private PreferencesView currentView;
    private final Map<String, Runnable> persistenceRunnables = new HashMap<>();

    public void show(Window parent) {
        JDialog dialog = new JDialog();
        dialog.setTitle("Preferences");
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
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
                PreferencesView newView = (PreferencesView) node.getUserObject();
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
        root.add(new DefaultMutableTreeNode(new FontSelectionView()));
        DefaultMutableTreeNode acNode = new DefaultMutableTreeNode(new AutoCompleterView());
        acNode.add(new DefaultMutableTreeNode(new GlossaryAutoCompleterOptionsView()));
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

    public static void main(String[] args) throws IOException {
        TestPreferencesInitializer.init();
        Core.setProject(new NotLoadedProject());
        new PreferencesWindowController().show(null);
    }
}
