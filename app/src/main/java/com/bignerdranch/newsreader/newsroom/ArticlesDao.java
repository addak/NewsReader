package com.bignerdranch.newsreader.newsroom;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ArticlesDao {
    @Query("Select * from SavedArticle")
    LiveData<List<SavedArticle>> getSavedArticles();
    @Insert
    void insertSavedArticle(SavedArticle article);
    @Update
    void updateSavedArticle(SavedArticle article);
    @Delete
    void deleteSavedArticle(SavedArticle article);
}
