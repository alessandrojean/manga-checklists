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
import io.github.alessandrojean.mangachecklists.parser.detail.NewPOPDetailParser;
import io.github.alessandrojean.mangachecklists.parser.detail.PaniniDetailParser;

import static io.github.alessandrojean.mangachecklists.parser.checklist.PaniniChecklistParser.CSS_SELECT;

/**
 * Created by Desktop on 19/12/2017.
 */

public class NewPOPChecklistParser extends ChecklistParser {
    private Context context;
    private ArrayList<ChecklistData> checklistDataList;
    private int checklistId;

    public static final String URL = "http://www.newpop.com.br/?p=";
    public static final String URL_IDS = "https://alessandrojean.github.io/manga-checklists/newpop/checklists.json";
    public static final String CSS_SELECT = "div.wp-caption.aligncenter";

    public static final String PATTERN_INFO = "^(.*?)(?: \\(.*\\))?(?:(?::)? Livro)?(?: #(\\d+)(?: de (?:#)?\\d+(?::)?)?)?(?: \\(.*| —.*| ai.*)?$";

    private static final String CHECKLIST_DATA_KEY = "checklist_data_newpop_key";
    private static final String CHECKLIST_DATA_OBTAINED_DATE = "checklist_data_obtained_date_newpop_key";

    public NewPOPChecklistParser(Context context) {
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

            Calendar obtainedDateCalendar = Calendar.getInstance();
            Calendar actualDate = getActualDate();

            obtainedDateCalendar.setTimeInMillis(obtainedDate);

            if (actualDate.after(obtainedDateCalendar) || obtainedDate == 0) {
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
        return URL + checklistId;
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
        return 3;
    }

    @Override
    public int getMinimumYear() {
        return 2016;
    }

    @Override
    public String getChecklistKey() {
        return "newpop_manga_list_key_";
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

        NewPOPDetailParser parser;

        for (Element e : list) {
            Manga m = getManga(e);

            parser = new NewPOPDetailParser(m);
            m = parser.getDetails();

            mangas.add(m);
        }

        return mangas;
    }

    private Manga getManga(Element e) {
        Manga manga = new Manga();

        manga.setThumbnailUrl(e.select("img").attr("src"));

        Pattern pattern = Pattern.compile(PATTERN_INFO);
        Matcher matcher = pattern.matcher(e.select("p.wp-caption-text").text());

        if (matcher.matches()) {
            manga.setName(matcher.group(1));
            manga.setVolume(matcher.group(2) == null ? -1 : Integer.parseInt(matcher.group(2)));
        }

        if (e.select("a").first() != null)
            if (e.select("a").first().attr("href").indexOf(".jpg") == -1)
                manga.setUrl(e.select("a").attr("href"));

        manga.setType(Manga.TYPE_NEWPOP);

        return manga;
    }
}
