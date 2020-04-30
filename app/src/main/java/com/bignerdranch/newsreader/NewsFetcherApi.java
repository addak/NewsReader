package com.bignerdranch.newsreader;

import com.bignerdranch.newsreader.articles.NewsArticlesObject;
import com.bignerdranch.newsreader.sources.NewsSourcesObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

public interface NewsFetcherApi {

    @GET("sources?apiKey=3c4456fee2df4f9e9a5370ee68adeb14")
    Call<NewsSourcesObject> getSourceData();

    @GET("everything")
    Call<NewsArticlesObject> getArticlesData(@QueryMap Map<String,String> parameters);

    @GET("top-headlines")
    Call<NewsArticlesObject> getTopHeadlines(@QueryMap Map<String,String> parameters);
}
