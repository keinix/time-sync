package io.keinix.timesync.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import io.keinix.timesync.R;

public class MessagesViewPagerFragment extends Fragment {

    @BindView(R.id.viewPager) ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_viewpager, container, false);

        MessagesFragment notificationsFragment = new MessagesFragment();
        Bundle notificationBundle = new Bundle().putString();
        MessagesFragment messagesFragment = new MessagesFragment();

        mViewPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 1:
                        break;
                    default:

                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        });
    }
}
