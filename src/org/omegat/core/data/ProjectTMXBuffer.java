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

package org.omegat.core.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for store buffer before apply it to ProjectTMX. Editor always set data to this class instead apply it
 * directly to ProjectTMX. This schema required because save(especially with repository rebase) can be enough
 * long operation that shouldn't change ProjectTMX. So, all data will be collected to buffer, than applied to
 * ProjectTMX before save.
 * 
 * @author Alex Buloichik (alex73mail@gmail.com)
 */
public class ProjectTMXBuffer {
    private ProjectTMX baseTMX;

    /**
     * Buffer for put data in editor. Stores null value for deleted.
     */
    private final Map<String, TMXEntry> bufferDefaults = new HashMap<String, TMXEntry>();

    /**
     * Buffer for put data in editor. Stores null value for deleted.
     */
    private final Map<EntryKey, TMXEntry> bufferAlternatives = new HashMap<EntryKey, TMXEntry>();

    public ProjectTMXBuffer(ProjectTMX baseTMX) {
        this.baseTMX = baseTMX;
    }

    /**
     * Get default translation or null if not exist.
     */
    public TMXEntry getDefaultTranslation(String source) {
        synchronized (this) {
            if (bufferDefaults.containsKey(source)) {
                return bufferDefaults.get(source);
            }
            return baseTMX.getDefaultTranslation(source);
        }
    }

    /**
     * Get multiple translation or null if not exist.
     */
    public TMXEntry getMultipleTranslation(EntryKey ek) {
        synchronized (this) {
            if (bufferAlternatives.containsKey(ek)) {
                return bufferAlternatives.get(ek);
            }
            return baseTMX.getMultipleTranslation(ek);
        }
    }

    /**
     * Set new translation.
     */
    public void setTranslation(SourceTextEntry ste, TMXEntry te, boolean isDefault) {
        synchronized (this) {
            if (te == null) {
                if (isDefault) {
                    bufferDefaults.put(ste.getKey().sourceText, null);
                } else {
                    bufferAlternatives.put(ste.getKey(), null);
                }
            } else {
                if (!ste.getSrcText().equals(te.source)) {
                    throw new IllegalArgumentException("Source must be the same as in SourceTextEntry");
                }
                if (isDefault != te.defaultTranslation) {
                    throw new IllegalArgumentException("Default/alternative must be the same");
                }
                if (isDefault) {
                    bufferDefaults.put(ste.getKey().sourceText, te);
                } else {
                    bufferAlternatives.put(ste.getKey(), te);
                }
            }
        }
    }

    /**
     * Returns the collection of TMX entries that have a default translation. Result map is copy of real map.
     */
    public Map<String, TMXEntry> getDefaults() {
        Map<String, TMXEntry> r = baseTMX.getDefaults();
        synchronized (this) {
            r.putAll(bufferDefaults);
        }
        return r;
    }

    /**
     * Returns the collection of TMX entries that have an alternative translation. Result map is copy of real
     * map.
     */
    public Map<EntryKey, TMXEntry> getAlternatives() {
        Map<EntryKey, TMXEntry> r = baseTMX.getAlternatives();
        synchronized (this) {
            r.putAll(bufferAlternatives);
        }
        return r;
    }

    /**
     * Add buffer data into main structures.
     */
    public void applyToBase() {
        synchronized (this) {
            synchronized (baseTMX) {
                baseTMX.applyDefaults(bufferDefaults);
                bufferDefaults.clear();

                baseTMX.applyAlternatives(bufferAlternatives);
                bufferAlternatives.clear();
            }
        }
    }
}
