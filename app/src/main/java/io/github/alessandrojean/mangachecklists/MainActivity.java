package io.github.alessandrojean.mangachecklists;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.github.alessandrojean.mangachecklists.fragment.ChecklistFragment;
import io.github.alessandrojean.mangachecklists.fragment.PlansFragment;

/**
 * Created by Desktop on 16/12/2017.
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Save to bundle.
    private static final String TOOLBAR_TITLE_KEY = "toolbar_title_key";
    private static final String TOOLBAR_SUBTITLE_KEY = "toolbar_subtitle_key";
    private static final String CHECKLIST_ACTUAL_MONTH_KEY = "checklist_actual_month_key";
    private static final String CHECKLIST_ACTUAL_YEAR_KEY = "checklist_actual_year_key";
    private static final String CHECKLIST_ACTUAL_FILTER_ID = "checklist_actual_filter_id_key";

    // Views.
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    // Class variables.
    private int checklistActualMonth = 0;
    private int checklistActualYear = 0;
    private int checklistActualFilterId = R.id.action_filter_jbc;
    private String title = ChecklistFragment.TITLE;
    private String subtitle = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        initDrawerNavigation();

        if (savedInstanceState != null) {
            title = savedInstanceState.getString(TOOLBAR_TITLE_KEY, title);
            subtitle = savedInstanceState.getString(TOOLBAR_SUBTITLE_KEY, subtitle);
            checklistActualMonth = savedInstanceState.getInt(CHECKLIST_ACTUAL_MONTH_KEY);
            checklistActualYear = savedInstanceState.getInt(CHECKLIST_ACTUAL_YEAR_KEY);
            checklistActualFilterId = savedInstanceState.getInt(CHECKLIST_ACTUAL_FILTER_ID);
        }

        Log.d("date-oncreate-bif", checklistActualMonth + "/" + checklistActualYear);

        if (getSupportFragmentManager().findFragmentByTag(ChecklistFragment.KEY) == null) {
            setNowDateToChecklist();
            switchFragment(
                    ChecklistFragment.newInstance(
                            checklistActualMonth,
                            checklistActualYear,
                            checklistActualFilterId
                    ),
                    ChecklistFragment.KEY
            );
        }

        Log.d("date-oncreate-aif", checklistActualMonth + "/" + checklistActualYear);
    }

    private void initDrawerNavigation() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        String key = null;

        switch (item.getItemId()) {
            case R.id.nav_checklist:
                fragment = ChecklistFragment.newInstance(
                        checklistActualMonth,
                        checklistActualYear,
                        checklistActualFilterId
                );
                key = ChecklistFragment.KEY;
                title = ChecklistFragment.TITLE;
                showDateInToolbar();
                break;
            case R.id.nav_plans:
                fragment = new PlansFragment();
                key = PlansFragment.KEY;
                title = PlansFragment.TITLE;
                subtitle = "";
                break;
            case R.id.nav_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
        }

        switchFragment(fragment, key);

        toolbar.setTitle(title);
        toolbar.setSubtitle(subtitle);

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void switchFragment(Fragment fragment, String key) {
        getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.rl_container, fragment, key)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle(title);
        toolbar.setSubtitle(subtitle);

        Log.d("MangaChecklists", "subtitle=" + subtitle);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(TOOLBAR_TITLE_KEY, title);
        outState.putString(TOOLBAR_SUBTITLE_KEY, toolbar.getSubtitle().toString());
        outState.putInt(CHECKLIST_ACTUAL_MONTH_KEY, checklistActualMonth);
        outState.putInt(CHECKLIST_ACTUAL_YEAR_KEY, checklistActualYear);
        outState.putInt(CHECKLIST_ACTUAL_FILTER_ID, checklistActualFilterId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        title = savedInstanceState.getString(TOOLBAR_TITLE_KEY, title);
        subtitle = savedInstanceState.getString(TOOLBAR_SUBTITLE_KEY, subtitle);
        checklistActualMonth = savedInstanceState.getInt(CHECKLIST_ACTUAL_MONTH_KEY);
        checklistActualYear = savedInstanceState.getInt(CHECKLIST_ACTUAL_YEAR_KEY);
        checklistActualFilterId = savedInstanceState.getInt(CHECKLIST_ACTUAL_FILTER_ID);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setNowDateToChecklist() {
        if (checklistActualMonth == 0 && checklistActualYear == 0) {
            Calendar calendar = Calendar.getInstance();

            checklistActualMonth = calendar.get(Calendar.MONTH) + 1;
            checklistActualYear = calendar.get(Calendar.YEAR);

            showDateInToolbar();
        }
    }

    public void setActualChecklist(int month, int year) {
        this.checklistActualMonth = month;
        this.checklistActualYear = year;

        showDateInToolbar();
    }

    public void setActualChecklistFilterId(int filterId) {
        this.checklistActualFilterId = filterId;
    }

    private void showDateInToolbar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(checklistActualYear, checklistActualMonth, 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM/yyyy");
        String formatted = dateFormat.format(calendar.getTime());

        formatted = Character.toUpperCase(formatted.charAt(0)) + formatted.substring(1);

        toolbar.setSubtitle(formatted);
        subtitle = formatted;
    }
}
