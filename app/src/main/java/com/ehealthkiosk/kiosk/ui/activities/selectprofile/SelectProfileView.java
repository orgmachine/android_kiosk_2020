package com.ehealthkiosk.kiosk.ui.activities.selectprofile;

import com.ehealthkiosk.kiosk.model.profilelist.ProfileData;

public interface SelectProfileView {

    void showProgress();
    void showSuccess(ProfileData profileListData, String msg);
    void showError(String msg);
}
