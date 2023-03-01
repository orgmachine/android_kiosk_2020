package com.ehealthkiosk.kiosk.ui.adapters;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.TestTypes;

import java.util.ArrayList;


public class TestTypesAdapter extends RecyclerView.Adapter<TestTypesAdapter.ViewHolder> {

    ArrayList<TestTypes> mValues = new ArrayList<TestTypes>();;
    Context mContext;
    protected ItemListener mListener;

    public TestTypesAdapter(Context context, ArrayList values, ItemListener itemListener) {

        mValues = values;
        mContext = context;
        mListener = itemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView textView;
        public ImageView imageView;
        public RelativeLayout relativeLayout;
        public View seperatorView;
        TestTypes item;

        public ViewHolder(View v) {

            super(v);

            v.setOnClickListener(this);
            textView = (TextView) v.findViewById(R.id.textView);
            imageView = (ImageView) v.findViewById(R.id.imageView);
            relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout);
            seperatorView = (View) v.findViewById(R.id.seperatorView);

        }

        public void setData(TestTypes item) {
            this.item = item;

            textView.setText(item.text);
            imageView.setImageResource(item.drawable);
            seperatorView.setBackgroundColor(Color.parseColor(item.color));
        }


        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(item, getAdapterPosition());
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_test_type, parent, false);

        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(ViewHolder Vholder, int position) {
        Vholder.setData(mValues.get(position));

    }

    @Override
    public int getItemCount() {

        return mValues.size();
    }

    public interface ItemListener {
        void onItemClick(TestTypes item, int position);
    }
}
