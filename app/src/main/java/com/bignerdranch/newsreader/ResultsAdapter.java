package com.bignerdranch.newsreader;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bignerdranch.newsreader.articles.Article;
import com.bignerdranch.newsreader.ui.NewsWebViewActivity;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.Result;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultViewHolder>{
    private List<Article> mArticles = new ArrayList<>();
    private Context mContext;

    public ResultsAdapter(Context context){ mContext = context; }

    public List<Article> getArticles(){
        return mArticles;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);

        return new ResultViewHolder(inflater, parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        holder.bind( mArticles.get( holder.getAdapterPosition() ) );
    }

    @Override
    public int getItemCount() {
        return mArticles.size();
    }

    public void setArticles(List<Article> articles){
        mArticles.clear();
        mArticles.addAll(articles);
        this.notifyDataSetChanged();
    }

    public void addArticles(List<Article> articles){
        mArticles.addAll(articles);
        this.notifyDataSetChanged();
    }

    public void deleteArticle(int position){
        mArticles.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeRemoved(position,1);
    }

    class ResultViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView mArticleImageView;
        private TextView mArticleTitle;

        private Article mArticle;

        public ResultViewHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item,parent,false));

            mArticleImageView = itemView.findViewById(R.id.article_image);
            mArticleTitle = itemView.findViewById(R.id.article_title);
            itemView.setOnClickListener(this);
        }

        private void bind(Article article){
            mArticle = article;
            mArticleTitle.setText(article.getTitle());

            if(article.getUrlToImage() != null && !article.getUrlToImage().isEmpty())
                Picasso.get()
                        .load(article.getUrlToImage())
                        .fit()
                        .centerCrop()
                        .into(mArticleImageView);
        }

        @Override
        public void onClick(View view) {
            Intent i = NewsWebViewActivity.newInstance(mContext);
            EventBus.getDefault().postSticky(mArticle);
            mContext.startActivity(i);
        }
    }
}