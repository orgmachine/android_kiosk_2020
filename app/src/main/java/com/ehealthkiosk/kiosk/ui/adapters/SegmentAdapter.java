package com.ehealthkiosk.kiosk.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.FaqList;
import com.ehealthkiosk.kiosk.ui.activities.FaqListActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SegmentAdapter extends BaseAdapter {

    private Context context;
    private List<FaqList> faqList;
    private ArrayList<String> segmentList;
    private List<String> expandableListTitle;
    private HashMap<String, List<String>> answersList;

    public SegmentAdapter(Context context, ArrayList<String> segmentList, List<FaqList> faqList) {

        this.context = context;
        this.segmentList = segmentList;
        this.faqList = faqList;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }
    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public int getCount() {
        return segmentList.size();
    }

    @Override
    public Object getItem(int position) {
        return segmentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.segment_item_list, null, true);

            holder.tvProduct = (TextView) convertView.findViewById(R.id.segmentList);

            convertView.setTag(holder);
        }else {
            // the getTag returns the viewHolder object set as a tag to the view
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvProduct.setText(segmentList.get(position));
        holder.tvProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> questions = new ArrayList<>();
                if(segmentList.get(position) == faqList.get(position).getSeqment()){
                    questions.add(faqList.get(position).getItems().get(position).getQuestion());
                    answersList = new HashMap<String, List<String>>();
                    answersList.put(faqList.get(position).getItems().get(position).getQuestion(),
                            faqList.get(position).getItems().get(position).getAnswers());

                }

                Intent i = new Intent(context, FaqListActivity.class);
                i.putStringArrayListExtra("questions", questions);
                i.putExtra("answers", answersList);
                context.startActivity(i);
            }
        });
        return convertView;
    }

    private class ViewHolder {

        protected TextView tvProduct;

    }

}
