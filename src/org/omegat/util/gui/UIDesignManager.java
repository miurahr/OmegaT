/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool
          with fuzzy matching, translation memory, keyword search,
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2000-2006 Keith Godfrey, Maxym Mykhalchuk, Henry Pijffers,
                         Benjamin Siband, and Kim Bruning
               2007 Zoltan Bartko
               2008 Andrzej Sawula, Alex Buloichik
               2009-2010 Alex Buloichik
               2014 Yu Tang
               2015 Aaron Madlon-Kay
               Home page: http://www.omegat.org/
               Support center: https://omegat.org/support

 This file is part of OmegaT.

 OmegaT is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 OmegaT is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **************************************************************************/

package org.omegat.util.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.plaf.ColorUIResource;

import com.formdev.flatlaf.FlatDarkLaf;
import com.vlsolutions.swing.docking.AutoHidePolicy;
import com.vlsolutions.swing.docking.AutoHidePolicy.ExpandMode;
import com.vlsolutions.swing.docking.DockableContainerFactory;
import com.vlsolutions.swing.docking.DockableState;
import com.vlsolutions.swing.docking.DockingDesktop;
import com.vlsolutions.swing.docking.ui.DockingUISettings;
import org.omegat.util.OStrings;
import org.omegat.util.Platform;

/**
 * UI Design Manager.
 * @author Keith Godfrey
 * @author Maxym Mykhalchuk
 * @author Henry Pijffers
 * @author Benjamin Siband
 * @author Kim Bruning
 * @author Zoltan Bartko
 * @author Andrzej Sawula
 * @author Alex Buloichik
 * @author Yu Tang
 * @author Aaron Madlon-Kay
 */
public final class UIDesignManager {

    private UIDesignManager() {
    }

    /**
     * Initialize docking subsystem.
     */
    public static void initialize() throws IOException {
        // load colors defaults
        loadDefaultColors();

        // Install VLDocking defaults
        DockingUISettings.getInstance().installUI();
        DockableContainerFactory.setFactory(new CustomContainerFactory());

        // Enable animated popup when mousing over minimized tab
        AutoHidePolicy.getPolicy().setExpandMode(ExpandMode.EXPAND_ON_ROLLOVER);

        // UI strings
        UIManager.put("DockViewTitleBar.minimizeButtonText", OStrings.getString("DOCKING_HINT_MINIMIZE"));
        UIManager.put("DockViewTitleBar.maximizeButtonText", OStrings.getString("DOCKING_HINT_MAXIMIZE"));
        UIManager.put("DockViewTitleBar.restoreButtonText", OStrings.getString("DOCKING_HINT_RESTORE"));
        UIManager.put("DockViewTitleBar.attachButtonText", OStrings.getString("DOCKING_HINT_DOCK"));
        UIManager.put("DockViewTitleBar.floatButtonText", OStrings.getString("DOCKING_HINT_UNDOCK"));
        UIManager.put("DockViewTitleBar.closeButtonText", "");
        UIManager.put("DockTabbedPane.minimizeButtonText", OStrings.getString("DOCKING_HINT_MINIMIZE"));
        UIManager.put("DockTabbedPane.maximizeButtonText", OStrings.getString("DOCKING_HINT_MAXIMIZE"));
        UIManager.put("DockTabbedPane.restoreButtonText", OStrings.getString("DOCKING_HINT_RESTORE"));
        UIManager.put("DockTabbedPane.floatButtonText", OStrings.getString("DOCKING_HINT_UNDOCK"));
        UIManager.put("DockTabbedPane.closeButtonText", "");

        // Fonts
        Font defaultFont = UIManager.getFont("Label.font");
        UIManager.put("DockViewTitleBar.titleFont", defaultFont);
        UIManager.put("JTabbedPaneSmartIcon.font", defaultFont);
        UIManager.put("AutoHideButton.font", defaultFont);

        // UI settings
        UIManager.put("DockViewTitleBar.isCloseButtonDisplayed", false);
        UIManager.put("DockingDesktop.closeActionAccelerator", null);
        UIManager.put("DockingDesktop.maximizeActionAccelerator", null);
        UIManager.put("DockingDesktop.dockActionAccelerator", null);
        UIManager.put("DockingDesktop.floatActionAccelerator", null);

        // Disused icons
        UIManager.put("DockViewTitleBar.menu.close", getIcon("empty.gif"));
        UIManager.put("DockTabbedPane.close", getIcon("empty.gif"));
        UIManager.put("DockTabbedPane.close.rollover", getIcon("empty.gif"));
        UIManager.put("DockTabbedPane.close.pressed", getIcon("empty.gif"));
        UIManager.put("DockTabbedPane.menu.close", getIcon("empty.gif"));

        // Classic design overridden by flat design
        //installClassicDesign();

        installFlatDesign();

        // Panel notification (blinking tabs/headers) settings
        UIManager.put("DockingDesktop.notificationBlinkCount", 2);
        UIManager.put("DockingDesktop.notificationColor", Styles.EditorColor.COLOR_NOTIFICATION_MAX.getColor());

        ensureTitlebarReadability();
    }

    @SuppressWarnings("unused")
    private static void installClassicDesign() {
        UIManager.put("OmegaTStatusArea.border", new MatteBorder(1, 1, 1, 1, Color.BLACK));

        UIManager.put("DockViewTitleBar.hide", getIcon("minimize.gif"));
        UIManager.put("DockViewTitleBar.hide.rollover", getIcon("minimize.rollover.gif"));
        UIManager.put("DockViewTitleBar.hide.pressed", getIcon("minimize.pressed.gif"));
        UIManager.put("DockViewTitleBar.maximize", getIcon("maximize.gif"));
        UIManager.put("DockViewTitleBar.maximize.rollover", getIcon("maximize.rollover.gif"));
        UIManager.put("DockViewTitleBar.maximize.pressed", getIcon("maximize.pressed.gif"));
        UIManager.put("DockViewTitleBar.restore", getIcon("restore.gif"));
        UIManager.put("DockViewTitleBar.restore.rollover", getIcon("restore.rollover.gif"));
        UIManager.put("DockViewTitleBar.restore.pressed", getIcon("restore.pressed.gif"));
        UIManager.put("DockViewTitleBar.dock", getIcon("restore.gif"));
        UIManager.put("DockViewTitleBar.dock.rollover", getIcon("restore.rollover.gif"));
        UIManager.put("DockViewTitleBar.dock.pressed", getIcon("restore.pressed.gif"));
        UIManager.put("DockViewTitleBar.float", getIcon("undock.gif"));
        UIManager.put("DockViewTitleBar.float.rollover", getIcon("undock.rollover.gif"));
        UIManager.put("DockViewTitleBar.float.pressed", getIcon("undock.pressed.gif"));
        UIManager.put("DockViewTitleBar.attach", getIcon("dock.gif"));
        UIManager.put("DockViewTitleBar.attach.rollover", getIcon("dock.rollover.gif"));
        UIManager.put("DockViewTitleBar.attach.pressed", getIcon("dock.pressed.gif"));

        UIManager.put("DockViewTitleBar.menu.hide", getIcon("minimize.gif"));
        UIManager.put("DockViewTitleBar.menu.maximize", getIcon("maximize.gif"));
        UIManager.put("DockViewTitleBar.menu.restore", getIcon("restore.gif"));
        UIManager.put("DockViewTitleBar.menu.dock", getIcon("restore.gif"));
        UIManager.put("DockViewTitleBar.menu.float", getIcon("undock.gif"));
        UIManager.put("DockViewTitleBar.menu.attach", getIcon("dock.gif"));

        UIManager.put("DockTabbedPane.menu.hide", getIcon("empty.gif"));
        UIManager.put("DockTabbedPane.menu.maximize", getIcon("empty.gif"));
        UIManager.put("DockTabbedPane.menu.float", getIcon("empty.gif"));
        UIManager.put("DockTabbedPane.menu.closeAll", getIcon("empty.gif"));
        UIManager.put("DockTabbedPane.menu.closeAllOther", getIcon("empty.gif"));

        UIManager.put("DragControler.detachCursor", getIcon("undock.gif").getImage());
    }

    private static void ensureTitlebarReadability() {
        // to ensure DockViewTitleBar title readability
        Color textColor = UIManager.getColor("InternalFrame.inactiveTitleForeground");
        Color backColor = UIManager.getColor("Panel.background");
        if (textColor != null && backColor != null) { // One of these could be null
            if (textColor.equals(backColor)) {
                float[] hsb = Color.RGBtoHSB(textColor.getRed(),
                        textColor.getGreen(), textColor.getBlue(), null);
                float brightness = hsb[2]; // darkest 0.0f <--> 1.0f brightest
                if (brightness >= 0.5f) {
                    brightness -= 0.5f; // to darker
                } else {
                    brightness += 0.5f; // to brighter
                }
                int rgb = Color.HSBtoRGB(hsb[0], hsb[1], brightness);
                ColorUIResource res = new ColorUIResource(rgb);
                UIManager.put("InternalFrame.inactiveTitleForeground", res);
            }
        }

        UIManager.put("DockingDesktop.notificationBlinkCount", 2);
        UIManager.put("DockingDesktop.notificationColor", Styles.EditorColor.COLOR_NOTIFICATION_MAX.getColor());
    }

    private static void installFlatDesign() {
        // Colors
        // #EEEEEE on Metal & OS X LAF
        Color standardBgColor = UIManager.getColor("Panel.background");
        // #EEEEEE -> #F6F6F6; Lighter than standard background
        Color activeTitleBgColor = adjustRGB(standardBgColor, 0xF6 - 0xEE);
        // #EEEEEE -> #DEDEDE; Darkest background
        Color bottomAreaBgColor = adjustRGB(standardBgColor, 0xDE - 0xEE);
        // #EEEEEE -> #9B9B9B; Standard border. Darker than standard background.
        Color borderColor = adjustRGB(standardBgColor, 0x9B - 0xEE);
        UIManager.put("OmegaTBorder.color", borderColor);
        // #EEEEEE -> #575757; Darkest border
        Color statusAreaColor = adjustRGB(standardBgColor, 0x57 - 0xEE);

        // General highlight & shadow used in a lot of places
        UIManager.put("VLDocking.highlight", activeTitleBgColor);
        UIManager.put("VLDocking.shadow", statusAreaColor);

        // Main window main area
        int outside = 5;
        UIManager.put("DockingDesktop.border", new EmptyBorder(outside, outside, outside, outside));

        // Docked, visible panels get two borders if we're not careful:
        // 1. Drawn by VLDocking. Surrounds panel content AND header. Set this to empty margin instead.
        int panel = 2;
        UIManager.put("DockView.singleDockableBorder", new EmptyBorder(panel, panel, panel, panel));
        int maxPanel = outside + panel;
        UIManager.put("DockView.maximizedDockableBorder", new EmptyBorder(maxPanel, maxPanel, maxPanel, maxPanel));
        // 2. Drawn by OmegaT-defined Dockables. Make this a 1px line.
        UIManager.put("OmegaTDockablePanel.border", new MatteBorder(1, 1, 1, 1, borderColor));

        // GTK+ LAF has a default border on the viewport. Disable this.
        UIManager.put("OmegaTDockablePanelViewport.border", new EmptyBorder(0, 0, 0, 0));

        // Use proportionally sized internal margin for text document-like panels
        UIManager.put("OmegaTDockablePanel.isProportionalMargins", true);

        // Tabbed docked, visible panels are surrounded by LAF-specific chrome, but the surrounding
        // colors don't appear to be available through the API. These values are from visual inspection.
        if (Platform.isMacOSX()) {
            UIManager.put("DockView.tabbedDockableBorder", new MatteBorder(0, 5, 5, 5, new Color(0xE6E6E6)));
        } else if (isWindowsLAF() && !isWindowsClassicLAF()) {
            UIManager.put("DockView.tabbedDockableBorder", new MatteBorder(2, 5, 5, 5, Color.WHITE));
        } else {
            UIManager.put("DockView.tabbedDockableBorder", new MatteBorder(5, 5, 5, 5, standardBgColor));
        }

        // Windows 8+ is very square.
        int cornerRadius = isFlatWindows() ? 0 : 8;

        // Panel title bars
        Color activeTitleText = UIManager.getColor("Label.foreground");
        // #000000 -> #808080; GTK+ has Color.WHITE for Label.disabledForeground
        Color inactiveTitleText = adjustRGB(activeTitleText, 0x80);
        UIManager.put("DockViewTitleBar.border",
                new RoundedCornerBorder(cornerRadius, borderColor, RoundedCornerBorder.SIDE_TOP));
        // Windows 7 "Classic" has Color.WHITE for this
        UIManager.put("InternalFrame.activeTitleForeground", activeTitleText);
        UIManager.put("InternalFrame.activeTitleBackground", activeTitleBgColor);
        UIManager.put("InternalFrame.inactiveTitleForeground", inactiveTitleText);
        UIManager.put("InternalFrame.inactiveTitleBackground", standardBgColor);
        // Disable gradient on pane title bars
        UIManager.put("DockViewTitleBar.disableCustomPaint", true);

        // Main window bottom area

        // AutoHideButtonPanel is where minimized panel tabs go. Use compound border to give left/right margins.
        UIManager.put("AutoHideButtonPanel.bottomBorder", new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, borderColor),
                new EmptyBorder(0, 2 * outside, 0, 2 * outside)));
        UIManager.put("AutoHideButtonPanel.background", bottomAreaBgColor);
        UIManager.put("AutoHideButton.expandBorderBottom",
                new RoundedCornerBorder(cornerRadius, borderColor, RoundedCornerBorder.SIDE_BOTTOM));
        UIManager.put("AutoHideButton.background", standardBgColor);
        // OmegaT-defined status box in lower right
        UIManager.put("OmegaTStatusArea.border", new MatteBorder(1, 1, 1, 1, statusAreaColor));
        // Lowermost section margins
        UIManager.put("OmegaTMainWindowBottomMargin.border", new EmptyBorder(0, 2 * outside, outside, 2 * outside));

        UIManager.put("OmegaTEditorFilter.border", new MatteBorder(1, 1, 0, 1, borderColor));

        // Undocked panel
        UIManager.put("activeCaption", Color.WHITE);
        UIManager.put("activeCaptionBorder", borderColor);
        UIManager.put("inactiveCaption", standardBgColor);
        UIManager.put("inactiveCaptionBorder", borderColor);

        // Icons
        UIManager.put("DockViewTitleBar.maximize", getIcon("appbar.app.tall.inactive.png"));
        UIManager.put("DockViewTitleBar.maximize.rollover", getIcon("appbar.app.tall.png"));
        UIManager.put("DockViewTitleBar.maximize.pressed", getIcon("appbar.app.tall.pressed.png"));
        UIManager.put("DockViewTitleBar.restore", getIcon("appbar.window.restore.inactive.png"));
        UIManager.put("DockViewTitleBar.restore.rollover", getIcon("appbar.window.restore.png"));
        UIManager.put("DockViewTitleBar.restore.pressed", getIcon("appbar.window.restore.pressed.png"));
        UIManager.put("DockViewTitleBar.hide", getIcon("appbar.hide.inactive.png"));
        UIManager.put("DockViewTitleBar.hide.rollover", getIcon("appbar.hide.png"));
        UIManager.put("DockViewTitleBar.hide.pressed", getIcon("appbar.hide.pressed.png"));
        UIManager.put("DockViewTitleBar.float", getIcon("appbar.fullscreen.inactive.png"));
        UIManager.put("DockViewTitleBar.float.rollover", getIcon("appbar.fullscreen.png"));
        UIManager.put("DockViewTitleBar.float.pressed", getIcon("appbar.fullscreen.pressed.png"));
        UIManager.put("DockViewTitleBar.dock", getIcon("appbar.window.restore.inactive.png"));
        UIManager.put("DockViewTitleBar.dock.rollover", getIcon("appbar.window.restore.png"));
        UIManager.put("DockViewTitleBar.dock.pressed", getIcon("appbar.window.restore.pressed.png"));
        UIManager.put("DockViewTitleBar.attach", getIcon("appbar.dock.window.inactive.png"));
        UIManager.put("DockViewTitleBar.attach.rollover", getIcon("appbar.dock.window.png"));
        UIManager.put("DockViewTitleBar.attach.pressed", getIcon("appbar.dock.window.pressed.png"));

        UIManager.put("DockViewTitleBar.menu.hide", getIcon("appbar.hide.png"));
        UIManager.put("DockViewTitleBar.menu.maximize", getIcon("appbar.app.tall.png"));
        UIManager.put("DockViewTitleBar.menu.restore", getIcon("appbar.window.restore.png"));
        UIManager.put("DockViewTitleBar.menu.dock", getIcon("appbar.window.restore.png"));
        UIManager.put("DockViewTitleBar.menu.float", getIcon("appbar.fullscreen.png"));
        UIManager.put("DockViewTitleBar.menu.attach", getIcon("appbar.dock.window.png"));

        UIManager.put("DockTabbedPane.menu.hide", getIcon("appbar.hide.png"));
        UIManager.put("DockTabbedPane.menu.maximize", getIcon("appbar.app.tall.png"));
        UIManager.put("DockTabbedPane.menu.float", getIcon("appbar.fullscreen.png"));

        // Windows only accepts a 32x32 cursor image with no semitransparency, so you basically
        // need a special image just for that.
        UIManager.put("DragControler.detachCursor", ResourcesUtil.getBundledImage("appbar.fullscreen.cursor32x32.png"));

        // Use more native-looking icons on OS X
        if (Platform.isMacOSX()) {
            UIManager.put("DockViewTitleBar.maximize", getIcon("appbar.fullscreen.corners.inactive.png"));
            UIManager.put("DockViewTitleBar.maximize.rollover", getIcon("appbar.fullscreen.corners.png"));
            UIManager.put("DockViewTitleBar.maximize.pressed", getIcon("appbar.fullscreen.corners.pressed.png"));
            UIManager.put("DockViewTitleBar.restore", getIcon("appbar.restore.corners.inactive.png"));
            UIManager.put("DockViewTitleBar.restore.rollover", getIcon("appbar.restore.corners.png"));
            UIManager.put("DockViewTitleBar.restore.pressed", getIcon("appbar.restore.corners.pressed.png"));
            UIManager.put("DockViewTitleBar.hide", getIcon("appbar.minus.inactive.png"));
            UIManager.put("DockViewTitleBar.hide.rollover", getIcon("appbar.minus.png"));
            UIManager.put("DockViewTitleBar.hide.pressed", getIcon("appbar.minus.pressed.png"));

            UIManager.put("DockViewTitleBar.menu.hide", getIcon("appbar.minus.png"));
            UIManager.put("DockViewTitleBar.menu.maximize", getIcon("appbar.fullscreen.corners.png"));
            UIManager.put("DockViewTitleBar.menu.restore", getIcon("appbar.restore.corners.png"));

            UIManager.put("DockTabbedPane.menu.hide", getIcon("appbar.minus.png"));
            UIManager.put("DockTabbedPane.menu.maximize", getIcon("appbar.fullscreen.corners.png"));

            UIManager.put("DragControler.detachCursor", ResourcesUtil.getBundledImage("appbar.fullscreen.png"));
        }
    }

    /**
     * Adjust a color by adding some constant to its RGB values, clamping to the
     * range 0-255.
     */
    private static Color adjustRGB(Color color, int adjustment) {
        Color result = new Color(Math.max(0, Math.min(255, color.getRed() + adjustment)),
                Math.max(0, Math.min(255, color.getGreen() + adjustment)),
                Math.max(0, Math.min(255, color.getBlue() + adjustment)));
        return result;
    }

    // Windows Classic LAF detection from http://stackoverflow.com/a/4386821/448068
    private static boolean isWindowsLAF() {
        return UIManager.getLookAndFeel().getID().equals("Windows");
    }

    private static boolean isWindowsClassicLAF() {
        return isWindowsLAF() && !(Boolean) Toolkit.getDefaultToolkit().getDesktopProperty("win.xpstyle.themeActive");
    }

    // This check fails to detect Windows 10 correctly on Java 1.8 prior to u60.
    // See: https://bugs.openjdk.java.net/browse/JDK-8066504
    private static boolean isFlatWindows() {
        return System.getProperty("os.name").startsWith("Windows")
                && System.getProperty("os.version").matches("6\\.[23]|10\\..*");
    }

    /**
     * Load icon from classpath.
     *
     * @param iconName
     *            icon file name
     * @return icon instance
     */
    private static ImageIcon getIcon(String iconName) {
        Image image = ResourcesUtil.getBundledImage(iconName);
        return image == null ? null : new ImageIcon(image);
    }

    /**
     * Removes first, last and duplicate separators from menu.
     */
    public static void removeUnusedMenuSeparators(final JPopupMenu menu) {
        if (menu.getComponentCount() > 0 && menu.getComponent(0) instanceof JSeparator) {
            // remove first separator
            menu.remove(0);
        }
        if (menu.getComponentCount() > 0
                && menu.getComponent(menu.getComponentCount() - 1) instanceof JSeparator) {
            // remove last separator
            menu.remove(menu.getComponentCount() - 1);
        }
        for (int i = 0; i < menu.getComponentCount() - 1; i++) {
            if (menu.getComponent(i) instanceof JSeparator && menu.getComponent(i + 1) instanceof JSeparator) {
                // remove duplicate separators
                menu.remove(i);
            }
        }
    }

    /**
     * Ensure that any "closed" Dockables are made visible.
     */
    public static void ensureDockablesVisible(DockingDesktop desktop) {
        for (DockableState state : desktop.getDockables()) {
            if (state.isClosed()) {
                // VLDocking says this is how you re-show a closed Dockable,
                // but it prints a stack trace. So just ignore it?
                desktop.addDockable(state.getDockable());
            }
        }
    }

    /**
     * Traverse the given container's parents until either an instance of
     * DockingDesktop is found, or null is found.
     *
     * @param c
     *            The container to search
     * @return Either the parent DockingDesktop, or null
     */
    public static DockingDesktop getDesktop(Container c) {
        while (c != null && !(c instanceof DockingDesktop)) {
            c = c.getParent(); // find dockable desktop
        }
        return (DockingDesktop) c;
    }

    /**
     * Heuristic detection of dark theme.
     * <p>
     *     isDarkTheme method derived from NetBeans licensed by Apache-2.0
     * @return true when dark theme, otherwise false.
     */
    private static boolean isDarkTheme() {
        // Based on tests with different LAFs and color combinations, a light
        // theme can be reliably detected by observing the brightness value of
        // the HSB Values of Table.background and Table.foreground
        //
        // Results from the test (Theme / Foreground / Background)
        // Gtk - Numix (light) / 0.2 / 0.97
        // Gtk - BlackMATE (dark) / 1.0 / 0.24
        // Gtk - Clearlooks (light) / 0.1 / 1.0
        // Gtk - ContrastHighInverse (dark) / 1.0 / 0.0
        // Gtk - DustSand (light) / 0.19 / 1.0
        // Gtk - TraditionalOkTest (light) / 0.0 / 0.74
        // Gtk - Menta (light) / 0.17 / 0.96
        // DarkNimbus (dark) / 0.9 / 0.19
        // DarkMetal (dark) / 0.87 / 0.19
        // CDE (light) / 0.0 / 0.76
        // Nimbus (light) / 0.0 / 1.0
        // Metall (light) / 0.2 / 1.0
        // Windows (light) / 0.0 / 1.0
        // Windows Classic (light) / 0.0 / 1.0
        // Windows HighContrast Black (dark) / 1.0 / 0
        Color foreground = UIManager.getColor("Table.foreground");
        Color background = UIManager.getColor("Table.background");
        float foreground_brightness = Color.RGBtoHSB(
                foreground.getRed(),
                foreground.getGreen(),
                foreground.getBlue(),
                null)[2];
        float background_brightness = Color.RGBtoHSB(
                background.getRed(),
                background.getGreen(),
                background.getBlue(),
                null)[2];
        return background_brightness < foreground_brightness;
    }

    private static void loadColors(final String scheme) throws IOException {
        ResourcesUtil.getBundleColorProperties(scheme).forEach((k, v) -> {
            if (v.toString().charAt(0) != '#') {
                throw new RuntimeException("Invalid color value for key " + k + ": " + v);
            }
            try {
                String hex = v.toString().substring(1);
                Color color;
                if (hex.length() <= 6) {
                    color = new Color(Integer.parseInt(hex, 16)); // int(rgb)
                } else {
                    long val = Long.parseLong(hex, 16);
                    int a = (int) (val & 0xFF);
                    int b = (int) (val >> 8 & 0xFF);
                    int g = (int) (val >> 16 & 0xFF);
                    int r = (int) (val >> 24 & 0xFF);
                    color = new Color(r, g, b, a); // hasAlpha
                }
                UIManager.put(k.toString(), color);
            } catch (NumberFormatException ex) {
                throw new RuntimeException("Invalid color value for key '" + k + "': " + v, ex);
            }
        });
    }

    /**
     * Set LookAndFeel default and load application colors
     */
    public static void setApplicationLaf() throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatDarkLaf());
        UIManager.put( "Button.arc", 999 );
        UIManager.put( "Component.arc", 999 );
        UIManager.put( "ProgressBar.arc", 999 );
        UIManager.put( "TextComponent.arc", 999 );
    }

    /**
     * Load application default colors
     */
    private static void loadDefaultColors() throws IOException {
        Color hilite;
        if (isDarkTheme()) {
            loadColors("dark");
            hilite = UIManager.getColor("TextArea.background").brighter();  // NOI18N
            // Hack for JDK GTKLookAndFeel bug.
            // TextPane.background is always white but should be a text_background of GTK.
            // List.background is as same color as text_background.
            if (Platform.isLinux() && Color.WHITE.equals(UIManager.getColor("TextPane.background"))) {
                UIManager.put("TextPane.background", UIManager.getColor("List.background"));
            }
        } else {
            loadColors("light");
            Color bg = UIManager.getColor("TextArea.background").darker();  // NOI18N
            hilite = new Color(bg.getRed(), bg.getBlue(), bg.getGreen(), 32);
        }
        UIManager.put("OmegaT.alternatingHilite", hilite);
    }

}
