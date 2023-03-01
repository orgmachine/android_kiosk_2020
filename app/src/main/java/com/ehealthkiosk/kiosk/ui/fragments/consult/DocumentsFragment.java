package com.ehealthkiosk.kiosk.ui.fragments.consult;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.api.RestClient;
import com.ehealthkiosk.kiosk.model.commonresponse.Base;
import com.ehealthkiosk.kiosk.model.consult.Document;
import com.ehealthkiosk.kiosk.model.consult.Report;
import com.ehealthkiosk.kiosk.model.consult.requests.DocumentsAPI;
import com.ehealthkiosk.kiosk.model.consult.requests.ReportsAPI;
import com.ehealthkiosk.kiosk.ui.activities.PDFViewActivity;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.utils.SharedPrefUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentsFragment extends Fragment {

    String kioskId = DeviceIdPrefHelper.getkioskId(HealthKioskApp.getAppContext(), Constants.KIOSK_ID);
    String token = SharedPrefUtils.getToken(HealthKioskApp.getAppContext());

    List<Document> reports = new ArrayList<>();

    GridView gridView;
    ReportsAdapter adapter;
    int appointment_id;

    void fetchDocuments(){
        Call<Base<List<Document>>> call = RestClient.getClient().getDocuments(
                token, kioskId, new DocumentsAPI(appointment_id)
        );
        call.enqueue(new Callback<Base<List<Document>>>() {
            @Override
            public void onResponse(Call<Base<List<Document>>> call, Response<Base<List<Document>>> response) {

                reports = response.body().getData();
                adapter = new ReportsAdapter(getActivity(), reports);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String url = reports.get(position).getUrl();
                        Intent i = new Intent(getActivity(), PDFViewActivity.class);
                        i.putExtra(Constants.PDF_PATH, url);
                        i.putExtra(PDFViewActivity.PDF_TYPE, "Prescription");
                        startActivity(i);
                    }
                });
            }

            @Override
            public void onFailure(Call<Base<List<Document>>> call, Throwable t) {

            }
        });
    }

    public DocumentsFragment(int appointment_id) {
        this.appointment_id = appointment_id;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_documents, container, false);
        gridView = v.findViewById(R.id.grid_view);
        fetchDocuments();
        return v;
    }

    class ReportsAdapter extends BaseAdapter {

        private final Context mContext;
        private List<Document> reports;

        public ReportsAdapter(Context context, List<Document> reports) {
            this.mContext = context;
            this.reports = reports;
        }

        @Override
        public int getCount() {
            return reports.size();
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final Document report = reports.get(position);

            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                convertView = layoutInflater.inflate(R.layout.item_document, null);
            }

            final TextView name = convertView.findViewById(R.id.doc_name);
            RelativeLayout docIcon = convertView.findViewById(R.id.doc);
            TextView docText = convertView.findViewById(R.id.icon_text);
            TextView date = convertView.findViewById(R.id.date);
            TextView time = convertView.findViewById(R.id.time);
            String url = report.getUrl();
            String filename = url.split("/")[url.split("/").length - 1];

            name.setText(filename);
            if(report.getType().equals("image")){
                docIcon.setBackground(getActivity().getDrawable(R.drawable.blue_doc));
                docText.setVisibility(View.INVISIBLE);
                date.setVisibility(View.INVISIBLE);
                name.setText(report.getTitle().replace("['", "").replace("']", "").replace("AUTOSCOPE", "OTOSCOPE"));
                time.setVisibility(View.INVISIBLE);
            }
            if(report.getType().equals("report")){
                docIcon.setBackground(getActivity().getDrawable(R.drawable.blue_doc));
                docText.setVisibility(View.VISIBLE);
                date.setVisibility(View.INVISIBLE);
                time.setVisibility(View.INVISIBLE);
                docText.setText("PDF");
            }
            if(report.getType().equals("prescription")){
                docIcon.setBackgroundColor(Color.parseColor("#FFFFFF"));
                docText.setVisibility(View.INVISIBLE);
                date.setVisibility(View.VISIBLE);
                time.setVisibility(View.VISIBLE);
            }

            return convertView;
        }



    }


}
