package io.github.alessandrojean.mangachecklists.parser.checklist;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.alessandrojean.mangachecklists.domain.Checklist;
import io.github.alessandrojean.mangachecklists.domain.ChecklistData;
import io.github.alessandrojean.mangachecklists.domain.Manga;
import io.github.alessandrojean.mangachecklists.parser.detail.PaniniDetailParser;

/**
 * Created by Desktop on 18/12/2017.
 */

public class PaniniChecklistParser extends ChecklistParser {
    private Context context;
    private ArrayList<ChecklistData> checklistDataList;
    private int checklistId;

    public static final String URL = "http://loja.panini.com.br/panini/solucoes/Busca.aspx?i=%d|%d,%d|%d&o=7";
    public static final String URL_IDS = "https://alessandrojean.github.io/manga-checklists/panini/checklists.json";
    public static final String CSS_SELECT = "div.product div.item";

    public static final String PATTERN_INFO = "^(?:Pré-Venda )?(.*?)(?: - (?:Edição|Volume Único)(?: (\\d+))?)?$";
    public static final String PATTERN_IMAGE = "^(?:.*)/(\\d+)_(\\d{3})x(\\d{3}).(?:.*)$";

    public static final int TYPE_BRAND = 40;
    public static final int BRAND_PLANET_MANGA = 1624;
    public static final int TYPE_CHECKLIST = 44;

    private static final String CHECKLIST_DATA_KEY = "checklist_data_panini_key";
    private static final String CHECKLIST_DATA_OBTAINED_DATE = "checklist_data_obtained_date_panini_key";

    public PaniniChecklistParser(Context context) {
        this.context = context;
        checklistDataList = new ArrayList<>();

        initChecklistIds();
    }

    private void initChecklistIds() {
        Hawk.init(context).build();

        if (!Hawk.contains(CHECKLIST_DATA_KEY)) {
            long nullDate = 0;

            Hawk.put(CHECKLIST_DATA_KEY, checklistDataList);
            Hawk.put(CHECKLIST_DATA_OBTAINED_DATE, nullDate);
        }

        List<ChecklistData> hawkList = Hawk.get(CHECKLIST_DATA_KEY);
        checklistDataList.clear();
        checklistDataList.addAll(hawkList);
    }

    private Calendar getActualDate() {
        Calendar actualDate = Calendar.getInstance();
        actualDate.set(Calendar.HOUR_OF_DAY, 0);
        actualDate.set(Calendar.MINUTE, 0);
        actualDate.set(Calendar.SECOND, 0);
        actualDate.set(Calendar.MILLISECOND, 0);

        return actualDate;
    }

    public List<ChecklistData> getChecklistDataFromAPI() {
        Log.d("checklist-api-panini", "Getting data from API.");

        try {
            String json = Jsoup
                            .connect(URL_IDS)
                            .ignoreContentType(true)
                            .execute()
                            .body();

            ChecklistData[] checklistDataArray = new Gson().fromJson(json, ChecklistData[].class);

            return Arrays.asList(checklistDataArray);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void obtainChecklistIdsFromAPI() {
        if (Hawk.contains(CHECKLIST_DATA_KEY)) {
            long obtainedDate = Hawk.get(CHECKLIST_DATA_OBTAINED_DATE);
            List<ChecklistData> hawkList = Hawk.get(CHECKLIST_DATA_KEY);

            Calendar obtainedDateCalendar = Calendar.getInstance();
            Calendar actualDate = getActualDate();

            obtainedDateCalendar.setTimeInMillis(obtainedDate);

            if (actualDate.after(obtainedDateCalendar) || obtainedDate == 0 || hawkList.size() == 0) {
                List<ChecklistData> checklistDataJson = getChecklistDataFromAPI();
                Hawk.put(CHECKLIST_DATA_KEY, checklistDataJson);
                Hawk.put(CHECKLIST_DATA_OBTAINED_DATE, actualDate.getTimeInMillis());
            }
        }

        List<ChecklistData> hawkList = Hawk.get(CHECKLIST_DATA_KEY);
        checklistDataList.clear();
        checklistDataList.addAll(hawkList);
    }


    @Override
    protected String getUrl(int month, int year) {
        obtainChecklistIdsFromAPI();

        checklistId = getChecklistId(month, year);
        return String.format(URL, TYPE_BRAND, BRAND_PLANET_MANGA, TYPE_CHECKLIST, checklistId);
    }

    private int getChecklistId(int month, int year) {
        for (ChecklistData checklistData : checklistDataList)
            if (checklistData.getYear() == year)
                for (Checklist checklist : checklistData.getChecklists())
                    if (checklist.getMonth() == month)
                        return checklist.getId();

        return -1;
    }

    @Override
    public int getMinimumMonth() {
        return 2;
    }

    @Override
    public int getMinimumYear() {
        return 2012;
    }

    @Override
    public String getChecklistKey() {
        return "panini_manga_list_key_";
    }

    @Override
    public ArrayList<ChecklistData> getAvailableChecklists() {
        return checklistDataList;
    }

    @Override
    protected List<Manga> parseHtml(Document html, int month, int year) {
        List<Manga> mangas = new ArrayList<>();

        if (checklistId == -1)
            return mangas;

        Elements list = html.select(CSS_SELECT);

        PaniniDetailParser parser;

        for (int i = 0; i < list.size(); i++) {
            if (isCanceled())
                return null;

            Element e = list.get(i);

            Manga m = getManga(e, month, year);

            parser = new PaniniDetailParser(m);
            m = parser.getDetails();

            if (onMangaLoaded != null)
                onMangaLoaded.onMangaLoaded(m, i, list.size());

            mangas.add(m);
        }

        return mangas;
    }

    private Manga getManga(Element e, int month, int year) {
        Manga manga = new Manga();
        Element image = e.select("div.image a img").first();
        Element info = e.select("div.description h4 a").first();
        Element price = e.select("div.description p.price").first();

        manga.setThumbnailUrl(getBetterThumbnail(image.attr("src")));
        manga.setPrice(Double.parseDouble(price.text().replace("R$", "").replace(",",".")));
        manga.setUrl(info.attr("href"));

        Pattern pattern = Pattern.compile(PATTERN_INFO);
        Matcher matcher = pattern.matcher(info.text());

        if (matcher.matches()) {
            manga.setName(matcher.group(1));
            manga.setVolume(matcher.group(2) == null ? -1 : Integer.parseInt(matcher.group(2)));
        }

        Log.i("thumbnail-url", manga.getThumbnailUrl());

        manga.setType(Manga.TYPE_PANINI);

        return manga;
    }

    private String getBetterThumbnail(String thumbnailUrl) {
        Pattern pattern = Pattern.compile(PATTERN_IMAGE);
        Matcher matcher = pattern.matcher(thumbnailUrl);

        if (matcher.matches()) {
            int imageId = Integer.parseInt(matcher.group(1));

            String newThumbnail = thumbnailUrl
                                    .replace(matcher.group(1), String.valueOf(imageId + 1))
                                    .replace("200x200", "520x520");
            return newThumbnail;
        }

        return thumbnailUrl;
    }
}
