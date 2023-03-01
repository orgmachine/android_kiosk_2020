/*
 *
 *  * Copyright (C) 2014 Antonio Leiva Gordillo.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.ehealthkiosk.kiosk.ui.activities;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.SendEmailPojo;
import com.ehealthkiosk.kiosk.model.SendSMSPojo;
import com.ehealthkiosk.kiosk.utils.Common_Utils;

import static com.ehealthkiosk.kiosk.HealthKioskApp.context;

public class SendSmsPresenterImpl implements SendSmsPresenter, SendSmsInteractor.SendMessageListener {

    private SendSmsView sendSmsView;
    private SendSmsInteractor sendSmsInteractor;

    public SendSmsPresenterImpl(SendSmsView sendSmsView) {
        this.sendSmsView = sendSmsView;
        this.sendSmsInteractor = new SendSmsInteractorImpl();
    }

    @Override
    public void sendMessage(SendSMSPojo sendEmailPojo) {
        if (!Common_Utils.isNetworkAvailable()) {
            Common_Utils.showToast(context.getResources().getString(R.string.no_internet));
            return;
        }

        if (sendSmsView != null) {
            sendSmsView.showProgress();
        }

        sendSmsInteractor.sendMessage(sendEmailPojo, this);
    }

    @Override
    public void onMessageSuccess(String msg) {
        if (sendSmsView != null) {
            sendSmsView.showSuccess(msg);
        }
    }

    @Override
    public void onMessageError(String errMsg) {
        if (sendSmsView != null) {
            sendSmsView.showError(errMsg);
        }
    }
}
