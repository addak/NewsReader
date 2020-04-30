package com.bignerdranch.newsreader.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.bignerdranch.newsreader.articles.Article;
import com.bignerdranch.newsreader.ui.NewsWebViewModel;
import com.bignerdranch.newsreader.ui.headlines.SubHeadlineViewModel;

public class NewsWebViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private Article mArticle;

    public NewsWebViewModelFactory(Application application, Article article){
        mApplication = application;
        mArticle = article;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new NewsWebViewModel(mApplication,mArticle);
    }
}
