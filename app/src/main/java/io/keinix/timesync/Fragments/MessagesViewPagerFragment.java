package io.keinix.timesync.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
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

public class MessagesViewPagerFragment extends Fragment {

    @BindView(R.id.viewPager) ViewPager mViewPager;
    @BindView(R.id.tabLayout) TabLayout mTabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_messages_viewpager, container, false);
        ButterKnife.bind(this, view);
        setUpViewPagerFragments();
        setUpViewpagerTabs();
        return view;
    }

    private void setUpViewPagerFragments() {
        MessagesFragment notificationsFragment = new MessagesFragment();
        Bundle notificationBundle = new Bundle();
        notificationBundle.putString(MessagesFragment.KEY_MESSAGE_TYPE, MessagesFragment.VALUE_MESSAGE_TYPE_NOTIFICATION);
        notificationsFragment.setArguments(notificationBundle);

        MessagesFragment messagesFragment = new MessagesFragment();
        Bundle messagesBundle = new Bundle();
        messagesBundle.putString(MessagesFragment.KEY_MESSAGE_TYPE, MessagesFragment.VALUE_MESSAGE_TYPE_MESSAGE);
        messagesFragment.setArguments(messagesBundle);

        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 1: return messagesFragment;
                    default: return notificationsFragment;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
    }

    private void setUpViewpagerTabs() {
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.getTabAt(0).setText("Notifications");
        mTabLayout.getTabAt(1).setText("Messages");
    }
}
