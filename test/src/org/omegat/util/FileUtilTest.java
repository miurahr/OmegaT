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
}
