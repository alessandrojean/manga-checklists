package io.github.alessandrojean.mangachecklists.parser.checklist;

import android.net.Uri;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.alessandrojean.mangachecklists.domain.Checklist;
import io.github.alessandrojean.mangachecklists.domain.ChecklistData;
import io.github.alessandrojean.mangachecklists.domain.Manga;
import io.github.alessandrojean.mangachecklists.parser.detail.JBCDetailParser;

/**
 * Created by Desktop on 18/12/2017.
 */

public class JBCChecklistParser extends ChecklistParser {

    private ArrayList<ChecklistData> checklistDataList;

    private static final String[] MONTHS = {"janeiro", "fevereiro", "marco", "abril", "maio",
            "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"};

    public static final String URL = "https://mangasjbc.com.br/checklist-%s-%d/";

    public static final String CSS_SELECT_BEFORE_MARCH_2016 = "ul.checklist li";
    public static final String CSS_SELECT_AFTER_MARCH_2016 = "ul.checklist li.adjust-font-size a";

    public static final String PATTERN_INFO = "^(.*?)(?: #(\\d+))?(?: (?:–|-) (\\d+)ª temp.)?(?: (?:–|-) (\\d+)\\/(\\d+))?$";
    public static final String PATTERN_IMAGE = "^(?:.*)(\\d{3})x(\\d{3}).(?:.*)$";

    public JBCChecklistParser() {
        checklistDataList = new ArrayList<>();
        initChecklistDataList();
    }

    private void initChecklistDataList() {
        int minimumYear = getMinimumYear();
        int minimumMonth = getMinimumMonth();

        Calendar actualDate = Calendar.getInstance();

        int maxMonth = actualDate.get(Calendar.MONTH) + 1;
        int maxYear = actualDate.get(Calendar.YEAR);

        int m = maxYear - minimumYear + 1;

        for (int i = 0; i < m; i++) {
            ChecklistData checklistData = new ChecklistData();
            checklistData.setYear(minimumYear + i);

            List<Checklist> checklists = new ArrayList<>();

            int n = i == m - 1 ? maxMonth : 12;
            int s = i == 0 ? minimumMonth - 1 : 0;

            for (int j = s; j < n; j++) {
                Checklist checklist = new Checklist();
                checklist.setMonth(j + 1);

                //Log.d("checklist-data", "Adding "+ (j + 1) + "/" + (minimumYear + i));

                checklists.add(checklist);
            }

            checklistData.setChecklists(checklists);

            checklistDataList.add(checklistData);
        }
    }

    @Override
    protected String getUrl(int month, int year) {
        return String.format(URL, MONTHS[month - 1], year);
    }

    @Override
    public int getMinimumMonth() {
        return 11;
    }

    @Override
    public int getMinimumYear() {
        return 2013;
    }

    @Override
    public String getChecklistKey() {
        return "jbc_manga_list_key_";
    }

    @Override
    public ArrayList<ChecklistData> getAvailableChecklists() {
        return checklistDataList;
    }

    @Override
    protected List<Manga> parseHtml(Document html, int month, int year) {
        List<Manga> mangas = new ArrayList<>();

        Elements list = html.select(
                (isDateAfter(month, year))
                        ? CSS_SELECT_AFTER_MARCH_2016
                        : CSS_SELECT_BEFORE_MARCH_2016
        );

        JBCDetailParser parser;

        for (Element e : list) {
            Manga m = getManga(e, month, year);

            parser = new JBCDetailParser(m);
            m = parser.getDetails();

            mangas.add(m);
        }

        return mangas;
    }

    private boolean isDateAfter(int month, int year) {
        Calendar baseDate = Calendar.getInstance();
        Calendar checklistDate = Calendar.getInstance();

        baseDate.set(2016, 2, 0);
        checklistDate.set(year, month, 0);

        return checklistDate.after(baseDate);
    }

    private Manga getManga(Element element, int month, int year) {
        Manga manga = new Manga();

        String info = element.text();
        Pattern pattern = Pattern.compile(PATTERN_INFO);
        Matcher matcher = pattern.matcher(info);


        if (matcher.matches()) {
            manga.setName(matcher.group(1));
            manga.setVolume(matcher.group(2) == null ? -1 : Integer.parseInt(matcher.group(2)));

            if (matcher.group(4) != null && matcher.group(5) != null)
                manga.setDate(matcher.group(4), matcher.group(5), year);
        }

        if (!isDateAfter(month, year))
            manga.setThumbnailUrl(fixUrl(element.select("noscript img").attr("src")));
        else {
            manga.setUrl(element.attr("href"));
            Log.d("manga-url", element.attr("href"));

            String thumbnailUrl = fixUrl(element.select("img").attr("src"));
            //manga.setThumbnailUrl(getBetterThumbnail(manga.getUrl(), thumbnailUrl));
            manga.setThumbnailUrl(thumbnailUrl);
        }

        manga.setType(Manga.TYPE_JBC);

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

    private String increaseThumbnail(String urlStr) {
        Pattern pattern = Pattern.compile(PATTERN_IMAGE);
        Matcher matcher = pattern.matcher(urlStr);

        if(matcher.matches()) {
            int width = Integer.parseInt(matcher.group(1));
            int height = Integer.parseInt(matcher.group(2));

            int newHeight = (300 * height) / width;

            return urlStr.replace(width + "x" + height, "300x" + newHeight);
        }

        return urlStr;
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
            e.printStackTrace();
            return thumbnailUrl;
        }
    }

    /**
     * Remove the accents from the URL.
     */
    private String fixUrl(String urlStr) {
        return Uri.encode(urlStr, "@#&=*+-_.,:!?()/~'%");
    }
}
