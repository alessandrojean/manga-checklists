package io.github.alessandrojean.mangachecklists.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import java.util.Calendar;

import io.github.alessandrojean.mangachecklists.R;

/**
 * Created by Desktop on 15/12/2017.
 */

public class MonthYearPickerDialog extends DialogFragment implements NumberPicker.OnValueChangeListener {
    private DatePickerDialog.OnDateSetListener listener;

    private NumberPicker monthPicker;
    private NumberPicker yearPicker;

    public static final String KEY = "month_year_picker_dialog";
    public static final String MINIMUM_MONTH = "minimum_month";
    public static final String MINIMUM_YEAR = "minimum_year";
    public static final String SELECTED_MONTH = "selected_month";
    public static final String SELECTED_YEAR = "selected_year";


    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Calendar calendar = Calendar.getInstance();
        int month = getArguments().getInt(SELECTED_MONTH, calendar.get(Calendar.MONTH) + 1);
        int maxYear = calendar.get(Calendar.YEAR);
        int year = getArguments().getInt(SELECTED_YEAR, maxYear);
        int minMonth = year == getArguments().getInt(MINIMUM_YEAR)
                ? getArguments().getInt(MINIMUM_MONTH)
                : 1;

        View dialog = inflater.inflate(R.layout.fragment_dialog_date, null);
        monthPicker = dialog.findViewById(R.id.picker_month);
        yearPicker = dialog.findViewById(R.id.picker_year);

        monthPicker.setMinValue(minMonth);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(month);
        monthPicker.setWrapSelectorWheel(false);

        yearPicker.setMinValue(getArguments().getInt(MINIMUM_YEAR, 2013));
        yearPicker.setMaxValue(maxYear);
        yearPicker.setValue(year);
        yearPicker.setOnValueChangedListener(this);
        yearPicker.setWrapSelectorWheel(false);

        builder.setTitle("Data do Checklist");

        builder.setView(dialog)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        listener.onDateSet(null, yearPicker.getValue(), monthPicker.getValue(), 0);
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        MonthYearPickerDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }

    @Override
    public void onResume() {
        super.onResume();

        yearPicker.setOnValueChangedListener(this);
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
        int actualMonth = monthPicker.getValue();
        int minimumYear = getArguments().getInt(MINIMUM_YEAR);
        int minimumMonth = getArguments().getInt(MINIMUM_MONTH);

        if (newVal == minimumYear) {
            monthPicker.setMinValue(minimumMonth);
            monthPicker.setMaxValue(12);

            if (actualMonth >= minimumMonth)
                monthPicker.setValue(actualMonth);
        }
        else {
            monthPicker.setMinValue(1);
            monthPicker.setMaxValue(12);

            monthPicker.setValue(actualMonth);
        }

        yearPicker.setWrapSelectorWheel(false);
        monthPicker.setWrapSelectorWheel(false);
    }
}
