package com.bignerdranch.newsreader.ui;

import android.app.Application;

import androidx.lifecycle.ViewModel;

import com.bignerdranch.newsreader.NewsRepository;
import com.bignerdranch.newsreader.articles.Article;
import com.bignerdranch.newsreader.newsroom.SavedArticle;

public class NewsWebViewModel extends ViewModel {

    private Boolean Favourited;
    private Application mApplication;
    private Article mArticle;

    public NewsWebViewModel(Application application, Article article){
        mApplication = application;
        mArticle = article;
        Favourited = NewsRepository.isSaved(article);
    }

    public Boolean isFavourited() {
        return Favourited;
    }

    public void setFavourited(Boolean favourited) {

        Favourited = favourited;

        if(Favourited){
            NewsRepository.insert(new SavedArticle(mArticle));
        }
        else{
            NewsRepository.delete(new SavedArticle(mArticle));
        }
    }

    public Application getApplication() {
        return mApplication;
    }

    public void setApplication(Application application) {
        mApplication = application;
    }

    public Article getArticle() {
        return mArticle;
    }

    public void setArticle(Article article) {
        mArticle = article;
    }
}
