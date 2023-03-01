package com.ehealthkiosk.kiosk.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.healthreports.Report;
import com.ehealthkiosk.kiosk.ui.interfaces.OnItemClickListener;
import com.ehealthkiosk.kiosk.utils.Common_Utils;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Report> reportItemList;
    private Context context;
    private OnItemClickListener mItemClickListener;
    private int leftDp,topDp;
    boolean isPlaying;
    MediaPlayer mp;
    Handler mSeekbarUpdateHandler;
    Runnable mUpdateSeekbar;

    public ReportsListAdapter(Context context,List<Report> vitalData){
        this.context = context;
        this.reportItemList = vitalData;
        leftDp = (int)Common_Utils.px2dp(context,30);
        topDp = (int) Common_Utils.px2dp(context,10);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.item_report, viewGroup, false);
        return new ViewHolder(view);

    }

    public void stopPlaying(){
        if(mp != null) {

            mp.stop();
            mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);

            isPlaying = false;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        Report vitalDatum = reportItemList.get(i);
        if(viewHolder instanceof ViewHolder){
            ViewHolder viewHolder1 = (ViewHolder) viewHolder;

            String name= vitalDatum.getType();
            String date = vitalDatum.getDate();
            String time = vitalDatum.getTime();
            String colorCode = vitalDatum.getColor();

            viewHolder1.txtName.setText(name);
            viewHolder1.txtDate.setText(date);
            viewHolder1.txtTime.setText(time);

            viewHolder1.linearLayoutType.setBackgroundColor(Color.parseColor(colorCode));

            String report_type = vitalDatum.getType();
            switch (report_type){
                case "STETHOSCOPE":
                    ((ViewHolder) viewHolder).image.setImageDrawable(context.getDrawable(R.drawable.stethoscope));
                    break;
                case "ECG":
                    ((ViewHolder) viewHolder).image.setImageDrawable(context.getDrawable(R.drawable.heart));
                    break;
                case "Basic Health":
                    ((ViewHolder) viewHolder).image.setImageDrawable(context.getDrawable(R.drawable.basic));
                    break;
                case "Wellness Test":
                    ((ViewHolder) viewHolder).image.setImageDrawable(context.getDrawable(R.drawable.heart));
                    break;
                case "OTOSCOPE":
                    ((ViewHolder) viewHolder).image.setImageDrawable(context.getDrawable(R.drawable.otoscope));
                    break;
                case "DERMASCOPE":
                    ((ViewHolder) viewHolder).image.setImageDrawable(context.getDrawable(R.drawable.dermo));
                    break;
            }

            if(vitalDatum.getType().equals("STETHOSCOPE")){


                viewHolder1.seekBar.setVisibility(View.INVISIBLE);
                viewHolder1.play.setVisibility(View.INVISIBLE);
                String desc = vitalDatum.getDescription();
                MediaPlayer mpTemp = new MediaPlayer();
                try {
                    mpTemp.setDataSource(vitalDatum.getPdf());
                    mpTemp.prepare();
                    Log.d("pranav", ""+mpTemp.getDuration());
                    desc = vitalDatum.getDescription() +  " (" + Common_Utils.getTimeString(mpTemp.getDuration())+")";
                } catch (IOException e) {
                    e.printStackTrace();
                }
                viewHolder1.description.setText(desc);
/*
                viewHolder1.play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(isPlaying) {
                            try{
                                mp.stop();
                                viewHolder1.seekBar.setVisibility(View.INVISIBLE);
                                viewHolder1.play.setText("play");
                                viewHolder1.seekBar.setProgress(0);
                                mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
                            }catch (Exception e){

                            }
                            isPlaying = false;

                        }else{
                            mp = new MediaPlayer();
                            Common_Utils.playAudio(vitalDatum.getPdf(), viewHolder1.seekBar, viewHolder1.play, mp);
                            mSeekbarUpdateHandler = new Handler();

                            mUpdateSeekbar = new Runnable() {
                                @Override
                                public void run() {
                                    viewHolder1.seekBar.setProgress(mp.getCurrentPosition());
                                    mSeekbarUpdateHandler.postDelayed(this, 50);

                                }
                            };


                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
                                    try{
                                        mp.stop();
                                        viewHolder1.seekBar.setVisibility(View.INVISIBLE);
                                        viewHolder1.play.setText("play");
                                        viewHolder1.seekBar.setProgress(0);
                                        mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
                                    }catch (Exception e){

                                    }
                                    isPlaying = false;
                                }
                            });



                            mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 0);
                            isPlaying = true;
                        }

                    }
                });

            */
            }else{

                stopPlaying();
                viewHolder1.seekBar.setVisibility(View.INVISIBLE);
                viewHolder1.play.setVisibility(View.INVISIBLE);
                viewHolder1.description.setText("");

            }

        }


    }

    @Override
    public int getItemCount() {
        return reportItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.txt_name)
        public TextView txtName;
        @BindView(R.id.image)
        public ImageView image;
        @BindView(R.id.txt_date)
        public TextView txtDate;
        @BindView(R.id.btn_play)
        public Button play;
        @BindView(R.id.seekbar)
        public SeekBar seekBar;
        @BindView(R.id.txt_time)
        public TextView txtTime;
        @BindView(R.id.txt_description)
        public TextView description;
        @BindView(R.id.lv_type)
        LinearLayout linearLayoutType;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mItemClickListener!=null){
                        mItemClickListener.onItemClick(getAdapterPosition(),itemView);
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(
            final OnItemClickListener mitemClickListener) {
        this.mItemClickListener = mitemClickListener;
    }
}

