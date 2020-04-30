package com.bignerdranch.newsreader.ui.headlines;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bignerdranch.newsreader.NetworkUpdateEvent;
import com.bignerdranch.newsreader.NewsRepository;
import com.bignerdranch.newsreader.R;
import com.bignerdranch.newsreader.ResultsAdapter;
import com.bignerdranch.newsreader.articles.NewsArticlesObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class SubHeadlineFragment extends Fragment {
    public static final String ARG_SECTION = "SECTION";
    public static final String ARG_QUERY ="QUERY";
    public static final String ARG_QUERY_VALUE = "QUERY_VALUE";

    private SubHeadlineViewModel mSubHeadlineViewModel;
    private ResultsAdapter mAdapter;
    private ProgressBar mProgressBar;
    private RecyclerView mSubHeadlineRecyclerView;
    private TextView mUnavailabilityTextView;

    public static SubHeadlineFragment newInstance(String section) {

        String[] values = section.split(" ");

        Bundle args = new Bundle();
        args.putString(ARG_SECTION,values[0]);
        args.putString(ARG_QUERY,values[1]);
        args.putString(ARG_QUERY_VALUE, values[2]);

        SubHeadlineFragment fragment = new SubHeadlineFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_headlines_categories, container, false);

        mProgressBar = v.findViewById(R.id.progressBarHeadline);
        mSubHeadlineRecyclerView = v.findViewById(R.id.recycler_headlines);
        mUnavailabilityTextView = v.findViewById(R.id.unavailability);

        SubHeadlineViewModelFactory factory = new SubHeadlineViewModelFactory(getActivity().getApplication(),
                getArguments().getString(ARG_QUERY), getArguments().getString(ARG_QUERY_VALUE));
        mSubHeadlineViewModel = ViewModelProviders.of(this,factory).get(getArguments().getString(ARG_SECTION), SubHeadlineViewModel.class);

        initViewModel();

        if( !mSubHeadlineViewModel.getInitLoad() && !NewsRepository.isConnected()){
            mUnavailabilityTextView.setVisibility(View.VISIBLE);
            mSubHeadlineRecyclerView.setVisibility(View.INVISIBLE);
        }
        else{
            mUnavailabilityTextView.setVisibility(View.INVISIBLE);
            mSubHeadlineRecyclerView.setVisibility(View.VISIBLE);
        }


        if (mSubHeadlineViewModel.isLoading()) {
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
        }


        if(mAdapter == null){
            mAdapter= new ResultsAdapter(getActivity());
            mSubHeadlineRecyclerView.setAdapter( mAdapter);
            mAdapter.setArticles(mSubHeadlineViewModel.getArticles());
        }
        else {
            mAdapter.setArticles(mSubHeadlineViewModel.getArticles());
        }

        mSubHeadlineRecyclerView.setAdapter(mAdapter);
        mSubHeadlineRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mSubHeadlineRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE &&
                        !mSubHeadlineViewModel.isLoading() && mSubHeadlineViewModel.getCurrentPageNo() <= mSubHeadlineViewModel.getTotalPageNo()) {

                    if(NewsRepository.isConnected() ){
                        mProgressBar.setVisibility(View.VISIBLE);
                        mSubHeadlineViewModel.fetchSearchResults().observe(getActivity(), new Observer<NewsArticlesObject>() {
                            @Override
                            public void onChanged(NewsArticlesObject newsArticlesObject) {
                                if( newsArticlesObject != null)
                                    update(newsArticlesObject);
                                else {
                                    Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT).show();
                                    mSubHeadlineViewModel.setLoading(false);
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(getActivity(),"Internet Unavailable",Toast.LENGTH_SHORT).show();
                    }
                }
                else if( !recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE
                        && !mSubHeadlineViewModel.isLoading()){

                    if( NewsRepository.isConnected() ){
                        mProgressBar.setVisibility(View.VISIBLE);
                        mSubHeadlineViewModel.setCurrentPageNo(1);
                        mSubHeadlineViewModel.fetchSearchResults().observe(getActivity(), new Observer<NewsArticlesObject>() {
                            @Override
                            public void onChanged(NewsArticlesObject newsArticlesObject) {
                                if( newsArticlesObject != null)
                                    update(newsArticlesObject);
                                else {
                                    Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT).show();
                                    mSubHeadlineViewModel.setLoading(false);
                                    mProgressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }
                    else{
                        Toast.makeText(getActivity(),"Internet Unavailable",Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });


        return v;
    }

    private void update(NewsArticlesObject newsArticlesObject){
        if( mSubHeadlineViewModel.getCurrentPageNo() == 1){
            mAdapter.setArticles(newsArticlesObject.getArticles());
            mSubHeadlineViewModel.setArticles(newsArticlesObject.getArticles());

            int totalPagenos = newsArticlesObject.getTotalResults() / 20;
            if( totalPagenos % 20 != 0){
                totalPagenos += 1;
            }
            mSubHeadlineViewModel.setTotalPageNo(totalPagenos);

            if(mSubHeadlineViewModel.getCurrentPageNo() < mSubHeadlineViewModel.getTotalPageNo()){
                mSubHeadlineViewModel.setCurrentPageNo( mSubHeadlineViewModel.getCurrentPageNo() + 1);
            }
        }
        else{
            mAdapter.addArticles(newsArticlesObject.getArticles());
            mSubHeadlineViewModel.addArticles(newsArticlesObject.getArticles());

            if(mSubHeadlineViewModel.getCurrentPageNo() < mSubHeadlineViewModel.getTotalPageNo()){
                mSubHeadlineViewModel.setCurrentPageNo( mSubHeadlineViewModel.getCurrentPageNo() + 1);
            }
        }

        mSubHeadlineViewModel.setLoading(false);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    public void initViewModel(){
        if(!mSubHeadlineViewModel.getInitLoad() && !mSubHeadlineViewModel.isLoading() && NewsRepository.isConnected()){
            mProgressBar.setVisibility(View.VISIBLE);
            mSubHeadlineViewModel.initLoad();
            mUnavailabilityTextView.setVisibility(View.INVISIBLE);

            mSubHeadlineViewModel.getNewsArticleObject().observe(getActivity(), new Observer<NewsArticlesObject>() {
                @Override
                public void onChanged(NewsArticlesObject newsArticlesObject) {
                    if(newsArticlesObject != null){
                        if( !mSubHeadlineViewModel.getInitLoad() ){
                            update(newsArticlesObject);
                            mSubHeadlineViewModel.setInitLoad(true);
                            mSubHeadlineRecyclerView.setVisibility(View.VISIBLE);
                        }
                    }
                    else{
                        Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT).show();
                        mSubHeadlineViewModel.setLoading(false);
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void OnMessage(NetworkUpdateEvent event){
        if(event.isNetworkAvailable)
            initViewModel();
    }
}
