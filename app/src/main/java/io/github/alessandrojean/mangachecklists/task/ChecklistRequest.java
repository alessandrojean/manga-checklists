package io.github.alessandrojean.mangachecklists.task;

import android.os.AsyncTask;
import android.util.Log;

import com.orhanobut.hawk.Hawk;
import java.lang.ref.WeakReference;
import java.util.List;

import io.github.alessandrojean.mangachecklists.fragment.ChecklistFragment;
import io.github.alessandrojean.mangachecklists.parser.checklist.ChecklistParser;
import io.github.alessandrojean.mangachecklists.domain.Manga;

/**
 * Created by Desktop on 14/12/2017.
 */

public class ChecklistRequest extends AsyncTask<Void, Void, List<Manga>> {
    private WeakReference<ChecklistFragment> fragment;
    private int month;
    private int year;
    private ChecklistParser parser;
    private boolean reloading;

    public ChecklistRequest(ChecklistFragment checklistFragment, int month, int year, ChecklistParser parser) {
        this.fragment = new WeakReference<>(checklistFragment);
        this.month = month;
        this.year = year;
        this.parser = parser;

        initList();
    }

    public boolean isReloading() {
        return reloading;
    }

    public void setReloading(boolean reloading) {
        this.reloading = reloading;
    }

    private void initList() {
        if (fragment.get() != null) {
            Hawk.init(fragment.get().getContext()).build();
        }
    }

    @Override
    protected List<Manga> doInBackground(Void... voids) {
        Log.d("MangaChecklists", "ChecklistRequest: doInBackground()");

        if (!reloading && Hawk.contains(parser.getChecklistKey() + month + year)) {
            List<Manga> hawkList = Hawk.get(parser.getChecklistKey() + month + year);

            for (Manga m : hawkList) {
                Log.d("MangaChecklists", "Getting " + m.getName() + " from Hawk.");
            }

            if (hawkList.size() > 0)
                return hawkList;
        }

        if (fragment.get() != null && !reloading) {
            fragment.get().showCorrectView(ChecklistFragment.STATE_LOADING);
        }

        List<Manga> mangas = parser.getChecklist(month, year);
        if (mangas != null)
            Hawk.put(parser.getChecklistKey() + month + year, mangas);

        return mangas;
    }

    @Override
    protected void onPostExecute(List<Manga> mangas) {
        super.onPostExecute(mangas);

        if (fragment.get() != null)
            fragment.get().showChecklist(mangas);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        parser.cancel();
    }
}
