package com.bignerdranch.newsreader.ui.customsearch;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bignerdranch.newsreader.NewsRepository;
import com.bignerdranch.newsreader.articles.Article;
import com.bignerdranch.newsreader.articles.NewsArticlesObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class CustomSearchViewModel extends ViewModel {

    private MutableLiveData<String> mTitleSubstring;
    private MutableLiveData<Date> mFrom;
    private MutableLiveData<Date> mTo;
    private MutableLiveData<String> mSource;
    private MutableLiveData<String> mLanguage;

    private Date mCurFromDate;
    private Date mCurToDate;
    private String mCurSource;
    private String mCurLanguage;

    private List<Article> mArticles;
    private int mCurrentPageNo;
    private int mTotalPageNo;
    private boolean mLoading;

    private LiveData<NewsArticlesObject> mNewsArticleObject;


    public CustomSearchViewModel() {
       mFrom = new MutableLiveData<>();
       mTo = new MutableLiveData<>();
       mSource = new MutableLiveData<>();
       mLanguage = new MutableLiveData<>();
       mNewsArticleObject = new MutableLiveData<>();
       mTitleSubstring = new MutableLiveData<>();
       mArticles = new ArrayList<>();

       mTitleSubstring.setValue(null);
       mFrom.setValue(new Date());
       mTo.setValue(new Date());
       mSource.setValue("");
       mLanguage.setValue("Default");
    }

    public List<Article> getArticles() {
        return mArticles;
    }

    public void setArticles(List<Article> articles) {
        mArticles = articles;
    }

    public void addArticles(List<Article> articles){
        mArticles.addAll(articles);
    }

    public String getTitleSubstring() {
        return mTitleSubstring.getValue();
    }

    public void setTitleSubstring(String titleSubstring) {
        mTitleSubstring.setValue(titleSubstring);
    }

    public MutableLiveData<Date> getFrom() {
        return mFrom;
    }

    public void setFrom(Date from) {
        mFrom.setValue(from);
    }

    public MutableLiveData<Date> getTo() {
        return mTo;
    }

    public void setTo(Date to) {
        mTo.setValue(to);
    }

    public MutableLiveData<String> getSource() {
        return mSource;
    }

    public void setSource(String source) {
        mSource.setValue(source);
    }

    public MutableLiveData<String> getLanguage() {
        return mLanguage;
    }

    public void setLanguage(String language) {
        mLanguage.setValue(language);
    }

    public LiveData<NewsArticlesObject> getNewsArticleObject() { return mNewsArticleObject; }


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

    public Date getCurFromDate() {
        return mCurFromDate;
    }

    public void setCurFromDate(Date curFromDate) {
        mCurFromDate = curFromDate;
    }

    public Date getCurToDate() {
        return mCurToDate;
    }

    public void setCurToDate(Date curToDate) {
        mCurToDate = curToDate;
    }

    public String getCurSource() {
        return mCurSource;
    }

    public void setCurSource(String curSource) {
        mCurSource = curSource;
    }

    public String getCurLanguage() {
        return mCurLanguage;
    }

    public void setCurLanguage(String curLanguage) {
        mCurLanguage = curLanguage;
    }

    public LiveData<NewsArticlesObject> fetchSearchResults(Map<String,String> searchParams){
        mLoading = true;

        mNewsArticleObject = NewsRepository.getInstance(null).fetchArticles(NewsRepository.TYPE_ARTICLES,searchParams);

        return mNewsArticleObject;
    }

}
