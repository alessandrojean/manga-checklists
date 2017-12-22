package io.github.alessandrojean.mangachecklists.fragment.settings;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

import io.github.alessandrojean.mangachecklists.R;

/**
 * Created by Desktop on 22/12/2017.
 */

public class NotificationSettingsFragment extends PreferenceFragmentCompat {
    public static final String TAG = NotificationSettingsFragment.class.getName();
    public static final String PAGE_ID = "page_id";

    public static NotificationSettingsFragment newInstance(String pageId) {
        NotificationSettingsFragment fragment = new NotificationSettingsFragment();
        Bundle args = new Bundle();
        args.putString(PAGE_ID, pageId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.app_preferences, rootKey);
    }
}
