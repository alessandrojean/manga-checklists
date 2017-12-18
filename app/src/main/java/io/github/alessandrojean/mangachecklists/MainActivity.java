package io.github.alessandrojean.mangachecklists;

import android.os.Bundle;
import android.os.PersistableBundle;
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
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;

import io.github.alessandrojean.mangachecklists.fragment.ChecklistFragment;
import io.github.alessandrojean.mangachecklists.fragment.FragmentAbstract;
import io.github.alessandrojean.mangachecklists.fragment.PlansFragment;

/**
 * Created by Desktop on 16/12/2017.
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    String title = ChecklistFragment.TITLE;
    String subtitle = "";

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    private int checklistActualMonth = 0;
    private int checklistActualYear = 0;
    private int checklistActualFilterId = R.id.action_filter_jbc;

    public static final String CHECKLIST_ACTUAL_MONTH_KEY = "checklist_actual_month_key";
    public static final String CHECKLIST_ACTUAL_YEAR_KEY = "checklist_actual_year_key";
    public static final String CHECKLIST_ACTUAL_FILTER_ID = "checklist_actual_filter_id_key";

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
            title = savedInstanceState.getString(FragmentAbstract.TITLE, title);
            subtitle = savedInstanceState.getString(FragmentAbstract.SUBTITLE, subtitle);
            checklistActualMonth = savedInstanceState.getInt(CHECKLIST_ACTUAL_MONTH_KEY);
            checklistActualYear = savedInstanceState.getInt(CHECKLIST_ACTUAL_YEAR_KEY);
            checklistActualFilterId = savedInstanceState.getInt(CHECKLIST_ACTUAL_FILTER_ID);
        }

        Log.d("date-oncreate-bif", checklistActualMonth + "/" + checklistActualYear);

        if (getSupportFragmentManager().findFragmentByTag(FragmentAbstract.KEY) == null) {
            setNowDateToChecklist();
            switchFragment(new ChecklistFragment(), FragmentAbstract.CHECKLIST);
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
        int type = 0;

        switch (item.getItemId()) {
            case R.id.nav_checklist:
                fragment = new ChecklistFragment();
                title = ChecklistFragment.TITLE;
                type = FragmentAbstract.CHECKLIST;
                break;
            case R.id.nav_plans:
                fragment = new PlansFragment();
                title = PlansFragment.TITLE;
                type = FragmentAbstract.PLAN;
                break;
        }

        subtitle = "";

        toolbar.setTitle(title);
        toolbar.setSubtitle(subtitle);
        switchFragment(fragment, type);

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void switchFragment(Fragment fragment, int type) {
        Bundle bundle = new Bundle();
        bundle.putInt(FragmentAbstract.TYPE_KEY, type);
        bundle.putInt(CHECKLIST_ACTUAL_MONTH_KEY, checklistActualMonth);
        bundle.putInt(CHECKLIST_ACTUAL_YEAR_KEY, checklistActualYear);
        bundle.putInt(CHECKLIST_ACTUAL_FILTER_ID, checklistActualFilterId);

        fragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                //.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.rl_container, fragment, FragmentAbstract.KEY)
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        toolbar.setTitle(title);
        toolbar.setSubtitle(subtitle);


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(FragmentAbstract.TITLE, title);
        outState.putString(FragmentAbstract.SUBTITLE, subtitle);
        outState.putInt(CHECKLIST_ACTUAL_MONTH_KEY, checklistActualMonth);
        outState.putInt(CHECKLIST_ACTUAL_YEAR_KEY, checklistActualYear);
        outState.putInt(CHECKLIST_ACTUAL_FILTER_ID, checklistActualFilterId);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        title = savedInstanceState.getString(FragmentAbstract.TITLE, title);
        subtitle = savedInstanceState.getString(FragmentAbstract.SUBTITLE, subtitle);
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
        }
    }

    public void setActualChecklist(int month, int year) {
        this.checklistActualMonth = month;
        this.checklistActualYear = year;
    }

    public void setActualChecklistFilterId(int filterId) {
        this.checklistActualFilterId = filterId;
    }
}
