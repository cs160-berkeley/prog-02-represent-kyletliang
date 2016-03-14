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
        Bundle b = new Bundle();
        if (row == 0 && col == 0){
            fragment = new InfoCardFragment();
        }
        else if (row == 1) {
            fragment = new VoteCardFragment();
            b.putString("OBAMA", page.obamaVote);
            b.putString("ROMNEY", page.romneyVote);
        }
        else {
            fragment = new RepCardFragment();
            b.putString("NAME", page.rep);
            b.putString("PARTY", page.party);
        }
        b.putString("COUNTY", page.county);
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
        return mContext.getResources().getDrawable(PAGES[row][column].mImageResource, null);
    }


}
