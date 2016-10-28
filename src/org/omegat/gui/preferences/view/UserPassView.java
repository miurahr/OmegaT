package org.omegat.gui.preferences.view;

import javax.swing.JComponent;

import org.omegat.gui.preferences.PreferencesView;
import org.omegat.util.Log;
import org.omegat.util.Preferences;
import org.omegat.util.StringUtil;

public class UserPassView implements PreferencesView {

    private UserPassPanel panel;

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
        return "Proxy Login";
    }

    private void initGui() {
        panel = new UserPassPanel();
    }

    @Override
    public void initDefaults() {
        String encodedUser = Preferences.getPreference(Preferences.PROXY_USER_NAME);
        String encodedPassword = Preferences.getPreference(Preferences.PROXY_PASSWORD);

        try {
            panel.userText.setText(new String(StringUtil.decodeBase64(encodedUser)));
            panel.passwordField.setText(new String(StringUtil.decodeBase64(encodedPassword)));
        } catch (IllegalArgumentException ex) {
            Log.logErrorRB("LOG_DECODING_ERROR");
            Log.log(ex);
        }
    }

    @Override
    public void persist() {
        String encodedUser = StringUtil.encodeBase64(panel.userText.getText().getBytes());
        String encodedPassword = StringUtil.encodeBase64(new String(panel.passwordField.getPassword()).getBytes());
        Preferences.setPreference(Preferences.PROXY_USER_NAME, encodedUser);
        Preferences.setPreference(Preferences.PROXY_PASSWORD, encodedPassword);
    }
}
