package ca.polymtl.inf8405.lab2.Managers;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import ca.polymtl.inf8405.lab2.R;

/**
 * A FragmentPagerAdapter that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private Context _ctx;
    private Fragment[] _fgms;

    public SectionsPagerAdapter(FragmentManager fm, Context ctx, Fragment[] fgms) {
        super(fm);
        _ctx = ctx;
        _fgms = fgms;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a PlaceholderFragment (defined as a static inner class below).
        return _fgms[position];
    }

    @Override
    public int getCount() {
        // Show 5 total tabs.
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return _ctx.getString(R.string.title_profile);
            case 1:
                return _ctx.getString(R.string.title_map);
            case 2:
                return _ctx.getString(R.string.title_status);
        }
        return null;
    }
}