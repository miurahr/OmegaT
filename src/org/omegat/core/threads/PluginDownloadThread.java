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

package org.omegat.core.threads;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Formatter;
import java.util.HashMap;

import org.omegat.util.net.HttpConnectionUtils;

public class PluginDownloadThread extends LongProcessThread {

    private final URL url;
    private final File targetdir;
    private final String archive;
    private final HashMap<String, String> headers = new HashMap<>();

    public PluginDownloadThread(URL url, String username, String token, File targetdir, String filename) throws UnsupportedEncodingException {
        this.url = url;
        this.targetdir = targetdir;
        this.archive = filename;
        String encoding = Base64.getEncoder().encodeToString((username + ":" + token).getBytes("UTF-8"));
        headers.put("authorization", "Basic " + encoding);
    }

    @Override
    public void run() {
        try {
            String jarChecksumUrl = url.toString() + ".sha256";
            HashMap<String, String> params = new HashMap<>();
            String expected = HttpConnectionUtils.get(jarChecksumUrl, params, headers);

            File targetFilePath = new File(targetdir, archive);
            boolean result = HttpConnectionUtils.downloadBinaryFile(url, headers, targetFilePath);
            checkInterrupted();
            if (!result || !expected.equals(calculateSha256(targetFilePath))) {
                // todo: remove download files and show warning
            }
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private String calculateSha256(final File targetFilePath) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        sha256.reset();

        byte[] buffer = new byte[8192];
        try (InputStream in = new BufferedInputStream(new FileInputStream(targetFilePath))) {
            while (true) {
                int len = in.read(buffer);
                if (len < 0) {
                    break;
                }
                sha256.update(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // out as hex
        Formatter formatter = new Formatter();
        try {
            for (byte b : sha256.digest()) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        } finally {
            formatter.close();
        }
    }
}
