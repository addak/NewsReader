package com.bignerdranch.newsreader.newsroom;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {SavedArticle.class}, exportSchema = false, version = 1)
public abstract class ArticlesDatabase extends RoomDatabase {
    private static final int  NUMBER_OF_THREADS = 4;

    private static final String DB_NAME = "news_articles.db";
    private static ArticlesDatabase sInstance;
    public static final ExecutorService mDatabaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static synchronized ArticlesDatabase getDatabase(Context context){
        if( sInstance == null ){
            sInstance = Room.databaseBuilder(context.getApplicationContext(), ArticlesDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return sInstance;
    }

    @Override
    public void init(@NonNull DatabaseConfiguration configuration) {
        super.init(configuration);
    }

    public abstract ArticlesDao ArticlesDao();

}
