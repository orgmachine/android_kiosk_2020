package com.ehealthkiosk.kiosk.ui.adapters;

import android.content.Context;
import android.graphics.Color;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.ehealthkiosk.kiosk.R;

import q.rorbin.verticaltablayout.adapter.TabAdapter;
import q.rorbin.verticaltablayout.widget.ITabView;
import q.rorbin.verticaltablayout.widget.TabView;

public class VerticalTabPagerAdapter extends TabPagerAdapter implements TabAdapter {

    Context mContext;
    public VerticalTabPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public ITabView.TabBadge getBadge(int position) {
        return null;
    }

    @Override
    public ITabView.TabIcon getIcon(int position) {
        return null;
    }

    @Override
    public ITabView.TabTitle getTitle(int position) {
        return new TabView.TabTitle.Builder()
                .setContent(mFragmentTitleList.get(position))
                .setTextColor(Color.WHITE, ContextCompat.getColor(mContext,R.color.textColorDarkSecondary))
                .build();
    }

    @Override
    public int getBackground(int position) {
        return -1;
    }
}
