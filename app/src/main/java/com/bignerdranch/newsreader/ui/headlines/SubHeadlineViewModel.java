package com.bignerdranch.newsreader.ui.headlines;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.bignerdranch.newsreader.NewsRepository;
import com.bignerdranch.newsreader.articles.Article;
import com.bignerdranch.newsreader.articles.NewsArticlesObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubHeadlineViewModel extends ViewModel {
    private Application mApplication;
    private Boolean mInitLoad;

    private List<Article> mArticles;
    private int mCurrentPageNo;
    private int mTotalPageNo;
    private boolean mLoading;
    private String mQuery;
    private String mQueryValue;

    private LiveData<NewsArticlesObject> mNewsArticleObject;

    public SubHeadlineViewModel(Application application, String query, String queryValue) {
        mCurrentPageNo = 1;
        mArticles = new ArrayList<>();
        mInitLoad = false;
        mLoading = false;
        mQuery = query;
        mQueryValue = queryValue;
    }

    public Boolean getInitLoad(){   return mInitLoad;   }

    public void setInitLoad(boolean status){ mInitLoad = status; }

    public void setQuery(String query) {
        mQuery = query;
    }

    public void setQueryValue(String queryValue) {
        mQueryValue = queryValue;
    }

    public List<Article> getArticles() {
        return mArticles;
    }

    public void setArticles(List<Article> articles) {
        mArticles = articles;
    }

    public void addArticles(List<Article> articles){ mArticles.addAll(articles); }

    public LiveData<NewsArticlesObject> getNewsArticleObject() {
        return mNewsArticleObject; }


    public int getCurrentPageNo() {
        return mCurrentPageNo;
    }

    public void setCurrentPageNo(int currentPageNo) {
        mCurrentPageNo = currentPageNo;
    }

    public int getTotalPageNo() {
        return mTotalPageNo;
    }

    public void setTotalPageNo(int totalPageNo) {
        mTotalPageNo = totalPageNo;
    }

    public boolean isLoading() {
        return mLoading;
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
    }

    public void initLoad(){
        mNewsArticleObject = fetchSearchResults();
    }


    public LiveData<NewsArticlesObject> fetchSearchResults(){
        mLoading = true;

        Map<String,String> parameters = new HashMap<>();
        parameters.put(mQuery,mQueryValue);
        parameters.put("page",Integer.toString( mCurrentPageNo));
        parameters.put("apiKey","3c4456fee2df4f9e9a5370ee68adeb14");
        parameters.put("language","en");

        mNewsArticleObject = NewsRepository.getInstance(null).fetchArticles(NewsRepository.TYPE_HEADLINE,parameters);

        return mNewsArticleObject;
    }
}