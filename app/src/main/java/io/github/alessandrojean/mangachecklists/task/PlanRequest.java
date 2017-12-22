package io.github.alessandrojean.mangachecklists.task;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.orhanobut.hawk.Hawk;

import java.lang.ref.WeakReference;
import java.util.List;
import io.github.alessandrojean.mangachecklists.domain.Plan;
import io.github.alessandrojean.mangachecklists.fragment.ChecklistFragment;
import io.github.alessandrojean.mangachecklists.fragment.PlansFragment;
import io.github.alessandrojean.mangachecklists.parser.plan.PlanParser;

/**
 * Created by Desktop on 15/12/2017.
 */

public class PlanRequest extends AsyncTask<Void, Void, List<Plan>> {
    private WeakReference<PlansFragment> fragment;
    private PlanParser parser;
    private boolean reloading;

    private String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36";

    public PlanRequest(PlansFragment plansFragment, PlanParser parser) {
        this.fragment = new WeakReference<>(plansFragment);
        this.parser = parser;

        initList();
    }

    public boolean isReloading() {
        return reloading;
    }

    public void setReloading(boolean reloading) {
        this.reloading = reloading;
    }

    private void initList() {
        if (fragment.get() != null) {
            Hawk.init(fragment.get().getContext()).build();
        }
    }

    @Override
    protected List<Plan> doInBackground(Void... voids) {
        Log.d("MangaChecklists", "PlanRequest: doInBackground()");

        if (!reloading && Hawk.contains(parser.getPlanKey())) {
            List<Plan> hawkList = Hawk.get(parser.getPlanKey());

            for (Plan p : hawkList) {
                Log.d("MangaChecklists", "Getting " + p.getManga().getName() + " from Hawk.");
            }

            if (hawkList.size() > 0)
                return hawkList;
        }

        if (fragment.get() != null && !reloading) {
            fragment.get().showCorrectView(ChecklistFragment.STATE_LOADING);
        }

        List<Plan> plans = parser.getPlans();
        if (plans != null)
            Hawk.put(parser.getPlanKey(), plans);

        return plans;
    }

    @Override
    protected void onPostExecute(List<Plan> plans) {
        super.onPostExecute(plans);

        if (fragment.get() != null) {
            fragment.get().showPlans(plans);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        parser.cancel();
    }
}
