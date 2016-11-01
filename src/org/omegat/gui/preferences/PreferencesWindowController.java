package org.omegat.gui.preferences;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.madlonkay.supertmxmerge.gui.ReasonablySizedPanel;
import org.omegat.core.team2.gui.RepositoriesCredentialsView;
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
import org.omegat.util.StringUtil;
import org.omegat.util.gui.StaticUIUtils;

public class PreferencesWindowController {

    private JDialog dialog;
    private PreferencesPanel panel;
    private JPanel viewHolder;
    private PreferencesView currentView;
    private final Map<String, Runnable> persistenceRunnables = new HashMap<>();

    public void show(Window parent) {
        dialog = new JDialog();
        dialog.setTitle("Preferences");
        dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        // TODO: Think about whether we really want to be modal here
        dialog.setModal(true);
        StaticUIUtils.setEscapeClosable(dialog);
        
        panel = new PreferencesPanel();
        dialog.add(panel);

        viewHolder = new ReasonablySizedPanel();
        viewHolder.setLayout(new BorderLayout());

        panel.searchTextField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void removeUpdate(DocumentEvent e) {
                incrementalSearch();
            }

            @Override
            public void insertUpdate(DocumentEvent e) {
                incrementalSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                incrementalSearch();
            }
        });
        panel.searchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE && panel.searchTextField.getDocument().getLength() > 0) {
                    panel.searchTextField.setText(null);
                    e.consume();
                }
            }
        });
        panel.availablePrefsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        panel.availablePrefsTree.setModel(new DefaultTreeModel(getRootNode()));
        panel.availablePrefsTree.addTreeSelectionListener(this::handleViewSelection);
        panel.selectedPrefsScrollPane.getViewport().setBackground(panel.getBackground());
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
        
        dialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                new SwingWorker<Void, Void>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        preloadGuis();
                        return null;
                    }
                }.execute();
            }
        });

        dialog.getRootPane().setDefaultButton(panel.okButton);

        dialog.setPreferredSize(new Dimension(800, 500));
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private static TreeNode getRootNode() {
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
        DefaultMutableTreeNode teamNode = new DefaultMutableTreeNode(new TeamOptionsView());
        teamNode.add(new DefaultMutableTreeNode(new RepositoriesCredentialsView()));
        root.add(teamNode);
        root.add(new DefaultMutableTreeNode(new ExternalTMXMatchesView()));
        root.add(new DefaultMutableTreeNode(new ViewOptionsView()));
        root.add(new DefaultMutableTreeNode(new SaveOptionsView()));
        root.add(new DefaultMutableTreeNode(new UserPassView()));
        return root;
    }

    private void handleViewSelection(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getNewLeadSelectionPath().getLastPathComponent();
        if (node == null) {
            return;
        }
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
                persistenceRunnables.put(oldView.getClass().getName(), oldView::persist);
            }
        }
        viewHolder.removeAll();
        viewHolder.add(newView.getGui(), BorderLayout.CENTER);
        panel.selectedPrefsScrollPane.setViewportView(viewHolder);
        currentView = newView;
        SwingUtilities.invokeLater(this::adjustWidth);
    }

    private void adjustWidth() {
        int preferredWidth = viewHolder.getMinimumSize().width;
        JScrollBar scrollBar = panel.selectedPrefsScrollPane.getVerticalScrollBar();
        int actualWidth = panel.selectedPrefsScrollPane.getWidth();
        if (preferredWidth > actualWidth) {
            int newWidth = dialog.getWidth() + (preferredWidth - actualWidth);
            if (scrollBar != null) {
                newWidth += scrollBar.getWidth();
            }
            dialog.setSize(newWidth, dialog.getHeight());
        }
    }

    private void incrementalSearch() {
        String query = panel.searchTextField.getText().trim();
        if (query.isEmpty()) {
            return;
        }
        Color textColor = incrementalSearchImpl(query) ? Color.BLACK : Color.RED;
        panel.searchTextField.setForeground(textColor);
    }

    private boolean incrementalSearchImpl(String query) {
        Pattern pattern = Pattern.compile(".*" + Pattern.quote(query) + ".*",
                Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

        DefaultMutableTreeNode root = (DefaultMutableTreeNode) panel.availablePrefsTree.getModel().getRoot();
        for (GuiSearchResult result : createIndex(root)) {
            if (pattern.matcher(result.string).matches()) {
                panel.availablePrefsTree.setSelectionPath(new TreePath(result.node.getPath()));
                ((JComponent) result.comp).scrollRectToVisible(result.comp.getBounds());
                return true;
            }
        }
        return false;
    }

    private static List<GuiSearchResult> createIndex(DefaultMutableTreeNode root) {
        List<GuiSearchResult> result = new ArrayList<>();
        walkTree(root, node -> {
            PreferencesView view = (PreferencesView) node.getUserObject();
            if (view != null) {
                visitUiStrings(view.getGui(), (str, comp) -> {
                    result.add(new GuiSearchResult(node, str, comp));
                });
            }
        });
        return result;
    }

    static class GuiSearchResult {
        final public DefaultMutableTreeNode node;
        final public String string;
        final public Component comp;

        public GuiSearchResult(DefaultMutableTreeNode node, String string, Component comp) {
            this.node = node;
            this.string = string;
            this.comp = comp;
        }
    }

    private static void visitUiStrings(Component comp, BiConsumer<String, Component> consumer) {
        StaticUIUtils.visitHierarchy(comp, c -> {
            try {
                Method getText = c.getClass().getMethod("getText");
                String str = (String) getText.invoke(c);
                if (!StringUtil.isEmpty(str)) {
                    consumer.accept(str, c);
                }
            } catch (NoSuchMethodException e) {
            } catch (SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    private static void walkTree(DefaultMutableTreeNode node, Consumer<DefaultMutableTreeNode> consumer) {
        consumer.accept(node);
        Enumeration<?> e = node.children();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            walkTree((DefaultMutableTreeNode) o, consumer);
        }
    }
    
    private void preloadGuis() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) panel.availablePrefsTree.getModel().getRoot();
        walkTree(root, node -> {
            PreferencesView view = (PreferencesView) node.getUserObject();
            if (view != null) {
                view.getGui();
            }
        });
    }

    private void doSave() {
        persistenceRunnables.values().forEach(Runnable::run);
    }
}
