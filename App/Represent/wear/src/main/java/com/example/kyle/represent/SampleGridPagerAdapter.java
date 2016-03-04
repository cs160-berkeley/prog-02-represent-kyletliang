package com.example.kyle.represent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridPagerAdapter;
import android.view.Gravity;

import java.util.List;

/**
 * Created by Kyle on 3/1/2016.
 */
public class SampleGridPagerAdapter extends FragmentGridPagerAdapter {

    private List mRows;
    private static final float MAXIMUM_CARD_EXPANSION_FACTOR = 3.0f;
    private Context mContext;
    private RepActivity.Page[][] PAGES;

    public SampleGridPagerAdapter(Context ctx, RepActivity.Page[][] data, FragmentManager fm) {
        super(fm);
        mContext = ctx;
        PAGES = data;

    }


    // Obtain the UI fragment at the specified position
    @Override
    public Fragment getFragment(int row, int col) {
        RepActivity.Page page = PAGES[row][col];
        CardFragment fragment;
        if (row == 1)
            fragment = new VoteCardFragment();
        else
            fragment = new RepCardFragment();
        Bundle b = new Bundle();
        b.putString("NAME", page.getRep());
        b.putString("PARTY", page.getParty());
        b.putString("ZIP", page.getZipCode());
        fragment.setArguments(b);
        fragment.setCardGravity(Gravity.BOTTOM);
        fragment.setExpansionEnabled(true);
        fragment.setExpansionDirection(CardFragment.EXPAND_DOWN);
        fragment.setExpansionFactor(MAXIMUM_CARD_EXPANSION_FACTOR);
        return fragment;
    }
    @Override
    public int getRowCount() {
        return PAGES.length;
    }

    @Override
    public int getColumnCount(int rowNum) {
        return PAGES[rowNum].length;
    }

    @Override
    public Drawable getBackgroundForPage(int row, int column) {
        return mContext.getResources().getDrawable(PAGES[row][column].getmImageResource(), null);
    }


}
