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
import io.github.alessandrojean.mangachecklists.parser.detail.DetailParser;

/**
 * Created by Desktop on 16/12/2017.
 */

public class DetailsRequest extends AsyncTask<Void, Void, Manga> {
    private WeakReference<MangaDetailsActivity> activity;
    private DetailParser parser;

    public DetailsRequest(MangaDetailsActivity activity, DetailParser parser) {
        this.activity = new WeakReference<>(activity);
        this.parser = parser;
    }

    @Override
    protected Manga doInBackground(Void... voids) {
        return parser.getDetails();
    }

    @Override
    protected void onPostExecute(Manga manga) {
        super.onPostExecute(manga);

        if (activity.get() != null) {
            activity.get().showInformation(manga, true);
        }
    }
}
