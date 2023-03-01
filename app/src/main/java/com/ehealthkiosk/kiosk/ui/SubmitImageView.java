package com.ehealthkiosk.kiosk.ui;

import com.ehealthkiosk.kiosk.model.sendDermaImage.SendImageDetails;

public interface SubmitImageView {

    void showProgress();
    void showSuccess(SendImageDetails sendImageResponse);
    void showError(String msg);

}
