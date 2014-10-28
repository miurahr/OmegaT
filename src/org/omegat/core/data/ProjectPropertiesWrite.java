package org.omegat.core.data;

import gen.core.filters.Filters;
import gen.core.project.Omegat;
import gen.core.project.Project;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.omegat.core.segmentation.SRX;
import org.omegat.util.FileUtil;
import org.omegat.util.OConsts;
import org.omegat.util.StringUtil;

public class ProjectPropertiesWrite {
    final protected Omegat xml;
    final protected File projectRootDir;

    public ProjectPropertiesWrite(File projectRootDir) {
        this.projectRootDir = projectRootDir;
        xml = new Omegat();
        xml.setProject(new Project());
    }

    /** Returns The Project Root Directory */
    public String getProjectRoot() {
        return projectRootDir.getAbsolutePath();
    }

    public ProjectPropertiesWrite(ProjectProperties orig) {
        orig.this.projectRootDir = projectRootDir;
        xml = new Omegat();
        xml.setProject(new Project());
    }

    /**
     * Sets The Source Language (language of the source files) of the Project
     */
    public void setSourceLanguage(String sourceLanguage) {
        xml.getProject().setSourceLang(sourceLanguage);
    }

    /**
     * Sets The Target Language (language of the translated files) of the Project
     */
    public void setTargetLanguage(String targetLanguage) {
        xml.getProject().setTargetLang(targetLanguage);
    }

    /**
     * Sets the class name of the source language tokenizer for the Project.
     */
    public void setSourceTokenizer(Class<?> sourceTokenizer) {
        xml.getProject().setSourceTok(sourceTokenizer.getName());
    }

    /**
     * Sets the class name of the target language tokenizer for the Project.
     */
    public void setTargetTokenizer(Class<?> targetTokenizer) {
        xml.getProject().setTargetTok(targetTokenizer.getName());
    }

    /** Sets whether The Sentence Segmenting is Enabled for this Project */
    public void setSentenceSegmentingEnabled(boolean sentenceSegmentingOn) {
        xml.getProject().setSentenceSeg(sentenceSegmentingOn);
    }

    public void setSupportDefaultTranslations(boolean supportDefaultTranslations) {
        xml.getProject().setSupportDefaultTranslations(supportDefaultTranslations);
    }

    public void setRemoveTags(boolean removeTags) {
        this.removeTags = removeTags;
    }

    public void setExternalCommand(String command) {
        xml.getProject().setExternalCommand(command);
    }

    /** Returns The Source (to be translated) Files Directory */
    public String getSourceRoot() {
        return computeAbsolute(xml.getProject().getSourceDir(), OConsts.DEFAULT_SOURCE);
    }

    /** Sets The Source (to be translated) Files Directory */
    public void setSourceRoot(String sourceRoot) throws IOException {
        xml.getProject().setSourceDir(compiteRelativeDir(sourceRoot, OConsts.DEFAULT_SOURCE));
    }

    /** Returns The Target (Compiled) Files Directory */
    public String getTargetRoot() {
        return computeAbsolute(xml.getProject().getTargetDir(), OConsts.DEFAULT_TARGET);
    }

    /** Sets The Target (Compiled) Files Directory */
    public void setTargetRoot(String targetRoot) throws IOException {
        xml.getProject().setTargetDir(compiteRelativeDir(targetRoot, OConsts.DEFAULT_TARGET));
    }

    /** Returns The Glossary Files Directory */
    public String getGlossaryRoot() {
        return computeAbsolute(xml.getProject().getGlossaryDir(), OConsts.DEFAULT_GLOSSARY);
    }

    /** Sets The Glossary Files Directory */
    public void setGlossaryRoot(String glossaryRoot) throws IOException {
        xml.getProject().setGlossaryDir(compiteRelativeDir(glossaryRoot, OConsts.DEFAULT_GLOSSARY));
    }

    /** Returns The Glossary File Location */
    public String getWriteableGlossary() {
        if (OConsts.DEFAULT_FOLDER_MARKER.equals(xml.getProject().getGlossaryFile())) {
            return new File(getGlossaryRoot(),)
        }
        return writeableGlossaryFile;
    }

    /** Sets The Writeable Glossary File Location */
    public void setWriteableGlossary(String writeableGlossaryFile) throws IOException {
        if (!StringUtil.isEmpty(writeableGlossaryFile)) {
            this.writeableGlossaryFile = writeableGlossaryFile;
        }
    }

    /** Returns The Translation Memory (TMX) Files Directory */
    public String getTMRoot() {
        return computeAbsolute(xml.getProject().getTmDir(), OConsts.DEFAULT_TM);
    }

    /** Sets The Translation Memory (TMX) Files Directory */
    public void setTMRoot(String tmRoot) throws IOException {
        xml.getProject().setTmDir(compiteRelativeDir(tmRoot, OConsts.DEFAULT_TM));
    }

    /** Returns The Dictionaries Files Directory */
    public String getDictRoot() {
        return computeAbsolute(xml.getProject().getDictionaryDir(), OConsts.DEFAULT_DICT);
    }

    /** Sets Dictionaries Files Directory */
    public void setDictRoot(String dictRoot) throws IOException {
        xml.getProject().setDictionaryDir(compiteRelativeDir(dictRoot, OConsts.DEFAULT_DICT));
    }

    public void setSourceRootExcludes(List<String> excludes) {
        xml.getProject().getSourceDirExcludes().getMask().clear();
        xml.getProject().getSourceDirExcludes().getMask().addAll(excludes);
    }

    public void setProjectSRX(SRX projectSRX) {
        this.projectSRX = projectSRX;
    }

    public void setProjectFilters(Filters projectFilters) {
        this.projectFilters = projectFilters;
    }

    protected String compiteRelativeDir(String path, String defaultValue) throws IOException {
        String p = FileUtil.computeRelativeOrAbsolutePath(projectRootDir, new File(path));
        if (defaultValue.equals(p)) {
            return OConsts.DEFAULT_FOLDER_MARKER;
        } else {
            return dir(p);
        }
    }

    protected String computeAbsolute(String path, String defaultValue) {
        if (OConsts.DEFAULT_FOLDER_MARKER.equals(path)) {
            return new File(projectRootDir, defaultValue).getAbsolutePath();
        } else {
            return new File(projectRootDir, path).getAbsolutePath();
        }
    }

    protected String dir(String dir) {
        if (!dir.endsWith("/")) {
            return dir + '/';
        } else {
            return dir;
        }
    }
}
