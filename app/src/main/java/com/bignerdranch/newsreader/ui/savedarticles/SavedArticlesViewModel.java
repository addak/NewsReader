package com.bignerdranch.newsreader.ui.savedarticles;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.bignerdranch.newsreader.NewsRepository;
import com.bignerdranch.newsreader.newsroom.SavedArticle;

import java.util.List;

public class SavedArticlesViewModel extends ViewModel {

    private LiveData<List<SavedArticle>> mSavedArticleList;
    private boolean mDeleting;

    public SavedArticlesViewModel() {
        mSavedArticleList = NewsRepository.getAllSavedArticles();
        mDeleting = false;
    }

    public LiveData<List<SavedArticle>> getSavedArticleList(){
        return mSavedArticleList;
    }

    public boolean isDeleting() {
        return mDeleting;
    }

    public void setDeleting(boolean deleting) {
        mDeleting = deleting;
    }
}