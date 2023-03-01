package com.ehealthkiosk.kiosk.ui;

import com.ehealthkiosk.kiosk.model.sendDermaImage.SendImageData;
import com.ehealthkiosk.kiosk.model.sendDermaImage.SendImageDetails;

public interface SubmitImageInteractor {

    interface SubmitImageViewListener{
        void onSuccess(SendImageDetails sendImageResponse);
        void onError(String msg);
    }

    void sendImageData(SendImageData sendImageData, SubmitImageInteractor.SubmitImageViewListener changeListener);
}
