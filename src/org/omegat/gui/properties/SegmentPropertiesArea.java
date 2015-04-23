package org.omegat.gui.properties;


import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.ScrollPaneConstants;
import javax.swing.Scrollable;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;
import org.omegat.core.Core;
import org.omegat.core.CoreEvents;
import org.omegat.core.data.EntryKey;
import org.omegat.core.data.SourceTextEntry;
import org.omegat.core.data.SourceTextEntry.DUPLICATE;
import org.omegat.core.data.TMXEntry;
import org.omegat.core.events.IEntryEventListener;
import org.omegat.core.events.IFontChangedEventListener;
import org.omegat.core.events.IProjectEventListener;
import org.omegat.gui.main.DockableScrollPane;
import org.omegat.gui.main.MainWindow;
import org.omegat.util.OStrings;
import org.omegat.util.Preferences;
import org.omegat.util.StaticUtils;
import org.omegat.util.gui.Styles;

import com.vlsolutions.swing.docking.DockableState.Location;

@SuppressWarnings("serial")
public class SegmentPropertiesArea extends DockableScrollPane implements IEntryEventListener, IProjectEventListener,
    IFontChangedEventListener, Scrollable {

    private final static DateFormat DATE_FORMAT = DateFormat.getDateInstance();
    private final static DateFormat TIME_FORMAT = DateFormat.getTimeInstance();
    
    private final static Pattern SPLIT_COMMAS = Pattern.compile("\\s*,\\s*");
    
    private final static String KEY_ISDUP = "isDup";
    private final static String KEY_FILE = "file";
    private final static String KEY_ID = "id";
    private final static String KEY_TRANSLATION = "translation";
    private final static String KEY_TRANSLATIONISFUZZY = "translationIsFuzzy";
    //private final static String KEY_NEXT = "next";
    //private final static String KEY_PREV = "prev";
    private final static String KEY_PATH = "path";
    private final static String KEY_HASNOTE = "hasNote";
    private final static String KEY_CHANGED = "changed";
    private final static String KEY_CHANGER = "changer";
    private final static String KEY_CREATED = "created";
    private final static String KEY_CREATOR = "creator";
    private final static String KEY_ISALT = "isAlt";
    private final static String KEY_LINKED = "linked";
    
    final List<String> properties = new ArrayList<String>();
    
    private ISegmentPropertiesView viewImpl;
        
    public SegmentPropertiesArea(MainWindow mw) {
        super("SEGMENTPROPERTIES", OStrings.getString("SEGPROP_PANE_TITLE"), null, true);
        mw.addDockable(this);
        
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        
        CoreEvents.registerEntryEventListener(this);
        CoreEvents.registerProjectChangeListener(this);
        CoreEvents.registerFontChangedEventListener(this);
        
        setForeground(Styles.EditorColor.COLOR_FOREGROUND.getColor());
        setBackground(Styles.EditorColor.COLOR_BACKGROUND.getColor());
        
        Class<?> initModeClass = SegmentPropertiesTableView.class;
        String initModeClassName = Preferences.getPreferenceDefault(Preferences.SEGPROPS_INITIAL_MODE, null);
        if (initModeClassName != null) {
            try {
                initModeClass = getClass().getClassLoader().loadClass(initModeClassName);
            } catch (ClassNotFoundException e1) {
            }   
        }
        final Class<?> finalClass = initModeClass;
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                installView(finalClass);
            }
        });
        
        addMouseListener(contextMenuListener);
    } 
    
    private void installView(Class<?> viewClass) {
        if (viewImpl != null && viewImpl.getClass().equals(viewClass)) {
            return;
        }
        ISegmentPropertiesView newImpl;
        try {
            Constructor<?> constructor = viewClass.getConstructor();
            newImpl = (ISegmentPropertiesView) constructor.newInstance();
        } catch (Throwable e) {
            Logger.getLogger(getClass().getName()).log(Level.FINE, e.getMessage());
            return;
        }
        viewImpl = newImpl;
        viewImpl.install(this);
    }
    
    private void toggleMode(Class<?> newMode) {
        installView(newMode);
        Preferences.setPreference(Preferences.SEGPROPS_INITIAL_MODE, newMode.getName());
        viewImpl.update();
    }
    
    final MouseListener contextMenuListener = new MouseAdapter() {
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.isPopupTrigger()) {
                doPopup(e);
            }
        }
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.isPopupTrigger()) {
                doPopup(e);
            }
        }
        private void doPopup(MouseEvent e) {
            Point p = SwingUtilities.convertPoint((Component) e.getSource(), e.getPoint(), SegmentPropertiesArea.this);
            showContextMenu(p);
        }
    };
    
    void showContextMenu(Point p) {
        JPopupMenu menu = new JPopupMenu();
        populateLocalContextMenuOptions(menu, p);
        populateGlobalContextMenuOptions(menu);
        try {
            menu.show(SegmentPropertiesArea.this, p.x, p.y);
        } catch (IllegalComponentStateException ignore) {
            ignore.printStackTrace();
        }
    }
    
    private void populateLocalContextMenuOptions(JPopupMenu contextMenu, Point p) {
        final String key = viewImpl.getKeyAtPoint(p);
        if (key == null) {
            return;
        }
        String displayKey = key;
        if (!Preferences.isPreference(Preferences.SEGPROPS_SHOW_RAW_KEYS)) {
            try {
                displayKey = OStrings.getString(ISegmentPropertiesView.PROPERTY_TRANSLATION_KEY + key.toUpperCase());
            } catch (MissingResourceException ignore) {
                // If this is not a known key then we can't translate it,
                // so use the "raw" key instead.
            }
        }
        String label = StaticUtils.format(OStrings.getString("SEGPROP_CONTEXTMENU_NOTIFY_ON_PROP"), displayKey);
        final JMenuItem notifyOnItem = new JCheckBoxMenuItem(label);
        notifyOnItem.setSelected(getKeysToNotify().contains(key));
        notifyOnItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setKeyToNotify(key, notifyOnItem.isSelected());
            }
        });
        contextMenu.add(notifyOnItem);
        contextMenu.addSeparator();
    }
    
    private void populateGlobalContextMenuOptions(JPopupMenu contextMenu) {
        JMenuItem tableModeItem = new JCheckBoxMenuItem(OStrings.getString("SEGPROP_CONTEXTMENU_TABLE_MODE"));
        tableModeItem.setSelected(viewImpl.getClass().equals(SegmentPropertiesTableView.class));
        tableModeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleMode(SegmentPropertiesTableView.class);
            }
        });
        JMenuItem listModeItem = new JCheckBoxMenuItem(OStrings.getString("SEGPROP_CONTEXTMENU_LIST_MODE"));
        listModeItem.setSelected(viewImpl.getClass().equals(SegmentPropertiesListView.class));
        listModeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleMode(SegmentPropertiesListView.class);
            }
        });
        ButtonGroup group = new ButtonGroup();
        group.add(tableModeItem);
        group.add(listModeItem);
        contextMenu.add(tableModeItem);
        contextMenu.add(listModeItem);
        contextMenu.addSeparator();
        final JMenuItem toggleKeyTranslationItem = new JCheckBoxMenuItem(OStrings.getString("SEGPROP_CONTEXTMENU_RAW_KEYS"));
        toggleKeyTranslationItem.setSelected(Preferences.isPreference(Preferences.SEGPROPS_SHOW_RAW_KEYS));
        toggleKeyTranslationItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Preferences.setPreference(Preferences.SEGPROPS_SHOW_RAW_KEYS, toggleKeyTranslationItem.isSelected());
                viewImpl.update();
            }
        });
        contextMenu.add(toggleKeyTranslationItem);
    }
    
    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return null;
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        return getFont().getSize();
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle visibleRect,
            int orientation, int direction) {
        return getFont().getSize();
    }

    @Override
    public void onProjectChanged(PROJECT_CHANGE_TYPE eventType) {
        if (eventType == PROJECT_CHANGE_TYPE.CLOSE) {
            setProperties(null);            
        }
    }

    @Override
    public void onNewFile(String activeFileName) {}

    @Override
    public void onEntryActivated(SourceTextEntry newEntry) {
        getDockKey().setNotification(false);
        setProperties(newEntry);
        doNotify(getKeysToNotify());
    }
    
    private List<String> getKeysToNotify() {
        if (!Preferences.existsPreference(Preferences.SEGPROPS_NOTIFY_PROPS)) {
            Preferences.setPreference(Preferences.SEGPROPS_NOTIFY_PROPS, Preferences.SEGPROPS_NOTIFY_DEFAULT_PROPS);
        }
        String rawProps = Preferences.getPreferenceDefaultAllowEmptyString(Preferences.SEGPROPS_NOTIFY_PROPS);
        return Arrays.asList(SPLIT_COMMAS.split(rawProps));
    }
    
    private void setKeyToNotify(String key, boolean enabled) {
        List<String> currentKeys = new ArrayList<String>(getKeysToNotify());
        if (enabled && !currentKeys.contains(key)) {
            currentKeys.add(key);
        }
        if (!enabled && currentKeys.contains(key)) {
            currentKeys.remove(key);
        }
        Preferences.setPreference(Preferences.SEGPROPS_NOTIFY_PROPS, StringUtils.join(currentKeys, ", "));
    }
    
    @Override
    public void onFontChanged(Font newFont) {
        viewImpl.getViewComponent().setFont(newFont);
    }
    
    private void doNotify(List<String> keys) {
        final List<Integer> notify = new ArrayList<Integer>();
        for (int i = 0; i < properties.size(); i += 2) {
            String prop = properties.get(i);
            if (keys.contains(prop)) {
                notify.add(i);
            }
        }
        if (notify.isEmpty()) {
            return;
        }
        Collections.sort(notify);
        if (getDockKey().getLocation() == Location.HIDDEN) {
            getDockKey().setNotification(true);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                viewImpl.notifyUser(notify);
            }
        });
    }
    
    private void setProperty(String key, String value) {
        if (value != null) {
            properties.add(key);
            properties.add(value);
        }
    }
    
    private void setProperty(String key, Object value) {
        if (value != null) {
            setProperty(key, value.toString());
        }
    }
    
    private void setProperties(SourceTextEntry ste) {
        properties.clear();
        if (ste != null) {
            for (String s : ste.getRawProperties()) {
                properties.add(s);
            }
            if (ste.getDuplicate() != DUPLICATE.NONE) {
                setProperty(KEY_ISDUP, ste.getDuplicate());
            }
            if (ste.getSourceTranslation() != null) {
                setProperty(KEY_TRANSLATION, ste.getSourceTranslation());
                if (ste.isSourceTranslationFuzzy()) {
                    setProperty(KEY_TRANSLATIONISFUZZY, true);
                }
            }
            setKeyProperties(ste.getKey());
            TMXEntry trg = Core.getProject().getTranslationInfo(ste);
            setTranslationProperties(trg);
        }
        viewImpl.update();
    }
    
    private void setKeyProperties(EntryKey key) {
        setProperty(KEY_FILE, key.file);
        setProperty(KEY_ID, key.id);
        //setProperty(KEY_NEXT, key.next);
        //setProperty(KEY_PREV, key.prev);
        setProperty(KEY_PATH, key.path);
    }
    
    private void setTranslationProperties(TMXEntry entry) {
        if (entry.hasNote()) {
            setProperty(KEY_HASNOTE, true);
        }
        if (!entry.isTranslated()) {
            return;
        }
        if (entry.changeDate != 0) {
            setProperty(KEY_CHANGED, DATE_FORMAT.format(new Date(entry.changeDate))
                    + " " + TIME_FORMAT.format(new Date(entry.changeDate)));
        }
        setProperty(KEY_CHANGER, entry.changer);
        if (entry.creationDate != 0) {
            setProperty(KEY_CREATED, DATE_FORMAT.format(new Date(entry.creationDate))
                    + " " + TIME_FORMAT.format(new Date(entry.creationDate)));
        }
        setProperty(KEY_CREATOR, entry.creator);
        if (!entry.defaultTranslation) {
            setProperty(KEY_ISALT, true);
        }
        setProperty(KEY_LINKED, entry.linked);
    }
}
