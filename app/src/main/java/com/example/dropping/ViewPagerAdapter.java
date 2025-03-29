package com.example.dropping;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(FragmentActivity activity) {
        super(activity);
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @Override
    public Fragment createFragment(int position) {
        if (position == 0) return new LocationsFragment();
        else if (position == 1) return new ContactsFragment();
        throw new IllegalStateException("Invalid position");
    }
}