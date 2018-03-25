package io.keinix.timesync.Fragments;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.keinix.timesync.R;

public class ViewPagerFragment extends Fragment {

    private int mHighlightColor;
    private int mColorWhite;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);
        ViewPager viewPager = view.findViewById(R.id.viewPager);
        final FeedFragment feedFragment = new FeedFragment();
        //final MessagesFragment messagesFragment = new MessagesFragment();
        final MessagesViewPagerFragment messagesViewPagerFragment= new MessagesViewPagerFragment();
        final AccountFragment accountFragment = new AccountFragment();
        mHighlightColor = ContextCompat.getColor(getActivity(), R.color.colorAccent);
        mColorWhite = ContextCompat.getColor(getActivity(), R.color.white);
        Drawable drawableTab0 = ContextCompat.getDrawable(getActivity(), R.drawable.ic_format_align_center_white_24dp);
        Drawable drawableTab1 = ContextCompat.getDrawable(getActivity(), R.drawable.ic_message_white_24dp);
        Drawable drawableTab2 = ContextCompat.getDrawable(getActivity(), R.drawable.ic_people_white_24dp);

        //TODO:might need to change fragmentManager b/c this si not a fragment within a fragment
        viewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0: return feedFragment;
                    case 1: return messagesViewPagerFragment;
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
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_format_align_center_white_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_message_white_24dp);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_people_white_24dp);

        return view;
    }
}
