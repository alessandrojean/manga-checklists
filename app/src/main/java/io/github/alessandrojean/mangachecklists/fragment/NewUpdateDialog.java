package io.github.alessandrojean.mangachecklists.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import io.github.alessandrojean.mangachecklists.R;
import io.github.alessandrojean.mangachecklists.data.updater.UpdaterService;

/**
 * Created by Desktop on 24/12/2017.
 */

public class NewUpdateDialog extends DialogFragment {
    public static final String KEY = "new_update_dialog_key";
    private static final String BODY_KEY = "body_key";
    private static final String URL_KEY = "url_key";

    public NewUpdateDialog() {

    }

    public static NewUpdateDialog newInstance(String body, String url) {
        NewUpdateDialog newUpdateDialog = new NewUpdateDialog();
        Bundle bundle = new Bundle();
        bundle.putString(BODY_KEY, body);
        bundle.putString(URL_KEY, url);
        newUpdateDialog.setArguments(bundle);

        return newUpdateDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog = inflater.inflate(R.layout.dialog_new_update, null);

        TextView textView = (TextView) dialog;
        textView.setText(getArguments().getString(BODY_KEY));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.update_check_title);
        builder.setView(dialog);
        builder.setPositiveButton(R.string.update_check_confirm, (d, i) -> {
            Context context = getActivity();

            if (context != null) {
                UpdaterService.downloadUpdate(context, getArguments().getString(URL_KEY), null);
            }
        });
        builder.setNegativeButton(R.string.update_check_ignore, (d, i) -> {
            NewUpdateDialog.this.getDialog().cancel();
        });

        return builder.create();
    }
}
