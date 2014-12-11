/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool 
          with fuzzy matching, translation memory, keyword search, 
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2014 Alex Buloichik
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

package org.omegat.util;

import java.io.File;

import org.apache.commons.io.FileUtils;

import junit.framework.TestCase;

/**
 * @author Alex Buloichik
 */
public class FileUtilTest extends TestCase {
    public void testRelative() throws Exception {
        assertFalse(FileUtil.isRelative("C:\\zz"));
        assertFalse(FileUtil.isRelative("z:/zz"));
        assertFalse(FileUtil.isRelative("c:\\zz"));
        assertFalse(FileUtil.isRelative("z:/zz"));
        assertTrue(FileUtil.isRelative("1:/zz"));
        assertFalse(FileUtil.isRelative("/zz"));
        assertFalse(FileUtil.isRelative("\\zz"));
        assertTrue(FileUtil.isRelative("zz/"));
    }

    public void testAbsoluteForSystem() throws Exception {
        assertEquals("C:/zzz", FileUtil.absoluteForSystem("C:\\zzz", Platform.OsType.WIN64));
        assertEquals("/zzz", FileUtil.absoluteForSystem("C:\\zzz", Platform.OsType.LINUX64));
        assertEquals("/zzz", FileUtil.absoluteForSystem("C:\\zzz", Platform.OsType.MAC64));
        assertEquals("/zzz", FileUtil.absoluteForSystem("\\zzz", Platform.OsType.WIN64));
        assertEquals("/zzz", FileUtil.absoluteForSystem("\\zzz", Platform.OsType.LINUX64));
        assertEquals("/zzz", FileUtil.absoluteForSystem("\\zzz", Platform.OsType.MAC64));
    }

    public void testEOL() throws Exception {
        File dir = new File("build/testdata/");
        dir.mkdirs();

        File in = new File(dir, "in.eol");
        File out = new File(dir, "out.eol");

        byte[] eoln = "12\n34\n56\n".getBytes("UTF-8");
        byte[] eolr = "12\r34\r56\r".getBytes("UTF-8");
        byte[] eolrn = "12\r\n34\r\n56\r\n".getBytes("UTF-8");
        byte[][] eols = new byte[][] { eoln, eolr, eolrn };

        FileUtils.writeByteArrayToFile(out, eoln);
        assertEquals("\n", FileUtil.getEOL(out, "UTF-8"));
        for (byte[] eolfrom : eols) {
            FileUtils.writeByteArrayToFile(in, eolfrom);
            FileUtil.copyFileWithEolConversion(in, out, "UTF-8");
            assertEquals("\n", FileUtil.getEOL(out, "UTF-8"));
        }

        FileUtils.writeByteArrayToFile(out, eolr);
        assertEquals("\r", FileUtil.getEOL(out, "UTF-8"));
        for (byte[] eolfrom : eols) {
            FileUtils.writeByteArrayToFile(in, eolfrom);
            FileUtil.copyFileWithEolConversion(in, out, "UTF-8");
            assertEquals("\r", FileUtil.getEOL(out, "UTF-8"));
        }

        FileUtils.writeByteArrayToFile(out, eolrn);
        assertEquals("\r\n", FileUtil.getEOL(out, "UTF-8"));
        for (byte[] eolfrom : eols) {
            FileUtils.writeByteArrayToFile(in, eolfrom);
            FileUtil.copyFileWithEolConversion(in, out, "UTF-8");
            assertEquals("\r\n", FileUtil.getEOL(out, "UTF-8"));
        }
    }
}
