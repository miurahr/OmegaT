/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2000-2006 Keith Godfrey, Maxym Mykhalchuk, and Henry Pijffers
               2007 Zoltan Bartko
               2008 Alex Buloichik
               2009-2010 Didier Briel
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

package org.omegat.core.data;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omegat.core.Core;
import org.omegat.core.CoreEvents;
import org.omegat.core.events.IProjectEventListener;
import org.omegat.core.matching.ITokenizer;
import org.omegat.core.matching.Tokenizer;
import org.omegat.core.statistics.CalcStandardStatistics;
import org.omegat.core.statistics.Statistics;
import org.omegat.core.statistics.StatisticsInfo;
import org.omegat.filters2.IAlignCallback;
import org.omegat.filters2.IFilter;
import org.omegat.filters2.TranslationException;
import org.omegat.filters2.master.FilterMaster;
import org.omegat.filters2.master.PluginUtils;
import org.omegat.util.DirectoryMonitor;
import org.omegat.util.FileUtil;
import org.omegat.util.Language;
import org.omegat.util.Log;
import org.omegat.util.OConsts;
import org.omegat.util.OStrings;
import org.omegat.util.Preferences;
import org.omegat.util.ProjectFileStorage;
import org.omegat.util.RuntimePreferences;
import org.omegat.util.StaticUtils;
import org.omegat.util.StringUtil;
import org.omegat.util.gui.UIThreadsUtil;

/**
 * Loaded project implementation. Only translation could be changed after project will be loaded and set by
 * Core.setProject.
 * 
 * All components can read all data directly without synchronization. All synchronization implemented inside
 * RealProject.
 * 
 * @author Keith Godfrey
 * @author Henry Pijffers (henry.pijffers@saxnot.com)
 * @author Maxym Mykhalchuk
 * @author Bartko Zoltan (bartkozoltan@bartkozoltan.com)
 * @author Alex Buloichik (alex73mail@gmail.com)
 * @author Didier Briel
 */
public class RealProject implements IProject {
    /** Local logger. */
    private static final Logger LOGGER = Logger.getLogger(RealProject.class.getName());

    private final ProjectProperties m_config;

    private FileChannel lockChannel;
    private FileLock lock;

    private boolean m_modifiedFlag;

    /** List of all segments in project. */
    private List<SourceTextEntry> allProjectEntries = new ArrayList<SourceTextEntry>(4096);

    private final StatisticsInfo hotStat = new StatisticsInfo();

    private final ITokenizer sourceTokenizer, targetTokenizer;

    private DirectoryMonitor tmMonitor;

    /**
     * Storage for all translation memories, which shouldn't be changed and saved, i.e. for /tm/*.tmx files,
     * aligned data from source files.
     * 
     * This map recreated each time when files changed. So, you can free use it without thinking about
     * synchronization.
     */
    private Map<String, ExternalTMX> transMemories = new TreeMap<String, ExternalTMX>();

    private ProjectTMX projectTMX;

    /** Segments count in project files. */
    private final List<FileInfo> projectFilesList = new ArrayList<FileInfo>();

    /**
     * Create new project instance. It required to call {@link #createProject() createProject} or
     * {@link #loadProject() loadProject} methods just after constructor before use project.
     * 
     * @param props
     *            project properties
     * @param isNewProject
     *            true if project need to be created
     */
    public RealProject(final ProjectProperties props) {
        m_config = props;

        sourceTokenizer = createTokenizer(true);
        targetTokenizer = createTokenizer(false);
    }

    public void saveProjectProperties() throws IOException {
        unlockProject();
        ProjectFileStorage.writeProjectFile(m_config);
        lockProject();
        Preferences.setPreference(Preferences.SOURCE_LOCALE, m_config.getSourceLanguage().toString());
        Preferences.setPreference(Preferences.TARGET_LOCALE, m_config.getTargetLanguage().toString());
    }

    /**
     * Create new project.
     */
    public void createProject() {
        LOGGER.info(OStrings.getString("LOG_DATAENGINE_CREATE_START"));
        UIThreadsUtil.mustNotBeSwingThread();

        lockProject();

        try {
            createDirectory(m_config.getProjectRoot(), null);
            createDirectory(m_config.getProjectInternal(), null);
            createDirectory(m_config.getSourceRoot(), "src");
            createDirectory(m_config.getGlossaryRoot(), "glos");
            createDirectory(m_config.getTMRoot(), "tm");
            createDirectory(m_config.getDictRoot(), "dictionary");
            createDirectory(m_config.getTargetRoot(), "target");

            saveProjectProperties();

            allProjectEntries = Collections.unmodifiableList(allProjectEntries);
        } catch (IOException e) {
            // trouble in tinsletown...
            Log.logErrorRB(e, "CT_ERROR_CREATING_PROJECT");
            Core.getMainWindow().displayErrorRB(e, "CT_ERROR_CREATING_PROJECT");
        }
        LOGGER.info(OStrings.getString("LOG_DATAENGINE_CREATE_END"));
    }

    /**
     * Load exist project in a "big" sense -- loads project's properties, glossaries, tms, source files etc.
     */
    public void loadProject() {
        LOGGER.info(OStrings.getString("LOG_DATAENGINE_LOAD_START"));
        UIThreadsUtil.mustNotBeSwingThread();

        lockProject();

        // load new project
        try {
            Preferences.setPreference(Preferences.CURRENT_FOLDER, new File(m_config.getProjectRoot())
                    .getParentFile().getAbsolutePath());
            Preferences.save();

            Core.getMainWindow().showStatusMessageRB("CT_LOADING_PROJECT");

            // sets for collect exist entries for check orphaned
            Set<String> existSource = new HashSet<String>();
            Set<EntryKey> existKeys = new HashSet<EntryKey>();

            loadSourceFiles(existSource, existKeys);

            loadTranslations(existSource, existKeys);

            existSource = null;
            existKeys = null;

            loadTM();

            // build word count
            String stat = CalcStandardStatistics.buildProjectStats(this, hotStat);
            String fn = getProjectProperties().getProjectInternal() + OConsts.STATS_FILENAME;
            Statistics.writeStat(fn, stat);

            allProjectEntries = Collections.unmodifiableList(allProjectEntries);

            // Project Loaded...
            Core.getMainWindow().showStatusMessageRB(null);

            m_modifiedFlag = false;
        } catch (Exception e) {
            Log.logErrorRB(e, "TF_LOAD_ERROR");
            Core.getMainWindow().displayErrorRB(e, "TF_LOAD_ERROR");
        }
        // Fix for bug 1571944 @author Henry Pijffers
        // (henry.pijffers@saxnot.com)
        catch (OutOfMemoryError oome) {
            // Oh shit, we're all out of storage space!
            // Of course we should've cleaned up after ourselves earlier,
            // but since we didn't, do a bit of cleaning up now, otherwise
            // we can't even inform the user about our slacking off.
            allProjectEntries.clear();
            projectFilesList.clear();
            transMemories.clear();
            projectTMX = null;

            // Well, that cleared up some, GC to the rescue!
            System.gc();

            // There, that should do it, now inform the user
            Object[] args = { Runtime.getRuntime().maxMemory() / 1024 / 1024 };
            Log.logErrorRB("OUT_OF_MEMORY", args);
            Log.log(oome);
            Core.getMainWindow().showErrorDialogRB("OUT_OF_MEMORY", args, "TF_ERROR");
            // Just quit, we can't help it anyway
            System.exit(0);
        }

        LOGGER.info(OStrings.getString("LOG_DATAENGINE_LOAD_END"));
    }

    /**
     * Align project.
     */
    public Map<String, Translation> align(final ProjectProperties props, final File translatedDir)
            throws Exception {
        FilterMaster fm = FilterMaster.getInstance();

        List<String> srcFileList = new ArrayList<String>();
        File root = new File(m_config.getSourceRoot());
        StaticUtils.buildFileList(srcFileList, root, true);

        AlignFilesCallback alignFilesCallback = new AlignFilesCallback();

        String srcRoot = m_config.getSourceRoot();
        for (String filename : srcFileList) {
            // shorten filename to that which is relative to src root
            String midName = filename.substring(srcRoot.length());

            Language targetLang = getProjectProperties().getTargetLanguage();
            fm.alignFile(srcRoot, midName, targetLang, translatedDir.getPath(), alignFilesCallback);
        }
        return alignFilesCallback.data;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isProjectLoaded() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public StatisticsInfo getStatistics() {
        return hotStat;
    }

    /**
     * Signals to the core thread that a project is being closed now, and if it's still being loaded, core
     * thread shouldn't throw any error.
     */
    public void closeProject() {
        tmMonitor.fin();
        unlockProject();
        LOGGER.info(OStrings.getString("LOG_DATAENGINE_CLOSE"));
    }

    /**
     * Lock omegat.project file against rename or move project.
     */
    protected void lockProject() {
        if (!RuntimePreferences.isProjectLockingEnabled()) {
            return;
        }
        try {
            File lockFile = new File(m_config.getProjectRoot(), OConsts.FILE_PROJECT);
            lockChannel = new RandomAccessFile(lockFile, "rw").getChannel();
            lock = lockChannel.lock();
        } catch (Exception ex) {
            Log.log(ex);
        }
    }

    /**
     * Unlock omegat.project file against rename or move project.
     */
    protected void unlockProject() {
        if (!RuntimePreferences.isProjectLockingEnabled()) {
            return;
        }
        try {
            lock.release();
            lockChannel.close();
        } catch (Exception ex) {
            Log.log(ex);
        }
    }

    /**
     * Builds translated files corresponding to sourcePattern and creates fresh TM files.
     * 
     * @param sourcePattern
     *            The regexp of files to create
     * @throws IOException
     * @throws TranslationException
     */
    public void compileProject(String sourcePattern) throws IOException, TranslationException {
        LOGGER.info(OStrings.getString("LOG_DATAENGINE_COMPILE_START"));
        UIThreadsUtil.mustNotBeSwingThread();

        Pattern FILE_PATTERN = Pattern.compile(sourcePattern);

        // build 3 TMX files:
        // - OmegaT-specific, with inline OmegaT formatting tags
        // - TMX Level 1, without formatting tags
        // - TMX Level 2, with OmegaT formatting tags wrapped in TMX inline tags
        try {
            // build TMX with OmegaT tags
            String fname = m_config.getProjectRoot() + m_config.getProjectName() + OConsts.OMEGAT_TMX
                    + OConsts.TMX_EXTENSION;

            projectTMX.save(new File(fname), false, false);

            // build TMX level 1 compliant file
            fname = m_config.getProjectRoot() + m_config.getProjectName() + OConsts.LEVEL1_TMX
                    + OConsts.TMX_EXTENSION;
            projectTMX.save(new File(fname), true, false);

            // build three-quarter-assed TMX level 2 file
            fname = m_config.getProjectRoot() + m_config.getProjectName() + OConsts.LEVEL2_TMX
                    + OConsts.TMX_EXTENSION;
            projectTMX.save(new File(fname), false, true);
        } catch (Exception e) {
            Log.logErrorRB("CT_ERROR_CREATING_TMX");
            Log.log(e);
            throw new IOException(OStrings.getString("CT_ERROR_CREATING_TMX") + "\n" + e.getMessage());
        }

        // build mirror directory of source tree
        List<String> fileList = new ArrayList<String>(256);
        String srcRoot = m_config.getSourceRoot();
        String locRoot = m_config.getTargetRoot();
        StaticUtils.buildDirList(fileList, new File(srcRoot));

        for (String filename : fileList) {
            String destFileName = locRoot + filename.substring(srcRoot.length());
            File destFile = new File(destFileName);
            if (!destFile.exists()) {
                // target directory doesn't exist - create it
                if (!destFile.mkdir()) {
                    throw new IOException(OStrings.getString("CT_ERROR_CREATING_TARGET_DIR") + destFileName);
                }
            }
        }

        // build translated files
        FilterMaster fm = FilterMaster.getInstance();

        fileList.clear();
        StaticUtils.buildFileList(fileList, new File(srcRoot), true);

        TranslateFilesCallback translateFilesCallback = new TranslateFilesCallback();

        Language targetLang = getProjectProperties().getTargetLanguage();

        for (String filename : fileList) {
            // shorten filename to that which is relative to src root
            String midName = filename.substring(srcRoot.length());
            Matcher fileMatch = FILE_PATTERN.matcher(midName);
            if (fileMatch.matches()) {
                Core.getMainWindow().showStatusMessageRB("CT_COMPILE_FILE_MX", midName);
                translateFilesCallback.setCurrentFileName(midName);
                fm.translateFile(srcRoot, midName, targetLang, locRoot, translateFilesCallback);
            }
        }
        Core.getMainWindow().showStatusMessageRB("CT_COMPILE_DONE_MX");

        CoreEvents.fireProjectChange(IProjectEventListener.PROJECT_CHANGE_TYPE.COMPILE);

        LOGGER.info(OStrings.getString("LOG_DATAENGINE_COMPILE_END"));
    }

    /** Saves the translation memory and preferences */
    public void saveProject() {
        if (!isProjectModified()) {
            LOGGER.info(OStrings.getString("LOG_DATAENGINE_SAVE_NONEED"));
            return;
        }

        LOGGER.info(OStrings.getString("LOG_DATAENGINE_SAVE_START"));
        UIThreadsUtil.mustNotBeSwingThread();

        Core.getAutoSave().disable();

        Preferences.save();

        String s = m_config.getProjectInternal() + OConsts.STATUS_EXTENSION;

        // rename existing project file in case a fatal error
        // is encountered during the write procedure - that way
        // everything won't be lost
        File backup = new File(s + OConsts.BACKUP_EXTENSION);
        File orig = new File(s);
        File newFile = new File(s + OConsts.NEWFILE_EXTENSION);
        if (orig.exists())
            orig.renameTo(backup);

        try {
            saveProjectProperties();

            projectTMX.save(newFile, false, false);

            if (backup.exists()) {
                backup.delete();
            }

            if (!orig.renameTo(backup)) {
                throw new IOException("Error name old file to backup");
            }

            if (!newFile.renameTo(orig)) {
                throw new IOException("Error name old file to backup");
            }

            m_modifiedFlag = false;
        } catch (Exception e) {
            Log.logErrorRB(e, "CT_ERROR_SAVING_PROJ");
            Core.getMainWindow().displayErrorRB(e, "CT_ERROR_SAVING_PROJ");
        }

        // update statistics
        String stat = CalcStandardStatistics.buildProjectStats(this, hotStat);
        String fn = getProjectProperties().getProjectInternal() + OConsts.STATS_FILENAME;
        Statistics.writeStat(fn, stat);

        CoreEvents.fireProjectChange(IProjectEventListener.PROJECT_CHANGE_TYPE.SAVE);

        Core.getAutoSave().enable();
        LOGGER.info(OStrings.getString("LOG_DATAENGINE_SAVE_END"));
    }

    /**
     * Create one of project directory and
     * 
     * @param dir
     * @param dirType
     * @throws IOException
     */
    private void createDirectory(final String dir, final String dirType) throws IOException {
        File d = new File(dir);
        if (!d.isDirectory()) {
            if (!d.mkdirs()) {
                StringBuilder msg = new StringBuilder(OStrings.getString("CT_ERROR_CREATE"));
                if (dirType != null) {
                    msg.append("\n(.../").append(dirType).append("/)");
                }
                throw new IOException(msg.toString());
            }
        }
    }

    // ///////////////////////////////////////////////////////////////
    // ///////////////////////////////////////////////////////////////
    // protected functions

    /** Finds and loads project's TMX file with translations (project_save.tmx). */
    private void loadTranslations(final Set<String> existSource, final Set<EntryKey> existKeys)
            throws Exception {

        final File tmxFile = new File(m_config.getProjectInternal() + OConsts.STATUS_EXTENSION);

        ProjectTMX.CheckOrphanedCallback cb = new ProjectTMX.CheckOrphanedCallback() {
            public boolean existSourceInProject(String src) {
                return existSource.contains(src);
            }

            public boolean existEntryInProject(EntryKey key) {
                return existKeys.contains(key);
            }
        };

        try {
            if (!tmxFile.exists()) {
                Log.logErrorRB("CT_ERROR_CANNOT_FIND_TMX", tmxFile.getAbsolutePath());
                // nothing to do here
                return;
            }
        } catch (SecurityException se) {
            // file probably exists, but something's wrong
            Log.logErrorRB(se, "CT_ERROR_ACCESS_PROJECT_FILE");
            Core.getMainWindow().displayErrorRB(se, "CT_ERROR_ACCESS_PROJECT_FILE");
            return;
        }

        try {
            Core.getMainWindow().showStatusMessageRB("CT_LOAD_TMX");

            projectTMX = new ProjectTMX(tmxFile, cb);

            // RFE 1001918 - backing up project's TMX upon successful read
            FileUtil.backupFile(tmxFile);
            FileUtil.removeOldBackups(tmxFile);
        } catch (Exception e) {
            Log.logErrorRB(e, "CT_ERROR_LOADING_PROJECT_FILE");
            Core.getMainWindow().displayErrorRB(e, "CT_ERROR_LOADING_PROJECT_FILE");
        }
    }

    /**
     * Load source files for project.
     * 
     * @param projectRoot
     *            project root dir
     */
    private void loadSourceFiles(final Set<String> existSource, final Set<EntryKey> existKeys)
            throws IOException, InterruptedIOException, TranslationException {
        long st = System.currentTimeMillis();
        FilterMaster fm = FilterMaster.getInstance();

        List<String> srcFileList = new ArrayList<String>();
        File root = new File(m_config.getSourceRoot());
        StaticUtils.buildFileList(srcFileList, root, true);
        Collections.sort(srcFileList, new FileNameComparator());

        for (String filename : srcFileList) {
            // strip leading path information;
            // feed file name to project window
            String filepath = filename.substring(m_config.getSourceRoot().length());

            Core.getMainWindow().showStatusMessageRB("CT_LOAD_FILE_MX", filepath);

            LoadFilesCallback loadFilesCallback = new LoadFilesCallback(existSource, existKeys);

            FileInfo fi = new FileInfo();
            fi.filePath = filepath;

            loadFilesCallback.setCurrentFile(fi);

            boolean fileLoaded = fm.loadFile(filename, loadFilesCallback);

            loadFilesCallback.fileFinished();

            if (fileLoaded && (fi.entries.size() > 0)) {
                projectFilesList.add(fi);
            }
        }

        findNonUniqueSegments();

        Core.getMainWindow().showStatusMessageRB("CT_LOAD_SRC_COMPLETE");
        long en = System.currentTimeMillis();
        Log.log("Load project source files: " + (en - st) + "ms");
    }

    /**
     * It finds non-unique segments in project.
     */
    private void findNonUniqueSegments() {
        Set<String> exists = new HashSet<String>(16384);

        for (FileInfo fi : projectFilesList) {
            for (int i = 0; i < fi.entries.size(); i++) {
                SourceTextEntry ste = fi.entries.get(i);
                ste.dublicateSource = exists.contains(ste.getSrcText());
                if (!ste.dublicateSource) {
                    exists.add(ste.getSrcText());
                }
            }
        }
    }

    /**
     * Locates and loads external TMX files with legacy translations. Uses directory monitor for check file
     * updates.
     */
    private void loadTM() throws IOException {
        File tmRoot = new File(m_config.getTMRoot());
        tmMonitor = new DirectoryMonitor(tmRoot, new DirectoryMonitor.Callback() {
            public void fileChanged(File file) {
                // create new translation memories map
                Map<String, ExternalTMX> newTransMemories = new TreeMap<String, ExternalTMX>(transMemories);
                if (file.exists()) {
                    try {
                        ExternalTMX newTMX = new ExternalTMX(file);
                        newTransMemories.put(file.getPath(), newTMX);
                    } catch (Exception e) {
                        Log.logErrorRB(e, "TF_TM_LOAD_ERROR");
                        Core.getMainWindow().displayErrorRB(e, "TF_TM_LOAD_ERROR");
                    }
                } else {
                    newTransMemories.remove(file.getPath());
                }
                transMemories = newTransMemories;
            }
        });
        tmMonitor.start();
    }

    /**
     * {@inheritDoc}
     */
    public List<SourceTextEntry> getAllEntries() {
        return allProjectEntries;
    }

    public Translation getTranslation(SourceTextEntry ste) {
        return projectTMX.getTranslation(ste.getKey());
    }

    /**
     * Returns the active Project's Properties.
     */
    public ProjectProperties getProjectProperties() {
        return m_config;
    }

    /**
     * Returns whether the project was modified.
     */
    public boolean isProjectModified() {
        return m_modifiedFlag;
    }

    /**
     * {@inheritDoc}
     */
    public void setAuthorTranslation(String author, SourceTextEntry entry, String trans) {

    }

    /**
     * {@inheritDoc}
     */
    public void setTranslation(final SourceTextEntry entry, String trans) {
        String author = Preferences.getPreferenceDefault(Preferences.TEAM_AUTHOR,
                System.getProperty("user.name"));

        Translation prevTrEntry = projectTMX.getTranslation(entry.getKey());

        // don't change anything if nothing has changed
        if (prevTrEntry == null) {
            if ("".equals(trans)) {
                return;
            }
        } else {
            if (trans.equals(prevTrEntry.translation)) {
                return;
            }
        }

        m_modifiedFlag = true;

        if (StringUtil.isEmpty(trans)) {
            projectTMX.setTranslation(entry, null, false);
        } else {
            Translation te = new Translation(trans, author, System.currentTimeMillis());
            projectTMX.setTranslation(entry, te, false);
        }
        String prevTranslation = prevTrEntry != null ? prevTrEntry.translation : null;

        /**
         * Calculate how to statistics should be changed.
         */
        int diff = StringUtil.isEmpty(prevTranslation) ? 0 : -1;
        diff += StringUtil.isEmpty(trans) ? 0 : +1;
        hotStat.numberofTranslatedSegments += diff;
    }

    public Map<String, ExternalTMX> getTransMemories() {
        return transMemories;
    }

    public void iterateByTranslations(TranslationIterator callback) {
        for (Map.Entry<String, Translation> en : projectTMX.translationDefault.entrySet()) {
            callback.onTmxEntry(null, en.getKey(), en.getValue().translation);
        }
        for (Map.Entry<EntryKey, Translation> en : projectTMX.translationMultiple.entrySet()) {
            callback.onTmxEntry(null, en.getKey().sourceText, en.getValue().translation);
        }
    }

    public void iterateByOrphaned(TranslationIterator callback) {
        for (Map.Entry<String, Translation> en : projectTMX.orphanedDefault.entrySet()) {
            callback.onTmxEntry(null, en.getKey(), en.getValue().translation);
        }
        for (Map.Entry<EntryKey, Translation> en : projectTMX.orphanedMultiple.entrySet()) {
            callback.onTmxEntry(null, en.getKey().sourceText, en.getValue().translation);
        }
    }

    public void iterateByTransMemories(TranslationIterator callback) {
        // TODO Auto-generated method stub

    }

    /**
     * {@inheritDoc}
     */
    public ITokenizer getSourceTokenizer() {
        return sourceTokenizer;
    }

    /**
     * {@inheritDoc}
     */
    public ITokenizer getTargetTokenizer() {
        return targetTokenizer;
    }

    /**
     * Create tokenizer by class specified in command line, or by default class.
     * 
     * @param forSource
     *            true if tokenizer for source language
     * @return tokenizer implementation
     */
    protected ITokenizer createTokenizer(final boolean forSource) {
        String className;
        if (forSource) {
            className = Core.getParams().get("ITokenizer");
        } else {
            className = Core.getParams().get("ITokenizerTarget");
        }
        ITokenizer t = null;
        try {
            if (className != null) {
                for (Class<?> c : PluginUtils.getTokenizerClasses()) {
                    if (c.getName().equals(className)) {
                        t = (ITokenizer) c.newInstance();
                        break;
                    }
                }
            }
        } catch (Exception ex) {
            Log.log(ex);
        }
        if (t == null) {
            if (forSource) {
                t = new Tokenizer();
            } else {
                t = sourceTokenizer;
            }
        }
        if (forSource) {
            Log.log("Source tokenizer: " + t.getClass().getName());
        } else {
            Log.log("Target tokenizer: " + t.getClass().getName());
        }
        return t;
    }

    /**
     * {@inheritDoc}
     */
    public List<FileInfo> getProjectFiles() {
        return Collections.unmodifiableList(projectFilesList);
    }

    private class LoadFilesCallback extends ParseEntry {
        private FileInfo fileInfo;

        private final Set<String> existSource;
        private final Set<EntryKey> existKeys;

        private final List<String> s = new ArrayList<String>();
        private final List<String> t = new ArrayList<String>();

        public LoadFilesCallback(final Set<String> existSource, final Set<EntryKey> existKeys) {
            super(m_config);
            this.existSource = existSource;
            this.existKeys = existKeys;
        }

        protected void setCurrentFile(FileInfo fi) {
            fileInfo = fi;
        }

        protected void fileFinished() {
            if (s.size() > 0) {
                ExternalTMX tmx = new ExternalTMX(fileInfo.filePath);
                tmx.source = s.toArray(new String[s.size()]);
                tmx.target = t.toArray(new String[t.size()]);
                transMemories.put(tmx.getName(), tmx);
            }

            s.clear();
            t.clear();
            fileInfo = null;
        }

        /**
         * {@inheritDoc}
         */
        protected void addSegment(String id, short segmentIndex, String segmentSource,
                String segmentTranslation, String comment) {
            // if the source string is empty, don't add it to TM
            if (segmentSource.length() == 0 || segmentSource.trim().length() == 0) {
                return;
            }

            EntryKey ek = new EntryKey(fileInfo.filePath, segmentSource, id);

            if (!StringUtil.isEmpty(segmentTranslation)) {
                projectTMX.putFromSourceFile(ek, new Translation(segmentTranslation, null, 0));
            }
            SourceTextEntry srcTextEntry = new SourceTextEntry(ek, allProjectEntries.size() + 1);
            allProjectEntries.add(srcTextEntry);
            fileInfo.entries.add(srcTextEntry);

            existSource.add(segmentSource);
            existKeys.add(srcTextEntry.getKey());
        }

        public void addFileTMXEntry(String source, String translation) {
            if (StringUtil.isEmpty(translation)) {
                return;
            }
            s.add(source);
            t.add(translation);
        }
    };

    private class TranslateFilesCallback extends TranslateEntry {
        private String currentFile;

        public TranslateFilesCallback() {
            super(m_config);
        }

        protected void setCurrentFileName(String fn) {
            currentFile = fn;
        }

        protected String getSegmentTranslation(String id, int segmentIndex, String segmentSource) {
            EntryKey ek = new EntryKey(currentFile, segmentSource, id);
            Translation tr = projectTMX.getTranslation(ek);
            return tr != null ? tr.translation : segmentSource;
        }
    };

    static class AlignFilesCallback implements IAlignCallback {
        Map<String, Translation> data = new HashMap<String, Translation>();

        public void addTranslation(String id, String source, String translation, boolean isFuzzy,
                String comment, IFilter filter) {
            if (source != null && translation != null) {
                ParseEntry.ParseEntryResult spr = new ParseEntry.ParseEntryResult();
                String sourceS = ParseEntry.stripSomeChars(source, spr);
                String transS = ParseEntry.stripSomeChars(translation, spr);
                if (isFuzzy) {
                    transS = "[" + filter.getFuzzyMark() + "] " + transS;
                }

                data.put(sourceS, new Translation(transS, null, 0));
            }
        }
    }

    static class FileNameComparator implements Comparator<String> {
        public int compare(String o1, String o2) {
            // Get the local collator and set its strength to PRIMARY
            Collator localCollator = Collator.getInstance(Locale.getDefault());
            localCollator.setStrength(Collator.PRIMARY);
            return localCollator.compare(o1, o2);
        }
    }
}
