/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2000-2006 Keith Godfrey, Maxym Mykhalchuk, 
                            Sandra Jean Chua, and Henry Pijffers
               2007 Didier Briel
               Home page: http://www.omegat.org/
               Support center: http://groups.yahoo.com/group/OmegaT/

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 **************************************************************************/

package org.omegat.gui.help;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.omegat.util.LFileCopy;
import org.omegat.util.Log;
import org.omegat.util.OConsts;
import org.omegat.util.OStrings;
import org.omegat.util.Preferences;
import org.omegat.util.StaticUtils;
import org.openide.awt.Mnemonics;

/**
 * Frame that displays help HTML files. Singleton.
 * 
 * @author Keith Godfrey
 * @author Sandra Jean Chua - sachachua at users.sourceforge.net
 * @author Maxym Mykhalchuk
 * @author Henry Pijffers (henry.pijffers@saxnot.com)
 */
public class HelpFrame extends JFrame {
    /*
     * The Singleton design pattern allows us to have just one instance of the
     * help frame at all times. In order to use this pattern, we need to prevent
     * other classes from calling HelpFrame's constructor. To get a reference to
     * the help frame, classes should call the static getInstance() method.
     */
    private static HelpFrame singleton;

    /** Creates the Help Frame */
    private HelpFrame() {
        m_historyList = new ArrayList<String>();

        // set window size & position
        initWindowLayout();

        Container cp = getContentPane();
        m_helpPane = new JEditorPane();
        m_helpPane.setEditable(false);
        m_helpPane.setContentType("text/html");
        JScrollPane scroller = new JScrollPane(m_helpPane);
        cp.add(scroller, "Center");

        m_homeButton = new JButton();
        m_homeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                m_historyList.add(m_filename);
                displayHome();
                m_backButton.setEnabled(true);
            }
        });

        m_backButton = new JButton();
        m_backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (m_historyList.size() > 0) {
                    String s = m_historyList.remove(m_historyList.size() - 1);
                    displayFile(s);
                }
                if (m_historyList.isEmpty()) {
                    m_backButton.setEnabled(false);
                }
            }
        });
        m_backButton.setEnabled(false);

        m_closeButton = new JButton();
        m_closeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        Box bbut = Box.createHorizontalBox();
        bbut.add(m_backButton);
        bbut.add(Box.createHorizontalStrut(10));
        bbut.add(m_homeButton);
        bbut.add(Box.createHorizontalGlue());
        bbut.add(m_closeButton);
        cp.add(bbut, "North");

        // HP
        // Handle escape key to close the window
        KeyStroke escape = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);
        Action escapeAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        };
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                escape, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", escapeAction);
        // END HP

        m_helpPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent he) {
                if (he.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    m_historyList.add(m_filename);
                    displayFile(he.getDescription());
                    m_backButton.setEnabled(true);
                }
            }
        });

        updateUIText();
        displayHome();
    }

    /**
     * Gets the only instance of Help Frame
     */
    public static HelpFrame getInstance() {
        if (singleton == null) {
            singleton = new HelpFrame();
        }
        return singleton;
    }

    public static InputStream getHelpFileStream(String filename)
            throws IOException {
        // find in install dir
        InputStream in = new FileInputStream(new File(StaticUtils.installDir()
                + File.separator + OConsts.HELP_DIR, filename));
        if (in == null) {
            // find in classpath
            in = HelpFrame.class.getResourceAsStream('/' + OConsts.HELP_DIR
                    + '/' + filename);

        }
        return in;
    }

    public static URL getHelpFileURL(String filename) throws IOException {
        // find in install dir
        File f = new File(StaticUtils.installDir() + File.separator
                + OConsts.HELP_DIR, filename);
        if (f.exists()) {
            return f.toURI().toURL();
        }
        // find in classpath
        URL r = HelpFrame.class.getResource('/' + OConsts.HELP_DIR + '/'
                + filename);

        return r;
    }

    public void displayHome() {
        // If not set, get the language (according to
        // the system locale) to display the manual in
        if (m_language == null) {
            m_language = detectDocLanguage();

            // If the manual is not available in the system locale language,
            // show a language selection screen
            if (m_language == null) {
                displayLanguageIndex();
                return;
            }
        }

        // Display the manual's index page
        displayFile(OConsts.HELP_HOME);
    }

    public void displayLanguageIndex() {
        try {
            // Read template from docs/languageIndex.html
            StringWriter templateText = new StringWriter(1024);

            InputStream in = getHelpFileStream(OConsts.HELP_LANG_INDEX);
            if (in == null) {
                throw new IOException(
                        "There is no 'docs/languageIndex.html' in install_dir or classpath");
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(in,
                    OConsts.UTF8));
            try {
                LFileCopy.copy(rd, templateText);
            } finally {
                rd.close();
            }

            // Get available translations and their versions
            StringBuffer translations = new StringBuffer(1024);
            translations.append("<table>\n");

            Set<String> subDirs = getTranslationsList();
            for (String locale : subDirs) {
                if (locale.length() == 0) {
                    // skip empty lines
                    continue;
                }

                // Get the locale name and translation version
                String localeName = getLocaleName(locale);
                String transVersion = getDocVersion(locale);

                // Skip incomplete translations
                if (transVersion == null)
                    continue;

                // Add some HTML for the translation
                translations
                        .append("<tr><td><a href=\"omegat:select-lang?lang=");
                translations.append(locale);
                translations.append("\">");
                translations.append(localeName);
                translations.append("</a></td><td>(");
                if (transVersion.equals(OStrings.VERSION))
                    translations.append("<font color=\"green\"><strong>");
                else
                    translations.append("<font color=\"red\">");
                translations.append(transVersion);
                if (transVersion.equals(OStrings.VERSION))
                    translations.append("</strong>");
                translations.append("</font>)</td></tr>\n");
            }
            translations.append("</table>");

            // Insert the translations table in the right place
            String index = templateText.toString().replaceFirst("\\$INDEX",
                    translations.toString());

            // Display the language selection page
            // m_helpPane.setContentType("text/plain"); // workaround for Java
            // (?)
            // bug
            // m_helpPane.setContentType("text/html"); // workaround for Java
            // (?)
            // bug
            m_helpPane.setText(index);
            m_helpPane.setCaretPosition(0);

            // Mark the current page, so we can get back to it
            m_filename = "omegat:lang-index";
        } catch (IOException ex) {
            Log.log(ex);
        }
    }

    /**
     * Load translations list from docs/list file.
     * 
     * @return translations list
     */
    private static Set<String> getTranslationsList() throws IOException {
        Set<String> result = new TreeSet<String>();
        for (Locale loc : Locale.getAvailableLocales()) {
            String language = loc.getLanguage().toLowerCase();
            String country = loc.getCountry().toUpperCase();

            if (getDocVersion(language) != null) {
                result.add(language);
            }
            String locName = language + '_' + country;
            if (getDocVersion(locName) != null) {
                result.add(locName);
            }
        }
        return result;
    }

    /**
     * Displays some file in Online Help.
     * <p>
     * If the <code>file</code> is a full URL starting from <code>http://</code>
     * , then say
     * 
     * <pre>
     * &lt;p&gt;You can display the User Manual in a normal web browser and have
     * access to external links by opening the &lt;b&gt;index.html&lt;/b&gt; file
     * located in the &lt;b&gt;/docs/&lt;/b&gt; directory of the OmegaT application
     * directory.&lt;/p&gt;
     * </pre>
     * 
     * @param file
     *            the file to display
     */
    private void displayFile(String file) {
        // workaround for Java (?) bug
        // m_helpPane.setContentType("text/plain");
        // m_helpPane.setContentType("text/html");

        if (file.startsWith("http://")) {
            String link = "<b>" + file + "</b>";
            StringBuffer buf = new StringBuffer();
            buf.append("<html><body><p>");
            buf.append(StaticUtils
                    .format(OStrings.getString("HF_ERROR_EXTLINK_TITLE"),
                            new Object[] { link }));
            buf.append("<p>");
            buf.append(StaticUtils.format(OStrings
                    .getString("HF_ERROR_EXTLINK_MSG"),
                    new Object[] { "<b>index.html</b>" }));
            buf.append("</body></html>");

            m_helpPane.setText(buf.toString());
        } else if (file.startsWith("omegat:")) {
            handleCommand(file);
        } else {
            if (file.startsWith("#"))
                file = m_filename_nosharp + file;

            int sharppos = file.indexOf('#');
            if (sharppos < 0) {
                sharppos = file.length();
            }
            String anch = file.substring(sharppos);
            m_filename_nosharp = file.substring(0, sharppos);

            String fullname = m_language + "/" + m_filename_nosharp;

            try {
//                fullname = fullname.replace("/./", "/").replace("//", "/");
                URL page = getHelpFileURL(fullname);
                m_helpPane.setPage(page);
                if (anch != null && anch.length() > 0) {
                    m_helpPane.scrollToReference(anch.substring(1));
                } else {
                    m_helpPane.setCaretPosition(0);
                }
                m_filename = file;
            } catch (IOException e) {
                String s = errorHaiku() + "<p>&nbsp;<p>"
                        + OStrings.getString("HF_CANT_FIND_HELP") + fullname;

                m_helpPane.setText(s);
            }
        }
    }

    private void handleCommand(String command) {
        // Check if the command is really a command
        if (!command.startsWith("omegat:"))
            throw new IllegalArgumentException(
                    "Command must start with 'omegat:'");

        // Extract the actual command string
        command = command.substring(7, command.length());

        // Handle the command
        if (command.startsWith("select-lang")) { // Language selection command
            // Get the language
            int langPos = command.indexOf("lang=");
            m_language = command.substring(langPos + 5, command.length());

            // Display the user manual index
            displayHome();
        } else if (command.startsWith("lang-index")) { // Display language index
            // command
            // Display the language index page
            displayLanguageIndex();
        } else {
            // We don't support the given command
            throw new IllegalArgumentException("Unrecognized command");
        }
    }

    // immortalize the BeOS 404 messages (some modified a bit for context)
    private String errorHaiku() {
        int id = new Random().nextInt(11) + 1;
        return OStrings.getString("HF_HAIKU_" + id);
    }

    private void updateUIText() {
        Mnemonics.setLocalizedText(m_closeButton, OStrings
                .getString("BUTTON_CLOSE"));
        Mnemonics.setLocalizedText(m_homeButton, OStrings
                .getString("BUTTON_HOME"));
        Mnemonics.setLocalizedText(m_backButton, OStrings
                .getString("BUTTON_BACK"));
        setTitle(OStrings.getString("HF_WINDOW_TITLE"));
    }

    /**
     * Detects the documentation language to use.
     * 
     * @author Henry Pijffers (henry.pijffers@saxnot.com)
     */
    private static String detectDocLanguage() {
        // Get the system locale (language and country)
        String language = Locale.getDefault().getLanguage().toLowerCase();
        String country = Locale.getDefault().getCountry().toUpperCase();

        // Check if there's a translation for the full locale (lang + country)
        String locale = language + "_" + country;
        String version = getDocVersion(locale);
        if (version != null && version.equals(OStrings.VERSION))
            return locale;

        // Check if there's a translation for the language only
        locale = language;
        version = getDocVersion(locale);
        if (version != null && version.equals(OStrings.VERSION))
            return locale;

        // No suitable translation found
        return null;
    }

    /**
     * Returns the version of (a translation of) the user manual. If there is no
     * translation for the specified locale, null is returned.
     * 
     * @author Henry Pijffers (henry.pijffers@saxnot.com)
     */
    private static String getDocVersion(String locale) {
        // Check if there's a manual for the specified locale
        // (Assume yes if the index file is there)
        String file = locale + "/version.properties";

        // Load the property file containing the doc version
        Properties prop = new Properties();
        InputStream in = null;
        try {
            in = getHelpFileStream(file);
            if (in == null) {
                return null;
            }
            prop.load(in);
        } catch (IOException ex) {
            return null;
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
            }
        }

        // Get the doc version and return it
        // (null if the version entry is not present)
        return prop.getProperty("version");
    }

    /**
     * Returns the full locale name for a locale tag.
     * 
     * @see Locale.getDisplayName(Locale inLocale)
     * @author Henry Pijffers (henry.pijffers@saxnot.com)
     * @author Didier Briel
     */
    private String getLocaleName(String localeTag) {
        String language = localeTag.substring(0, 2);
        String country = localeTag.length() >= 5 ? localeTag.substring(3, 5)
                : "";
        Locale locale = new Locale(language, country);
        // The following test is necessary to fix
        // [1748552] sh language is not expanded in the manual
        // since Java does not display correctly the "sh" langage name
        if (language.equalsIgnoreCase("sh"))
            return "srpskohrvatski";
        else
            return locale.getDisplayName(locale);
    }

    /**
     * Loads/sets the position and size of the help window.
     */
    private void initWindowLayout() {
        // main window
        try {
            String dx = Preferences.getPreference(Preferences.HELPWINDOW_X);
            String dy = Preferences.getPreference(Preferences.HELPWINDOW_Y);
            int x = Integer.parseInt(dx);
            int y = Integer.parseInt(dy);
            setLocation(x, y);
            String dw = Preferences.getPreference(Preferences.HELPWINDOW_WIDTH);
            String dh = Preferences
                    .getPreference(Preferences.HELPWINDOW_HEIGHT);
            int w = Integer.parseInt(dw);
            int h = Integer.parseInt(dh);
            setSize(w, h);
        } catch (NumberFormatException nfe) {
            // set default size and position
            setSize(600, 500);
        }
    }

    /**
     * Saves the size and position of the help window
     */
    private void saveWindowLayout() {
        Preferences.setPreference(Preferences.HELPWINDOW_WIDTH, getWidth());
        Preferences.setPreference(Preferences.HELPWINDOW_HEIGHT, getHeight());
        Preferences.setPreference(Preferences.HELPWINDOW_X, getX());
        Preferences.setPreference(Preferences.HELPWINDOW_Y, getY());
    }

    public void processWindowEvent(WindowEvent w) {
        int evt = w.getID();
        if (evt == WindowEvent.WINDOW_CLOSING
                || evt == WindowEvent.WINDOW_CLOSED) {
            // save window size and position
            saveWindowLayout();
        }
        super.processWindowEvent(w);
    }

    private JEditorPane m_helpPane;
    private JButton m_closeButton;
    private JButton m_homeButton;
    private JButton m_backButton;
    private List<String> m_historyList;

    /**
     * Stores the information about the currently opened HTML file, without
     * trailing #...
     */
    private String m_filename_nosharp;

    /**
     * Stores the full information about the currently opened HTML file,
     * including trailing #...
     */
    private String m_filename = "";

    /** The language of the help files, English by default */
    private String m_language;
}
