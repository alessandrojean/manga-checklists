package io.github.alessandrojean.mangachecklists.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import io.github.alessandrojean.mangachecklists.BuildConfig;
import io.github.alessandrojean.mangachecklists.R;
import io.github.alessandrojean.mangachecklists.data.updater.GitHubUpdateChecker;
import io.github.alessandrojean.mangachecklists.data.updater.GitHubUpdateResult;
import io.github.alessandrojean.mangachecklists.data.updater.UpdaterJob;
import io.github.alessandrojean.mangachecklists.data.updater.UpdaterService;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Desktop on 22/12/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceClickListener{
    public static final String TAG = SettingsFragment.class.getName();
    public static final String PAGE_ID = "page_id";
    public static final String KEY = "fragment";
    public static final String TITLE = "Configurações";

    private boolean isUpdaterEnabled = !BuildConfig.DEBUG && BuildConfig.INCLUDE_UPDATER;
    private GitHubUpdateChecker updateChecker;
    private Subscription releaseSubscription = null;

    public SettingsFragment() {
        updateChecker = new GitHubUpdateChecker();
    }

    public static SettingsFragment newInstance(String pageId) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(PAGE_ID, pageId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Load the Preferences from the XML file.
        addPreferencesFromResource(R.xml.app_preferences);

        // Setup automatic updates.
        Preference preferenceAutomaticUpdates = findPreference("preference_automatic_updates");
        if (isUpdaterEnabled) {
            preferenceAutomaticUpdates.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean checked = (Boolean) newValue;

                if (checked)
                    UpdaterJob.setupTask();
                else
                    UpdaterJob.cancelTask();

                return true;
            });
        }

        // Show build date.
        Preference preferenceBuildDate = findPreference("preference_build_date");
        preferenceBuildDate.setSummary(BuildConfig.BUILD_TIME);

        // Show current version in about section.
        Preference preferenceVersion = findPreference("preference_version");
        String showVersion = BuildConfig.DEBUG ? "r" + BuildConfig.COMMIT_COUNT : BuildConfig.VERSION_NAME;
        preferenceVersion.setSummary(showVersion);
        if (isUpdaterEnabled)
            preferenceVersion.setOnPreferenceClickListener(this);

    }

    @Override
    public Fragment getCallbackFragment() {
        return this;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        checkVersion();

        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (releaseSubscription != null)
            releaseSubscription.unsubscribe();
        releaseSubscription = null;
    }

    /**
     * Checks version and shows a user prompt if an update is available.
     * Function adapted from Tachiyomi repository.
     * Available at: https://github.com/inorichi/tachiyomi
     */
    private void checkVersion() {
        if (getActivity() == null)
            return;

        Toast.makeText(getContext(), R.string.update_check_look_for_updates, Toast.LENGTH_SHORT).show();
        if (releaseSubscription != null)
            releaseSubscription.unsubscribe();
        releaseSubscription = updateChecker.checkForUpdate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result instanceof GitHubUpdateResult.NewUpdate) {
                        GitHubUpdateResult.NewUpdate update = (GitHubUpdateResult.NewUpdate) result;
                        String body = update.getRelease().getChangelog();
                        String url = update.getRelease().getDownloadLink();

                        showDialogUpdateAvailable(body, url);
                    }
                    else if (result instanceof GitHubUpdateResult.NoNewUpdate)
                        Toast.makeText(getContext(), R.string.update_check_no_new_updates, Toast.LENGTH_SHORT).show();
                }, error -> {
                    Log.e("MangaChecklists", error.getMessage());
                });
    }

    private void showDialogUpdateAvailable(String body, String url) {
        NewUpdateDialog newUpdateDialog = NewUpdateDialog.newInstance(body, url);
        newUpdateDialog.show(getActivity().getSupportFragmentManager(), NewUpdateDialog.KEY);
    }
}
