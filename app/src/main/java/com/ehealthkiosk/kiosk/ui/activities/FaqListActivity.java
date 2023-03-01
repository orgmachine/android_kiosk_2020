package com.ehealthkiosk.kiosk.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.ui.adapters.FaqListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FaqListActivity extends AppCompatActivity {
    ExpandableListView expandableListView;
    FaqListAdapter faqListAdapter;
    ArrayList<String> questionsList;
    HashMap<String, List<String>> answersList;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq_list);

        toolbar = findViewById(R.id.top_bar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        showSystemUI();



        expandableListView = findViewById(R.id.expandableListView);

        questionsList = getIntent().getStringArrayListExtra("questions");
        answersList = (HashMap<String, List<String>>) getIntent().getSerializableExtra("answers");


        faqListAdapter = new FaqListAdapter(this, questionsList, answersList);
        expandableListView.setAdapter(faqListAdapter);

        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
            }
        });

        expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {

                return false;
            }
        });


    }

    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    @Override
    public void onBackPressed() {
      super.onBackPressed();
    }
}
