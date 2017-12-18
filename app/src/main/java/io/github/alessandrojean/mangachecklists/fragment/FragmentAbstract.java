package io.github.alessandrojean.mangachecklists.fragment;

import android.app.DatePickerDialog;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;

import io.github.alessandrojean.mangachecklists.R;
import io.github.alessandrojean.mangachecklists.constant.JBC;
import io.github.alessandrojean.mangachecklists.util.LoadingUtils;
import me.zhanghai.android.materialprogressbar.IndeterminateCircularProgressDrawable;

/**
 * Created by Desktop on 16/12/2017.
 */

public abstract class FragmentAbstract extends Fragment{
    public static final String KEY = "fragment";
    public static final String TITLE = "title";
    public static final String SUBTITLE = "subtitle";
    public static final String TYPE_KEY = "type_key";

    protected static final String IS_RELOADING_KEY = "is_reloading_key";
    protected static final String IS_LOADING_KEY = "is_loading_key";

    public static final int PLAN = 0;
    public static final int CHECKLIST = 1;

    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;
    protected FloatingActionButton floatingActionButton;
    protected ProgressBar progressBar;

    protected Parcelable rvState;

    protected boolean isLoading;
    protected boolean isReloading;
    protected int animationDuration;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            isLoading = savedInstanceState.getBoolean(IS_LOADING_KEY);
            isReloading = savedInstanceState.getBoolean(IS_RELOADING_KEY);
        }

        animationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);

        progressBar = getActivity().findViewById(R.id.progress_bar);
        progressBar.setIndeterminateDrawable(new IndeterminateCircularProgressDrawable(getContext()));

        swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setRefreshing(isReloading);

        floatingActionButton = getActivity().findViewById(R.id.fab);

        recyclerView = getActivity().findViewById(R.id.recycler_view);

        if (getArguments().getInt(TYPE_KEY, PLAN) == PLAN) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(linearLayoutManager);

            DividerItemDecoration divider = new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation());
            recyclerView.addItemDecoration(divider);

            floatingActionButton.setVisibility(View.GONE);
        }
        else {
            GridLayoutManager gridLayoutManager;
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                gridLayoutManager = new GridLayoutManager(getContext(), 2);
            else
                gridLayoutManager = new GridLayoutManager(getContext(), 4);

            recyclerView.setLayoutManager(gridLayoutManager);
        }

        if (isLoading) {
            swipeRefreshLayout.setVisibility(View.GONE);
            floatingActionButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
        else {
            progressBar.setVisibility(View.GONE);
        }

        rvState();
    }

    @Override
    public void onPause() {
        super.onPause();
        rvState();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_LOADING_KEY, isLoading);
        outState.putBoolean(IS_RELOADING_KEY, isReloading);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.getLayoutManager().onRestoreInstanceState(rvState);
    }

    private void rvState() {
        rvState = recyclerView.getLayoutManager().onSaveInstanceState();
    }

    protected void crossfade(boolean inverse) {
        if (inverse)
            LoadingUtils.showLoading(swipeRefreshLayout, floatingActionButton, progressBar, animationDuration);
        else
            LoadingUtils.showContent(swipeRefreshLayout, floatingActionButton, progressBar, animationDuration);
    }
}
