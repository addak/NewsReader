package com.bignerdranch.newsreader.ui.headlines;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class SubHeadlineViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;
    private String query;
    private String queryValue;

    public SubHeadlineViewModelFactory(Application application, String q, String qV){
        mApplication = application;
        query = q;
        queryValue =qV;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new SubHeadlineViewModel(mApplication,query,queryValue);
    }
}
