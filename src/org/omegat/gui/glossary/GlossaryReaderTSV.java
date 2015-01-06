/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
               2010 Alex Buloichik
               Home page: http://www.omegat.org/
               Support center: http://groups.yahoo.com/group/OmegaT/

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

package org.omegat.gui.glossary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.BOMInputStream;
import org.omegat.util.OConsts;
import org.omegat.util.StringUtil;

/**
 * Reader for tab separated glossaries.
 * 
 * @author Keith Godfrey
 * @author Maxym Mykhalchuk
 * @author Alex Buloichik <alex73mail@gmail.com>
 */
public class GlossaryReaderTSV {
    /**
     * Get charset of glossary file.
     */
    public static String getCharset(File file) throws IOException {
        BOMInputStream in = new BOMInputStream(new FileInputStream(file));
        try {
            if (in.hasBOM()) {
                return in.getBOMCharsetName();
            } else {
                String fname_lower = file.getName().toLowerCase();
                if (fname_lower.endsWith(OConsts.EXT_TSV_DEF)) {
                    return Charset.defaultCharset().name();
                } else if (fname_lower.endsWith(OConsts.EXT_TSV_UTF8)
                        || fname_lower.endsWith(OConsts.EXT_TSV_TXT)) {
                    return OConsts.UTF8;
                } else {
                    return Charset.defaultCharset().name();
                }
            }
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static List<GlossaryEntry> read(final File file, boolean priorityGlossary) throws IOException {
        List<GlossaryEntry> result = new ArrayList<GlossaryEntry>();

        String encoding = getCharset(file);
        BOMInputStream fis = new BOMInputStream(new FileInputStream(file));
        try {
            InputStreamReader reader = new InputStreamReader(new FileInputStream(file), encoding);

            BufferedReader in = new BufferedReader(reader);

            // BOM (byte order mark) bugfix
            in.mark(1);
            int ch = in.read();
            if (ch != 0xFEFF)
                in.reset();

            for (String s = in.readLine(); s != null; s = in.readLine()) {
                // skip lines that start with '#'
                if (s.startsWith("#"))
                    continue;

                // divide lines on tabs
                String tokens[] = s.split("\t");
                // check token list to see if it has a valid string
                if (tokens.length < 2 || tokens[0].length() == 0)
                    continue;

                // creating glossary entry and add it to the hash
                // (even if it's already there!)
                String comment = "";
                if (tokens.length >= 3)
                    comment = tokens[2];
                result.add(new GlossaryEntry(tokens[0], tokens[1], comment, priorityGlossary));
            }
        } finally {
            fis.close();
        }

        return result;
    }

    /**
     * Appends entry to glossary file. If file does not exist yet, it will be created.
     *
     * @param file The file to (create and) append to
     * @param newEntry the entry to append.
     * @throws IOException
     */
    public static void append(final File file, GlossaryEntry newEntry) throws IOException {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        String encoding = getCharset(file);
        Writer wr = new OutputStreamWriter(new FileOutputStream(file, true), encoding);
        wr.append(newEntry.getSrcText()).append('\t').append(newEntry.getLocText());
        if (!StringUtil.isEmpty(newEntry.getCommentText())) {
            wr.append('\t').append(newEntry.getCommentText());
        }
        wr.append(System.getProperty("line.separator"));
        wr.close();
    }
}
