package com.bignerdranch.newsreader.ui.headlines;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bignerdranch.newsreader.R;
import com.bignerdranch.newsreader.ui.CustomTabLayout;


public class HeadlinesFragment extends Fragment {

    private SubHeadlineViewModel mHeadlinesViewModel;
    private HeadlinesCollectionPagerAdapter mViewPagerAdapter;
    private ViewPager mPager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_headlines, container, false);


        mPager = root.findViewById(R.id.content_pager);
        CustomTabLayout tabs = root.findViewById(R.id.tabs);

        mViewPagerAdapter = new HeadlinesCollectionPagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mViewPagerAdapter);
        tabs.setupWithViewPager(mPager);


        return root;
    }

    public class HeadlinesCollectionPagerAdapter extends FragmentPagerAdapter{

        private String[] headlineCategories = getResources().getStringArray(R.array.headlines);

        public HeadlinesCollectionPagerAdapter(FragmentManager fm){ super(fm);  }

        @Override
        public Fragment getItem(int position) {
            return SubHeadlineFragment.newInstance(headlineCategories[position]);
        }

        @Override
        public int getCount() {
            return headlineCategories.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            String headline = headlineCategories[position].split(" ")[0];
            return headline;
        }
    }

}