package com.ehealthkiosk.kiosk.ui.activities.reportlist;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.healthreports.Report;
import com.ehealthkiosk.kiosk.model.healthreports.ReportsParam;
import com.ehealthkiosk.kiosk.ui.activities.AudioPlayerActivity;
import com.ehealthkiosk.kiosk.ui.activities.BaseActivity;
import com.ehealthkiosk.kiosk.ui.activities.PDFViewActivity;
import com.ehealthkiosk.kiosk.ui.activities.TestTypesActivity;
import com.ehealthkiosk.kiosk.ui.adapters.ReportsListAdapter;
import com.ehealthkiosk.kiosk.ui.interfaces.OnItemClickListener;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.widgets.EmptyView.ProgressRelativeLayout;
import com.ehealthkiosk.kiosk.widgets.EndlessRecyclerViewScrollListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ReportsListActivity extends BaseActivity implements HealthReportsView, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.top_bar)
    Toolbar toolbar;
    @BindView(R.id.reports_list_recyclerView)
    RecyclerView reportsListRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.progress_empty)
    ProgressRelativeLayout progressEmpty;
    @BindString(R.string.retry)
    String strRetry;
    private ReportsListAdapter mAdapter;
    private GridLayoutManager gridLayoutManager;

    private List<Report> reportList;
    private HealthReportsPresenter reportsPresenter;
    private boolean isPlaying = false;

    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    MediaPlayer mp;

    int limit = 1000;
    int offset = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_list);
        ButterKnife.bind(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mp = new MediaPlayer();
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarTitle.setText("Reports");
        init();
    }

    private void init(){
        reportsPresenter = new HealthReportsPresenterImpl(this);

        setupSwipeRefreshLayout();
        setupRecyclerView();

        offset = 0;
        getReportList(offset,Constants.ONCREATE);
    }

    private void getReportList(int offset, String from){

        ReportsParam reportsParam = new ReportsParam();
        reportsParam.setLimit(limit);
        reportsParam.setOffset(offset);
        reportsPresenter.getHealthReports(reportsParam,from);
    }

    private void setupSwipeRefreshLayout(){
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.white));
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(this);

    }

    private void setupRecyclerView(){
        reportList = new ArrayList<>();
        mAdapter = new ReportsListAdapter(this,reportList);
        gridLayoutManager = new GridLayoutManager(this,4);
        reportsListRecyclerView.setLayoutManager(gridLayoutManager);
        reportsListRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position, View view) {

                Integer id = reportList.get(position).getId();
                String pdfPath = reportList.get(position).getPdf();
//                Bundle bundle = new Bundle();
//                bundle.putInt(Constants.REPORT_ID,id);
//                bundle.putString(Constants.PDF_PATH,pdfPath);

                if(reportList.get(position).getType().equals("STETHOSCOPE")){
//                    if(isPlaying){
//                        try {
//                            mp.stop();
//                        }catch (Exception e){
//
//                        }
//                        isPlaying = false;
//                    }else {
//                        mp = new MediaPlayer();
//                        Common_Utils.playAudio(pdfPath, mp);
//                        isPlaying = true;
//                    }
                    MediaPlayer mpTemp = new MediaPlayer();
                    String desc = reportList.get(position).getTime() + ", " + reportList.get(position).getDate();
                    String ts = "";
                    try {
                        mpTemp.setDataSource(reportList.get(position).getPdf());
                        mpTemp.prepare();
                        Log.d("pranav", ""+mpTemp.getDuration());
                        ts = desc +  " (" + Common_Utils.getTimeString(mpTemp.getDuration())+")";
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent i = new Intent(ReportsListActivity.this, AudioPlayerActivity.class);
                    i.putExtra(AudioPlayerActivity.AUDIO_URL, reportList.get(position).getPdf());
                    i.putExtra(AudioPlayerActivity.AUDIO_TITLE, ts);
                    i.putExtra(AudioPlayerActivity.AUDIO_DESC, reportList.get(position).getDescription() );
                    startActivity(i);
                }else {
                    mAdapter.stopPlaying();
                    Intent i = new Intent(ReportsListActivity.this, PDFViewActivity.class);
                    i.putExtra(Constants.REPORT_DATA, id);
                    i.putExtra("from", "report_list");
                    i.putExtra(Constants.PDF_PATH, pdfPath);
                    startActivity(i);
                }
            }
        });

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                getReportList(offset,Constants.ONLOADMORE);

            }
        };
        // Adds the scroll listener to RecyclerView
        reportsListRecyclerView.addOnScrollListener(scrollListener);
    }

    @Override
    public void onRefresh() {
        offset = 0;
        getReportList(offset, Constants.ONREFRESH);
    }


    @Override
    public void showError(String msg) {
        swipeRefreshLayout.setRefreshing(false);
        Common_Utils.hideProgress();
        Common_Utils.showToast(msg);
        progressEmpty.showError(R.drawable.ic_home_illustration, msg, "", strRetry, retryClickListener, getVisibleView());
    }

    private View.OnClickListener retryClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            offset = 0;
            getReportList(offset, Constants.ONCREATE);
        }
    };

    @Override
    public void showNetworkError(String msg) {
        progressEmpty.showError(R.drawable.ic_home_illustration, msg, "", strRetry, retryClickListener, getVisibleView());
        swipeRefreshLayout.setRefreshing(false);
        Common_Utils.showToast(msg);
    }

    @Override
    public void showProgress() {
        progressEmpty.showLoading();
    }

    @Override
    public void getHealthReports(List<Report> reportList, int offset, String from) {
        swipeRefreshLayout.setRefreshing(false);


        if (this.reportList != null && !this.reportList.isEmpty() && !from.equalsIgnoreCase(Constants.ONLOADMORE)) {
            this.reportList.clear();
        }

        if (reportList.size() > 0){
            progressEmpty.showContent();
        } else if (!from.equalsIgnoreCase(Constants.ONLOADMORE)){
            progressEmpty.showEmpty(R.drawable.ic_home_illustration, "No Reports","");
        }

        for(Report r: reportList){
            r.setPlaying(false);
        }


        this.reportList.addAll(reportList);
        mAdapter.notifyDataSetChanged();
        this.offset = offset + limit;
    }

    public void playAudio(int j){
        for(int i=0; i<this.reportList.size(); i++ ){
            this.reportList.get(i).setPlaying(j == i);
        }
        mAdapter.notifyDataSetChanged();
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

    @Override
    public void onBackPressed() {
        if(mAdapter != null){
            mAdapter.stopPlaying();
        }
        Intent i = new Intent(this, TestTypesActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mAdapter != null){
            mAdapter.stopPlaying();
        }
        Intent i = new Intent(this, TestTypesActivity.class);
        startActivity(i);
        finish();
        return super.onOptionsItemSelected(item);
    }
}
