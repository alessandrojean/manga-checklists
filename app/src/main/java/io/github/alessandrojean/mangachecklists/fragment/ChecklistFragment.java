package io.github.alessandrojean.mangachecklists.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;

import com.orhanobut.hawk.Hawk;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.alessandrojean.mangachecklists.MainActivity;
import io.github.alessandrojean.mangachecklists.adapter.MangasAdapter;
import io.github.alessandrojean.mangachecklists.constant.JBC;
import io.github.alessandrojean.mangachecklists.domain.Manga;
import io.github.alessandrojean.mangachecklists.task.JBCChecklistRequest;

/**
 * Created by Desktop on 16/12/2017.
 */

public class ChecklistFragment extends FragmentAbstract implements DatePickerDialog.OnDateSetListener {
    public static final String TITLE = "Checklist";

    private static final String ACTUAL_MONTH_KEY = "actual_month_key";
    private static final String ACTUAL_YEAR_KEY = "actual_year_key";

    private MangasAdapter mangasAdapter;

    private List<Manga> mangas;
    private int actualMonth;
    private int actualYear;

    private JBCChecklistRequest jbcChecklistRequest;

    public ChecklistFragment() {
        super();

        this.mangas = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        actualMonth = getArguments().getInt(MainActivity.CHECKLIST_ACTUAL_MONTH_KEY);
        actualYear = getArguments().getInt(MainActivity.CHECKLIST_ACTUAL_YEAR_KEY);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDateDialog();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isReloading = true;

                retrieveMangas(actualMonth, actualYear);
            }
        });

        actualMonth = getArguments().getInt(MainActivity.CHECKLIST_ACTUAL_MONTH_KEY);
        actualYear = getArguments().getInt(MainActivity.CHECKLIST_ACTUAL_YEAR_KEY);

        if (savedInstanceState != null) {
            actualMonth = savedInstanceState.getInt(ACTUAL_MONTH_KEY);
            actualYear = savedInstanceState.getInt(ACTUAL_YEAR_KEY);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        mangasAdapter = new MangasAdapter(getContext(), mangas);
        recyclerView.setAdapter(mangasAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        initList();
        //if (isLoading || isReloading)
            retrieveMangas(actualMonth, actualYear);
        //else
            //retrieveMangas();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ACTUAL_MONTH_KEY, actualMonth);
        outState.putInt(ACTUAL_YEAR_KEY, actualYear);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (jbcChecklistRequest != null && jbcChecklistRequest.getStatus() == JBCChecklistRequest.Status.RUNNING) {
            jbcChecklistRequest.cancel(true);
            jbcChecklistRequest = null;
        }
    }

    protected void initDateDialog() {
        Bundle bundle = new Bundle();
        bundle.putInt(MonthYearPickerDialog.MINIMUM_MONTH, JBC.MINIMUM_MONTH);
        bundle.putInt(MonthYearPickerDialog.MINIMUM_YEAR, JBC.MINIMUM_YEAR);
        bundle.putInt(MonthYearPickerDialog.SELECTED_MONTH, actualMonth);
        bundle.putInt(MonthYearPickerDialog.SELECTED_YEAR, actualYear);

        MonthYearPickerDialog monthYearPickerDialog = new MonthYearPickerDialog();
        monthYearPickerDialog.setArguments(bundle);
        monthYearPickerDialog.setListener(this);
        monthYearPickerDialog.show(getActivity().getSupportFragmentManager(), MonthYearPickerDialog.KEY);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        if (actualYear != year || actualMonth != month) {
            actualMonth = month;
            actualYear = year;

            ((MainActivity) getActivity()).setActualChecklist(actualMonth, actualYear);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("");

            isLoading = true;
            retrieveMangas(month, year);
        }
    }

    private void initList() {
        Hawk.init(getContext()).build();

        if (!Hawk.contains(JBC.MANGA_LIST_KEY + actualMonth + actualYear))
            Hawk.put(JBC.MANGA_LIST_KEY + actualMonth + actualYear, mangas);

        List<Manga> hawkChecklist = Hawk.get(JBC.MANGA_LIST_KEY + actualMonth + actualYear);
        mangas.addAll(hawkChecklist);
    }

    private void retrieveMangas() {
        actualMonth = getArguments().getInt(MainActivity.CHECKLIST_ACTUAL_MONTH_KEY);
        actualYear = getArguments().getInt(MainActivity.CHECKLIST_ACTUAL_YEAR_KEY);

        retrieveMangas(actualMonth, actualYear);
    }

    private void retrieveMangas(int month, int year) {
        if (!isReloading && Hawk.contains(JBC.MANGA_LIST_KEY + month + year)) {
            List<Manga> hawkList = Hawk.get(JBC.MANGA_LIST_KEY + month + year);

            if (hawkList.size() != 0) {
                updateChecklist(hawkList, false);
                return;
            }
        }

        if (!isReloading)
            crossfade(true);

        jbcChecklistRequest = new JBCChecklistRequest(this, month, year);
        jbcChecklistRequest.execute();
    }

    public void updateChecklist(List<Manga> mangas, boolean animate) {
        if (mangas != null) {
            this.mangas.clear();
            this.mangas.addAll(mangas);

            Hawk.put(JBC.MANGA_LIST_KEY + actualMonth + actualYear, mangas);
            mangasAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);

            showDateInToolbar();
            if (animate && !isReloading)
                crossfade(false);
            else {
                floatingActionButton.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }

        isReloading = false;
        isLoading = false;
    }

    private void showDateInToolbar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(actualYear, actualMonth, 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM/yyyy");
        String formatted = dateFormat.format(calendar.getTime());

        formatted = Character.toUpperCase(formatted.charAt(0)) + formatted.substring(1);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(formatted);
    }
}
