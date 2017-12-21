package io.github.alessandrojean.mangachecklists.task;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.github.alessandrojean.mangachecklists.parser.checklist.ChecklistParser;
import io.github.alessandrojean.mangachecklists.domain.Manga;
import io.github.alessandrojean.mangachecklists.fragment.ChecklistFragment;

/**
 * Created by Desktop on 14/12/2017.
 */

public class ChecklistRequest extends AsyncTask<Void, Void, List<Manga>> {
    private WeakReference<ChecklistFragment> fragment;
    private int month;
    private int year;
    private ChecklistParser parser;

    public ChecklistRequest(ChecklistFragment checklistFragment, int month, int year, ChecklistParser parser) {
        this.fragment = new WeakReference<>(checklistFragment);
        this.month = month;
        this.year = year;
        this.parser = parser;
    }

    @Override
    protected List<Manga> doInBackground(Void... voids) {
        return parser.getChecklist(month, year);
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
        parser.cancel();
    }
}
