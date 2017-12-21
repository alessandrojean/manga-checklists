package io.github.alessandrojean.mangachecklists.parser.checklist;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import io.github.alessandrojean.mangachecklists.domain.ChecklistData;
import io.github.alessandrojean.mangachecklists.domain.Manga;

/**
 * Created by Desktop on 18/12/2017.
 */

abstract public class ChecklistParser {
    protected static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.84 Safari/537.36";
    protected boolean canceled;


    protected String getUrl(int month, int year) {
        return null;
    }

    public int getMinimumMonth() {
        return 0;
    }

    public int getMinimumYear() {
        return 0;
    }

    public String getChecklistKey() {
        return "";
    }

    public ArrayList<ChecklistData> getAvailableChecklists() {
        return null;
    }

    public List<Manga> getChecklist(int month, int year) {
        Document html;
        List<Manga> mangas = new ArrayList<>();

        if (!isDateAfterMinimum(month, year))
            return mangas;

        Log.i("checklist", "Getting checklist: " + getUrl(month, year));

        try {
            html = Jsoup
                    .connect(getUrl(month, year))
                    .userAgent(USER_AGENT)
                    .get();

            mangas = parseHtml(html, month, year);
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Collections.sort(mangas);

        return mangas;
    }

    private boolean isDateAfterMinimum(int month, int year) {
        Calendar baseDate = Calendar.getInstance();
        Calendar checklistDate = Calendar.getInstance();

        baseDate.set(getMinimumYear(), getMinimumMonth() - 1, 0);
        checklistDate.set(year, month, 0);

        return checklistDate.after(baseDate);
    }

    protected List<Manga> parseHtml(Document html, int month, int year) {
        return null;
    }

    public void cancel() {
        this.canceled = true;
    }

    public boolean isCanceled() {
        return canceled;
    }
}
