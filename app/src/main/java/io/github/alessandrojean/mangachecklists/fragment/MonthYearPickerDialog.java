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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.github.alessandrojean.mangachecklists.R;
import io.github.alessandrojean.mangachecklists.domain.Checklist;
import io.github.alessandrojean.mangachecklists.domain.ChecklistData;

/**
 * Created by Desktop on 15/12/2017.
 */

public class MonthYearPickerDialog extends DialogFragment implements NumberPicker.OnValueChangeListener {
    private DatePickerDialog.OnDateSetListener listener;

    private NumberPicker monthPicker;
    private NumberPicker yearPicker;

    private ArrayList<ChecklistData> availableChecklists;

    public static final String KEY = "month_year_picker_dialog";
    public static final String MINIMUM_MONTH = "minimum_month";
    public static final String MINIMUM_YEAR = "minimum_year";
    public static final String SELECTED_MONTH = "selected_month";
    public static final String SELECTED_YEAR = "selected_year";
    public static final String AVAILABLE_CHECKLISTS = "available_checklists_key";


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

        availableChecklists = getArguments().getParcelableArrayList(AVAILABLE_CHECKLISTS);

        View dialog = inflater.inflate(R.layout.fragment_dialog_date, null);
        monthPicker = dialog.findViewById(R.id.picker_month);

        //monthPicker.setMinValue(minMonth);
        //monthPicker.setMaxValue(12);
        //monthPicker.setValue(month);
        //monthPicker.setWrapSelectorWheel(false);

        //yearPicker.setMinValue(getArguments().getInt(MINIMUM_YEAR, 2013));
        //yearPicker.setMaxValue(maxYear);
       // yearPicker.setValue(year);
        //yearPicker.setOnValueChangedListener(this);
        //yearPicker.setWrapSelectorWheel(false);

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
            //else if (month < checklistData.getChecklists().get(i).getMonth())
            //    monthPosition = i;
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


        //int actualMonth = monthPicker.getValue();

        //ChecklistData minimumYea;


        /*int minimumYear = getArguments().getInt(MINIMUM_YEAR);
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
        }*/

       // yearPicker.setWrapSelectorWheel(false);
      //  monthPicker.setWrapSelectorWheel(false);
    }
}
