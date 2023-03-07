package com.ehealthkiosk.kiosk.utils;

import com.ehealthkiosk.kiosk.BuildConfig;

public class Constants {

    // values have to be globally unique
    public static final String INTENT_ACTION_DISCONNECT = BuildConfig.APPLICATION_ID + ".Disconnect";
    public static final String NOTIFICATION_CHANNEL = BuildConfig.APPLICATION_ID + ".Channel";
    public static final String INTENT_CLASS_MAIN_ACTIVITY = BuildConfig.APPLICATION_ID + ".MainActivity";

    // values have to be unique within each app
    public static final int NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001;

    private Constants() {}

    public static String NO_INTERNET_CONNECTION = "Network not available";

    public static final String PREF = "com.ehealthkiosk.kiosk";
    public static final String PERSONAL_PREF = "com.ehealthkiosk.kiosk.personal";
    public static final String PROFILE_ID = "profile_id";
    public static final String PROFILE = "profile";
    public static final String TOKEN = "TOKEN";
    public static final String KIOSK_ID = "";
    public static Boolean KIOSK_ID_PRESENT = false;
    public static final String REFERENCEHEIGHT = "Reference_Height";
    public static final String APP_NAME = "Yolohealth";


    public static final String MANUAL_HEIGHT_FT = "MANUAL_HEIGHT_FT";
    public static final String MANUAL_HEIGHT_INCH = "MANUAL_HEIGHT_INCH";
    public static final String MANUAL_WEIGHT = "MANUAL_WEIGHT";
    public static final String BMI = "BMI";
    public static final String HYDRATION = "HYDRATION";
    public static final String FAT = "FAT";
    public static final String BONEMASS = "BONEMASS";
    public static final String OBESITY = "OBESITY";
    public static final String PROTEIN = "PROTEIN";
    public static final String VFI = "VFI";
    public static final String BMR = "BMR";
    public static final String BODY_AGE = "BODY_AGE";



    public static final String MUSCLE = "MUSCLE";
    public static final String MANUAL_SYSTOLIC = "MANUAL_SYSTOLIC";
    public static final String MANUAL_DIASTOLIC = "MANUAL_DIASTOLIC";
    public static final String MANUAL_PULSE = "MANUAL_PULSE";
    public static final String MANUAL_Sp02 = "MANUAL_Sp02";
    public static final String MANUAL_TEMPERATURE = "MANUAL_TEMPERATURE";
    public static final String MANUAL_GLUCOSE = "MANUAL_GLUCOSE";
    public static final String MANUAL_HB = "MANUAL_HB";



    public static final String STATE = "state";
    public static final int STATE_INITIAL = 1;
    public static final int STATE_REGISTERED = 2;

    public static final String FROM = "from";

    public static final String NEW_USER = "new_user";
    public static final String ADD_PROFILE = "add_profile";

    public static final String REPORT_ITEM = "report_item";

    public static final String base_url = "https://stgapi.yolohealth.co.in/api/v1/";
    public static final String SERVER_ERROR = "Servers cannot be reached.\n Please try again";

    public static final String DIALOG_DISMISS = "Dismiss";
    public static final String GO_TO_SETTINGS = "Go to settings";

    public static final String ProfileImagePathTag = "profileImage";
    public static final String IMAGE_DOESNOT_EXIST = "Image not found.";

    public static final String MOBILE_NO = "MobileNo";
    public static final String PHONE_CODE = "PhoneCode";
    public static final String PHONE_NO = "PhoneNo";
    public static final String USER_EMAIL = "user email";


    public static final String AUTHORIZATION_TAG = "Authorization";
    public static final String HEADER_TAG = "x-kiosk-id";


    public static final String ONCREATE = "oncreate";
    public static final String ONREFRESH = "onrefresh";
    public static final String ONRESUME = "onresume";
    public static final String ONLOADMORE = "onloadmore";

    public static final String TITLE = "title";
    public static final String PDF_PATH="pdfpath";
    public static final String PDF_HTML = "pdfHtml";
    public static final String REPORT_ID = "report id";


    public static final String REPORT_DATA="reportData";
    public static final String TEST_TYPE = "test type";
    public static final String TEST_TYPE_BASIC = "basic_test";
    public static final String TEST_TYPE_WELLNESS = "wellness_test";
    public static final String TEST_TYPE_DERMA = "dermascope_test";
    public static final String TEST_TYPE_AUTOSCOPE = "autoscope_test";
    public static final String TEST_TYPE_STETHESCOPE = "stethescope_test";
    public static final String TEST_TYPE_ECG = "ECG";

    public static final String REPORT_TYPE_DERMA = "DERMASCOPE";
    public static final String REPORT_TYPE_AUTOSCOPE = "AUTOSCOPE";
    public static final String REPORT_TYPE = "report_type";

    public static final String IS_BASIC_TEST = "is_basic_test";
    public static final String IS_WELLNESS_TEST = "is_wellness_test";
    public static final String IS_DERMA_TEST = "is_derma_test";
    public static final String IS_AUTO_TEST = "is_autoscope_test";
    public static final String DEVICE_PREF = "device_pref";









    public static final String CONSULT_DOCTOR = "consult_doctor";





    public static final String BASIC_HEALTH_BILL = "basic_test_price";
    public static final String WELLNESS_TEST_BILL = "wellness_test_price";
    public static final String ADMIN_PASSWORD = "admin_password";
    public static final String ISSUED_BY = "issued_by";
    public static final String EXPIRY_DATE = "expiry";
    public static final String PRINT_URL = "print url";
    public static final String BASIC_PRINT_CHECK_URL = "https://stgapi.yolohealth.co.in";
    public static final String PRINT_STATUS_CHECK_URL = "http://www.africau.edu/images/default/sample.pdf";




    // device names
    public static final String PULSE = "pulse";
    public static final String TEMPERATURE = "temperature";
    public static final String BP = "bp";
    public static final String BP_DEVICE_TYPE = "bp_device_type";
    public static final String PULSE_DEVICE_TYPE = "pulse_device_type";
    public static final String WEIGHT = "weight";
    public static final String HEIGHT = "height";
    public static final String DEFAULT_HEIGHT = "default_height";
    public static final String GLUECOSE = "gluecose";
    public static final String HEMOGLOBIN = "hb";
    public static final String BG_TEST_TYPE = "bg test type";
    public static final String BG_RANDOM_TEST = "random";
    public static final String BG_FASTING_TEST = "fasting";
    public static final String BG_POST_PRANDIAL_TEST = "post prandial";

    //Consult doc QB Details

    public static final String QB_ID = "qb_id";
    public static final String QB_LOGIN = "qb_login";
    public static final String QB_PASSWORD = "qb_password";

    public static final String LOGIN_PASSWORD_SAVED = "login_password_saved";

    public static final String APP_ID = "80293";
    public static final String AUTH_KEY = "tPWasabEppO8jpT";
    public static final String AUTH_SECRET = "xVHuJFqghs3ESsB";
    public static final String ACCOUNT_KEY = "K5vdM8zVB2R2MsGfT3NH";
    public static final Integer OPPONENT_ID = 102784978;
    public static final Integer QB_USER_ID = 103859820;
    public static final String LOGIN_CRED = "sample";
    public static final String LOGIN_PASSWORD = "sample123";







    public static final String RETRY_CTA = "RETRY";
    public static final String CAPTURE_IMAGE = "CAPTURE IMAGE";




    //Data sent to attendant and doc consult app

    public static final String SEND_KIOSK_TOKEN = "send_kioks_token";
    public static final String SEND_QB_ID = "send_qb_id";
    public static final String SEND_QB_LOGIN = "send_qb_login";
    public static final String SEND_QB_PASSWORD = "send_qb_password";
    public static final String SEND_PROFILE_ID = "send_profile_id";


    //Printer Settings
    public static final String IS_WIFI_PRINTER = "wifi_printer";
    public static final String IS_RASPBERRY_PI = "raspberry_pi";

    public static final String APP_VERSION = "APP VERSION: ";








    public static final String READING_DEVICE = "RD";
    public static final String READING_MANUAL = "RM";

    public static final String id = "id";
    public static final String SQL_BOOLEAN_IS_CHECKED_IN = "sql_is_checked_in";
    public static final String SQL_DB_NAME = "yolohealth";
    public static final String SQL_BOOLEAN_TABLE_NAME = "shared_booleans";
    public static final String PROVIDER_NAME = "com.example.yolohealth_attendant_app.storage.YolohealthContentProvider";

}
