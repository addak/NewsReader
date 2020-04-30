package com.bignerdranch.newsreader.ui.customsearch;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

import com.bignerdranch.newsreader.NewsRepository;
import com.bignerdranch.newsreader.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SearchConfigDialog extends DialogFragment{

    public static final String DIALOG_SEARCHCONFIG= "DialogSearchConfig";

    private CustomSearchViewModel mCustomSearchViewModel;

    private EditText mTitleSubstring;
    private Button mFromDate;
    private Button mToDate;
    private Spinner mLanguageSpinner;
    private AutoCompleteTextView mSourceAutoCompleteView;

    private String mType;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mCustomSearchViewModel = ViewModelProviders.of(getParentFragment()).get(CustomSearchViewModel.class);

        View v = inflater.inflate(R.layout.fragment_searchdialog, container, false);

        mFromDate = v.findViewById(R.id.search_startdate);
        mToDate = v.findViewById(R.id.search_enddate);
        mLanguageSpinner = v.findViewById(R.id.search_language);
        mSourceAutoCompleteView = v.findViewById(R.id.search_source);

        final SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d");

        mFromDate.setText( formatter.format(mCustomSearchViewModel.getFrom().getValue()));
        mToDate.setText(formatter.format(mCustomSearchViewModel.getTo().getValue()));


        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.select_dialog_item, NewsRepository.getSourceNames());
        mSourceAutoCompleteView.setThreshold(1);
        mSourceAutoCompleteView.setAdapter(adapter);


        mSourceAutoCompleteView.setText(( mCustomSearchViewModel.getSource().getValue()));

        mSourceAutoCompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String value = (String) adapterView.getItemAtPosition(i);
                mCustomSearchViewModel.setSource(value);
            }
        });

        mFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mType = "From";
                datePickerDialog(mCustomSearchViewModel.getFrom().getValue());
            }
        });

        mToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mType = "To";
                datePickerDialog(mCustomSearchViewModel.getTo().getValue());
            }
        });

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, NewsRepository.getLangaugeNames());
        mLanguageSpinner.setAdapter(spinnerAdapter);
        mLanguageSpinner.setSelection( getIndex(mLanguageSpinner, mCustomSearchViewModel.getLanguage().getValue()));

        mLanguageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String value = (String) adapterView.getItemAtPosition(i);
                mCustomSearchViewModel.setLanguage(value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return v;
    }

    public void datePickerDialog(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        final SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMMM d");

        DatePickerDialog datePickerDialog =  new DatePickerDialog(getActivity(),new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Date date = new GregorianCalendar(year, month,day).getTime();

                if(mType.equals("To")){
                    mCustomSearchViewModel.setTo(date);
                    mToDate.setText(formatter.format(date));
                }
                else{
                    mCustomSearchViewModel.setFrom(date);
                    mFromDate.setText(formatter.format(date));
                }
            }
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();

    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

}

