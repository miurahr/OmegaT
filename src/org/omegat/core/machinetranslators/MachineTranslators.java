package org.omegat.core.machinetranslators;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.omegat.gui.exttrans.IMachineTranslation;

public class MachineTranslators {

    private static final List<IMachineTranslation> TRANSLATORS = new CopyOnWriteArrayList<>();

    public static void add(IMachineTranslation machineTranslator) {
        TRANSLATORS.add(machineTranslator);
    }

    public static List<IMachineTranslation> getMachineTranslators() {
        return Collections.unmodifiableList(TRANSLATORS);
    }
}
