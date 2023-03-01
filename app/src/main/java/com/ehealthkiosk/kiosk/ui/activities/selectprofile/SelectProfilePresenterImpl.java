package com.ehealthkiosk.kiosk.ui.activities.selectprofile;

import com.ehealthkiosk.kiosk.model.otp.OTPParam;
import com.ehealthkiosk.kiosk.model.profilelist.ProfileData;

public class SelectProfilePresenterImpl implements SelectProfilePresenter, SelectProfileInteractor.SelectProfileViewListener {

    private SelectProfileView selectProfileView;
    private SelectProfileInteractor selectProfileInteractor;

    public SelectProfilePresenterImpl(SelectProfileView selectProfileView) {
        this.selectProfileView = selectProfileView;
        this.selectProfileInteractor = new SelectProfileInteractorImpl();
    }
    @Override
    public void onSuccess(ProfileData profileListData, String msg) {
        if(selectProfileView!=null)
            selectProfileView.showSuccess(profileListData,msg);
    }

    @Override
    public void onError(String msg) {
        if(selectProfileView!=null)
            selectProfileView.showError(msg);
    }

    @Override
    public void getProfileList() {
        if(selectProfileView!=null)
            selectProfileView.showProgress();

        selectProfileInteractor.getProfileList(this);
    }
}
