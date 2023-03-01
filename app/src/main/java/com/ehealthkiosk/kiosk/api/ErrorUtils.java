package com.ehealthkiosk.kiosk.api;

import com.ehealthkiosk.kiosk.model.commonresponse.CommonResponse;
import com.ehealthkiosk.kiosk.model.SessionExpiredEvent;
import com.ehealthkiosk.kiosk.utils.Common_Utils;

import org.greenrobot.eventbus.EventBus;

import java.lang.annotation.Annotation;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;


public class ErrorUtils {

    public static final String ErrorMessage = "Servers cannot be reached. Please try again";

    public static String parseError(Response<?> response) {

        if (response.code() == 401) {
            EventBus.getDefault().post(new SessionExpiredEvent(true));
            return "";
        }

        Converter<ResponseBody, CommonResponse> converter =
                RestClient.getRetrofit()
                        .responseBodyConverter(CommonResponse.class, new Annotation[0]);


        CommonResponse error;
        Common_Utils.hideProgress();
        try {
            error = converter.convert(response.errorBody());
            return error.getStatus().getMessage();
        } catch (Exception e) {
            return ErrorMessage;
        }
    }

    public static String parseLoginError(Response<?> response) {

        Converter<ResponseBody, CommonResponse> converter =
                RestClient.getRetrofit()
                        .responseBodyConverter(CommonResponse.class, new Annotation[0]);


        CommonResponse error;
        Common_Utils.hideProgress();
        try {
            error = converter.convert(response.errorBody());
            return error.getStatus().getMessage();
        } catch (Exception e) {
            return ErrorMessage;
        }
    }

    public static void parseErrorThrow(Throwable throwable) {
        if (throwable == null)
            return;

        if(Common_Utils.isNotNullOrEmpty(throwable.getMessage())){
            Common_Utils.showToast(throwable.getMessage());
        }
    }
}
