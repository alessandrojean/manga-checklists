package io.github.alessandrojean.mangachecklists.parser.plan;

import android.util.Log;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.alessandrojean.mangachecklists.domain.Manga;
import io.github.alessandrojean.mangachecklists.domain.Plan;
import io.github.alessandrojean.mangachecklists.parser.detail.JBCDetailParser;

/**
 * Created by Desktop on 18/12/2017.
 */

public class JBCPlanParser extends PlanParser {

    public static final String CSS_SELECT_PLAN = "div.box.mb-md ul li";

    public static final String PATTERN_INFO_PLAN = "^(.*?)(?: #(\\d+))?$";
    public static final String PATTERN_PLAN = "^(?:.*?) - enviado em (\\d+)\\/(\\d+)\\/(\\d+)(?: - (.*))?";

    @Override
    protected String getUrl() {
        return "https://mangasjbc.com.br/assinantes/";
    }

    @Override
    public String getPlanKey() {
        return "jbc_plans_list_key";
    }

    @Override
    protected List<Plan> parseHtml(Document html) {
        List<Plan> plans = new ArrayList<>();

        Elements list = html.select(CSS_SELECT_PLAN);

        for (Element e : list) {
            Plan plan = getPlan(e);
            plans.add(plan);
        }

        return plans;
    }

    private Plan getPlan(Element element) {
        Plan plan = new Plan();

        Manga manga;
        JBCDetailParser jbcDetailParser;

        Pattern pattern = Pattern.compile(PATTERN_INFO_PLAN);
        Matcher matcher = pattern.matcher(element.select("strong a").text());

        if (matcher.matches()) {
            manga = new Manga();

            manga.setName(matcher.group(1));
            manga.setVolume(matcher.group(2) == null ? -1 : Integer.parseInt(matcher.group(2)));
            manga.setUrl(element.select("strong a").attr("href"));

            //jbcDetailParser = new JBCDetailParser(manga);
            //manga = jbcDetailParser.getDetails();

            plan.setManga(manga);
        }

        Log.i("manga-url", plan.getManga().getUrl());

        pattern = Pattern.compile(PATTERN_PLAN);
        matcher = pattern.matcher(element.text());

        Log.i("element.text", element.text());

        if (matcher.matches()) {
            plan.setSentDate(matcher.group(1), matcher.group(2), matcher.group(3));
            plan.getManga().setDate(plan.getSentDate());
            plan.setGift(matcher.group(4));
        }

        return plan;
    }
}
