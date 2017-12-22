package io.github.alessandrojean.mangachecklists.parser.plan;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.github.alessandrojean.mangachecklists.domain.Plan;

/**
 * Created by Desktop on 18/12/2017.
 */

public abstract class PlanParser {
    protected static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36";
    protected boolean canceled;
    protected Context context;

    public PlanParser(Context context) {
        this.context = context;
    }

    protected String getUrl() {
        return null;
    }

    public String getPlanKey() {
        return "";
    }

    protected boolean getLoadDetails() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.getBoolean("preference_details_plan", false);
    }

    public List<Plan> getPlans() {
        Document html;
        List<Plan> plans = new ArrayList<>();

        Log.i("plan", "Getting plans: " + getUrl());

        try {
            html = Jsoup
                    .connect(getUrl())
                    .userAgent(USER_AGENT)
                    .get();

            plans = parseHtml(html);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return plans;
    }

    protected List<Plan> parseHtml(Document html) {
        return null;
    }

    public void cancel() {
        this.canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }
}
