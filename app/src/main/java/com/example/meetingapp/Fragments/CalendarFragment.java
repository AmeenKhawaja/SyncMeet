package com.example.meetingapp.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.meetingapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * This class is responsible for displaying the calendar on the Calendar view of the application
 */
public class CalendarFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        TextView dateTodayText = view.findViewById(R.id.todays_date_text);
        String currDate = simpleDateFormat.format(new Date());
        String dateFormatted = String.format("Today's Date: %s" , currDate);
        dateTodayText.setText(dateFormatted);

        return view;
    }
}