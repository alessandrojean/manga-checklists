package io.github.alessandrojean.mangachecklists;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.os.PersistableBundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ProgressBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.github.alessandrojean.mangachecklists.adapter.MangasAdapter;
import io.github.alessandrojean.mangachecklists.constant.JBC;
import io.github.alessandrojean.mangachecklists.domain.Manga;
import io.github.alessandrojean.mangachecklists.fragment.MonthYearPickerDialog;
import io.github.alessandrojean.mangachecklists.mock.JBCMock;
import io.github.alessandrojean.mangachecklists.task.JBCRequest;
import me.zhanghai.android.materialprogressbar.CircularProgressDrawable;
import me.zhanghai.android.materialprogressbar.IndeterminateCircularProgressDrawable;

public class MainActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener{
    private MangasAdapter mangasAdapter;
    private ArrayList<Manga> mangas;

    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton fab;
    private ProgressBar progressBar;

    private int actualMonth, actualYear;
    private int shortAnimationDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            mangas = savedInstanceState.getParcelableArrayList(Manga.MANGAS_KEY);
            initViews();
        }
        else {
            mangas = new ArrayList<>();
            initViews();
            retrieveMangas();
        }

        initViews();
    }

    private void initViews() {
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initDateDialog();
            }
        });

        progressBar = findViewById(R.id.progress_bar);
        progressBar.setIndeterminateDrawable(new IndeterminateCircularProgressDrawable(this));

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                retrieveMangas(actualMonth, actualYear);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.mangas);
        recyclerView.setHasFixedSize(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        mangasAdapter = new MangasAdapter(this, mangas);
        recyclerView.setAdapter(mangasAdapter);

        swipeRefreshLayout.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);

        shortAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);
    }

    private void showContent() {
        swipeRefreshLayout.setAlpha(0f);
        fab.setAlpha(0f);
        swipeRefreshLayout.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);

        swipeRefreshLayout.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);
        fab.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);

        progressBar.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void showProgressBar() {
        progressBar.setAlpha(0f);
        progressBar.setVisibility(View.VISIBLE);

        progressBar.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);

        swipeRefreshLayout.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        swipeRefreshLayout.setVisibility(View.GONE);
                    }
                });

        fab.animate()
                .alpha(0f)
                .setDuration(shortAnimationDuration)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fab.setVisibility(View.GONE);
                    }
                });
    }

    private void crossfade(boolean inverse) {
        if (inverse)
            showProgressBar();
        else
            showContent();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putParcelableArrayList(Manga.MANGAS_KEY, mangas);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void initDateDialog() {
        Bundle bundle = new Bundle();
        bundle.putInt(MonthYearPickerDialog.MINIMUM_MONTH, JBC.MINIMUM_MONTH);
        bundle.putInt(MonthYearPickerDialog.MINIMUM_YEAR, JBC.MINIMUM_YEAR);
        bundle.putInt(MonthYearPickerDialog.SELECTED_MONTH, actualMonth);
        bundle.putInt(MonthYearPickerDialog.SELECTED_YEAR, actualYear);

        MonthYearPickerDialog monthYearPickerDialog = new MonthYearPickerDialog();
        monthYearPickerDialog.setArguments(bundle);
        monthYearPickerDialog.setListener(this);
        monthYearPickerDialog.show(getSupportFragmentManager(), MonthYearPickerDialog.KEY);
    }

    private void retrieveMangas() {
        Calendar calendar = Calendar.getInstance();

        actualMonth = calendar.get(Calendar.MONTH) + 1;
        actualYear = calendar.get(Calendar.YEAR);

        retrieveMangas(actualMonth, actualYear);
    }

    private void retrieveMangas(int month, int year) {
        new JBCRequest(this, month, year).execute();
    }

    public void updateLista(List<Manga> mangas) {
        this.mangas.clear();
        this.mangas.addAll(mangas);
        mangasAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);

        showDateInActionBar();
        crossfade(false);
    }

    private void showDateInActionBar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(actualYear, actualMonth, 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM/yyyy");
        String formatted = dateFormat.format(calendar.getTime());

        formatted = Character.toUpperCase(formatted.charAt(0)) + formatted.substring(1);

        getSupportActionBar().setSubtitle(formatted);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        if (actualYear != year || actualMonth != month) {
            actualMonth = month;
            actualYear = year;

            crossfade(true);
            new JBCRequest(this, month, year).execute();
        }
    }
}
