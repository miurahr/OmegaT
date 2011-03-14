package org.omegat.filters;

import org.omegat.core.data.IProject;
import org.omegat.filters3.xml.svg.SvgFilter;

public class SvgFilterTest extends TestFilterBase {
   
    public void testLoad() throws Exception {
        String f = "test/data/filters/SVG/Litva.svg";
        IProject.FileInfo fi = loadSourceFiles(new SvgFilter(), f);

        checkMultiStart(fi, f);
        skipMulti();
        skipMulti();
        skipMulti();
        checkMulti("Polatsk lands", null, null, "Navahrudak lands", "Turaw-Pinsk lands", null);
        checkMulti("Turaw-Pinsk lands", null, null, "Polatsk lands", "Pinsk", null);
        checkMulti("Pinsk", null, null, "Turaw-Pinsk lands", "Slonim", null);
    }
}
