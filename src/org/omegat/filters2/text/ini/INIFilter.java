/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool
          with fuzzy matching, translation memory, keyword search,
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
               2009-2010 Alex Buloichik
               2011 Alex Buloichik, Didier Briel
               2021 Hiroshi Miura
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

package org.omegat.filters2.text.ini;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.omegat.filters2.AbstractFilter;
import org.omegat.filters2.FilterContext;
import org.omegat.filters2.Instance;
import org.omegat.util.LinebreakPreservingReader;
import org.omegat.util.NullBufferedWriter;
import org.omegat.util.OStrings;
import org.omegat.util.StringUtil;

/**
 * Filter to support Files with Key=Value pairs, which are sometimes used for
 * i18n of software.
 *
 * @author Maxym Mykhalchuk
 * @author Alex Buloichik
 * @author Didier Briel
 */
public class INIFilter extends AbstractFilter {
    private static String unnamed_format = "[$$UnNamee_%d_]"; // general key cannot be [..]
    protected Map<String, String> align;

    public String getFileFormatName() {
        return OStrings.getString("INIFILTER_FILTER_NAME");
    }

    public boolean isSourceEncodingVariable() {
        return true;
    }

    public boolean isTargetEncodingVariable() {
        return true;
    }

    public Instance[] getDefaultInstances() {
        return new Instance[] { new Instance("*.ini"),
                                new Instance("*.lng"),
                new Instance("*.strings", StandardCharsets.UTF_8.name(), StandardCharsets.UTF_8.name()) };
    }

    /**
     * Trims the string from left.
     */
    private String leftTrim(String s) {
        int i = 0;
        while (i < s.length()) {
            int cp = s.codePointAt(i);
            if (cp != ' ' && cp != '\t') {
                break;
            }
            i += Character.charCount(cp);
        }
        return s.substring(i, s.length());
    }

    /**
     * Doing the processing of the file...
     */
    @Override
    public void processFile(BufferedReader reader, BufferedWriter outfile, FilterContext fc) throws IOException {
        LinebreakPreservingReader lbpr = new LinebreakPreservingReader(reader); // fix for bug 1462566
        String str;
        String group = null;
        int unnamed_counter = 0;

        while ((str = lbpr.readLine()) != null) {
            String key;
            String value;
            int equalsPos;
            int afterEqualsPos;
            String trimmed = str.trim();
            boolean hasQuote = false;

            // skipping empty strings and comments
            if (trimmed.isEmpty() || trimmed.codePointAt(0) == '#' || trimmed.codePointAt(0) == ';') {
                // outfile.write(str+"\n");
                outfile.write(str);
                outfile.write(lbpr.getLinebreak()); // fix for bug 1462566
                continue;
            }

            // retrieve section as group name
            if (trimmed.startsWith("[") && trimmed.endsWith("]")) {
                group = trimmed.substring(trimmed.offsetByCodePoints(0, 1),
                        trimmed.offsetByCodePoints(trimmed.length(), -1));
                outfile.write(str);
                continue;
            }

            // key=value pairs
            equalsPos = str.indexOf('=');

            // if there's no separator, assume it's an unnamed key with a value
            // It may be a continuous line, so allow translate.
            if (equalsPos == -1) {
                // produce virtual key
                key = (group != null ? group + '/' : "") + String.format(unnamed_format, unnamed_counter);
                unnamed_counter++;
                value = str;
                // nothing to output
            } else {
                // advance if there're spaces after =
                while (str.codePointCount(equalsPos, str.length()) > 1) {
                    int nextOffset = str.offsetByCodePoints(equalsPos, 1);
                    if (str.codePointAt(nextOffset) != ' ') {
                        break;
                    }
                    equalsPos = nextOffset;
                }

                afterEqualsPos = str.offsetByCodePoints(equalsPos, 1);

                // writing out everything before = (and = itself)
                outfile.write(str.substring(0, afterEqualsPos));

                key = (group != null ? group + '/' : "") + str.substring(0, equalsPos).trim();
                value = str.substring(afterEqualsPos);
            }

            value = leftTrim(value);
            if (value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(value.offsetByCodePoints(0, 1),
                        value.offsetByCodePoints(value.length(), -1));
                hasQuote = true;
            }

            if (entryAlignCallback != null) {
                align.put(key, value);
            } else if (entryParseCallback != null) {
                entryParseCallback.addEntry(key, value, null, false, null, null, this, null);
            } else if (entryTranslateCallback != null) {
                String trans = entryTranslateCallback.getTranslation(key, value, null);
                if (trans == null) {
                    trans = value;
                }
                if (hasQuote) {
                    outfile.write('"');
                }
                outfile.write(trans);
                if (hasQuote) {
                    outfile.write('"');
                }

                // outfile.write("\n");
                outfile.write(lbpr.getLinebreak()); // fix for bug 1462566
            }
        }
        lbpr.close();
    }

    @Override
    protected void alignFile(BufferedReader sourceFile, BufferedReader translatedFile, org.omegat.filters2.FilterContext fc) throws Exception {
        Map<String, String> source = new HashMap<String, String>();
        Map<String, String> translated = new HashMap<String, String>();

        align = source;
        processFile(sourceFile, new NullBufferedWriter(), fc);
        align = translated;
        processFile(translatedFile, new NullBufferedWriter(), fc);
        for (Map.Entry<String, String> en : source.entrySet()) {
            String tr = translated.get(en.getKey());
            if (!StringUtil.isEmpty(tr)) {
                entryAlignCallback.addTranslation(en.getKey(), en.getValue(), tr, false, null, this);
            }
        }
    }
}
