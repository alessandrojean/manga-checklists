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

import java.time.Month;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.alessandrojean.mangachecklists.R;
import io.github.alessandrojean.mangachecklists.domain.Checklist;
import io.github.alessandrojean.mangachecklists.domain.ChecklistData;
import io.github.alessandrojean.mangachecklists.parser.checklist.ChecklistParser;

/**
 * Created by Desktop on 15/12/2017.
 */

public class MonthYearPickerDialog extends DialogFragment implements NumberPicker.OnValueChangeListener {
    private DatePickerDialog.OnDateSetListener listener;

    private NumberPicker monthPicker;
    private NumberPicker yearPicker;

    private ArrayList<ChecklistData> availableChecklists;

    public static final String KEY = "month_year_picker_dialog";
    private static final String ARG_MINIMUM_MONTH = "arg_minimum_month";
    private static final String ARG_MINIMUM_YEAR = "arg_minimum_year";
    private static final String ARG_SELECTED_MONTH = "arg_selected_month";
    private static final String ARG_SELECTED_YEAR = "arg_selected_year";
    private static final String ARG_AVAILABLE_CHECKLISTS = "arg_available_checklists_key";

    public MonthYearPickerDialog() {

    }

    public static MonthYearPickerDialog newInstance(ChecklistParser parser, int monthSelected, int yearSelected) {
        MonthYearPickerDialog monthYearPickerDialog = new MonthYearPickerDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_MINIMUM_MONTH, parser.getMinimumMonth());
        bundle.putInt(ARG_MINIMUM_YEAR, parser.getMinimumYear());
        bundle.putInt(ARG_SELECTED_MONTH, monthSelected);
        bundle.putInt(ARG_SELECTED_YEAR, yearSelected);
        bundle.putParcelableArrayList(ARG_AVAILABLE_CHECKLISTS, parser.getAvailableChecklists());
        monthYearPickerDialog.setArguments(bundle);
        return monthYearPickerDialog;
    }


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
        int month = getArguments().getInt(ARG_SELECTED_MONTH, calendar.get(Calendar.MONTH) + 1);
        int maxYear = calendar.get(Calendar.YEAR);
        int year = getArguments().getInt(ARG_SELECTED_YEAR, maxYear);
        int minMonth = year == getArguments().getInt(ARG_MINIMUM_YEAR)
                ? getArguments().getInt(ARG_MINIMUM_MONTH)
                : 1;

        availableChecklists = getArguments().getParcelableArrayList(ARG_AVAILABLE_CHECKLISTS);

        View dialog = inflater.inflate(R.layout.fragment_dialog_date, null);
        monthPicker = dialog.findViewById(R.id.picker_month);

        startYearNumberPicker(dialog);
        selectMonthAndYear(month, year);

        builder.setTitle("Data do Checklist");

        builder.setView(dialog)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ChecklistData actualYear = availableChecklists.get(yearPicker.getValue());

                        int year = actualYear.getYear();
                        int month = actualYear.getChecklists().get(monthPicker.getValue()).getMonth();

                        listener.onDateSet(null, year, month, 0);
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

    private void selectMonthAndYear(int month, int year) {
        int yearPosition = 0;
        for (int i = 0; i < availableChecklists.size(); i++) {
            if (availableChecklists.get(i).getYear() == year) {
                yearPosition = i;
                break;
            }
        }

        ChecklistData checklistData = availableChecklists.get(yearPosition);

        int monthPosition = 0;
        for (int i = 0; i < checklistData.getChecklists().size(); i++) {
            if (month == checklistData.getChecklists().get(i).getMonth())
                monthPosition = i;
        }

        int oldValue = yearPicker.getValue();
        yearPicker.setValue(yearPosition);
        onValueChange(yearPicker, oldValue, yearPosition);

        monthPicker.setValue(monthPosition);
    }

    private void startYearNumberPicker(View dialog) {
        String[] years = new String[availableChecklists.size()];
        for (int i = 0; i < availableChecklists.size(); i++)
            years[i] = availableChecklists.get(i).getYear().toString();

        yearPicker = dialog.findViewById(R.id.picker_year);
        yearPicker.setDisplayedValues(years);
        yearPicker.setMaxValue(years.length - 1);
        yearPicker.setMinValue(0);
        yearPicker.setWrapSelectorWheel(false);
        yearPicker.setOnValueChangedListener(this);
        yearPicker.setValue(years.length - 1);
        onValueChange(yearPicker, years.length - 1, years.length - 1);

        ChecklistData actualYear = availableChecklists.get(availableChecklists.size() - 1);

        monthPicker.setValue(actualYear.getChecklists().size() - 1);
    }

    @Override
    public void onResume() {
        super.onResume();

        yearPicker.setOnValueChangedListener(this);
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
        ChecklistData actualYear = availableChecklists.get(oldVal);

        int actualMonth = actualYear.getChecklists().get(monthPicker.getValue()).getMonth();

        List<Checklist> checklistList = availableChecklists.get(newVal).getChecklists();

        String[] months = new String[checklistList.size()];
        int monthToSelect = 0;

        for (int i = 0; i < checklistList.size(); i++) {
            months[i] = checklistList.get(i).getMonth().toString();
            if (months[i] == String.valueOf(actualMonth))
                monthToSelect = i;
        }

        monthPicker.setValue(0);
        monthPicker.setDisplayedValues(months);
        monthPicker.setMaxValue(months.length - 1);
        monthPicker.setMinValue(0);
        monthPicker.setWrapSelectorWheel(false);
        monthPicker.setValue(monthToSelect);
    }
}
