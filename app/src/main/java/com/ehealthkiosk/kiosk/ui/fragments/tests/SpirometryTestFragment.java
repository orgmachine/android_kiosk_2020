package com.ehealthkiosk.kiosk.ui.fragments.tests;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.MessageEvent;
import com.ehealthkiosk.kiosk.ui.fragments.BaseFragment;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

public class SpirometryTestFragment extends BaseFragment {

    @BindView(R.id.img_instruction)
    ImageView imgInstruction;
    @BindView(R.id.tv_instruction)
    TextView tvInstruction;

    @BindView(R.id.btn_start)
    Button btnStart;
    @BindView(R.id.btn_skip)
    Button btnSkip;



    @BindView(R.id.layout_setup_spiro)
    LinearLayout layoutSetupHeight;

    @BindView(R.id.layout_instruction)
    LinearLayout layoutInstruction;

    @BindView(R.id.layout_btn_height)
    LinearLayout layoutBtnHeight;

    @BindView(R.id.layout_reading_height)
    LinearLayout layoutReadingHeight;

    @BindView(R.id.layout_measuring_height)
    LinearLayout layoutMeasuringHeight;
    @BindView(R.id.btn_continue)
    Button btnContinue;
    @BindView(R.id.btn_skip1)
    Button btnSkip1;

    @BindView(R.id.lineChart)
    LineChart lineChart;


    @Override
    protected int getLayout() {
        return R.layout.fragment_test_spirometry;
    }

    private void init() {

        layoutSetupHeight.setVisibility(View.VISIBLE);
        layoutSetupHeight.postDelayed(new Runnable() {
            public void run() {
                layoutSetupHeight.setVisibility(View.INVISIBLE);
                layoutBtnHeight.setVisibility(View.VISIBLE);
                layoutInstruction.setVisibility(View.VISIBLE);
            }
        }, 3000);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }


    @OnClick({R.id.btn_start, R.id.btn_skip, R.id.btn_continue, R.id.btn_skip1})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                layoutBtnHeight.setVisibility(View.INVISIBLE);
                layoutInstruction.setVisibility(View.INVISIBLE);
                layoutMeasuringHeight.setVisibility(View.VISIBLE);
                layoutMeasuringHeight.postDelayed(new Runnable() {
                    public void run() {
                        layoutMeasuringHeight.setVisibility(View.INVISIBLE);
                        layoutReadingHeight.setVisibility(View.VISIBLE);

                    }
                }, 3000);
                break;

            case R.id.btn_skip1:
            case R.id.btn_skip:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 6));
                break;
            case R.id.btn_continue:
                EventBus.getDefault().post(new MessageEvent(MessageEvent.EVENT_CHANGE_TEST, 6));
                break;

        }
    }


}
