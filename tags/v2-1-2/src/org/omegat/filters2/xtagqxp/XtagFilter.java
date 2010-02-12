/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool
          with fuzzy matching, translation memory, keyword search,
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2000-2006 Keith Godfrey and Maxym Mykhalchuk
               2008 Didier Briel
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

package org.omegat.filters2.xtagqxp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

import java.util.ArrayList;
import java.util.List;
import org.omegat.filters2.AbstractFilter;
import org.omegat.filters2.Instance;
import org.omegat.filters2.TranslationException;
import org.omegat.util.OStrings;
        
/**
 * Filter to support Xtag files generated by CopyFlow Gold
 * for QuarkXPress
 *
 * @author Keith Godfrey
 * @author Maxym Mykhalchuk
 * @author Didier Briel
 */
public class XtagFilter extends AbstractFilter
{

    protected static final String EOL = "\r\n";                                 // NOI18N
    
    public String getFileFormatName()
    {
        return OStrings.getString("XTAGFILTER_FILTER_NAME");                    
    }

    public Instance[] getDefaultInstances()
    {
        return new Instance[]
        {
            new Instance("*.tag", "UTF-16LE", "UTF-16LE"),                      // NOI18N
            new Instance("*.xtg", "UTF-16LE", "UTF-16LE"),                      // NOI18N
        };
    }

    public boolean isSourceEncodingVariable()
    {
        return true;
    }

    public boolean isTargetEncodingVariable()
    {
        return true;
    }

    
    public void processFile(BufferedReader in, BufferedWriter out)
            throws IOException, TranslationException
    {
        // BOM (byte order mark) bugfix
        in.mark(1);
        int ch = in.read();
        if (ch!=0xFEFF)
            in.reset();
        else
            out.write(ch); // If there was a BOM, we rewrite it
        
        processXtagFile(in, out);
    }

    /**
     * Processes a CopyFlow Gold for QuarkXpress document.
     * Transmits the translatable parts to privateProcessEntry.
     * @param inFile Source document
     * @param outFile Target document
     * @throws java.io.IOException
     * @throws org.omegat.filters2.TranslationException
     */
    private void processXtagFile(BufferedReader inFile, Writer outFile)
            throws IOException, TranslationException {
        final int STATE_WAIT_TEXT = 1;
        final int STATE_READ_TEXT = 2;
        int state = STATE_WAIT_TEXT;
        String tr;
                
        String s;
        s = inFile.readLine();
        while (s != null) {
            // Translatable text
            if (s.startsWith("@$:")){                                           // NOI18N
                outFile.write("@$:");                                           // NOI18N
                s = s.substring(3);
                state = STATE_READ_TEXT;
            } else if (s.startsWith("#boxname")){                               // NOI18N
                state = STATE_WAIT_TEXT;
            }
            if (state == STATE_READ_TEXT){
                tr = privateProcessEntry(s);
            } else
                tr = s;
            outFile.write(tr);
            s = inFile.readLine();
            if (s != null)
                outFile.write(EOL);
        }
    }  
  
    /**
     * Lists of Xtags in an entry
     */
    private List<Xtag> listTags = new ArrayList<Xtag>();
    
    /**
     * Finds the Xtag corresponding to an OmegaT tag
     * @param tag OmegaT tag, without &lt; and &gt;
     * @return either the original Xtag, or the tag with &lt; and &gt; 
     * characters converted to the Xtag equivalent
     */
    private String findTag(StringBuffer tag){
        for (Xtag oneTag : listTags){
            if (oneTag.toShortcut().equals(tag.toString()))
                return oneTag.toOriginal();
        }
        // It was not a real tag
        // We must convert < to <\<> and > to <\>>
        String changedString = "";
        for (int i=0; i<tag.length(); i++) {
            char c = tag.charAt(i);
            changedString += convertSpecialCharacter(c); 
        }
        return changedString;
    }
    
    /**
     * Receives a character, and convert it to the Xtag equivalent if necessary
     * @param c A character
     * @return either the original character, or an Xtag version of that 
     * character
     */
    private String convertSpecialCharacter(char c){
        String changedString = "";
        if (c == '<')
            changedString += "<\\<>";
        else if (c == '>')
            changedString += "<\\>>";
        else
            changedString = Character.toString(c);
 
        return changedString;
    }
    /**
     * Receives an entry with CopyFlow Gold for QuarkXpress pseudo tags (Xtags)
     * Transforms the Xtags into OmegaT tags
     * @param s An entry with Xtags to process
     * @return the entry with OmegaT tags
     */
    private String convertToTags(String s) {
        String changedString = "";                                              // NOI18N
        final int STATE_NORMAL = 1;
        final int STATE_COLLECT_TAG = 2;
        
        int state = STATE_NORMAL;
        int num = 0;
        listTags.clear(); 
        
        StringBuffer tag = new StringBuffer(s.length());
        for (int i=0; i<s.length(); i++) {
             char c = s.charAt(i);
             // Start of a tag
             if ( (c == '<') && (! (state == STATE_COLLECT_TAG)) ) {
                 tag.setLength(0);
                 state = STATE_COLLECT_TAG;
             // Possible end of a tag
             // Exception for <\>>, which is how CopyFlow stores a >
             } else if ( (c == '>') &&  
                     (! (tag.lastIndexOf("\\") == tag.length()-1) )) {
                 num ++;
                 Xtag oneTag = new Xtag(tag.toString(), num);  
                 changedString += oneTag.toShortcut();
                 listTags.add(oneTag);
                 tag.setLength(0);
                 state = STATE_NORMAL;
             } else if (state == STATE_COLLECT_TAG)
                 tag.append(c);
             else
                 changedString += c;
        }

        return changedString;
    }

    /**
     * Receives an entry with OmegaT tags.
     * Transorms the OmegaT tags back into the original Xtags
     * @param s An entry with OmegaT tags to process
     * @return the entry with the original Xtags
     */
    private String convertToXtags(String s) {
        String changedString = "";
        final int STATE_NORMAL = 1;
        final int STATE_COLLECT_TAG = 2;
        
        int state = STATE_NORMAL;
        
        StringBuffer tag = new StringBuffer(s.length());
        for (int i=0; i<s.length(); i++) {
             char c = s.charAt(i);
             // Start of a tag
             if ( (c == '<') && (! (state == STATE_COLLECT_TAG)) ) {
                 tag.setLength(0);
                 tag.append(c);
                 state = STATE_COLLECT_TAG;
             // End of a tag
             } else if ( (c == '>') && (state == STATE_COLLECT_TAG)) {
                 tag.append(c);
                 changedString += findTag(tag);
                 state = STATE_NORMAL;
                 tag.setLength(0);
             } else if (state == STATE_COLLECT_TAG)
                 tag.append(c);
             else 
                 changedString += convertSpecialCharacter(c);
        }
        // Copy what might remain at the end of the string
        changedString += findTag(tag);
        return changedString;
    }
    
    /**
     * Processes Xtags before and after sending the entry to OmegaT.
     * The Xtags in the entry are converted to OmegaT tags, then the entry 
     * is sent to OmegaT, and the OmegaT tags are converted back to Xtags.
     * @param entry An entry to process
     * @return The entry for the target document
     */
    private String privateProcessEntry(String entry) {
        entry = convertToTags(entry);  
        entry = processEntry(entry);   
        entry = convertToXtags(entry); 
        return entry;
    }             
      
}