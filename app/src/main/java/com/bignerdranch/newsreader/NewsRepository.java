package com.bignerdranch.newsreader;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.bignerdranch.newsreader.articles.Article;
import com.bignerdranch.newsreader.articles.NewsArticlesObject;
import com.bignerdranch.newsreader.newsroom.ArticlesDao;
import com.bignerdranch.newsreader.newsroom.ArticlesDatabase;
import com.bignerdranch.newsreader.newsroom.SavedArticle;
import com.bignerdranch.newsreader.sources.*;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsRepository {
    public static final String TYPE_HEADLINE = "Headlines";
    public static final String TYPE_ARTICLES = "Articles";

    public static final String sBaseUrl = "http://newsapi.org/v2/";


    private static NewsRepository sNewsRepository;
    private static Context sContext;

    private static boolean Connected = false;
    private static boolean initConnectionSuccess = false;

    private static ArticlesDao mArticleDao;
    private static LiveData<List<SavedArticle>> mSavedArticles;
    private static ArticlesDatabase mDatabase;

    private static Call<NewsArticlesObject> mCaller;

    private static EventBus mBus = EventBus.getDefault();

    private static HashMap<String,String> sSourceMap = new HashMap<>();
    private static ArrayList<Source> sSource = new ArrayList<>();
    private static HashMap<String,String> sLanguageMap = new HashMap<>();

    private static HashSet<String> mSaveUrlSet = new HashSet<>();


    public static NewsRepository getInstance(Context context){
        if( sNewsRepository == null)
            return sNewsRepository = new NewsRepository(context);

        return sNewsRepository;
    }

    private NewsRepository(Context context){
        sContext = context;
        fetchLanguages();

        ConnectivityManager.NetworkCallback callback = new ConnectivityManager.NetworkCallback(){
            @Override
            public void onAvailable(@NonNull Network network) {

                if(!initConnectionSuccess){
                    fetchSources();
                }
                Log.v("NewsRepository","GainedConnection");
                Connected = true;
                mBus.post(new NetworkUpdateEvent(true));
            }

            @Override
            public void onLost(@NonNull Network network) {

                Connected = false;
                Log.v("NewsRepository","LostConnection");

                mBus.post(new NetworkUpdateEvent(false));
            }
        };
        ConnectivityManager cm = (ConnectivityManager)sContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        cm.registerDefaultNetworkCallback(callback);

        mDatabase = ArticlesDatabase.getDatabase(sContext);
        mArticleDao = mDatabase.ArticlesDao();
        mSavedArticles = mArticleDao.getSavedArticles();

        mSavedArticles.observeForever(new Observer<List<SavedArticle>>() {
            @Override
            public void onChanged(List<SavedArticle> savedArticles) {

                for(SavedArticle article : savedArticles){
                    mSaveUrlSet.add(article.getUrl());
                }
                mSavedArticles.removeObserver(this);
            }
        });

    }

    public static ArrayList<String> getSourceNames(){
        ArrayList<String> list = new ArrayList<>(sSourceMap.keySet());
        Collections.sort(list);

        return list;
    }

    public static ArrayList<String> getLangaugeNames(){
        ArrayList<String> list = new ArrayList<>(sLanguageMap.keySet());

        return list;
    }

    public static boolean isConnected() {
        return Connected;
    }

    public static boolean isInitConnectionSuccess() {
        return initConnectionSuccess;
    }

    public static String getLanguageCode(String language){
        return sLanguageMap.get(language);
    }

    public static String getSourceId(String source){
        return sSourceMap.get(source);
    }

    public void fetchSources(){
        NewsFetcherApi mNewsFetcherApi = new RetrofitHelper(sBaseUrl).getRetrofit().create(NewsFetcherApi.class);
        Call<NewsSourcesObject> caller = mNewsFetcherApi.getSourceData();
        caller.enqueue(new Callback<NewsSourcesObject>() {
            @Override
            public void onResponse(Call<NewsSourcesObject> call, Response<NewsSourcesObject> response) {

                if( response.isSuccessful() ){
                    NewsSourcesObject newsSource = response.body();

                    List<Source> sources = newsSource.getSources();

                    for(Source source : sources){

                        sSourceMap.put( source.getName(), source.getId());
                        sSource.add(source);
                    }
                    initConnectionSuccess = true;
                }
            }

            @Override
            public void onFailure(Call<NewsSourcesObject> call, Throwable t) {
                Toast.makeText(sContext, "Something went wrong...Please try later!", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void fetchLanguages(){
        String[] langArray = sContext.getResources().getStringArray(R.array.languages);

        for(String line : langArray){
            String[] values = line.split(" ");
            sLanguageMap.put(values[0],values[1]);
        }
    }

    public LiveData<NewsArticlesObject> fetchArticles(String Type,Map<String,String> paramters){

        final MutableLiveData<NewsArticlesObject> data = new MutableLiveData<>();

        NewsFetcherApi mNewsFetcherApi = new RetrofitHelper(NewsRepository.sBaseUrl).getRetrofit().create(NewsFetcherApi.class);

        if( Type.equals(TYPE_ARTICLES))
            mCaller = mNewsFetcherApi.getArticlesData(paramters);
        else if( Type.equals(TYPE_HEADLINE))
            mCaller = mNewsFetcherApi.getTopHeadlines(paramters);

        Log.v("ViewModel","inFetch");
        mCaller.enqueue(new Callback<NewsArticlesObject>() {
            @Override
            public void onResponse(Call<NewsArticlesObject> call, Response<NewsArticlesObject> response) {
                if(response.isSuccessful()){
                    data.setValue( response.body() );
                    Log.v("ViewModel","Successful");
                    Log.v("ViewModel",response.body().getTotalResults().toString());
                }
                else{

                    try {
                        Log.v("ViewModel","Unsuccessful");
                        Log.v("ViewModel",response.errorBody().string());
                    }catch (Exception e){

                    }

                    data.setValue(null);
                }
            }

            @Override
            public void onFailure(Call<NewsArticlesObject> call, Throwable t) {
                Log.v("ViewModel","Failed!!!");
                t.printStackTrace();
                data.setValue(null);
            }
        });

        return data;
    }

    public static LiveData<List<SavedArticle>> getAllSavedArticles(){
        return mSavedArticles;
    }

    public static void insert(SavedArticle article){
        mSavedArticles.getValue().add(article);
        mSaveUrlSet.add(article.getUrl());
        mDatabase.mDatabaseExecutor.execute(()->{
            mSavedArticles.getValue().add(article);
            mArticleDao.insertSavedArticle(article);
        });
    }

    public static void delete(SavedArticle article){
        mSavedArticles.getValue().remove(article);
        mSaveUrlSet.remove(article.getUrl());
        mDatabase.mDatabaseExecutor.execute(()->{
            mSavedArticles.getValue().remove(article);
            mArticleDao.deleteSavedArticle(article);
        });
    }

    public static boolean isSaved(Article article){
        return mSaveUrlSet.contains(article.getUrl());
    }

}
