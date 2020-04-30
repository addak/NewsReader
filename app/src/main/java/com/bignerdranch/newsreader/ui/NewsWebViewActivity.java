package com.bignerdranch.newsreader.ui;

import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.bignerdranch.newsreader.R;
import com.bignerdranch.newsreader.articles.Article;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.greenrobot.eventbus.EventBus;

public class NewsWebViewActivity extends AppCompatActivity {

    private NewsWebViewModel mNewsWebViewModel;
    private FloatingActionButton mFloatingActionButton;
    private WebView mArticleWebView;


    public static Intent newInstance(Context context){

        Intent i = new Intent( context, NewsWebViewActivity.class );
        return i;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_articlerviewer);
        super.onCreate(savedInstanceState);

        final Article article = EventBus.getDefault().removeStickyEvent(Article.class);
        final NewsWebViewModelFactory factory = new NewsWebViewModelFactory(getApplication(), article);

        mNewsWebViewModel = ViewModelProviders.of(this, factory).get(NewsWebViewModel.class);

        mFloatingActionButton = findViewById(R.id.save_article);
        mArticleWebView = findViewById(R.id.article_viewer);

        mArticleWebView.setWebViewClient(new WebViewClient(){

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.v("NewsWebActivity","ReceivedError");
                super.onReceivedError(view, request, error);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                Log.v("NewsWebActivity","ReceivedHttpError");
                super.onReceivedHttpError(view, request, errorResponse);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                Log.v("NewsWebActivity","ReceivedSslError");
                super.onReceivedSslError(view, handler, error);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.v("NewsWebActivity","Loaded");
                super.onPageFinished(view, url);
            }
        });
        mArticleWebView.getSettings().setDomStorageEnabled(true);
        mArticleWebView.getSettings().setJavaScriptEnabled(true);
        mArticleWebView.setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        mArticleWebView.getSettings();

        mFloatingActionButton.setSelected(mNewsWebViewModel.isFavourited());

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mNewsWebViewModel.isFavourited()){
                    mNewsWebViewModel.setFavourited(false);
                    mFloatingActionButton.setSelected(false);
                }
                else{
                    mNewsWebViewModel.setFavourited(true);
                    mFloatingActionButton.setSelected(true);
                }
            }
        });

        if(savedInstanceState != null)
            mArticleWebView.restoreState(savedInstanceState);
        else
            mArticleWebView.loadUrl(mNewsWebViewModel.getArticle().getUrl());

    }

    @Override
    protected void onPause() {
        super.onPause();
        mArticleWebView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mArticleWebView.onResume();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mArticleWebView.saveState(outState);
        super.onSaveInstanceState(outState);
    }
}
