/**************************************************************************
 OmegaT - Java based Computer Assisted Translation (CAT) tool
 Copyright (C) 2002-2005  Keith Godfrey et al
                          keithgodfrey@users.sourceforge.net
                          907.223.2039

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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import org.omegat.filters2.TranslationException;
import org.omegat.filters2.xml.XMLBlock;
import org.omegat.filters2.xml.XMLStreamReader;

/**
 * Class to load & save OmegaT preferences
 *
 * @author Keith Godfrey
 */
public class PreferenceManager
{
	public PreferenceManager()
	{
		pref = this;
		m_prefFileName = OConsts.PROJ_PREFERENCE;
		m_loaded = false;
		m_preferenceMap = new HashMap(64);
		m_nameList = new ArrayList(32);
		m_valList = new ArrayList(32);
		m_changed = false;
	}

    /**
     * Gets the preference out of OmegaT's preferences file.
     * <p>
     * If the key is not found, returns the empty string
     *
     * @param name name of the key to look up
     */
	public synchronized String getPreference(String name)
	{
		if (name == null || name.equals(""))								// NOI18N
			return "";															// NOI18N
		if (!m_loaded)
			doLoad();
		
		Integer i = (Integer) m_preferenceMap.get(name);
		String v = "";															// NOI18N
		if (i != null)
		{
			// mapping exists - recover value
			v = (String) m_valList.get(i.intValue());
		}
		return v;
	}

	public synchronized void setPreference(String name, String val)
	{
		if (name != null && !name.equals("") && val != null)				// NOI18N
		{
			if (!m_loaded)
				doLoad();
			Integer i = (Integer) m_preferenceMap.get(name);
			if (i == null)
			{
				// value doesn't exist - add it
				i = new Integer(m_valList.size());
				m_preferenceMap.put(name, i);
				m_valList.add(val);
				m_nameList.add(name);
			}
			else
			{
				// mapping exists - reset value to new
				m_valList.set(i.intValue(), val);
			}
			m_changed = true;
		}
	}

	public synchronized void save()
	{
		try 
		{
			if (m_changed)
				doSave();
		}
		catch (IOException e)
		{
			StaticUtils.log(OStrings.getString("PM_ERROR_SAVE") + e);
		}
	}

    private void doLoad()
	{
		try
		{
			// mark as loaded - if the load fails, there's no use 
			//  trying again later
			m_loaded = true;

			XMLStreamReader xml = new XMLStreamReader();
			xml.killEmptyBlocks();
			xml.setStream(new File(m_prefFileName));
			XMLBlock blk;
			ArrayList lst;

			m_preferenceMap.clear();
			String pref;
			String val;
			// advance to omegat tag
			if (xml.advanceToTag("omegat") == null)	// NOI18N
				return;
			
			// advance to project tag
			if ((blk = xml.advanceToTag("preference")) == null)	// NOI18N
				return;
	
			String ver = blk.getAttribute("version");			// NOI18N
			if (ver != null && !ver.equals("1.0"))	// NOI18N
			{
				// unsupported preference file version - abort read
				return;
			}
	
			lst = xml.closeBlock(blk);
			if (lst == null)
				return;
	
			Integer j;
			for (int i=0; i<lst.size(); i++)
			{
				blk = (XMLBlock) lst.get(i);
				if (blk.isClose())
					continue;
	
				if (!blk.isTag())
					continue;

				pref = blk.getTagName();
				blk = (XMLBlock) lst.get(++i);
				if (blk.isTag())
				{
					// parse error - keep trying
					continue;
				}
				val = blk.getText();
				if (pref != null && !pref.equals("") && val != null)		// NOI18N
				{
					// valid match - record these
					j = new Integer(m_valList.size());
					m_preferenceMap.put(pref, j);
					m_nameList.add(pref);
					m_valList.add(val);
				}
			}
		}
		catch (TranslationException te)
		{
			// error loading preference file - keep whatever was
			//  loaded then return gracefully to calling function
			// print an error to the console as an FYI
			StaticUtils.log(OStrings.getString("PM_WARNING_PARSEERROR_ON_READ") + te);
            te.printStackTrace(StaticUtils.getLogStream());
		}
		catch (IndexOutOfBoundsException e3)
		{
			// error loading preference file - keep whatever was
			//  loaded then return gracefully to calling function
			// print an error to the console as an FYI
			StaticUtils.log(OStrings.getString("PM_WARNING_PARSEERROR_ON_READ") + e3);
            e3.printStackTrace(StaticUtils.getLogStream());
		}
		catch (UnsupportedEncodingException e3)
		{
			// unrecognized file - forget about it
            StaticUtils.log("Unrecognized file's encoding"); // NOI18N
            e3.printStackTrace(StaticUtils.getLogStream());
		}
		catch (IOException e4)
		{
			// can't read file - forget about it and move on
            // e4.printStackTrace();
		}
	}
	
	private void doSave() throws IOException
	{
		String str;														// NOI18N
		String name;														// NOI18N
		String val;														// NOI18N
		BufferedWriter out = new BufferedWriter(new FileWriter(
					m_prefFileName));
		
		str =  "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n";					// NOI18N

		str += "<omegat>\n";													// NOI18N
		str += "  <preference version=\"1.0\">\n";								// NOI18N
		out.write(str, 0, str.length());

		for (int i=0; i<m_nameList.size(); i++)
		{
			name = (String) m_nameList.get(i);
			val = StaticUtils.makeValidXML((String) m_valList.get(i));
			if (val.equals(""))													// NOI18N
				continue;	// don't write blank preferences
			str = "    <" + name + ">";											// NOI18N
			str += val;
			str += "</" + name + ">\n";											// NOI18N
			out.write(str, 0, str.length());
		}
		str =  "  </preference>\n";												// NOI18N
		str += "</omegat>\n";													// NOI18N
		out.write(str, 0, str.length());
		out.flush();
		m_changed = false;
	}

	
	private boolean	m_loaded;
	private boolean	m_changed;
	private String	m_prefFileName;

	// use a hash map for fast lookup of data
	// use array lists for orderly recovery of it for saving to disk
    private ArrayList	m_nameList;
	private ArrayList	m_valList;
	private HashMap	m_preferenceMap;

	public static PreferenceManager pref;

}
