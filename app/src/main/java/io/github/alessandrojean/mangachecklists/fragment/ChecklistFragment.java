package io.github.alessandrojean.mangachecklists.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.orhanobut.hawk.Hawk;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.alessandrojean.mangachecklists.MainActivity;
import io.github.alessandrojean.mangachecklists.R;
import io.github.alessandrojean.mangachecklists.adapter.MangasAdapter;
import io.github.alessandrojean.mangachecklists.domain.Checklist;
import io.github.alessandrojean.mangachecklists.parser.checklist.ChecklistParser;
import io.github.alessandrojean.mangachecklists.parser.checklist.JBCChecklistParser;
import io.github.alessandrojean.mangachecklists.parser.checklist.PaniniChecklistParser;
import io.github.alessandrojean.mangachecklists.constant.JBC;
import io.github.alessandrojean.mangachecklists.domain.Manga;
import io.github.alessandrojean.mangachecklists.task.ChecklistRequest;

/**
 * Created by Desktop on 16/12/2017.
 */

public class ChecklistFragment extends FragmentAbstract implements DatePickerDialog.OnDateSetListener {
    public static final String TITLE = "Checklist";

    private static final String ACTUAL_MONTH_KEY = "actual_month_key";
    private static final String ACTUAL_YEAR_KEY = "actual_year_key";
    private static final String ACTUAL_FILTER_ID_KEY = "actual_filter_id_key";

    private MangasAdapter mangasAdapter;

    private List<Manga> mangas;
    private int actualMonth;
    private int actualYear;
    private int actualFilterId;

    private ChecklistRequest checklistRequest;
    private ChecklistParser checklistParser;

    public ChecklistFragment() {
        super();

        this.mangas = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        actualMonth = getArguments().getInt(MainActivity.CHECKLIST_ACTUAL_MONTH_KEY);
        actualYear = getArguments().getInt(MainActivity.CHECKLIST_ACTUAL_YEAR_KEY);
        actualFilterId = getArguments().getInt(MainActivity.CHECKLIST_ACTUAL_FILTER_ID, R.id.action_filter_jbc);
        checklistParser = getCorrectParser(actualFilterId);

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
            actualFilterId = savedInstanceState.getInt(ACTUAL_FILTER_ID_KEY);
            checklistParser = getCorrectParser(actualFilterId);
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
        outState.putInt(ACTUAL_FILTER_ID_KEY, actualFilterId);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (checklistRequest != null && checklistRequest.getStatus() == ChecklistRequest.Status.RUNNING) {
            checklistRequest.cancel(true);
            checklistRequest = null;
        }
    }

    protected void initDateDialog() {
        Bundle bundle = new Bundle();
        bundle.putInt(MonthYearPickerDialog.MINIMUM_MONTH, checklistParser.getMinimumMonth());
        bundle.putInt(MonthYearPickerDialog.MINIMUM_YEAR, checklistParser.getMinimumYear());
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

        if (!Hawk.contains(checklistParser.getChecklistKey() + actualMonth + actualYear))
            Hawk.put(checklistParser.getChecklistKey() + actualMonth + actualYear, mangas);

        List<Manga> hawkChecklist = Hawk.get(checklistParser.getChecklistKey() + actualMonth + actualYear);
        mangas.addAll(hawkChecklist);
    }

    private void retrieveMangas() {
        actualMonth = getArguments().getInt(MainActivity.CHECKLIST_ACTUAL_MONTH_KEY);
        actualYear = getArguments().getInt(MainActivity.CHECKLIST_ACTUAL_YEAR_KEY);

        retrieveMangas(actualMonth, actualYear);
    }

    private void retrieveMangas(int month, int year) {
        if (!isReloading && Hawk.contains(checklistParser.getChecklistKey() + month + year)) {
            List<Manga> hawkList = Hawk.get(checklistParser.getChecklistKey() + month + year);

            if (hawkList.size() != 0) {
                updateChecklist(hawkList, false);
                return;
            }
        }

        if (!isReloading)
            crossfade(true);

        //checklistRequest = new ChecklistRequest(this, month, year, new JBCChecklistParser());
        //checklistRequest = new ChecklistRequest(this, month, year, new PaniniChecklistParser(getContext()));
        checklistRequest = new ChecklistRequest(this, month, year, checklistParser);
        checklistRequest.execute();
    }

    public void updateChecklist(List<Manga> mangas, boolean animate) {
        if (mangas != null) {
            this.mangas.clear();
            this.mangas.addAll(mangas);

            Hawk.put(checklistParser.getChecklistKey() + actualMonth + actualYear, mangas);
            mangasAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);

            GridLayoutManager layoutManager = (GridLayoutManager) recyclerView.getLayoutManager();
            layoutManager.scrollToPositionWithOffset(0, 0);

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_checklists, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        SubMenu menuFilter = menu.getItem(0).getSubMenu();

        for (int i = 0; i < menuFilter.size(); i++)
            if (menuFilter.getItem(i).getItemId() == actualFilterId)
                menuFilter.getItem(i).setChecked(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_filter_jbc:
                actualFilterId = R.id.action_filter_jbc;
                break;
            case R.id.action_filter_panini:
                actualFilterId = R.id.action_filter_panini;
                break;
        }

        ChecklistParser oldParser = checklistParser;

        ((MainActivity) getActivity()).setActualChecklistFilterId(actualFilterId);
        checklistParser = getCorrectParser(actualFilterId);
        item.setChecked(true);

        if (isDateBeforeMinimum(oldParser, checklistParser)) {
            actualMonth = checklistParser.getMinimumMonth();
            actualYear = checklistParser.getMinimumYear();
        }

        retrieveMangas(actualMonth, actualYear);

        return true;
    }

    private boolean isDateBeforeMinimum(ChecklistParser oldParser, ChecklistParser newParser) {
        Calendar oldDate = Calendar.getInstance();
        Calendar newDate = Calendar.getInstance();
        Calendar actualDate = Calendar.getInstance();

        oldDate.set(oldParser.getMinimumYear(), oldParser.getMinimumMonth() - 1, 0);
        newDate.set(newParser.getMinimumYear(), newParser.getMinimumMonth() - 1, 0);
        actualDate.set(actualYear, actualMonth, 0);

        return newDate.after(oldDate) && oldDate.equals(actualDate);
    }

    private ChecklistParser getCorrectParser(int filterId) {
        switch (filterId) {
            case R.id.action_filter_jbc:
                return new JBCChecklistParser();
            case R.id.action_filter_panini:
                return new PaniniChecklistParser(getContext());
            default:
                return null;
        }
    }
}
