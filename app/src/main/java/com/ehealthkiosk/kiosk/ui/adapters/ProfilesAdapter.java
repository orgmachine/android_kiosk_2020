package com.ehealthkiosk.kiosk.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.ehealthkiosk.kiosk.HealthKioskApp;
import com.ehealthkiosk.kiosk.R;
import com.ehealthkiosk.kiosk.model.profilelist.ProfilesItem;
import com.ehealthkiosk.kiosk.ui.interfaces.OnItemClickListener;
import com.ehealthkiosk.kiosk.utils.Common_Utils;
import com.ehealthkiosk.kiosk.utils.Constants;
import com.ehealthkiosk.kiosk.utils.DeviceIdPrefHelper;
import com.ehealthkiosk.kiosk.widgets.RoundImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfilesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    OnItemClickListener mListener;

    private static final int TYPE_ITEM = 1;
    private static final int TYPE_ADD = 2;
    private static final String TAG = "ProfilesAdapter";


    private List<ProfilesItem> mProfilesList;
    Handler mHandler;

    private final Activity mContext;

    public ProfilesAdapter(Context context, List<ProfilesItem> mProfilesList) {
        this.mProfilesList = mProfilesList;
        mContext = (Activity) context;
        mHandler = new Handler();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM){
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_profile, parent, false);

            return new ProfilesViewHolder(itemView);
        }else{
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_add_profile, parent, false);

            return new AddProfilesViewHolder(itemView);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder h, int position) {

        if (position < mProfilesList.size()){
            if (h instanceof ProfilesViewHolder) {
                ProfilesViewHolder holder = (ProfilesViewHolder) h;
                ProfilesItem userItem = mProfilesList.get(position);
                holder.tvName.setText(userItem.getName());
                String profileImage = userItem.getPhotoUrl();
                Drawable drawable = ContextCompat.getDrawable(HealthKioskApp.context, R.drawable.user_profile);
                Common_Utils.getCircularImageFromServer(holder.imgProfile, profileImage, drawable);

                holder.tvAgeGender.setText(userItem.getAge() +", "+ Common_Utils.toCamelCase(userItem.getGender()));

            }


        }
    }

    public void add(List<ProfilesItem> mList) {
        this.mProfilesList.addAll(mList);
        //notifyDataSetChanged();
        notifyItemRangeInserted(getItemCount(), mList.size());
    }

    public void clearList() {
        this.mProfilesList.clear();
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mListener = mItemClickListener;
    }

    public void setList(List<ProfilesItem> l) {
        mProfilesList = l;
    }

    @Override
    public int getItemCount() {
        return (mProfilesList == null) ? 1 : mProfilesList.size() + 1;
    }

    //    need to override this method
    @Override
    public int getItemViewType(int position) {

        if (position == mProfilesList.size()) {
            return TYPE_ADD;
        }
        return TYPE_ITEM;
    }

    private ProfilesItem getItem(int position) {
        return mProfilesList.get(position);
    }


    public class ProfilesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.cardView)
        CardView cardView;
        @BindView(R.id.img_profile)
        RoundImageView imgProfile;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_age_gender)
        TextView tvAgeGender;

        public ProfilesViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public class AddProfilesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.addCardView)
        CardView addCardView;

        public AddProfilesViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            addCardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition(), v);
        }
    }

}
