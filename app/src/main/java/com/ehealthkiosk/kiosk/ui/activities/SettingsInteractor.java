
package com.ehealthkiosk.kiosk.ui.activities;

public interface SettingsInteractor {

    interface SettingsViewListener {
        void onSuccess(SettingsDataResponse settingsDataResponse, String msg);

        void onError(String errMsg);
    }

    void settings(KioskIdData kioskIdData, SettingsViewListener listener);

}
