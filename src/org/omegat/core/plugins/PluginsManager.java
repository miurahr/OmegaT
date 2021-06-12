/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool
          with fuzzy matching, translation memory, keyword search,
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2021 Hiroshi Miura
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

package org.omegat.core.plugins;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.jar.Manifest;

import org.omegat.core.data.PluginInformation;
import org.omegat.filters2.master.PluginUtils;
import org.omegat.gui.preferences.view.PluginsPreferencesController;
import org.omegat.util.Log;
import org.omegat.util.StaticUtils;

/**
 * Plugin information and installation manager.
 *
 * @author Hiroshi Miura
 */
public final class PluginsManager {

    private final Map<String, PluginInformation> installedPlugins;

    /**
     * Fallback properties file of plugin descriptions.
     */
    public static final String PLUGINS_LIST_FILE = "Plugins.properties";

    /**
     * plugin folder in program installation folder.
     */
    public static final File pluginsDir = new File(StaticUtils.installDir(), "plugins");
    /**
     * plugin folder for user installation.
     */
    public static final File homePluginsDir = new File(StaticUtils.getConfigDir(), "plugins");

    public PluginsManager() {
        installedPlugins = new TreeMap<>();
        PluginUtils.getPluginInformations().stream()
                .sorted(Comparator.comparing(PluginInformation::getClassName))
                .filter(info -> !installedPlugins.containsKey(getPluginInformationKey(info)))
                .forEach(info -> installedPlugins.put(getPluginInformationKey(info), info));
    }

    /**
     * Get plugin information installed to system specified by parameter.
     * @param info PluginInformation to search
     * @return PluginInformation when found, otherwise return null
     */
    public PluginInformation getInstalledPluginInformation(PluginInformation info) {
        return installedPlugins.getOrDefault(getPluginInformationKey(info), null);
    }

    /**
     * Parse Manifest from plugin jar file.
     * @param pluginJarFile plugin jar file
     * @return PluginInformation
     */
    public Set<PluginInformation> parsePluginJarFileManifest(File pluginJarFile) {
        Set<PluginInformation> pluginInfo = new HashSet<>();
        try {
            URL[] urls = new URL[1];
            urls[0] = pluginJarFile.toURI().toURL();
            URLClassLoader pluginsClassLoader = new URLClassLoader(urls,
                    PluginsPreferencesController.class.getClassLoader());
            for (Enumeration<URL> mlist = pluginsClassLoader.getResources("META-INF/MANIFEST.MF"); mlist
                    .hasMoreElements();) {
                URL mu = mlist.nextElement();
                try (InputStream in = mu.openStream()) {
                    Manifest m = new Manifest(in);
                    String pluginClasses = m.getMainAttributes().getValue("OmegaT-Plugins");
                    if (pluginClasses != null) {
                        for (String clazz : pluginClasses.split("\\s+")) {
                            if (clazz.trim().isEmpty()) {
                                continue;
                            }
                            pluginInfo.add(new PluginInformation(clazz, m, mu));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Log.log(ex);
        }
        return pluginInfo;
    }

    /**
     * Format plugin information for details pane of UI.
     * @param info PluginInformation to show
     * @return HTML text
     */
    public static String formatDetailText(PluginInformation info) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h4>Author: ");
        if (info.getAuthor() != null) {
            sb.append(info.getAuthor()).append("<br/>\n");
        } else {
            sb.append("Unknown<br/>\n");
        }
        if (info.getVersion() != null) {
            sb.append("Version: ").append(info.getVersion()).append("<br/>\n");
        }
        sb.append("</h4>\n");
        if (info.getDescription() != null) {
            sb.append("<p>").append(info.getDescription()).append("</p>\n");
        }
        return sb.toString();
    }

    /**
     * Return installed plugins.
     * @return Set of PluginInformation
     */
    public Map<String, PluginInformation> getInstalledPlugins() {
        return installedPlugins;
    }


    private String getPluginInformationKey(PluginInformation info) {
        return info.getClassName();
    }

}
