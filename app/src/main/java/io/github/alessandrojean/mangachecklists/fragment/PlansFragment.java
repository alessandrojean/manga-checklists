package io.github.alessandrojean.mangachecklists.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.alessandrojean.mangachecklists.R;
import io.github.alessandrojean.mangachecklists.adapter.PlansAdapter;
import io.github.alessandrojean.mangachecklists.domain.Plan;
import io.github.alessandrojean.mangachecklists.parser.plan.JBCPlanParser;
import io.github.alessandrojean.mangachecklists.parser.plan.PlanParser;
import io.github.alessandrojean.mangachecklists.task.PlanRequest;
import me.zhanghai.android.materialprogressbar.IndeterminateCircularProgressDrawable;


public class PlansFragment extends Fragment implements
        View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = PlansFragment.class.getName();
    public static final String TITLE = "Assinaturas";
    public static final String KEY = "fragment";

    // Arguments from bundle.
    private static final String ARG_VIEW_VISIBLE = "arg_view_visible";
    private static final String ARG_REFRESHING = "arg_refreshing";
    private static final String ARG_PLAN_LIST = "arg_plan_list";

    // Views available to show.
    public static final int STATE_CONTENT = 0;
    public static final int STATE_ERROR = 1;
    public static final int STATE_LOADING = 2;

    // Class variables.
    private int mViewVisible;
    private boolean mRefreshing;
    private ArrayList<Plan> mPlanList;

    private Parcelable rvState;

    // Views to show.
    private View viewContent;
    private View viewError;
    private View viewLoading;

    // Views in viewContent
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private TextView textViewEmpty;

    // Views in viewError
    private Button buttonTryAgain;

    // Views in viewLoading
    private ProgressBar progressBarLoading;

    // Another
    private PlansAdapter plansAdapter;
    private PlanRequest planRequest;
    private PlanParser planParser;


    public PlansFragment() {
        // Required empty public constructor
        mViewVisible = STATE_CONTENT;
        mPlanList = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {
            mViewVisible = savedInstanceState.getInt(ARG_VIEW_VISIBLE);
            mRefreshing = savedInstanceState.getBoolean(ARG_REFRESHING);
            mPlanList = savedInstanceState.getParcelableArrayList(ARG_PLAN_LIST);
        }

        showCorrectView(mViewVisible);
        swipeRefreshLayout.setRefreshing(mRefreshing);

        plansAdapter = new PlansAdapter(getContext(), mPlanList);
        recyclerView.setAdapter(plansAdapter);

        planParser = getCorrectParser(0);

        if (mPlanList.size() == 0 || mRefreshing || mViewVisible == STATE_LOADING)
            retrievePlans();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plans, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewContent = view.findViewById(R.id.layout_content_plans);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout_plans);
        swipeRefreshLayout.setOnRefreshListener(this);

        recyclerView = view.findViewById(R.id.recycler_view_plans);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration divider = new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(divider);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        textViewEmpty = view.findViewById(R.id.text_empty_plans);

        viewError = view.findViewById(R.id.layout_error_plans);

        buttonTryAgain = view.findViewById(R.id.button_try_again_plans);
        buttonTryAgain.setOnClickListener(this);

        viewLoading = view.findViewById(R.id.layout_loading_plans);

        progressBarLoading = view.findViewById(R.id.progress_bar_plans);
        progressBarLoading.setIndeterminateDrawable(new IndeterminateCircularProgressDrawable(getContext()));
    }

    @Override
    public void onPause() {
        super.onPause();
        rvState();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (rvState != null)
            recyclerView.getLayoutManager().onRestoreInstanceState(rvState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        cancelRequest();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        cancelRequest();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ARG_VIEW_VISIBLE, mViewVisible);
        outState.putBoolean(ARG_REFRESHING, mRefreshing);
        outState.putParcelableArrayList(ARG_PLAN_LIST, mPlanList);
        super.onSaveInstanceState(outState);
    }

    private void cancelRequest() {
        if (planRequest != null && planRequest.getStatus() == PlanRequest.Status.RUNNING) {
            planRequest.cancel(true);
            planRequest = null;
        }
    }

    private void rvState() {
        rvState = recyclerView.getLayoutManager().onSaveInstanceState();
    }

    public void showCorrectView(int viewId) {
        getActivity().runOnUiThread(() -> {
            mViewVisible = viewId;

            viewContent.setVisibility(viewId == STATE_CONTENT ? View.VISIBLE : View.GONE);
            viewError.setVisibility(viewId == STATE_ERROR ? View.VISIBLE : View.GONE);
            viewLoading.setVisibility(viewId == STATE_LOADING ? View.VISIBLE : View.GONE);
        });
    }

    private void retrievePlans() {
        planRequest = new PlanRequest(this, getCorrectParser(0));
        planRequest.setReloading(mRefreshing);
        planRequest.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void showPlans(List<Plan> planList) {
        int oldView = mViewVisible;

        showCorrectView(planList == null ? STATE_ERROR : STATE_CONTENT);

        if (planList != null) {
            if (oldView == STATE_LOADING) {
                this.mPlanList.clear();
                this.mPlanList.addAll(planList);

                plansAdapter.notifyDataSetChanged();
            }
            else {
                int size = this.mPlanList.size();
                this.mPlanList.clear();

                plansAdapter.notifyItemRangeRemoved(0, size);

                this.mPlanList.addAll(planList);

                plansAdapter.notifyItemRangeInserted(0, this.mPlanList.size());
            }

            swipeRefreshLayout.setRefreshing(false);

            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            layoutManager.scrollToPositionWithOffset(0, 0);

            textViewEmpty.setVisibility(planList.size() == 0 ? View.VISIBLE : View.GONE);
        }

        mRefreshing = false;
    }

    // Since we only have one parser available.
    private PlanParser getCorrectParser(int filter) {
        return new JBCPlanParser();
    }

    @Override
    public void onRefresh() {
        mRefreshing = true;

        retrievePlans();
    }

    @Override
    public void onClick(View view) {
        viewLoading.setVisibility(View.VISIBLE);
        viewError.setVisibility(View.GONE);

        showCorrectView(STATE_LOADING);
    }
}
