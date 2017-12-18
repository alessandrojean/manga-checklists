package io.github.alessandrojean.mangachecklists.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import io.github.alessandrojean.mangachecklists.adapter.PlansAdapter;
import io.github.alessandrojean.mangachecklists.constant.JBC;
import io.github.alessandrojean.mangachecklists.domain.Plan;
import io.github.alessandrojean.mangachecklists.task.JBCPlanRequest;

/**
 * Created by Desktop on 16/12/2017.
 */

public class PlansFragment extends FragmentAbstract {
    public static final String TITLE = "Assinaturas";

    private PlansAdapter plansAdapter;

    private List<Plan> plans;

    private JBCPlanRequest jbcPlanRequest;

    public PlansFragment() {
        this.plans = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isReloading = true;

                retrievePlans();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        plansAdapter = new PlansAdapter(getActivity(), plans);
        recyclerView.setAdapter(plansAdapter);

        initList();
        retrievePlans();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (jbcPlanRequest != null && jbcPlanRequest.getStatus() == JBCPlanRequest.Status.RUNNING) {
            jbcPlanRequest.cancel(true);
            jbcPlanRequest = null;
        }
    }

    private void initList() {
        Hawk.init(getContext()).build();

        if (!Hawk.contains(JBC.PLANS_LIST_KEY))
            Hawk.put(JBC.PLANS_LIST_KEY, plans);

        List<Plan> hawkList = Hawk.get(JBC.PLANS_LIST_KEY);
        plans.addAll(hawkList);
    }

    private void retrievePlans() {
        if (!isReloading && Hawk.contains(JBC.PLANS_LIST_KEY)) {
            List<Plan> hawkList = Hawk.get(JBC.PLANS_LIST_KEY);

            if (hawkList.size() != 0) {
                updatePlans(hawkList, false);
                return;
            }
        }

        if (!isReloading)
            crossfade(true);

        jbcPlanRequest = new JBCPlanRequest(this);
        jbcPlanRequest.execute();
    }

    public void updatePlans(List<Plan> plans, boolean animate) {
        if (plans != null) {
            this.plans.clear();
            this.plans.addAll(plans);

            Hawk.put(JBC.PLANS_LIST_KEY, plans);
            plansAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);

            if (animate && !isReloading)
                crossfade(false);
            else {
                floatingActionButton.setVisibility(View.GONE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }

        isReloading = false;
        isLoading = false;
    }
}
