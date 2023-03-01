package com.ehealthkiosk.kiosk.ui.activities.consult;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.commonresponse.Base;
import com.ehealthkiosk.kiosk.model.consult.AppointmentDetail;
import com.ehealthkiosk.kiosk.model.consult.requests.GetAppointmentDetailsAPI;
import com.ehealthkiosk.kiosk.ui.activities.TestTypesActivity;
import com.ehealthkiosk.kiosk.ui.fragments.consult.CameraFragment;
import com.ehealthkiosk.kiosk.ui.fragments.consult.ChatFragment;
import com.ehealthkiosk.kiosk.ui.fragments.consult.ConsultationsListFragment;
import com.ehealthkiosk.kiosk.utils.CameraParent;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.google.android.material.tabs.TabLayout;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.BaseSession;
import com.quickblox.videochat.webrtc.QBMediaStreamManager;
import com.quickblox.videochat.webrtc.QBRTCAudioTrack;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientAudioTracksCallback;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientVideoTracksCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSessionEventsCallback;
import com.quickblox.videochat.webrtc.view.QBRTCSurfaceView;
import com.quickblox.videochat.webrtc.view.QBRTCVideoTrack;

import org.w3c.dom.Text;
import org.webrtc.RendererCommon;
import org.webrtc.SurfaceViewRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements CameraParent {
    public static final String EXTRA_APPOINTMENT_ID = "appointment_id";
    private ProgressDialog dialog;

    boolean pressedTwice = false;

    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);
    String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView title;
    @BindView(R.id.frame)
    FrameLayout frame;
    @BindView(R.id.frame2)
    FrameLayout frame2;

    @BindView(R.id.local_view)
    QBRTCSurfaceView localView;
    @BindView(R.id.ll2)
    LinearLayout ll2;
    @BindView(R.id.container)
    LinearLayout container;
    @BindView(R.id.remote_view_container)
    RelativeLayout remoteViewContainer;

    @BindView(R.id.remote_view)
    QBRTCSurfaceView remoteView;
    @BindView(R.id.video_icon)
    ImageView videoIcon;
    @BindView(R.id.mute1_icon)
    ImageView volumeIcon;
    @BindView(R.id.mute2_icon)
    ImageView micIcon;

    int appointmentId = 52;
    AppointmentDetail appointment;
    QBChatService chatService;
    QBRTCClient rtcClient;
    QBRTCSession callSession;
    QBRTCAudioTrack localAudio, remoteAudio;

    boolean fullscreen = false;
    boolean videoEnabled = true;
    boolean isAudioMuted = false;

    ChatFragment chatFragment;

    void setFragment(Fragment f, int frameId){
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(frameId, f);
        t.commit();
    }


    void setFragment(Fragment f){
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.frame, f);
        t.commit();
    }

    void fetchDetails(){
        Log.d("pranav", "appointment id - " + appointmentId);
        Call<Base<AppointmentDetail>> call = RestClient.getClient().getAppointmentDetails(
                token, kioskId, new GetAppointmentDetailsAPI(appointmentId)
        );
        call.enqueue(new Callback<Base<AppointmentDetail>>() {
            @Override
            public void onResponse(Call<Base<AppointmentDetail>> call, Response<Base<AppointmentDetail>> response) {
                appointment = response.body().getData();
                title.setText("Appointment with " + appointment.getDoctor().getName());
                prepareForCall();
                chatFragment = new ChatFragment(
                        appointment.getPatient(),
                        appointment.getDialogId(),
                        appointment.getDoctor().getFirstName(),
                        appointment.getId(),
                        false
                );

                setFragment(chatFragment);
            }

            @Override
            public void onFailure(Call<Base<AppointmentDetail>> call, Throwable t) {

            }
        });
    }

    @OnClick(R.id.expand)
    void toggleFullScreen() {
        if(fullscreen){
            container.setPadding(40, 40, 40, 40);
            ll2.setVisibility(View.VISIBLE);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) remoteViewContainer.getLayoutParams();
            lp.setMargins(17, 0,0, 0);
            remoteViewContainer.setLayoutParams(lp);
        }else{
            container.setPadding(0, 0, 0, 0);
            ll2.setVisibility(View.GONE);
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) remoteViewContainer.getLayoutParams();
            lp.setMargins(0, 0,0, 0);
            remoteViewContainer.setLayoutParams(lp);
        }
        fullscreen = !fullscreen;
    }

    private void prepareForCall() {
        final QBUser user = new QBUser();
        user.setLogin(appointment.getPatient().getQbLogin());
        user.setPassword(appointment.getPatient().getQbPassword());

        QBUsers.signIn(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                qbUser.setPassword(appointment.getPatient().getQbPassword());
                setupChatService(qbUser);
            }

            @Override
            public void onError(QBResponseException e) {
                Log.d("pranav", e.toString());
            }
        });
    }

    @OnClick(R.id.mic_toggle)
    public void toggleMic(){
        if(callSession == null)return;
        QBMediaStreamManager qbMediaStreamManager = callSession.getMediaStreamManager();
        if(qbMediaStreamManager == null)return;
        qbMediaStreamManager.setAudioEnabled(!qbMediaStreamManager.isAudioEnabled());
        if(qbMediaStreamManager.isAudioEnabled()){
            micIcon.setColorFilter(Color.parseColor("#FFFFFF"));
            micIcon.setImageDrawable(getDrawable(R.drawable.voice));
        }else{
            micIcon.setColorFilter(Color.parseColor("#000000"));
            micIcon.setImageDrawable(getDrawable(R.drawable.voice_off));
        }

    }


    public void MuteAudio(){
        if(remoteAudio != null){
            remoteAudio.setEnabled(false);
        }

    }

    public void UnMuteAudio(){
        if(remoteAudio != null){
            remoteAudio.setEnabled(true);
        }

    }

    @OnClick(R.id.audio_toggle)
    public void toggleAudio(){
        if(callSession == null)return;

        if(isAudioMuted){
            UnMuteAudio();
            isAudioMuted = false;
        }else{
            MuteAudio();
            isAudioMuted = true;
        }


        if(!isAudioMuted){
            volumeIcon.setColorFilter(Color.parseColor("#FFFFFF"));
            volumeIcon.setImageDrawable(getDrawable(R.drawable.volume));
        }else{
            volumeIcon.setColorFilter(Color.parseColor("#000000"));
            volumeIcon.setImageDrawable(getDrawable(R.drawable.volume_off));
        }
    }

    private void cleanUp(){
        try {
            endCall();

            Log.d("pranav", "ended call");
        }catch (Exception e){

        }
        Log.d("pranav", "cleaning up");
        boolean isLoggedIn = chatService.isLoggedIn();
        if(isLoggedIn){
            chatService.logout(new QBEntityCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid, Bundle bundle) {
                    chatService.destroy();
                }

                @Override
                public void onError(QBResponseException e) {

                }
            });
        }
        QBRTCClient.getInstance(this).destroy();


    }

    @OnClick(R.id.video_toggle)
    public void toggleVideo(){
        if(callSession == null)return;
        QBMediaStreamManager qbMediaStreamManager = callSession.getMediaStreamManager();
        if(qbMediaStreamManager == null)return;
        qbMediaStreamManager.setVideoEnabled(!qbMediaStreamManager.isVideoEnabled());
        Log.d("pranav", "" + qbMediaStreamManager.isVideoEnabled());
        videoEnabled = !videoEnabled;
        if(videoEnabled){
            videoIcon.setColorFilter(Color.parseColor("#000000"));
            videoIcon.setImageDrawable(getDrawable(R.drawable.video));
        }else{
            videoIcon.setColorFilter(Color.parseColor("#FFFFFF"));
            videoIcon.setImageDrawable(getDrawable(R.drawable.video_off));
        }
    }

    @OnClick(R.id.end_call)
    public void endCallBtnClick(){
        if(callSession == null)return;

        AlertDialog alertDialog;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Are you sure you want to end the call?");
        alertDialogBuilder.setPositiveButton("End Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Map<String, String> userInfo = new HashMap<>();
                callSession.hangUp(userInfo);

            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    public void endCall(){
        if(callSession == null)return;

        Map<String, String> userInfo = new HashMap<>();
        callSession.hangUp(userInfo);

    }

    private void fillVideoView(QBRTCSurfaceView videoView, QBRTCVideoTrack videoTrack) {
        // To remove renderer if Video Track already has another one
        videoTrack.cleanUp();

        if (videoView != null) {
            videoTrack.addRenderer(videoView);
            updateVideoView(videoView);
        }
    }

    private void updateVideoView(SurfaceViewRenderer videoView) {
        RendererCommon.ScalingType scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;
        videoView.setScalingType(scalingType);
        videoView.setMirror(false);
        videoView.requestLayout();
    }

    private void setupChatService(QBUser user) {
        QBChatService.ConfigurationBuilder configurationBuilder = new QBChatService.ConfigurationBuilder();
        configurationBuilder.setSocketTimeout(300);
        configurationBuilder.setUseTls(true);
        configurationBuilder.setKeepAlive(true);
        configurationBuilder.setAutojoinEnabled(false);
        configurationBuilder.setAutoMarkDelivered(true);
        configurationBuilder.setReconnectionAllowed(true);
        configurationBuilder.setAllowListenNetwork(true);
        configurationBuilder.setPort(5223);

        QBChatService.setConfigurationBuilder(configurationBuilder);

        chatService = QBChatService.getInstance();
        chatService.login(user, new QBEntityCallback() {
            @Override
            public void onSuccess(Object o, Bundle bundle) {
                setupRTCClient();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.d("pranav", e.toString());
                if(e.getMessage().equals("You have already logged in chat")){
                    setupRTCClient();
                }
            }
        });

    }

    private void setupRTCClient() {
        Log.d("pranav", "setting up client");
        rtcClient = QBRTCClient.getInstance(ChatActivity.this);

        QBChatService.getInstance().getVideoChatWebRTCSignalingManager().addSignalingManagerListener(new QBVideoChatSignalingManagerListener() {
            @Override
            public void signalingCreated(QBSignaling qbSignaling, boolean createdLocally) {
                Log.d("pranav", "inside signal created");

                if (!createdLocally) {
                    rtcClient.addSignaling(qbSignaling);
                }
            }
        });
        rtcClient.prepareToProcessCalls();
        makeCall();
    }

    private void makeCall() {
        List<Integer> opponents = new ArrayList<>();
        opponents.add(appointment.getDoctor().getQbId());

        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("appointment_id", "" + appointment.getId());

        QBRTCTypes.QBConferenceType qbConferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;

        callSession = QBRTCClient.getInstance(this).createNewSessionWithOpponents(opponents, qbConferenceType);
        callSession.addVideoTrackCallbacksListener(new QBRTCClientVideoTracksCallbacks() {
            @Override
            public void onLocalVideoTrackReceive(BaseSession baseSession, QBRTCVideoTrack qbrtcVideoTrack) {
                fillVideoView(localView, qbrtcVideoTrack);
            }

            @Override
            public void onRemoteVideoTrackReceive(BaseSession baseSession, QBRTCVideoTrack qbrtcVideoTrack, Integer integer) {
                dialog.dismiss();
                fillVideoView(remoteView, qbrtcVideoTrack);
            }
        });
        callSession.addAudioTrackCallbacksListener(new QBRTCClientAudioTracksCallback() {
            @Override
            public void onLocalAudioTrackReceive(BaseSession baseSession, QBRTCAudioTrack qbrtcAudioTrack) {
                localAudio = qbrtcAudioTrack;
            }

            @Override
            public void onRemoteAudioTrackReceive(BaseSession baseSession, QBRTCAudioTrack qbrtcAudioTrack, Integer integer) {
                remoteAudio = qbrtcAudioTrack;
            }
        });

        callSession.addEventsCallback(new QBRTCSessionEventsCallback() {
            @Override
            public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {
                Log.d("pranav", "user is not answering");

                Common_Utils.showToast("Doctor is not answering");
                cleanUp();
                goBack();
            }

            @Override
            public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {

                Log.d("pranav", "user rejected the call");
                Common_Utils.showToast("Doctor rejected the call");
                cleanUp();
                goBack();
            }

            @Override
            public void onCallAcceptByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {

                Log.d("pranav", "user accepted the call");
            }

            @Override
            public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {

                Log.d("pranav", "user hung up");
                Common_Utils.showToast("The Doctor hung up the call");

            }

            @Override
            public void onSessionClosed(QBRTCSession qbrtcSession) {

                Log.d("pranav", "session closed");
                Log.d("pranav", qbrtcSession.getSessionID() + " " + callSession.getSessionID());
                Common_Utils.showToast("Call ended");
                cleanUp();
                goBack();
            }
        });

        Log.d("pranav", "Starting a call");
        callSession.startCall(userInfo);
    }

    public void goBack(){
        Intent i = new Intent(this, ConsultationsActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);

        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);

        MenuItem item1 = menu.findItem(R.id.action_logout);
        item1.setVisible(false);

        MenuItem item2 = menu.findItem(R.id.action_profile);
        item2.setVisible(false);

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){

        AlertDialog alertDialog;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Are you sure you want to end the call?");
        alertDialogBuilder.setPositiveButton("End Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cleanUp();

                if(item.getItemId() == android.R.id.home) {

                    goBack();
                }
                else goToHome();
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        return true;
    }

    private void goToHome() {
        Intent i = new Intent(this, TestTypesActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Are you sure you want to end the call?");
        alertDialogBuilder.setPositiveButton("End Call", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cleanUp();
                goBack();
            }
        });

        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#FFFFFF"));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#FFFFFF"));
            }
        });
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(getIntent().hasExtra(EXTRA_APPOINTMENT_ID)){
            appointmentId = getIntent().getIntExtra(EXTRA_APPOINTMENT_ID, 98);
        }


        fetchDetails();

        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Calling Doctor...");
        try {
            dialog.show();
        }catch (Exception e){

        }
        

    }

    @Override
    public void sendImageChat(String imageUrl, String imgType, String description) {
        closeCamera();
        Log.d("pranav", "Will send the chat " + imageUrl);
        chatFragment.sendAttachment(imageUrl, description, "photo");
    }

    @Override
    public void showCamera() {
        frame.setVisibility(View.INVISIBLE);
        frame2.setVisibility(View.VISIBLE);
        setFragment(new CameraFragment(), R.id.frame2);
    }

    @Override
    public void closeCamera() {
        frame.setVisibility(View.VISIBLE);
        frame2.setVisibility(View.INVISIBLE);
    }
}
