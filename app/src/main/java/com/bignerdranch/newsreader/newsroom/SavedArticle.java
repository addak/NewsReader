package com.bignerdranch.newsreader.newsroom;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.bignerdranch.newsreader.articles.Article;

@Entity(tableName = "SavedArticle")
public class SavedArticle {
    @ColumnInfo( name = "title")
    private String title;
    @ColumnInfo( name = "author")
    private String author;
    @ColumnInfo( name = "description")
    private String description;
    @NonNull
    @PrimaryKey
    @ColumnInfo( name = "url")
    private String url;
    @ColumnInfo ( name = "urlToImage")
    private String urlToImage;
    @ColumnInfo ( name = "content")
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrlToImage() {
        return urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public SavedArticle(String title, String author, String description, String url, String urlToImage, String content){
        this.title = title;
        this.author = author;
        this.description = description;
        this.content = content;
        this.url = url;
        this.urlToImage = urlToImage;
    }

    public SavedArticle(Article article){
        this.title = article.getTitle();
        this.author = article.getAuthor();
        this.description = article.getDescription();
        this.content = article.getContent();
        this.url = article.getUrl();
        this.urlToImage = article.getUrlToImage();
    }
}
