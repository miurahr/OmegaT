/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool
          with fuzzy matching, translation memory, keyword search,
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2009 Alex Buloichik
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

package org.omegat.util;

import java.io.File;
import java.io.FileFilter;
import java.util.Map;
import java.util.TreeMap;

/**
 * Class for monitor directory content changes. It just looks directory every 3
 * seconds and run callback if some files changed.
 * 
 * @author Alex Buloichik <alex73mail@gmail.com>
 */
public class DirectoryMonitor extends Thread {
    private boolean stopped = false;
    protected final File dir;
    protected final Callback callback;
    private final Map<String, FileInfo> existFiles = new TreeMap<String, FileInfo>();
    protected static final long LOOKUP_PERIOD = 3000;

    /**
     * Create monitor.
     * 
     * @param dir
     *            directory to monitoring
     */
    public DirectoryMonitor(final File dir, final Callback callback) {
        this.dir = dir;
        this.callback = callback;
    }

    /**
     * Stop directory monitoring.
     */
    public void fin() {
        stopped = true;
    }

    @Override
    public void run() {
        setName(this.getClass().getSimpleName());

        while (!stopped) {
            // find deleted or changed files
            for (Map.Entry<String, FileInfo> en : existFiles.entrySet()) {
                File f = new File(en.getKey());
                if (!f.exists() || en.getValue().isChanged(f)) {
                    if (!stopped) {
                        callback.fileChanged(f);
                    }
                }
            }
            // find new files
            for (File f : readCurrentDir()) {
                String fn = f.getAbsolutePath();
                if (!existFiles.keySet().contains(fn)) {
                    existFiles.put(fn, new FileInfo(f));
                    if (!stopped) {
                        callback.fileChanged(f);
                    }
                }
            }
            try {
                Thread.sleep(LOOKUP_PERIOD);
            } catch (InterruptedException ex) {
                stopped = true;
            }
        }
    }

    protected File[] readCurrentDir() {
        File[] result = dir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return !pathname.isDirectory();
            }
        });
        return result != null ? result : new File[0];
    }

    /**
     * Information about exist file.
     */
    protected class FileInfo {
        public long lastModified, length;

        public FileInfo(final File file) {
            lastModified = file.lastModified();
            length = file.length();
        }

        public boolean isChanged(final File diskFile) {
            return lastModified != diskFile.lastModified() || length != diskFile.length();
        }
    }

    /**
     * Callback for monitoring.
     */
    public interface Callback {
        /**
         * Called on any file changes - created, modified, deleted.
         */
        void fileChanged(File file);
    }
}
