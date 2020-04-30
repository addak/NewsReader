package com.bignerdranch.newsreader.ui.customsearch;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
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

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class CustomSearch extends Fragment {
        private CustomSearchViewModel mCustomSearchViewModel;
        private ResultsAdapter mAdapter;

        private ImageButton mSearchConfig;
        private RecyclerView mSearchResultsRecylerView;
        private SearchView mSearchView;
        private ProgressBar mProgressBar;
        private TextView mUnavailabilityTextView;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            mCustomSearchViewModel = ViewModelProviders.of(getParentFragment()).get(CustomSearchViewModel.class);

            View root = inflater.inflate(R.layout.fragment_customsearch, container, false);

            mSearchResultsRecylerView = root.findViewById(R.id.recycler_searchresults);
            mSearchView = root.findViewById(R.id.searchView);
            mSearchConfig = root.findViewById(R.id.search_config);
            mProgressBar = root.findViewById(R.id.progressBar);
            mUnavailabilityTextView = root.findViewById(R.id.unavailability);

            updateUI();

            mSearchView.setQuery(mCustomSearchViewModel.getTitleSubstring(),false);

            if(mAdapter == null){
                mAdapter= new ResultsAdapter(getActivity());
                mSearchResultsRecylerView.setAdapter( mAdapter);
                mAdapter.setArticles(mCustomSearchViewModel.getArticles());
            }
            else {
                mAdapter.setArticles(mCustomSearchViewModel.getArticles());
            }

            mSearchResultsRecylerView.setLayoutManager(new LinearLayoutManager(getActivity()));

            mSearchResultsRecylerView.setAdapter(mAdapter);

            mSearchConfig.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getFragmentManager();
                    SearchConfigDialog searchConfigDialog = new SearchConfigDialog();

                    searchConfigDialog.show(fm,SearchConfigDialog.DIALOG_SEARCHCONFIG);

                }
            });

            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String titleSubstring) {

                    Map<String,String> searchParams = new HashMap<>();

                    if( !titleSubstring.isEmpty() ){
                        searchParams.put("qInTitle",titleSubstring);
                        mCustomSearchViewModel.setTitleSubstring(titleSubstring);
                    }

                    if( !mCustomSearchViewModel.getLanguage().getValue().equals("Default") ){
                        String value = NewsRepository.getLanguageCode(mCustomSearchViewModel.getLanguage().getValue());
                        searchParams.put("language", value);
                    }

                    if( !mCustomSearchViewModel.getSource().getValue().isEmpty() ){
                        String value = NewsRepository.getSourceId(mCustomSearchViewModel.getSource().getValue());
                        searchParams.put("sources", value);
                    }

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                    String toDate = formatter.format( mCustomSearchViewModel.getTo().getValue() );
                    String fromDate = formatter.format( mCustomSearchViewModel.getFrom().getValue() );

                    searchParams.put("from",fromDate);
                    searchParams.put("to",toDate);
                    searchParams.put("apiKey","3c4456fee2df4f9e9a5370ee68adeb14");

                    mCustomSearchViewModel.setCurrentPageNo(1);
                    mCustomSearchViewModel.setCurFromDate(mCustomSearchViewModel.getFrom().getValue());
                    mCustomSearchViewModel.setCurToDate(mCustomSearchViewModel.getTo().getValue());
                    mCustomSearchViewModel.setCurLanguage(mCustomSearchViewModel.getLanguage().getValue());
                    mCustomSearchViewModel.setCurSource(mCustomSearchViewModel.getSource().getValue());

                    mCustomSearchViewModel.getArticles().clear();

                    mProgressBar.setVisibility(View.VISIBLE);
                    mCustomSearchViewModel.fetchSearchResults(searchParams).observe(getActivity(), new Observer<NewsArticlesObject>() {
                        @Override
                        public void onChanged(NewsArticlesObject newsArticlesObject) {
                            update(newsArticlesObject);
                        }
                    });
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });

            mSearchResultsRecylerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    Map<String,String> searchParams = new HashMap<>();

                    if (!recyclerView.canScrollVertically(1) && newState==RecyclerView.SCROLL_STATE_IDLE &&
                            !mCustomSearchViewModel.isLoading() && mCustomSearchViewModel.getCurrentPageNo() <= mCustomSearchViewModel.getTotalPageNo()) {

                        if(NewsRepository.isConnected()){
                            if(!mCustomSearchViewModel.getTitleSubstring().isEmpty()){
                                searchParams.put("qInTitle",mCustomSearchViewModel.getTitleSubstring());
                            }

                            if( !mCustomSearchViewModel.getCurLanguage().equals("Default")){
                                String value = NewsRepository.getLanguageCode(mCustomSearchViewModel.getCurLanguage());
                                searchParams.put("language", value);
                            }

                            if( !mCustomSearchViewModel.getCurSource().isEmpty() ){
                                String value = NewsRepository.getSourceId(mCustomSearchViewModel.getCurSource());
                                searchParams.put("sources", value);
                            }

                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

                            String toDate = formatter.format( mCustomSearchViewModel.getCurToDate() );
                            String fromDate = formatter.format( mCustomSearchViewModel.getCurFromDate() );

                            searchParams.put("from",fromDate);
                            searchParams.put("to",toDate);
                            searchParams.put("apiKey","3c4456fee2df4f9e9a5370ee68adeb14");
                            searchParams.put("page",Integer.toString(mCustomSearchViewModel.getCurrentPageNo()));

                            mProgressBar.setVisibility(View.VISIBLE);
                            mCustomSearchViewModel.fetchSearchResults(searchParams).observe(getActivity(), new Observer<NewsArticlesObject>() {
                                @Override
                                public void onChanged(NewsArticlesObject newsArticlesObject) {
                                    update(newsArticlesObject);
                                }
                            });

                        }
                        else{
                            Toast.makeText(getActivity(),"Internet Unavailable",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });


            return root;
        }

        private void update(NewsArticlesObject newsArticlesObject){

            if( newsArticlesObject != null){
                if( mCustomSearchViewModel.getCurrentPageNo() == 1){
                    mAdapter.setArticles(newsArticlesObject.getArticles());
                    mCustomSearchViewModel.setArticles(newsArticlesObject.getArticles());

                    int totalPagenos = newsArticlesObject.getTotalResults() / 20;
                    if( totalPagenos % 20 != 0){
                        totalPagenos += 1;
                    }
                    mCustomSearchViewModel.setTotalPageNo(totalPagenos);

                    if(mCustomSearchViewModel.getCurrentPageNo() < mCustomSearchViewModel.getTotalPageNo()){
                        mCustomSearchViewModel.setCurrentPageNo( mCustomSearchViewModel.getCurrentPageNo() + 1);
                    }
                }
                else{
                    mAdapter.addArticles(newsArticlesObject.getArticles());
                    mCustomSearchViewModel.addArticles(newsArticlesObject.getArticles());

                    if(mCustomSearchViewModel.getCurrentPageNo() < mCustomSearchViewModel.getTotalPageNo()){
                        mCustomSearchViewModel.setCurrentPageNo( mCustomSearchViewModel.getCurrentPageNo() + 1);
                    }
                }
                updateUI();
            }
            else{
                Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT).show();
            }
            mCustomSearchViewModel.setLoading(false);
            mProgressBar.setVisibility(View.INVISIBLE);
        }

        public void updateUI(){
            if( mCustomSearchViewModel.getArticles().isEmpty() ){
                if( NewsRepository.isConnected()){
                    mSearchView.setVisibility(View.VISIBLE);
                    mSearchConfig.setVisibility(View.VISIBLE);
                    mUnavailabilityTextView.setVisibility(View.INVISIBLE);
                }
                else if( !NewsRepository.isConnected() ){

                    mSearchView.setVisibility(View.INVISIBLE);
                    mSearchConfig.setVisibility(View.INVISIBLE);
                    mUnavailabilityTextView.setVisibility(View.VISIBLE);
                }
                mSearchResultsRecylerView.setVisibility(View.INVISIBLE);
            }
            else{
                mSearchResultsRecylerView.setVisibility(View.VISIBLE);
                mSearchView.setVisibility(View.VISIBLE);
                mSearchConfig.setVisibility(View.VISIBLE);
                mUnavailabilityTextView.setVisibility(View.INVISIBLE);
            }
        }

        @Subscribe(threadMode = ThreadMode.MAIN)
        public void onMessage(NetworkUpdateEvent event){
            if(mCustomSearchViewModel != null  && mCustomSearchViewModel.getArticles().isEmpty() )
                updateUI();
        }
}