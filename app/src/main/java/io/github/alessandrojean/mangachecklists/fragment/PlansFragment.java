package io.github.alessandrojean.mangachecklists.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.List;

import io.github.alessandrojean.mangachecklists.adapter.PlansAdapter;
import io.github.alessandrojean.mangachecklists.domain.Plan;
import io.github.alessandrojean.mangachecklists.parser.plan.JBCPlanParser;
import io.github.alessandrojean.mangachecklists.parser.plan.PlanParser;
import io.github.alessandrojean.mangachecklists.task.PlanRequest;

/**
 * Created by Desktop on 16/12/2017.
 */

public class PlansFragment extends FragmentAbstract {
    public static final String TITLE = "Assinaturas";

    private PlansAdapter plansAdapter;

    private List<Plan> plans;

    private PlanRequest planRequest;
    private PlanParser parser;

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

        parser = new JBCPlanParser();

        initList();
        retrievePlans();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (planRequest != null && planRequest.getStatus() == PlanRequest.Status.RUNNING) {
            planRequest.cancel(true);
            planRequest = null;
        }
    }

    private void initList() {
        Hawk.init(getContext()).build();

        if (!Hawk.contains(parser.getPlanKey()))
            Hawk.put(parser.getPlanKey(), plans);

        List<Plan> hawkList = Hawk.get(parser.getPlanKey());
        plans.addAll(hawkList);
    }

    private void retrievePlans() {
        parser = new JBCPlanParser();

        if (!isReloading && Hawk.contains(parser.getPlanKey())) {
            List<Plan> hawkList = Hawk.get(parser.getPlanKey());

            if (hawkList.size() != 0) {
                updatePlans(hawkList, false);
                return;
            }
        }

        if (!isReloading)
            crossfade(true);

        planRequest = new PlanRequest(this, parser);
        planRequest.execute();
    }

    public void updatePlans(List<Plan> plans, boolean animate) {
        if (plans != null) {
            this.plans.clear();
            this.plans.addAll(plans);

            Hawk.put(parser.getPlanKey(), plans);
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
