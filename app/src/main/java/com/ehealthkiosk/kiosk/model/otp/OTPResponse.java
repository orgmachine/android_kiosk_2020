package com.ehealthkiosk.kiosk.model.otp;

import com.ehealthkiosk.kiosk.model.commonresponse.Status;

public class OTPResponse {

    /**
     * status : {
     *         "result": 1,
     *         "message": "OTP verified successfully"
     *     }
     * data : {
     *         "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtb2JpbGUiOiIrOTE5OTMwMDQyMTc1In0.cMRD9PAy9O3ThtELkCZreFrAWWHBy1o302V2J1jWvRE"
     *     }
     */

    private Status status;
    private OTPData data;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public OTPData getData() {
        return data;
    }

    public void setData(OTPData data) {
        this.data = data;
    }


    public static class OTPData {
        /**
         * token : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtb2JpbGUiOiIrOTE5OTMwMDQyMTc1In0.cMRD9PAy9O3ThtELkCZreFrAWWHBy1o302V2J1jWvRE
         */

        private String token;

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

}
