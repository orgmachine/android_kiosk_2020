
package com.ehealthkiosk.kiosk.ui.activities;


public interface SettingsView {

    void showProgress();

    void showSuccess(SettingsDataResponse settingsDataResponse, String msg);

    void showError(String errMsg);

}
