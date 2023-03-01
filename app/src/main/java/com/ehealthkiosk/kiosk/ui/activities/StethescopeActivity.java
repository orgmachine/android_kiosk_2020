package com.ehealthkiosk.kiosk.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ayu.recorder.AudioPermissionHelper;
import com.ayu.recorder.AudioRecorder;
import com.ayu.recorder.AudioRecorderInterface;
import com.ayu.recorder.USBDeviceScanner;
import com.ayu.recorder.WaveSaver;
import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.commonresponse.Base;
import com.ehealthkiosk.kiosk.model.consult.Url;
import com.ehealthkiosk.kiosk.ui.activities.selectprofile.SelectProfileActivity;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StethescopeActivity extends AppCompatActivity implements AudioRecorderInterface.OnSampleReceivedListener {

    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);
    String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());

    private Button start, confirm, retry, play;

    MediaPlayer mp;

    boolean recording = false;
    boolean deviceFound = true;
    static String filePath = "";
    WaveSaver waveSaver;
    Dialog aDialog;
    EditText description;
    LinearLayout resultRL;
    Toolbar toolbar;
    TextView toolbarTitle, playTime;
    AudioRecorder recorder;
    SeekBar seekBar;

    int profile_id;

    private ArrayList<Byte> dataToStore = new ArrayList();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stethescope);

        toolbar = findViewById(R.id.top_bar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Stethoscope Test");

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        profile_id = Integer.valueOf(SharedPrefUtils.getProfileId(this));
        start = findViewById(R.id.start);
        confirm = findViewById(R.id.confirm);
        retry = findViewById(R.id.retry);
        resultRL = findViewById(R.id.rl_result);
        play = findViewById(R.id.play);
        description = findViewById(R.id.description);
        seekBar = findViewById(R.id.seekbar);
        playTime = findViewById(R.id.play_time);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mp!=null && mp.isPlaying())
                    mp.seekTo(seekBar.getProgress());

            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAudioScope();
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start.setVisibility(View.VISIBLE);
                retry.setVisibility(View.GONE);
                confirm.setVisibility(View.GONE);
                playTime.setText("");
                resultRL.setVisibility(View.INVISIBLE);
                dataToStore = new ArrayList();
                if(mp != null && mp.isPlaying()){
                    mp.stop();
                }
                seekBar.setProgress(0);

                play.setEnabled(true);

//                Intent mStartActivity = new Intent(StethescopeActivity.this, StethescopeActivity.class);
//                int mPendingIntentId = 123456;
//                PendingIntent mPendingIntent = PendingIntent.getActivity(StethescopeActivity.this, mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//                AlarmManager mgr = (AlarmManager)(StethescopeActivity.this).getSystemService(Context.ALARM_SERVICE);
//                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//                System.exit(0);
                //startActivity(getIntent());
                //finish();


            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp != null && mp.isPlaying()){
                    mp.stop();
                }
                callUploadAudioAPI(filePath);
            }
        });
    }

    private void playAudio(){
         mp = new MediaPlayer();
        try {
            mp.setDataSource(filePath);
            mp.prepare();
            mp.setLooping(false);
            mp.start();
            play.setEnabled(false);
            seekBar.setProgress(0);
            seekBar.setMax(mp.getDuration());
            seekBar.setVisibility(View.VISIBLE);
            playTime.setText(Common_Utils.getTimeString(mp.getDuration()));

            Handler mSeekbarUpdateHandler = new Handler();

            Runnable mUpdateSeekbar = new Runnable() {
                @Override
                public void run() {
                    seekBar.setProgress(mp.getCurrentPosition());
                    mSeekbarUpdateHandler.postDelayed(this, 50);

                    playTime.setText(  Common_Utils.getTimeString(mp.getCurrentPosition()) + " / " + Common_Utils.getTimeString(mp.getDuration()));

                }
            };

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
                    play.setEnabled(true);
                }
            });


            mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);


        } catch (IOException e) {
            Log.e("pranav", "prepare() failed" + e.toString());
        }
    }

    private void showAudioScope() {
        Log.d("pranav", "audio");
        deviceFound = false;
        USBDeviceScanner.fillOutDevices(this, usbDevice -> {
            Log.d("pranav", "device found " + usbDevice.getDeviceName());

            aDialog = new MaterialAlertDialogBuilder(this)
                    .setView(R.layout.device_found_dialogue)
                    .setCancelable(false)
                    .create();

            runOnUiThread(aDialog::show);


            aDialog.findViewById(R.id.btn_on_device_found).setOnClickListener((view1)->{
                Log.d("pranav", "pressed button to start recording");
                Log.d("pranav", "recording" + recording);

                if (recording){
                    ((Button) view1).setText("Start Recording");
                } else {

                    ((Button) view1).setText("Stop Recording");
                }
                recordAudio();

            });
            deviceFound = true;

        });
        if(!deviceFound) {
            Common_Utils.showToast("No Device Found! Please connect the device and try again.");
        }
    }


    private void recordAudio() {
        Log.d("pranav", "record audio called");

        if(recording){
            recording = false;
            save(dataToStore);
        } else {
            Log.d("pranav", "asking for permission");

            if (AudioPermissionHelper.askPermissionForRecording(this)) {
                Log.d("pranav", "got permission");

                recorder = AudioRecorder.getInstance(this);
                Log.d("pranav", "got instance");

                recorder.addOnSampleReceivedListener(this);

                Log.d("pranav", "added on sample received listener");

                recorder.startRecording();
                Log.d("pranav", "recording started");

            }
            Log.d("pranav", "created new wavesaver");

            waveSaver = new WaveSaver();
            recording = true;
            Log.d("pranav", "onclick complete");

        }
    }


    private void save(ArrayList<Byte> data){


        byte[] dataArrByte = new byte[data.size()];
        for (int i = 0; i < dataArrByte.length; i++){
            dataArrByte[i] = data.get(i);
        }


        short[] audio = WaveSaver.bytesToShort(dataArrByte);


        File folder = new File(getExternalFilesDir(null).getAbsolutePath().toString(), "demo_data");

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
            showResult();

            aDialog.dismiss();
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Common_Utils.showToast("File Saved!");
//                }
//            });
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void showResult() {
        start.setVisibility(View.GONE);
        retry.setVisibility(View.VISIBLE);
        confirm.setVisibility(View.VISIBLE);
        resultRL.setVisibility(View.VISIBLE);
    }


    private void callUploadAudioAPI(String path) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Common_Utils.showProgress(StethescopeActivity.this);
            }
        });
        HashMap<String, RequestBody> comment = new HashMap<>();
        comment.put("profile_id", RestClient.createPartFromString(String.valueOf(profile_id)));
        comment.put("description", RestClient.createPartFromString(description.getText().toString()));

        File file = new File(path);
        MultipartBody.Part body = RestClient.prepareFilePart("audio", file);

        Call<Base<Url>> call = RestClient.getClient().uploadAudio(
                token, kioskId, comment, body
        );

        call.enqueue(new Callback<Base<Url>>() {
            @Override
            public void onResponse(Call<Base<Url>> call, Response<Base<Url>> response) {
                Log.d("pranav", response.body().toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Common_Utils.hideProgress();
                    }
                });
                Common_Utils.showToast("audio uploaded!");

                goBack();
            }

            @Override
            public void onFailure(Call<Base<Url>> call, Throwable t) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Common_Utils.hideProgress();
                    }
                });
                Log.d("pranav", t.toString());
                Common_Utils.showToast("Unable to send audio, please try again");
            }
        });
    }

    private void goBack() {
        Intent i = new Intent(this, TestTypesActivity.class);
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

        MenuItem item3 = menu.findItem(R.id.action_home);
        item3.setVisible(true);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        goBack();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mp != null && mp.isPlaying()){
            mp.stop();
        }
        goBack();
    }

    @Override
    public void onSampleReceived(byte[] sample) {
        for (byte byteSingle : sample){
            dataToStore.add(byteSingle);
        }

//        Log.i(TAG, Arrays.toString(sample));

    }
}