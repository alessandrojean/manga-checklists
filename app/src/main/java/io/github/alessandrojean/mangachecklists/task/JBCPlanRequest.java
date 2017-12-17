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
import io.github.alessandrojean.mangachecklists.constant.JBC;
import io.github.alessandrojean.mangachecklists.domain.Manga;
import io.github.alessandrojean.mangachecklists.domain.Plan;
import io.github.alessandrojean.mangachecklists.fragment.ChecklistFragment;
import io.github.alessandrojean.mangachecklists.fragment.PlansFragment;

/**
 * Created by Desktop on 15/12/2017.
 */

public class JBCPlanRequest extends AsyncTask<Void, Void, List<Plan>> {
    private WeakReference<PlansFragment> fragment;

    private String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36";

    public JBCPlanRequest(PlansFragment plansFragment) {
        this.fragment = new WeakReference<>(plansFragment);
    }

    @Override
    protected List<Plan> doInBackground(Void... voids) {
        Document html;
        List<Plan> plans = new ArrayList<>();

        try {
            html = Jsoup
                    .connect(JBC.URL_PLAN)
                    .userAgent(USER_AGENT)
                    .get();

            Log.i("plans", JBC.URL_PLAN);

            Elements list = html.select(JBC.CSS_SELECT_PLAN);

            for (Element e : list) {
                if (isCancelled())
                    return null;

                Plan plan = getPlan(e);
                plans.add(plan);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return plans;
    }

    private Plan getPlan(Element element) {
        Plan plan = new Plan();

        Manga manga;

        Pattern pattern = Pattern.compile(JBC.PATTERN_INFO_PLAN);
        Matcher matcher = pattern.matcher(element.select("strong a").text());

        if (matcher.matches()) {
            manga = new Manga();

            manga.setName(matcher.group(1));
            manga.setVolume(matcher.group(2) == null ? -1 : Integer.parseInt(matcher.group(2)));
            manga.setUrl(element.select("strong a").attr("href"));

            plan.setManga(manga);
        }

        Log.i("manga-url", plan.getManga().getUrl());

        pattern = Pattern.compile(JBC.PATTERN_PLAN);
        matcher = pattern.matcher(element.text());

        Log.i("element.text", element.text());

        if (matcher.matches()) {
            plan.setSentDate(matcher.group(1), matcher.group(2), matcher.group(3));
            plan.getManga().setDate(plan.getSentDate());
            plan.setGift(matcher.group(4));
        }

        return plan;
    }

    @Override
    protected void onPostExecute(List<Plan> plans) {
        super.onPostExecute(plans);

        if (fragment.get() != null) {
            fragment.get().updatePlans(plans, true);
        }
    }
}
