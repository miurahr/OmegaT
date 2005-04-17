/**************************************************************************
 OmegaT - Java based Computer Assisted Translation (CAT) tool
 Copyright (C) 2002-2004  Keith Godfrey et al
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

package org.omegat.filters2.xml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import org.omegat.filters2.AbstractFilter;
import org.omegat.filters2.Instance;
import org.omegat.filters2.TranslationException;
import org.omegat.util.AntiCRReader;
import org.omegat.util.EncodingAwareReader;
import org.omegat.util.OStrings;
import org.omegat.util.UTF8Writer;

/**
 * Filter to handle plain XML files.
 * This filter is usually used as a basic class for specific filters of 
 * XML-syntaxed documents (like OpenOffice).
 *
 * @author Keith Godfrey
 * @author Maxym Mykhalchuk
 */
public abstract class XMLAbstractFilter extends AbstractFilter
{
    /**
     * Creates an abstract XML filter.
     * This filter is not usable standalone.
     */
	public XMLAbstractFilter()
	{
		m_preTextList = new ArrayList();
		m_textList = new ArrayList();
		m_postTextList = new ArrayList();
		m_tagMap = new HashMap();

		m_formatList = new ArrayList();
		m_formatDisplayList = new ArrayList();
		m_verbatumList = new ArrayList();
		m_verbatumDisplayList = new ArrayList();
		m_compressWhitespace = false;
		m_streamFilter = null;
	}

	protected void setStreamFilter(XMLStreamFilter filter)
	{
		m_streamFilter = filter;
	}

	public void reset()
	{
		m_preTextList.clear();
		m_textList.clear();
		m_postTextList.clear();
		m_tagMap.clear();
	}

	protected void defineVerbatumTag(String tag, String display)
	{
		m_verbatumList.add(tag);
		m_verbatumDisplayList.add(display);
	}

	protected void defineFormatTag(String tag, String display)
	{
		m_formatList.add(tag);
		m_formatDisplayList.add(display);
	}

    /**
     * Converts simplified formatting tags to originals.
     * Also converts characters to valid XML.
     */
	public String formatString(String text)
	{
		// pull labels from string, ident them in tag
		// and replace them w/ the original data
		char c;

		StringBuffer tagBuf = new StringBuffer();
		StringBuffer outBuf = new StringBuffer(text.length() * 2);

		final int TEXT = 1;
		final int TAG_START = 2;
		final int TAG = 3;

		int state = TEXT;
		for (int i=0; i<text.length(); i++)
		{
			c = text.charAt(i);
			switch (state)
			{
				case TAG_START:
					if (c == '<')
					{
						// double < encountered
						outBuf.append("&lt;");	// NOI18N
						tagBuf.setLength(0);
						state = TEXT;
					}
					else
					{
						tagBuf.append(XMLStreamReader.makeValidXML(c,
									m_streamFilter));
						state = TAG;
					}
					break;

				case TAG:
					if (c == '>')
					{
						String orig = (String) m_tagMap.get(tagBuf.toString());
						if (orig == null || orig.equals(""))	// NOI18N
						{
							orig = "<" + tagBuf.toString() + ">";	// NOI18N
						}
						outBuf.append(orig);
						tagBuf.setLength(0);
						state = TEXT;
					}
					else
						tagBuf.append(c);
					break;

				case TEXT:
					if (c == '<')
						state = TAG_START;
					else
					{
						outBuf.append(XMLStreamReader.makeValidXML(c,
									m_streamFilter));
					}
					break;
			}
		}
		if (tagBuf.length() != 0)
		{
			// string corrupted - signal error
		}
		return outBuf.toString();
	}

    public void processFile(Reader infile, Writer outfile) 
            throws IOException, TranslationException
	{
		int i;
		String s;

		XMLStreamReader xml = new XMLStreamReader();
		xml.setStreamFilter(m_streamFilter);
		xml.compressWhitespace(m_compressWhitespace);
		xml.breakOnWhitespace(m_breakWhitespace);
		XMLBlock blk;
		xml.setStream(new BufferedReader(infile));

		// to keep track of blocks in current segment
		m_preTextList.clear();
		m_textList.clear();
		m_postTextList.clear();
		m_tagMap.clear();

		// this will be set to either text list or pretext list depending
		//	on whether text has been found or not
		ArrayList target = m_preTextList;

		// write original XML preamble as first block
		m_preTextList.add(xml.getHeadBlock());

//		try
//		{
			while ((blk = xml.getNextBlock()) != null)
			{
				if (target == m_preTextList)
				{
					if (blk.hasText() && !blk.isComment())
					{
						// first real text encountered - switch list
						target = m_textList;
						target.add(blk);
					}
					else
					{
						// all tags before text are stored in pre list
						target.add(blk);
					}
				}
				else if (!blk.isTag())
				{
					target.add(blk);
				}
				else
				{
					// tag encountered
					// first cycle through verbatum tag list to see if match
					for (i=0; i<m_verbatumList.size(); i++)
					{
						s = (String) m_verbatumList.get(i);
						if (blk.getTagName().equals(s))
						{
							// store the identifying tag
							target.add(blk);

							// give it a shortcut
							s = (String) m_verbatumDisplayList.get(i);
							blk.setShortcut(s);

							// copy everything until close block
							// give imbedded format tags a shortcut
							ArrayList lst = xml.closeBlock(blk, true);
							XMLBlock openBlock = blk;
							for (int j=0; j<lst.size(); j++)
							{
								blk = (XMLBlock) lst.get(j);
								// if format tag, write shortcut
								if (target == m_textList &&
										(blk.isTag() || blk.isComment()))
								{
									for (int k=0; k<m_formatList.size(); k++)
									{
										s = (String) m_formatList.get(k);
										if (blk.getTagName().equals(s))
										{
											s = (String) 
												m_formatDisplayList.get(k);
											blk.setShortcut(s);
											break;
										}
									}
								}

								target.add(blk);
								if (target == m_preTextList &&
                                        blk.hasText())
								{
									// text encountered - switch to correct
									//	list (if not already done so)
									target = m_textList;
								}
							}
							blk.setShortcut(openBlock.getShortcut());

							break;
						}
					}
					// verbatum block handled - continue with new block
					if (i < m_verbatumList.size())
						continue;

					// cycle through format tag list to see if match
					for (i=0; i<m_formatList.size(); i++)
					{
						s = (String) m_formatList.get(i);
						if (blk.getTagName().equals(s))
						{
							// give it a shortcut
							s = (String) m_formatDisplayList.get(i);
							blk.setShortcut(s);

							target.add(blk);
							break;
						}
					}
					// block handled - continue fresh processing of next
					if (i < m_formatList.size())
						continue;
					
					// if we've made it this far it must be a structural tag
					// consolidate lists and write entry
					// move empty blocks at end of text list to post list
					XMLBlock end;
					for (i=m_textList.size()-1; i>=0; i--)
					{
						end = (XMLBlock) m_textList.get(i);
						if (!end.hasText())
						{
							m_postTextList.add(0, end);
							m_textList.remove(i);
						}
						else
						{
							// last element is text - nothing there to move
							break;
						}
					}
					writeEntry(outfile, blk);
					m_preTextList.clear();
					m_textList.clear();
					m_postTextList.clear();
					m_tagMap.clear();
					target = m_preTextList;
				}
			}
			if (m_preTextList.size() > 0)
				writeEntry(outfile, null);
        /**
		}
		catch(ParseException e) 
		{
			throw new IOException(MessageFormat.format(
				OStrings.getString("XFH_ERROR_PARSEERROR"), 
				new Object[] {m_file, new Integer(e.getErrorOffset() + line()), e.toString()}));
		}
         */
	}

	private void writeEntry(Writer m_outFile, XMLBlock breaker) throws IOException
	{
		ListIterator it;
		String str;
		XMLBlock blk;
		int ctr = 1;

		// if there's nothing interesting and no outfile, ignore it
		if (m_textList.size() == 0 && m_outFile == null)
		{
			m_preTextList.clear();
			m_postTextList.clear();
			m_tagMap.clear();

			return;
		}

		// write out ignored leading tags
		if (m_preTextList.size() > 0 && m_outFile != null)
		{
			it = m_preTextList.listIterator();
			while (it.hasNext())
			{
				blk = (XMLBlock) it.next();
				str = blk.getText();
				if (m_compressWhitespace)
					str += "\n";	// NOI18N
				m_outFile.write(str, 0, str.length());
			}
		}

		if (m_textList.size() > 0)
		{
			// process display text
			it = m_textList.listIterator();
			StringBuffer out = new StringBuffer(256);
			while (it.hasNext())
			{
				blk = (XMLBlock) it.next();
				// need to convert non-tag chars to control values 
				//	when writing
				if (blk.isTag() || blk.isComment())
				{
					String sh = blk.getShortcut();
					String display;
					if (sh != null && !sh.equals(""))	// NOI18N
					{
						display = blk.getShortcut() + ctr++;
						m_tagMap.put(display, blk.getText());
						display = "<" + display + ">";	// NOI18N
					}
					else
						display = blk.getText();
					out.append(display);
				}
				else
				{
					out.append(blk.getText());
				}
			}
            m_outFile.write(processEntry(out.toString()));
		}

		// write out ignored trailing tags
		if (m_postTextList.size() > 0 && m_outFile != null)
		{
			it = m_postTextList.listIterator();
			while (it.hasNext())
			{
				blk = (XMLBlock) it.next();
				str = blk.getText();
				m_outFile.write(str, 0, str.length());
			}
		}

		if (m_outFile != null && breaker != null)
		{
			if (m_compressWhitespace)
				str = "\n" + breaker.getText();		// NOI18N
			else
				str = breaker.getText();
			m_outFile.write(str, 0, str.length());
		}
	}

	protected void compressWhitespace()
	{
		m_compressWhitespace = true;
	}
	
	protected void breakWhitespace()
	{
		m_breakWhitespace = true;
	}
	
//	private LinkedList 	m_tagList = null;

	private ArrayList	m_preTextList;
	private ArrayList	m_textList;
	private ArrayList	m_postTextList;

	private HashMap	m_tagMap;	// associate block shortcut with text

	private ArrayList	m_formatList;
	private ArrayList	m_formatDisplayList;
	private ArrayList	m_verbatumList;
	private ArrayList	m_verbatumDisplayList;

	private boolean	m_compressWhitespace;
	private boolean	m_breakWhitespace;
	private XMLStreamFilter	m_streamFilter;
}
