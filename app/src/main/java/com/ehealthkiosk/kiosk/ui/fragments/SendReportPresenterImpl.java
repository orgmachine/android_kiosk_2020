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

package com.ehealthkiosk.kiosk.ui.fragments;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.SendEmailPojo;
import com.ehealthkiosk.kiosk.utils.Common_Utils;

import static com.ehealthkiosk.kiosk.HealthKioskApp.context;

public class SendReportPresenterImpl implements SendReportPresenter, SendReportInteractor.SendReporterListener {

    private SendReportView sendReportView;
    private SendReportInteractor sendReportInteractor;

    public SendReportPresenterImpl(SendReportView sendReportView) {
        this.sendReportView = sendReportView;
        this.sendReportInteractor = new SendReportInteractorImpl();
    }

    @Override
    public void sendEmail(SendEmailPojo sendEmailPojo) {
        if (!Common_Utils.isNetworkAvailable()) {
            Common_Utils.showToast(context.getResources().getString(R.string.no_internet));
            return;
        }

        if (sendReportView != null) {
            sendReportView.showProgress();
        }

        sendReportInteractor.sendEmail(sendEmailPojo, this);
    }

    @Override
    public void onSuccess(String msg) {
        if (sendReportView != null) {
            sendReportView.showSuccess(msg);
        }
    }

    @Override
    public void onError(String errMsg) {
        if (sendReportView != null) {
            sendReportView.showError(errMsg);
        }
    }

}