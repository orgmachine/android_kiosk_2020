package com.ehealthkiosk.kiosk.api;

import com.ehealthkiosk.kiosk.model.FaqResponse;
import com.ehealthkiosk.kiosk.model.PrintParam;
import com.ehealthkiosk.kiosk.model.SendEmailPojo;
import com.ehealthkiosk.kiosk.model.SendEmailResponse;
import com.ehealthkiosk.kiosk.model.SendSMSPojo;
import com.ehealthkiosk.kiosk.model.SetServerCheckPojo;
import com.ehealthkiosk.kiosk.model.commonresponse.Base;
import com.ehealthkiosk.kiosk.model.commonresponse.CommonResponse;
import com.ehealthkiosk.kiosk.model.commonresponse.Status;
import com.ehealthkiosk.kiosk.model.consult.Appointment;
import com.ehealthkiosk.kiosk.model.consult.AppointmentDetail;
import com.ehealthkiosk.kiosk.model.consult.DoctorList;
import com.ehealthkiosk.kiosk.model.consult.DoctorStatus;
import com.ehealthkiosk.kiosk.model.consult.Document;
import com.ehealthkiosk.kiosk.model.consult.Report;
import com.ehealthkiosk.kiosk.model.consult.SlotList;
import com.ehealthkiosk.kiosk.model.consult.Url;
import com.ehealthkiosk.kiosk.model.consult.requests.CreateAppointmentAPI;
import com.ehealthkiosk.kiosk.model.consult.requests.DoctorListAPI;
import com.ehealthkiosk.kiosk.model.consult.requests.DoctorStatusAPI;
import com.ehealthkiosk.kiosk.model.consult.requests.DocumentsAPI;
import com.ehealthkiosk.kiosk.model.consult.requests.GetAppointmentDetailsAPI;
import com.ehealthkiosk.kiosk.model.consult.requests.GetAppointmentsAPI;
import com.ehealthkiosk.kiosk.model.consult.requests.ReportsAPI;
import com.ehealthkiosk.kiosk.model.consult.requests.SlotsAPI;
import com.ehealthkiosk.kiosk.model.consult.requests.UpdateAppointmentAPI;
import com.ehealthkiosk.kiosk.model.generatereport.GenerateReportParam;
import com.ehealthkiosk.kiosk.model.generatereport.GenerateReportResponse;
import com.ehealthkiosk.kiosk.model.healthreports.ReportsParam;
import com.ehealthkiosk.kiosk.model.healthreports.ReportsResponse;
import com.ehealthkiosk.kiosk.model.login.LoginParam;
import com.ehealthkiosk.kiosk.model.otp.OTPParam;
import com.ehealthkiosk.kiosk.model.otp.OTPResponse;
import com.ehealthkiosk.kiosk.model.profilelist.ProfileListResponse;
import com.ehealthkiosk.kiosk.model.register.RegisterResponse;
import com.ehealthkiosk.kiosk.model.sendDermaImage.SendImageDetails;
import com.ehealthkiosk.kiosk.ui.activities.KioskIdData;
import com.ehealthkiosk.kiosk.ui.activities.SettingsDataResponse;
import com.ehealthkiosk.kiosk.utils.Constants;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;


public interface RestInterface {

    @POST("print/")
    Call<Status> print(@Body PrintParam urlParam);

    @POST("login")
    Call<CommonResponse> login(@Header(Constants.HEADER_TAG) String kioskID,
                               @Body LoginParam otpValueParam);

    @POST("verify_otp")
    Call<OTPResponse> otp(@Header(Constants.HEADER_TAG) String kioskID,
                          @Body OTPParam otpValueParam);

    @Multipart
    @POST("register")
    Call<RegisterResponse> register(@Header(Constants.HEADER_TAG) String kioskID,
                                    @PartMap() Map<String, RequestBody> partMap,
                                    @Part MultipartBody.Part file);

    @Multipart
    @POST("profiles/add")
    Call<RegisterResponse> addProfile(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                      @Header(Constants.HEADER_TAG) String kioskID,
                                      @PartMap() Map<String, RequestBody> partMap,
                                      @Part MultipartBody.Part file);

    @POST("profiles")
    Call<ProfileListResponse> getProfileList(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                             @Header(Constants.HEADER_TAG) String kioskID);

    @POST("reports")
    Call<ReportsResponse> getHealthReportList(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                              @Header(Constants.HEADER_TAG) String kioskID,
                                              @Body ReportsParam reportsParam);

    @POST("reports/submit_basic_checkup")
    Call<GenerateReportResponse> generateReport(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                                @Header(Constants.HEADER_TAG) String kioskID,
                                                @Body GenerateReportParam generateReportParam);

    @POST("reports/email")
    Call<SendEmailResponse> getEmail(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                     @Header(Constants.HEADER_TAG) String kioskID,
                                     @Body SendEmailPojo sendEmailPojo);

    @POST("reports/sms")
    Call<SendEmailResponse> getSms(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                   @Header(Constants.HEADER_TAG) String kioskID,
                                   @Body SendSMSPojo sendSMSPojo);

    @POST("kiosk/settings")
    Call<SettingsDataResponse> getSettings(@Header(Constants.HEADER_TAG) String kioskID,
                                           @Body KioskIdData kioskIdData);


    @Multipart
    @POST("reports/submit_image_report")
    Call<SendImageDetails> getImageData(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                        @Header(Constants.HEADER_TAG) String kioskID,
                                        @PartMap() Map<String, RequestBody> partMap,
                                        @Part MultipartBody.Part file);

    @Multipart
    @POST("reports/submit_multiple_image_report")
    Call<SendImageDetails> uploadImages(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                        @Header(Constants.HEADER_TAG) String kioskID,
                                        @PartMap() Map<String, RequestBody> partMap,
                                        @Part List<MultipartBody.Part> bodies);

    @GET("kiosk/faqs")
    Call<FaqResponse> getFaqs();

    @POST("kiosk/heartbeat")
    Call<Status> getPolling(@Header(Constants.HEADER_TAG) String kioskID);


    @POST("force_update")
    Call<Status> getServerStatus(@Body SetServerCheckPojo setServerCheckPojo);


    @POST("patients/doctors")
    Call<Base<DoctorList>> getDoctors(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                      @Header(Constants.HEADER_TAG) String kioskID,
                                      @Body DoctorListAPI doctorListParam);


    @POST("patients/doctor_slots_grouped")
    Call<Base<SlotList>> getSlots(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                  @Header(Constants.HEADER_TAG) String kioskID,
                                  @Body SlotsAPI slotsParam);

    @POST("patients/create_appointment")
    Call<Base> createAppointment(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                 @Header(Constants.HEADER_TAG) String kioskID,
                                 @Body CreateAppointmentAPI params);

    @POST("patients/update_appointment")
    Call<Base> updateAppointment(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                 @Header(Constants.HEADER_TAG) String kioskID,
                                 @Body UpdateAppointmentAPI params);

    @POST("patients/appointments")
    Call<Base<List<Appointment>>> getAppointments(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                                  @Header(Constants.HEADER_TAG) String kioskID,
                                                  @Body GetAppointmentsAPI params);
    @POST("patients/appointment_details_full")
    Call<Base<AppointmentDetail>> getAppointmentDetails(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                                        @Header(Constants.HEADER_TAG) String kioskID,
                                                        @Body GetAppointmentDetailsAPI params);

    @POST("patients/reports")
    Call<Base<List<Report>>> getReports(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                        @Header(Constants.HEADER_TAG) String kioskID,
                                        @Body ReportsAPI params);
    @POST("patients/documents")
    Call<Base<List<Document>>> getDocuments(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                           @Header(Constants.HEADER_TAG) String kioskID,
                                           @Body DocumentsAPI params);


    @Multipart
    @POST("patients/audio")
    Call<Base<Url>> uploadAudio(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                 @Header(Constants.HEADER_TAG) String kioskID,
                                 @PartMap() Map<String, RequestBody> partMap,
                                 @Part MultipartBody.Part file);

    @POST("patients/doctor_status")
    Call<Base<DoctorStatus>> getDoctorStatus(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                      @Header(Constants.HEADER_TAG) String kioskID,
                                      @Body DoctorStatusAPI statusParam);



    @Multipart
    @POST("patients/upload_ecg")
    Call<Base<Url>> uploadECG(@Header(Constants.AUTHORIZATION_TAG) String accessToken,
                                @Header(Constants.HEADER_TAG) String kioskID,
                                @PartMap() Map<String, RequestBody> partMap,
                                @Part MultipartBody.Part file);
}