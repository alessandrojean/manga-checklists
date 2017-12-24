package io.github.alessandrojean.mangachecklists;

import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.github.alessandrojean.mangachecklists.fragment.ChecklistFragment;
import io.github.alessandrojean.mangachecklists.fragment.PlansFragment;
import io.github.alessandrojean.mangachecklists.fragment.SettingsFragment;
import io.github.alessandrojean.mangachecklists.fragment.settings.NotificationSettingsFragment;

/**
 * Created by Desktop on 16/12/2017.
 */

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        PreferenceFragmentCompat.OnPreferenceStartScreenCallback,
        FragmentManager.OnBackStackChangedListener {

    // Save to bundle.
    private static final String TOOLBAR_TITLE_KEY = "toolbar_title_key";
    private static final String TOOLBAR_SUBTITLE_KEY = "toolbar_subtitle_key";
    private static final String CHECKLIST_ACTUAL_MONTH_KEY = "checklist_actual_month_key";
    private static final String CHECKLIST_ACTUAL_YEAR_KEY = "checklist_actual_year_key";
    private static final String CHECKLIST_ACTUAL_FILTER_ID = "checklist_actual_filter_id_key";
    private static final String SHOW_BACK_ARROW_KEY = "show_back_arrow_key";

    // Views.
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;

    // Class variables.
    private int checklistActualMonth = 0;
    private int checklistActualYear = 0;
    private int checklistActualFilterId = R.id.action_filter_jbc;
    private String title = ChecklistFragment.TITLE;
    private String subtitle = "";
    private boolean showBackArrow = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Show version in navigationView header.
        View hView = navigationView.getHeaderView(0);
        TextView textViewVersion = hView.findViewById(R.id.app_version);
        textViewVersion.setText(BuildConfig.VERSION_NAME);

        initDrawerNavigation();

        getSupportFragmentManager().addOnBackStackChangedListener(this);

        if (savedInstanceState != null) {
            title = savedInstanceState.getString(TOOLBAR_TITLE_KEY, title);
            subtitle = savedInstanceState.getString(TOOLBAR_SUBTITLE_KEY, subtitle);
            checklistActualMonth = savedInstanceState.getInt(CHECKLIST_ACTUAL_MONTH_KEY);
            checklistActualYear = savedInstanceState.getInt(CHECKLIST_ACTUAL_YEAR_KEY);
            checklistActualFilterId = savedInstanceState.getInt(CHECKLIST_ACTUAL_FILTER_ID);
            showBackArrow = savedInstanceState.getBoolean(SHOW_BACK_ARROW_KEY);
        }
        else {
            // App opening, so we need to show the default fragment.
            setNowDateToChecklist();
            replaceFragment(ChecklistFragment.newInstance(
                    checklistActualMonth,
                    checklistActualYear,
                    checklistActualFilterId
            ));
        }

        showBackButton(showBackArrow);
    }

    private void initDrawerNavigation() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void replaceFragment(Fragment fragment) {
        String showTag = fragment.getClass().getName();
        FragmentManager manager = getSupportFragmentManager();
        manager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.rl_container, fragment, showTag);

        if (fragment instanceof NotificationSettingsFragment)
            transaction.addToBackStack(null);

        transaction.commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        switch (item.getItemId()) {
            case R.id.nav_checklist:
                fragment = ChecklistFragment.newInstance(
                        checklistActualMonth,
                        checklistActualYear,
                        checklistActualFilterId
                );
                title = ChecklistFragment.TITLE;
                showDateInToolbar();
                break;
            case R.id.nav_plans:
                fragment = new PlansFragment();
                title = PlansFragment.TITLE;
                subtitle = "";
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                title = SettingsFragment.TITLE;
                subtitle = "";
                break;
        }

        replaceFragment(fragment);

        toolbar.setTitle(title);
        toolbar.setSubtitle(subtitle);

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
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
        outState.putBoolean(SHOW_BACK_ARROW_KEY, showBackArrow);
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
        showBackArrow = savedInstanceState.getBoolean(SHOW_BACK_ARROW_KEY, showBackArrow);
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

    private void showBackButton(boolean show) {
        showBackArrow = show;

        if (show) {
            actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if (actionBarDrawerToggle.getToolbarNavigationClickListener() == null) {
                actionBarDrawerToggle.setToolbarNavigationClickListener(v -> onBackPressed());
            }

            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            actionBarDrawerToggle.setToolbarNavigationClickListener(null);

            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }

    @Override
    public boolean onPreferenceStartScreen(PreferenceFragmentCompat caller, PreferenceScreen pref) {
        NotificationSettingsFragment fragment = NotificationSettingsFragment.newInstance("Notificações");
        Bundle args = new Bundle();
        args.putString(PreferenceFragmentCompat.ARG_PREFERENCE_ROOT, pref.getKey());
        fragment.setArguments(args);

        replaceFragment(fragment);

        showBackButton(!pref.getKey().equals("preference_screen"));

        return true;
    }

    @Override
    public void onBackStackChanged() {
        Fragment current = getSupportFragmentManager().findFragmentById(R.id.rl_container);
        if (current instanceof SettingsFragment)
            showBackButton(false);
    }
}
