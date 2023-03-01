package com.ehealthkiosk.kiosk.ui;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.sendDermaImage.SendImageData;
import com.ehealthkiosk.kiosk.model.sendDermaImage.SendImageDetails;
import com.ehealthkiosk.kiosk.utils.Common_Utils;

import static com.ehealthkiosk.kiosk.HealthKioskApp.context;

public class SubmitImagePresenterImpl implements SubmitImagePresenter, SubmitImageInteractor.SubmitImageViewListener {

    private SubmitImageView sendImageView;
    private SubmitImageInteractor sendImageInteractor;

    public SubmitImagePresenterImpl(SubmitImageView sendImageView) {
        this.sendImageView = sendImageView;
        this.sendImageInteractor = new SubmitImageInteractorImpl();
    }

    @Override
    public void onSuccess(SendImageDetails sendImageDetails) {
        if(sendImageView!=null)
            sendImageView.showSuccess(sendImageDetails);
    }


    @Override
    public void onError(String msg) {
        if(sendImageView!=null)
            sendImageView.showError(msg);
    }

    @Override
    public void setSendImageData(SendImageData sendImageData) {
        if (!Common_Utils.isNetworkAvailable()) {
            Common_Utils.showToast(context.getResources().getString(R.string.no_internet));
            return;
        }


        if(sendImageView!=null)
            sendImageView.showProgress();

        sendImageInteractor.sendImageData(sendImageData,this);
    }
}
