package com.ehealthkiosk.kiosk.ui.fragments.settings;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.api.RestInterface;
import com.ehealthkiosk.kiosk.model.FaqData;
import com.ehealthkiosk.kiosk.model.FaqList;
import com.ehealthkiosk.kiosk.model.FaqResponse;
import com.ehealthkiosk.kiosk.model.QnAList;
import com.ehealthkiosk.kiosk.ui.adapters.FaqListAdapter;
import com.ehealthkiosk.kiosk.ui.adapters.SegmentAdapter;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.webrtc.ContextUtils.getApplicationContext;

public class FAQsSettingsFragment  extends Fragment {

    ListView listView;
    SegmentAdapter segmentAdapter;

    List<String> expandableListTitle;
    HashMap<String, List<String>> expandableListDetail;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_faqs, container, false);
        listView = view.findViewById(R.id.listView);
        callFaqAPI();

        return view;
    }


    private void callFaqAPI() {

        Common_Utils.showProgress(getActivity());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RestInterface service = retrofit.create(RestInterface.class);

        Call<FaqResponse> call = service.getFaqs();
        call.enqueue(new Callback<FaqResponse>() {
            @Override
            public void onResponse(Call<FaqResponse> call, Response<FaqResponse> response) {
                Common_Utils.hideProgress();
                if(response.body().getStatus().getResult() == 1) {
                    FaqResponse faqResponse = response.body();
                    FaqData faqData = faqResponse.getData();
                    List<FaqList> faqList = faqData.getFaqs();
                    ArrayList<String> segmentlist = new ArrayList<String>();


                    for (int i = 0; i < faqList.size(); i++) {
                        segmentlist.add(faqList.get(i).getSeqment());
                    }
                    segmentAdapter = new SegmentAdapter(getActivity(), segmentlist, faqData.getFaqs());
                    listView.setAdapter(segmentAdapter);

                }else{
                    Toast.makeText(getActivity(), response.body().getStatus().getMessage(), Toast.LENGTH_SHORT).show();
                }

        }

            @Override
            public void onFailure(Call<FaqResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Api failed to load", Toast.LENGTH_SHORT).show();
                Common_Utils.hideProgress();
            }
        });

    }
}



