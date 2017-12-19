package io.github.alessandrojean.mangachecklists.task;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.alessandrojean.mangachecklists.MainActivity;
import io.github.alessandrojean.mangachecklists.domain.Manga;
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

    private String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36";

    public PlanRequest(PlansFragment plansFragment, PlanParser parser) {
        this.fragment = new WeakReference<>(plansFragment);
        this.parser = parser;
    }

    @Override
    protected List<Plan> doInBackground(Void... voids) {
        return parser.getPlans();
    }

    @Override
    protected void onPostExecute(List<Plan> plans) {
        super.onPostExecute(plans);

        if (fragment.get() != null) {
            fragment.get().updatePlans(plans, true);
        }
    }
}
