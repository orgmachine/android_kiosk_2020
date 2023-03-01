package com.ehealthkiosk.kiosk.ui.fragments.consult;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ayu.recorder.AudioPermissionHelper;
import com.ayu.recorder.AudioRecorder;
import com.ayu.recorder.AudioRecorderInterface;
import com.ayu.recorder.USBDeviceScanner;
import com.ayu.recorder.WaveSaver;
import com.bumptech.glide.Glide;
import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.commonresponse.Base;
import com.ehealthkiosk.kiosk.model.consult.Patient;
import com.ehealthkiosk.kiosk.model.consult.Url;
import com.ehealthkiosk.kiosk.ui.activities.PDFViewActivity;
import com.ehealthkiosk.kiosk.ui.activities.consult.ChatActivity;
import com.ehealthkiosk.kiosk.utils.CameraParent;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment implements AudioRecorderInterface.OnSampleReceivedListener {

    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);
    String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());
    EditText input;
    RecyclerView rvChats;
    Button choose;
    TextView empty;
    ProgressBar progressBar;
    ImageButton sendChat;
    LinearLayout chatLL, inputLL;

    Activity parent;


    boolean recording = false;
    static String filePath = "";
    WaveSaver waveSaver;
    private ArrayList<Byte> dataToStore = new ArrayList();


    Patient patient;
    String doctorName;
    String dialogId;
    int appointment_id;
    Dialog aDialog;

    ChatAdapter adapter;

    List<QBChatMessage> chats = new ArrayList<>();
    QBChatDialog dialog;
    QBChatService chatService;

    private boolean isCompleted;
    boolean fromDetail = false;



    void loginToQB(){
        final QBUser user = new QBUser();
        user.setLogin(patient.getQbLogin());
        user.setPassword(patient.getQbPassword());

        QBUsers.signIn(user).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                qbUser.setPassword(patient.getQbPassword());
                signInToChat(qbUser);
            }

            @Override
            public void onError(QBResponseException e) {
                Log.d("pranav", "login: " + e.toString());
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    String getChatDate(long timestamp){
        SimpleDateFormat outformat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
        return outformat.format(new Date(timestamp * 1000L));
    }

    List<String> getDates(){
        List<String> dates = new ArrayList<>();
        for(int i=0; i<chats.size(); i++){
            String d  = getChatDate(chats.get(i).getDateSent());
            if(!dates.contains(d)){
                dates.add(d);
            }else{
                dates.add("");
            }
        }
        return dates;
    }

    void refreshChatList(){
        adapter = new ChatAdapter(chats, getActivity(), getDates());
        rvChats.setAdapter(adapter);
        if(chats.isEmpty()){
            rvChats.setVisibility(View.INVISIBLE);
            empty.setVisibility(View.VISIBLE);
        }else{
            rvChats.setVisibility(View.VISIBLE);
            empty.setVisibility(View.INVISIBLE);
        }
        rvChats.scrollToPosition(chats.size() - 1);

        if(!isCompleted)
            inputLL.setVisibility(View.VISIBLE);


    }


    void signInToChat(QBUser user){
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
                fetchDialog();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.d("pranav", e.toString());
                if(e.getMessage().equals("You have already logged in chat")){
                    fetchDialog();
                }
            }
        });
    }

    void fetchDialog(){
        QBRequestGetBuilder requestBuilder = new QBRequestGetBuilder();
        requestBuilder.setLimit(50);

        QBRestChatService.getChatDialogs(null, requestBuilder)
                .performAsync(new QBEntityCallback<ArrayList<QBChatDialog>>() {
            @Override
            public void onSuccess(ArrayList<QBChatDialog> result, Bundle params) {
                Log.d("pranav", "dialogs: " + result.toString());
                for(QBChatDialog d: result){
                    if(d.getDialogId().equals(dialogId)) dialog = d;
                }
                if(dialog != null) {
                    dialog.initForChat(QBChatService.getInstance());
                    loadChats();
                }else{
                    Common_Utils.showToast("Error connecting to Quickblox");
                    try {
                        ((ChatActivity) getActivity()).goBack();
                    } catch (Exception e){

                    }
                }
            }

            @Override
            public void onError(QBResponseException responseException) {
                Log.d("pranav", "Dialog Error: " + responseException.toString());
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void loadChats() {
        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(100);

        setChatListener();
        QBRestChatService.getDialogMessages(dialog, messageGetBuilder)
            .performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                    Log.d("pranav", qbChatMessages.toString());
                    progressBar.setVisibility(View.INVISIBLE);
                    chats = qbChatMessages;
                    refreshChatList();
                }

                @Override
                public void onError(QBResponseException e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Log.d("pranav", "Dialog Messages: " + e.toString());
                }
            });
    }

    private void sendChat(String message){
        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody(message);
        chatMessage.setSaveToHistory(true);

        dialog.sendMessage(chatMessage, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                Log.d("pranav", "message sent");


                chatMessage.setSenderId(Integer.valueOf(patient.getQbId()));
                chatMessage.setDateSent((new Date().getTime())/1000);
                chats.add(chatMessage);
                refreshChatList();

            }

            @Override
            public void onError(QBResponseException e) {

                Log.d("pranav", "message error:" + e);
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    public void sendAttachment(String url, String title, String type){
        Log.d("pranav", "will send message" + url);
        QBChatMessage chatMessage = new QBChatMessage();
        chatMessage.setBody(title);
        QBAttachment attachment = new QBAttachment(type);
        attachment.setUrl(url);
        attachment.setId(url);
        chatMessage.addAttachment(attachment);
        chatMessage.setSaveToHistory(true);

        dialog.sendMessage(chatMessage, new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                Log.d("pranav", "message sent");
                chatMessage.setSenderId(Integer.valueOf(patient.getQbId()));
                chatMessage.setDateSent((new Date().getTime())/1000);
                chats.add(chatMessage);
                refreshChatList();

            }

            @Override
            public void onError(QBResponseException e) {

                Log.d("pranav", "message error:" + e);
                progressBar.setVisibility(View.INVISIBLE);

            }
        });
    }

    private void setChatListener(){
        QBIncomingMessagesManager incomingMessagesManager = chatService.getIncomingMessagesManager();

        incomingMessagesManager.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String dialogID, QBChatMessage qbChatMessage, Integer senderID) {
                if(dialogID.equals(dialog.getDialogId())){
                    chats.add(qbChatMessage);
                    refreshChatList();
                }
            }

            @Override
            public void processError(String dialogID, QBChatException e, QBChatMessage qbChatMessage, Integer senderID) {
                Log.d("pranav", "Error Chat: " + e.getMessage() + " | " + qbChatMessage.getBody());
            }
        });
    }

    void openDeviceSelectionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose a device");
        String[] devices = { "Stethoscope"};
        builder.setItems(devices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: showAudioScope();// camel

                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showAudioScope() {
        Log.d("pranav", "audio");
        USBDeviceScanner.fillOutDevices(parent, usbDevice -> {
            Log.d("pranav", "device found " + usbDevice.getDeviceName());

            aDialog = new MaterialAlertDialogBuilder(parent)
                    .setView(R.layout.device_found_dialogue)
                    .create();

            getActivity().runOnUiThread(aDialog::show);

            aDialog.findViewById(R.id.btn_on_device_found).setOnClickListener((view1)->{
                Log.d("pranav", "pressed button to start recording");
                if (recording){
                    ((Button) view1).setText("Start Recording");
                } else {

                    ((Button) view1).setText("Stop Recording");
                }
                recordAudio();

            });

        });
    }

    private void recordAudio() {
        if(recording){
            recording = false;
            save(dataToStore);
        } else {

            if (AudioPermissionHelper.askPermissionForRecording(parent)) {
                AudioRecorder recorder = AudioRecorder.getInstance(parent);
                recorder.addOnSampleReceivedListener(this);
                recorder.startRecording();
            }

            waveSaver = new WaveSaver();
            recording = true;
        }
    }


    private void save(ArrayList<Byte> data){


        byte[] dataArrByte = new byte[data.size()];
        for (int i = 0; i < dataArrByte.length; i++){
            dataArrByte[i] = data.get(i);
        }


        short[] audio = WaveSaver.bytesToShort(dataArrByte);


        File folder = new File(getActivity().getExternalFilesDir(null).getAbsolutePath().toString(), "demo_data");

        if(! folder.exists()){
            folder.mkdir();
        }

        File wav = new File(folder, "filename" + System.currentTimeMillis() + ".wav");
        Log.d("pranav", "file" + wav.toString());
        Log.d("pranav", "file" + wav.getAbsolutePath());


        if(! wav.exists()){
            try {
                wav.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        try {
            waveSaver.saveFile(audio, wav.getPath());
            filePath = wav.getPath();
            callUploadAudioAPI(filePath);
            aDialog.dismiss();
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Common_Utils.showToast("File Saved!");
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void showDermaScope(){
        ((CameraParent) getActivity()).showCamera();
    }

    private void showOtoScope(){
        ((CameraParent) getActivity()).showCamera();
    }

    private void showChat(){
        chatLL.setVisibility(View.VISIBLE);

    }

    public ChatFragment(Patient patient, String dialogId, String doctorName, int aid,  boolean isCompleted) {
        this.patient = patient;
        this.dialogId = dialogId;
        this.doctorName = doctorName;
        this.isCompleted = isCompleted;
        this.appointment_id = aid;
    }

    public ChatFragment(Patient patient, String dialogId,String doctorName, int aid,  boolean isCompleted, boolean fromDetail) {
        this.patient = patient;
        this.dialogId = dialogId;
        this.doctorName = doctorName;
        this.fromDetail = fromDetail;
        this.appointment_id = aid;
        this.isCompleted = isCompleted;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loginToQB();
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        input = v.findViewById(R.id.chat_input);
        sendChat = v.findViewById(R.id.send_chat);
        progressBar = v.findViewById(R.id.chat_progress);
        rvChats = v.findViewById(R.id.chat_list);
        empty = v.findViewById(R.id.empty);
        rvChats.setLayoutManager(new LinearLayoutManager(getActivity()));
        sendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendChat(input.getText().toString());
                input.setText("");
            }
        });

        choose = v.findViewById(R.id.choose_device);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeviceSelectionDialog();
            }
        });

        chatLL = v.findViewById(R.id.ll_chat);
        inputLL = v.findViewById(R.id.ll_input);

        inputLL.setVisibility(View.INVISIBLE);
//        inputLL.setVisibility(View.VISIBLE);
        return v;
    }


    private void callUploadAudioAPI(String path) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Common_Utils.showProgress(getActivity());
            }
        });
        HashMap<String, RequestBody> comment = new HashMap<>();
        comment.put("appointment_id", RestClient.createPartFromString(String.valueOf(appointment_id)));

        File file = new File(path);
        MultipartBody.Part body = RestClient.prepareFilePart("audio", file);

        Call<Base<Url>> call = RestClient.getClient().uploadAudio(
                token, kioskId, comment, body
        );

        call.enqueue(new Callback<Base<Url>>() {
            @Override
            public void onResponse(Call<Base<Url>> call, Response<Base<Url>> response) {
                Log.d("pranav", response.body().toString());

                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Common_Utils.hideProgress();
                        showChat();
                    }
                });
                Common_Utils.showToast("audio uploaded " + response.body().getData().getUrl());
                sendAttachment(response.body().getData().getUrl(), "Audio", "audio");
            }

            @Override
            public void onFailure(Call<Base<Url>> call, Throwable t) {
                parent.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Common_Utils.hideProgress();
                        showChat();
                    }
                });
                Log.d("pranav", t.toString());
                Common_Utils.showToast("Server Error - " + t.getMessage());
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Activity){
            parent=(Activity) context;
        }

    }

    @Override
    public void onSampleReceived(byte[] sample) {
        for (byte byteSingle : sample){
            dataToStore.add(byteSingle);
        }

    }

    class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
        private List<QBChatMessage> messages;
        private Context mContext;
        private List<String> dates;

        class MyViewHolder extends RecyclerView.ViewHolder {
            public View view;
            public TextView dateText;
            public ImageView attachmentImage, pdfIcon;
            public RelativeLayout bubble;
            public TextView chatText, sender, time;
            public LinearLayout senderLL, containerLL, pdfLL;

            public MyViewHolder(View v) {
                super(v);
                view = v;
            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public ChatAdapter(List<QBChatMessage> data, Context c, List<String> dates) {
            messages = data;
            mContext = c;
            this.dates = dates;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat, parent, false);
            MyViewHolder vh = new MyViewHolder(v);
            vh.chatText = v.findViewById(R.id.chat_text);
            vh.sender = v.findViewById(R.id.chat_sender);
            vh.attachmentImage = v.findViewById(R.id.attachment_image);
            vh.pdfIcon = v.findViewById(R.id.pdf_image);
            vh.bubble = v.findViewById(R.id.chat_bubble);
            vh.time = v.findViewById(R.id.chat_time);
            vh.senderLL = v.findViewById(R.id.senderll);
            vh.containerLL = v.findViewById(R.id.ll_container);
            vh.pdfLL = v.findViewById(R.id.ll_pdf);
            vh.dateText = v.findViewById(R.id.datetext);
            return vh;
        }

        String getDateFromTimestamp(long timestamp){
            SimpleDateFormat outformat = new SimpleDateFormat("hh:mm a");
            return outformat.format(new Date(timestamp * 1000L));
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, int position) {
            QBChatMessage message = messages.get(position);
            String date = dates.get(position);
            if(!date.equals("")){
                holder.dateText.setText(date);
                holder.dateText.setVisibility(View.VISIBLE);
            }else{

                holder.dateText.setVisibility(View.INVISIBLE);
            }
            boolean isLeft = message.getSenderId().toString().equals(patient.getQbId());
            holder.chatText.setText(message.getBody());
            holder.time.setText(getDateFromTimestamp(message.getDateSent()));
            if(message.getAttachments() != null && message.getAttachments().size() > 0){
                ArrayList<QBAttachment> attachments = new ArrayList<>(message.getAttachments());
                QBAttachment attachment = attachments.get(0);
                final String url = attachments.get(0).getId().split("\\?")[0];
                final boolean is_pdf = attachment.getType() != null && attachment.getType().equals("pdf");
                if(is_pdf){
                    holder.pdfIcon.setVisibility(View.VISIBLE);
                    holder.attachmentImage.setVisibility(View.GONE);
                }else{
                    if(attachment.getType().equals("photo")) {
                        Glide.with(mContext)
                                .load(url.split("\\?")[0])
                                .centerCrop()
                                .into(holder.attachmentImage);
                        Log.d("pranav", url);
                        holder.attachmentImage.setVisibility(View.VISIBLE);

                        holder.pdfIcon.setVisibility(View.GONE);
                    }
                }
                String fileName = url.substring( url.lastIndexOf('/')+1, url.length() );


                if(is_pdf)
                holder.chatText.setText("Prescription");
                else holder.chatText.setText(" ");


                holder.bubble.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(attachment.getType().equals("audio")){
                            MediaPlayer mp = new MediaPlayer();
                            try {
                                mp.setDataSource(url);
                                mp.prepare();
                                mp.setLooping(false);
                                mp.start();
                            } catch (IOException e) {
                                Log.e("pranav", "prepare() failed");
                            }
                        }else{
                            if(is_pdf){
                                Intent i = new Intent(getActivity(), PDFViewActivity.class);
                                i.putExtra(Constants.PDF_PATH, url);
                                i.putExtra(PDFViewActivity.PDF_TYPE, "Prescription");
                                startActivity(i);
                            }else {
                                Common_Utils.downloadFile(getActivity(), url);
                            }
                        }
                    }
                });
            }else{
                holder.attachmentImage.setVisibility(View.GONE);
                holder.pdfIcon.setVisibility(View.GONE);
            }
            if(isLeft){
                holder.sender.setText("You ");
                holder.senderLL.setGravity(Gravity.END);
                holder.containerLL.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                holder.bubble.setBackground(getActivity().getDrawable(R.drawable.bg_chat_left));
            }else{
                holder.sender.setText(" " + doctorName);
                holder.senderLL.setGravity(Gravity.START);
                holder.containerLL.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                holder.bubble.setBackground(getActivity().getDrawable(R.drawable.bg_chat_right));
            }
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }
    }
}
