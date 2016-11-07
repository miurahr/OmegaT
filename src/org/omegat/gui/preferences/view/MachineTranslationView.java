package org.omegat.gui.preferences.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.table.AbstractTableModel;

import org.omegat.core.machinetranslators.MachineTranslators;
import org.omegat.gui.exttrans.IMachineTranslation;
import org.omegat.gui.preferences.PreferencesView;
import org.omegat.util.Preferences;

public class MachineTranslationView implements PreferencesView {

    private final Map<String, Boolean> providerStatus = new HashMap<>();
    private MachineTranslationPanel panel;

    @Override
    public JComponent getGui() {
        if (panel == null) {
            initGui();
            initDefaults();
        }
        return panel;
    }

    @Override
    public String toString() {
        return "Machine Translation";
    }
    
    enum ProviderColumn {
        CHECKBOX(0, Boolean.class), NAME(1, String.class), CONFIG_BUTTON(2, String.class);
        
        private Class<?> clazz;

        private ProviderColumn(int i, Class<?> clazz) {
            this.clazz = clazz;
        }

        static ProviderColumn get(int i) {
            return values()[i];
        }
    }

    private void initGui() {
        panel = new MachineTranslationPanel();
	MachineTranslators.getMachineTranslators().stream().forEach(p -> {
	    JComponent comp = p.getUiComponent();
	    if (comp != null) {
	        panel.mtPrefsTabbedPane.add(p.getName(), comp);
	    }
	});
    }

    @Override
    public void initDefaults() {
        panel.autoFetchCheckBox.setSelected(Preferences.isPreference(Preferences.MT_AUTO_FETCH));
        panel.untranslatedOnlyCheckBox.setSelected(Preferences.isPreference(Preferences.MT_ONLY_UNTRANSLATED));
        List<IMachineTranslation> mtProviders = MachineTranslators.getMachineTranslators();
        mtProviders.stream().forEach(p -> providerStatus.put(p.getName(), p.isEnabled()));
        panel.mtProviderTable.setModel(new ProvidersTableModel(mtProviders));
    }

    @Override
    public void persist() {
        Preferences.setPreference(Preferences.MT_AUTO_FETCH, panel.autoFetchCheckBox.isSelected());
        Preferences.setPreference(Preferences.MT_ONLY_UNTRANSLATED, panel.untranslatedOnlyCheckBox.isSelected());
        MachineTranslators.getMachineTranslators().stream().forEach(p -> {
            p.prefsUpdated();
            Boolean status = providerStatus.get(p.getName());
            if (status != null) {
                p.setEnabled(status);
            }
        });
    }

    @SuppressWarnings("serial")
    class ProvidersTableModel extends AbstractTableModel {

        private final List<IMachineTranslation> mtProviders;

        public ProvidersTableModel(List<IMachineTranslation> mtProviders) {
            this.mtProviders = mtProviders;
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return ProviderColumn.get(column) == ProviderColumn.CHECKBOX;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            if (ProviderColumn.get(column) == ProviderColumn.CHECKBOX) {
                providerStatus.put(mtProviders.get(row).getName(), (Boolean) aValue);
            }
        }

        @Override
        public int getColumnCount() {
            return ProviderColumn.values().length;
        }

        @Override
        public String getColumnName(int column) {
            return "";
        }

        @Override
        public Object getValueAt(int row, int column) {
            switch (ProviderColumn.get(column)) {
            case CHECKBOX:
                return providerStatus.get(mtProviders.get(row));
            case NAME:
                return mtProviders.get(row).getName();
            case CONFIG_BUTTON:
                return "<html><font color=\"blue\"><u>Config</u></font></html>";
            default:
                throw new IndexOutOfBoundsException();
            }
        }

        @Override
        public int getRowCount() {
            return mtProviders.size();
        }

        @Override
        public java.lang.Class<?> getColumnClass(int columnIndex) {
            return ProviderColumn.get(columnIndex).clazz;
        };
    }
}
