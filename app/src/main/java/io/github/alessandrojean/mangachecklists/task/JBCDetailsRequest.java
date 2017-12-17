package io.github.alessandrojean.mangachecklists.task;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import io.github.alessandrojean.mangachecklists.MangaDetailsActivity;
import io.github.alessandrojean.mangachecklists.domain.Detail;
import io.github.alessandrojean.mangachecklists.domain.DetailGroup;
import io.github.alessandrojean.mangachecklists.domain.Manga;

/**
 * Created by Desktop on 16/12/2017.
 */

public class JBCDetailsRequest extends AsyncTask<Void, Void, Manga> {
    private WeakReference<MangaDetailsActivity> activity;
    private Manga manga;

    private String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36";

    public JBCDetailsRequest(MangaDetailsActivity activity, Manga manga) {
        this.activity = new WeakReference<>(activity);
        this.manga = manga;
    }

    @Override
    protected Manga doInBackground(Void... voids) {
        Document html;

        try {
            html = Jsoup
                    .connect(manga.getUrl())
                    .userAgent(USER_AGENT)
                    .get();

            parseManga(html);
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return manga;
    }

    private void parseManga(Document html) {
        if (html.selectFirst("div.extra-info-container") != null)
            parseMangaNew(html);
        else
            parseMangaOld(html);

        // If manga is from a plan.
        if (manga.getThumbnailUrl() == null) {
            manga.setThumbnailUrl(fixUrl(html.select("img.center-block.mb20").attr("src")));
        }
    }

    private void parseMangaOld(Document html) {
        Element subtitle = html.selectFirst("em.text-center.excerpt");
        Element synopsis = html.selectFirst("div.mb30[itemprop=\"description\"] p");
        Element headerImage = html.selectFirst("img.colectionHeader.mb10");
        Elements detailsGroup = html.select("div.mb30[itemprop=\"description\"] p:has(strong)");

        manga.setSubtitle(subtitle.text());
        manga.setSynopsis(synopsis.text());

        if (headerImage != null)
            manga.setHeaderUrl(headerImage.attr("src"));

        List<DetailGroup> detailGroupList = new ArrayList<>();

        for (Element p : detailsGroup) {
            Element detailGroupName = p.previousElementSibling();

            DetailGroup detailGroup = new DetailGroup();
            detailGroup.setName(detailGroupName.text());

            List<Detail> detailList = new ArrayList<>();

            Elements details = p.select("strong");

            for (Element e : details) {
                Detail detail = new Detail();
                detail.setName(e.text().replace(":", ""));

                Node node = e.nextSibling();
                detail.setDetail(node.toString().trim());

                Log.d("detail", detail.getName() + detail.getDetail());

                detailList.add(detail);
            }

            detailGroup.setDetails(detailList);

            detailGroupList.add(detailGroup);
        }

        manga.setDetailGroups(detailGroupList);
    }

    private void parseMangaNew(Document html) {
        Element subtitle = html.selectFirst("em.text-center.excerpt");
        Element synopsis = html.selectFirst("div.mb30[itemprop=\"description\"] p");
        Element headerImage = html.selectFirst("img.colectionHeader.mb10");
        Elements detailsGroup = html.select("div.extra-info-col-content");

        manga.setSubtitle(subtitle.text());
        manga.setSynopsis(synopsis.text());

        if (headerImage != null)
            manga.setHeaderUrl(headerImage.attr("src"));

        List<DetailGroup> detailGroupList = new ArrayList<>();

        for (Element e : detailsGroup) {
            DetailGroup detailGroup = new DetailGroup();

            Element name = e.selectFirst("h2") == null
                    ? e.selectFirst("h3")
                    : e.selectFirst("h2");

            detailGroup.setName(name.text());

            List<Detail> detailsList = new ArrayList<>();

            Elements info = e.select("ul.extra-info li");

            for (Element f : info) {
                Detail detail = new Detail();
                detail.setName(f.select("strong").text().replace(":", ""));
                detail.setDetail(f.select("span").text());

                Log.d("detail", detail.getName() + detail.getDetail());

                detailsList.add(detail);
            }

            detailGroup.setDetails(detailsList);

            detailGroupList.add(detailGroup);
        }

        manga.setDetailGroups(detailGroupList);
    }

    private String fixUrl(String urlStr) {
        return Uri.encode(urlStr, "@#&=*+-_.,:!?()/~'%");
    }

    @Override
    protected void onPostExecute(Manga manga) {
        super.onPostExecute(manga);

        if (activity.get() != null) {
            activity.get().showInformation(manga, true);
        }
    }
}
