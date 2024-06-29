package com.example.meetingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.meetingapp.Fragments.ActivityFragment;
import com.example.meetingapp.Fragments.CalendarFragment;
import com.example.meetingapp.Fragments.ContactsFragment;
import com.example.meetingapp.Fragments.MeetingsFragment;
import com.example.meetingapp.Fragments.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * This class is responsible for navigation through the different views on the app. For example,
 * if the user clicks "calendar" or "contacts" on the navigation bar, it will take them to that
 * view/layout.
 * A great resource I used to help put together this class is the fragment documentation from Android
 * https://developer.android.com/guide/fragments
 */
public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    ContactsFragment contactsFragment = new ContactsFragment();
    CalendarFragment calendarFragment = new CalendarFragment();
    MeetingsFragment meetingsFragment = new MeetingsFragment();

    ActivityFragment activityFragment = new ActivityFragment();
    SettingsFragment settingsFragment = new SettingsFragment();

    private static final String FRAGMENT_KEY_FOR_DEVICE_ROTATION = "";
    private int currFragmentValue = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (savedInstanceState != null) {
            currFragmentValue = savedInstanceState.getInt(FRAGMENT_KEY_FOR_DEVICE_ROTATION, -1);
        }
        // this is for when the device is rotated, it remembers the state of which fragment it was
        // on, which allows it to be restored
        if (currFragmentValue == -1) {
            changeViewFragment(meetingsFragment);
        } else {
            if (currFragmentValue == 0) {
                changeViewFragment(meetingsFragment);
            } else if (currFragmentValue == 1) {
                changeViewFragment(calendarFragment);
            } else if (currFragmentValue == 2) {
                changeViewFragment(contactsFragment);
            } else if (currFragmentValue == 3) {
                changeViewFragment(activityFragment);
            } else if (currFragmentValue == 4) {
                changeViewFragment(settingsFragment);
            }
        }

        // this is for when the user clicks any of the navigation menu buttons.
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if(item.getItemId() == R.id.meeting_button){
                changeViewFragment(meetingsFragment);
                currFragmentValue = 0;
                return true;
            } else if(item.getItemId() == R.id.calendar_button){
                changeViewFragment(calendarFragment);
                currFragmentValue = 1;
                return true;
            } else if(item.getItemId() == R.id.contacts_button){
                changeViewFragment(contactsFragment);
                currFragmentValue = 2;
                return true;
            } else if(item.getItemId() == R.id.activity_button){
                changeViewFragment(activityFragment);
                currFragmentValue = 3;
                return true;
            } else if(item.getItemId() == R.id.settings_button){
                changeViewFragment(settingsFragment);
                currFragmentValue = 4;
                return true;
            }
            return false;
        });
    }
    private void changeViewFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(R.id.container1,fragment).commit();
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(FRAGMENT_KEY_FOR_DEVICE_ROTATION, currFragmentValue);
    }

}


