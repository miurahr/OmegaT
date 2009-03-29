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

package org.omegat.core.dictionaries;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Dictionary implementation for StarDict format.
 * 
 * Stardict format described on
 * http://code.google.com/p/babiloo/wiki/StarDict_format
 * 
 * @author Alex Buloichik <alex73mail@gmail.com>
 */
public class StarDict implements IDictionary {
	protected final File ifoFile;
	protected static final Charset UTF8 = Charset.forName("UTF-8");

	/**
	 * @param ifoFile
	 *            ifo file with dictionary
	 */
	public StarDict(File ifoFile) {
		this.ifoFile = ifoFile;
	}

	public Map<String, Object> readHeader() throws IOException {
		Map<String, Object> result = new HashMap<String, Object>();

		String f = ifoFile.getPath();
		if (f.endsWith(".ifo")) {
			f = f.substring(0, f.length() - 4);
		}
		File idxFile = new File(f + ".idx");
		File dataFile = new File(f + ".dict");

		RandomAccessFile data = new RandomAccessFile(dataFile, "r");
		try {
			DataInputStream idx = new DataInputStream(new BufferedInputStream(
					new FileInputStream(idxFile)));
			ByteArrayOutputStream mem = new ByteArrayOutputStream();
			try {
				while (true) {
					int b = idx.read();
					if (b == -1) {
						break;
					}
					if (b == 0) {
						String key = new String(mem.toByteArray(), 0, mem
								.size(), UTF8);
						mem.reset();
						int bodyOffset = idx.readInt();
						int bodyLength = idx.readInt();
						String text = readArticleText(data, bodyOffset,
								bodyLength);
						result.put(key, text);
					} else {
						mem.write(b);
					}
				}
			} finally {
				idx.close();
			}
		} finally {
			data.close();
		}

		return result;
	}

	public String readArticle(String word, Object acticleData) {
		return (String) acticleData;
	}

	protected String readArticleText(RandomAccessFile file, int off, int len)
			throws IOException {
		file.seek(off);
		byte[] data = new byte[len];
		file.readFully(data);
		return new String(data, 0, len, UTF8).replace("\n", "<br>");
	}
}
