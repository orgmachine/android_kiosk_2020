package com.ehealthkiosk.kiosk.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.ui.activities.reportlist.ReportsListActivity;
import com.ehealthkiosk.kiosk.utils.Common_Utils;

import java.io.IOException;

public class AudioPlayerActivity extends AppCompatActivity {

    public static final String AUDIO_URL = "audio_url";
    public static final String AUDIO_TITLE = "audio_title";
    public static final String AUDIO_DESC = "audio_desc";

    Toolbar toolbar;
    TextView toolbarTitle;
    TextView time, description;
    String url;
    Button play;
    MediaPlayer mp;
    SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_player);

        toolbar = findViewById(R.id.top_bar);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("Stethoscope Test");
        time = findViewById(R.id.time);
        description = findViewById(R.id.description);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        url = getIntent().getStringExtra(AUDIO_URL);
        time.setText(getIntent().getStringExtra(AUDIO_TITLE));
        description.setText(getIntent().getStringExtra(AUDIO_DESC));

        play = findViewById(R.id.play);
        seekBar = findViewById(R.id.seekbar);
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
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp!=null && mp.isPlaying()){
                    mp.pause();
                    play.setText("Play");
                } else {
                    playAudio();
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mp!=null)mp.stop();
        Intent intent = new Intent(this, ReportsListActivity.class);
        startActivity(intent);
        finish();
        return;

    }


    private void playAudio(){
        if(mp == null){
            try {
                mp = new MediaPlayer();

                mp.setDataSource(url);
                mp.prepare();
                mp.setLooping(false);

                seekBar.setProgress(0);
                seekBar.setMax(mp.getDuration());
            }catch (IOException e) {
                Log.e("pranav", "prepare() failed" + e.toString());
            }
        }

        mp.start();

            play.setText("Pause");
            seekBar.setVisibility(View.VISIBLE);

            Handler mSeekbarUpdateHandler = new Handler();

            Runnable mUpdateSeekbar = new Runnable() {
                @Override
                public void run() {
                    if(seekBar != null) {
                        seekBar.setProgress(mp.getCurrentPosition());
                        mSeekbarUpdateHandler.postDelayed(this, 50);
                    }

                }
            };

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer m) {
                    mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
//                    seekBar.setVisibility(View.INVISIBLE);
                    seekBar.setProgress(0);
                    mp = null;
                    play.setText("Play");
                }
            });


            mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);



    }
}