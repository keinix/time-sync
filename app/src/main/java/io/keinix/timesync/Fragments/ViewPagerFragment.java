package io.keinix.timesync.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import io.keinix.timesync.R;

public class ViewPagerFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        final FeedFragment feedFragment = new FeedFragment();
        final MessagesFragment messagesFragment = new MessagesFragment();
        final AccountFragment accountFragment = new AccountFragment();

        //TODO:might need to change fragmentManager b/c this si not a fragment within a fragment
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0: return feedFragment;
                    case 1: return messagesFragment;
                    default: return accountFragment;
                }
            }


            @Override
            public int getCount() {
                return 3;
            }
        });

        //TODO:change icon color when a tab is selected
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(R.mipmap.ic_format_align_center_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.mipmap.ic_message_black_24dp);
        tabLayout.getTabAt(2).setIcon(R.mipmap.ic_people_black_24dp);

        return view;
    }
}
