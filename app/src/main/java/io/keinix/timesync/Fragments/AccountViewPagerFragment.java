package io.keinix.timesync.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.keinix.timesync.R;

public class AccountViewPagerFragment extends Fragment {

    @BindView(R.id.viewPager) ViewPager mViewPager;
    @BindView(R.id.tabLayout) TabLayout mTabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages_viewpager, container, false);
        ButterKnife.bind(this, view);
        setUpViewPager();
        setUpTabs();
        return view;
    }

    public void setUpViewPager() {
        Bundle args = new Bundle();
        FeedFragment upVotesFragment = new FeedFragment();
        FeedFragment savedFragment = new FeedFragment();
        FeedFragment postsFragment = new FeedFragment();

        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        args.putString(FeedFragment.KEY_FEED_TYPE, FeedFragment.VALUE_FEED_TYPE_UPVOTED);
                        upVotesFragment.setArguments(args);
                        return upVotesFragment;
                    case 1:
                        args.putString(FeedFragment.KEY_FEED_TYPE, FeedFragment.VALUE_FEED_TYPE_SAVED);
                        savedFragment.setArguments(args);
                        return savedFragment;
                    default:
                        args.putString(FeedFragment.KEY_FEED_TYPE, FeedFragment.VALUE_FEED_TYPE_POSTS);
                        postsFragment.setArguments(args);
                        return postsFragment;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        });
    }

    public void setUpTabs() {
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("UpVoted");
        mTabLayout.getTabAt(1).setText("Saved");
        mTabLayout.getTabAt(2).setText("Posts");
    }
}
