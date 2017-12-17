package io.github.alessandrojean.mangachecklists.task;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.alessandrojean.mangachecklists.MainActivity;
import io.github.alessandrojean.mangachecklists.constant.JBC;
import io.github.alessandrojean.mangachecklists.domain.Manga;
import io.github.alessandrojean.mangachecklists.fragment.ChecklistFragment;

/**
 * Created by Desktop on 14/12/2017.
 */

public class JBCChecklistRequest extends AsyncTask<Void, Void, List<Manga>> {
    private WeakReference<ChecklistFragment> fragment;
    private int month;
    private int year;

    private String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36";

    private static final String[] MONTHS = {"janeiro", "fevereiro", "marco", "abril", "maio",
        "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"};

    public JBCChecklistRequest(ChecklistFragment checklistFragment, int month, int year) {
        this.fragment = new WeakReference<>(checklistFragment);
        this.month = month;
        this.year = year;
    }

    @Override
    protected List<Manga> doInBackground(Void... voids) {
        Document html;
        List<Manga> mangas = new ArrayList<>();
        String url = String.format(JBC.URL, MONTHS[month - 1], year);

        Log.i("checklist", "Getting checklist: " + url);

        try {
            html = Jsoup
                    .connect(url)
                    .userAgent(USER_AGENT)
                    .get();

            Elements list = html.select(
                    (year < 2016 || (year == 2016 && month < 3))
                    ? JBC.CSS_SELECT_BEFORE_MARCH_2016
                    : JBC.CSS_SELECT_AFTER_MARCH_2016
            );

            for (Element e : list) {
                if (isCancelled())
                    return null;

                Manga m = getManga(e);
                mangas.add(m);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Collections.sort(mangas);

        return mangas;
    }

    private Manga getManga(Element element) {
        Manga manga = new Manga();

        String info = element.text();
        Pattern pattern = Pattern.compile(JBC.PATTERN_INFO);
        Matcher matcher = pattern.matcher(info);


        if (matcher.matches()) {
            manga.setName(matcher.group(1));
            manga.setVolume(matcher.group(2) == null ? -1 : Integer.parseInt(matcher.group(2)));

            if (matcher.group(4) != null && matcher.group(5) != null)
                manga.setDate(matcher.group(4), matcher.group(5), year);
        }

        if (year < 2016 || (year == 2016 && month < 3))
            manga.setThumbnailUrl(fixUrl(element.select("noscript img").attr("src")));
        else {
            manga.setUrl(element.attr("href"));

            String thumbnailUrl = fixUrl(element.select("img").attr("src"));
            manga.setThumbnailUrl(getBetterThumbnail(manga.getUrl(), thumbnailUrl));
        }

        Log.i("thumbnail-url", manga.getThumbnailUrl());

        return manga;
    }

    private String getBetterThumbnail(String link, String thumbnailUrl) {
        String increasedUrl = increaseThumbnail(thumbnailUrl);

        if (imageExists(increasedUrl))
            return increasedUrl;
        else
            return getOutThumbnail(link, thumbnailUrl);
    }

    private String getOutThumbnail(String link, String thumbnailUrl) {
        Document html;

        try {
            html = Jsoup
                    .connect(link)
                    .userAgent(USER_AGENT)
                    .get();

            return html.select("img.center-block.mb20").attr("src");
        }
        catch (IOException | IllegalArgumentException e) {
            return thumbnailUrl;
        }
    }

    private boolean imageExists(String urlStr) {
        try {
            URL url = new URL(urlStr);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("HEAD");
            httpURLConnection.connect();

            return httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Remove the accents from the URL.
     */
    private String fixUrl(String urlStr) {
        return Uri.encode(urlStr, "@#&=*+-_.,:!?()/~'%");
    }

    private String increaseThumbnail(String urlStr) {
        Pattern pattern = Pattern.compile(JBC.PATTERN_IMAGE);
        Matcher matcher = pattern.matcher(urlStr);

        if(matcher.matches()) {
            int width = Integer.parseInt(matcher.group(1));
            int height = Integer.parseInt(matcher.group(2));

            int newHeight = (300 * height) / width;

            return urlStr.replace(width + "x" + height, "300x" + newHeight);
        }

        return urlStr;
    }

    @Override
    protected void onPostExecute(List<Manga> mangas) {
        super.onPostExecute(mangas);

        if (fragment.get() != null) {
            fragment.get().updateChecklist(mangas, true);
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
