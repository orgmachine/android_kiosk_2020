package com.ehealthkiosk.kiosk.ui.activities.selectprofile;

import com.ehealthkiosk.kiosk.model.profilelist.ProfileData;

public interface SelectProfileInteractor {
    interface SelectProfileViewListener{
        void onSuccess(ProfileData profileListData, String msg);
        void onError(String msg);
    }

    void getProfileList(SelectProfileViewListener changeListener);
}
