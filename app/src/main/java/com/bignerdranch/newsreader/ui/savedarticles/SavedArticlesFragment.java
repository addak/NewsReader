package com.bignerdranch.newsreader.ui.savedarticles;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bignerdranch.newsreader.NewsRepository;
import com.bignerdranch.newsreader.R;
import com.bignerdranch.newsreader.ResultsAdapter;
import com.bignerdranch.newsreader.articles.Article;
import com.bignerdranch.newsreader.newsroom.SavedArticle;

import java.util.List;
import java.util.stream.Collectors;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;

public class SavedArticlesFragment extends Fragment {

    private SavedArticlesViewModel mSavedArticlesViewModel;
    private ResultsAdapter mAdapter;
    private RecyclerView mSavedArticlesRecyclerView;
    private Fragment mCurrentFragment;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mCurrentFragment= this;
        mSavedArticlesViewModel = ViewModelProviders.of(getActivity()).get(SavedArticlesViewModel.class);

        View root = inflater.inflate(R.layout.fragment_savedarticles, container, false);
        mSavedArticlesRecyclerView = root.findViewById(R.id.recyclerview_savedarticles);

        final Observer<List<SavedArticle>> observer = new Observer<List<SavedArticle>>() {
            @Override
            public void onChanged(List<SavedArticle> savedArticles) {
                if(!mSavedArticlesViewModel.isDeleting())
                    mAdapter.setArticles(savedArticles.stream().map(Article::new).collect(Collectors.toList()));
                mSavedArticlesViewModel.setDeleting(false);
            }
        };

        mSavedArticlesViewModel.getSavedArticleList().observe(mCurrentFragment, observer);
        mAdapter= new ResultsAdapter(getActivity());
        mSavedArticlesRecyclerView.setAdapter(mAdapter);
        mSavedArticlesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ItemTouchHelper swipeLeft= new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mSavedArticlesViewModel.setDeleting(true);
                int pos = viewHolder.getAdapterPosition();
                NewsRepository.delete(new SavedArticle(mAdapter.getArticles().get(pos)));
                mAdapter.deleteArticle(pos);

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                View itemview = viewHolder.itemView;

                Drawable unfavouriteIcon = ContextCompat.getDrawable(getContext(), R.drawable.ic_favorite_border_24dp);

                float IconHeight = unfavouriteIcon.getIntrinsicHeight();
                float IconWidth = unfavouriteIcon.getIntrinsicWidth();

                float itemHeight = itemview.getBottom() - itemview.getTop();

                if (actionState == ACTION_STATE_SWIPE) {

                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

                    RectF layout = new RectF(itemview.getLeft(), itemview.getTop(), itemview.getLeft() + dX, itemview.getBottom());

                    paint.setColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));

                    c.drawRect(layout, paint);

                    int deleteIconTop = (int) (itemview.getTop() + (itemHeight - IconHeight) / 2);
                    int deleteIconBottom = (int) (deleteIconTop + IconHeight);
                    int deleteIconMargin = (int) ((itemHeight - IconHeight) / 2);
                    int deleteIconLeft = (int)(itemview.getLeft() + deleteIconMargin);
                    int deleteIconRight = (int)(itemview.getLeft() + deleteIconMargin + IconWidth);

                    unfavouriteIcon.setBounds(deleteIconLeft,deleteIconTop,deleteIconRight,deleteIconBottom);
                    unfavouriteIcon.draw(c);

                    getDefaultUIUtil().onDraw(c, recyclerView, viewHolder.itemView, dX, dY, actionState, isCurrentlyActive);

                }
            }
        });

        swipeLeft.attachToRecyclerView(mSavedArticlesRecyclerView);

        return root;
    }
}