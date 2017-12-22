package io.github.alessandrojean.mangachecklists.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import io.github.alessandrojean.mangachecklists.BuildConfig;
import io.github.alessandrojean.mangachecklists.R;

/**
 * Created by Desktop on 22/12/2017.
 */

public class SettingsFragment extends PreferenceFragmentCompat{
    public static final String TAG = SettingsFragment.class.getName();
    public static final String PAGE_ID = "page_id";
    public static final String KEY = "fragment";
    public static final String TITLE = "Configurações";

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

        // Show current version in about section.
        Preference preferenceVersion = findPreference("preference_version");
        String showVersion = BuildConfig.DEBUG ? "r" + BuildConfig.COMMIT_COUNT : BuildConfig.VERSION_NAME;
        preferenceVersion.setSummary(showVersion);

        // Show build date.
        Preference preferenceBuildDate = findPreference("preference_build_date");
        preferenceBuildDate.setSummary(BuildConfig.BUILD_TIME);
    }

    @Override
    public Fragment getCallbackFragment() {
        return this;
    }
}
