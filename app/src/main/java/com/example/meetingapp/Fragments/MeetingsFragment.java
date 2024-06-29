package com.example.meetingapp.Fragments;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.meetingapp.Meetings.MeetingsDatabase;
import com.example.meetingapp.Meetings.Meeting;
import com.example.meetingapp.Meetings.MeetingsAdapter;
import com.example.meetingapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This class is responsible for the entire meetings layout/view. It initializes all the buttons in
 * the view and sets on click listeners for them. Most importantly, it handles the creation of a
 * Meeting and stores and retrieves the information from the MeetingsDatabase class.
 */
public class MeetingsFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {
    Button buttonAddMeeting, buttonOK, buttonCancel;
    Button buttonShowAllMeetings, buttonMeetingsToday, buttonMeetingsTomorrow, buttonAddContactToMeeting;
    ImageButton buttonEditCard;
    TextView startDate, startTime, endDate, endTime;
    EditText meetingName, meetingContacts;
    TextView mondayDay, tuesdayDay, wednesdayDay, thursdayDay, fridayDay, saturdayDay, sundayDay;
    List<Meeting> meetingsList = new ArrayList<>();
    MeetingsAdapter adapter = new MeetingsAdapter(meetingsList);
    MeetingsDatabase meetingsDatabase;
    private RecyclerView recyclerView;
    private static final int READ_CONTACT_PERMISSION_REQUEST = 79;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meetings, container, false);
        initializeButtonsAndTextViews(view);
        recyclerView = view.findViewById(R.id.recycler_view_meetings);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MeetingsAdapter(meetingsList);
        recyclerView.setAdapter(adapter);
        meetingsDatabase = new MeetingsDatabase(getContext());
        meetingsDatabase.deleteExpiredMeetings(); // i made it so that it deletes expired meetings everytime the app is opened
        loadAllMeetings();
        return view;
    }

    private void initializeButtonsAndTextViews(View view) {
        mondayDay = view.findViewById(R.id.monday_day);
        tuesdayDay = view.findViewById(R.id.tuesday_day);
        wednesdayDay = view.findViewById(R.id.wednesday_day);
        thursdayDay = view.findViewById(R.id.thursday_day);
        fridayDay = view.findViewById(R.id.friday_day);
        saturdayDay = view.findViewById(R.id.saturday_day);
        sundayDay = view.findViewById(R.id.sunday_day);
        buttonShowAllMeetings = view.findViewById(R.id.show_all_meetings);
        buttonMeetingsToday = view.findViewById(R.id.show_meetings_today);
        buttonMeetingsTomorrow = view.findViewById(R.id.show_meetings_tomorrow);
        buttonAddMeeting = view.findViewById(R.id.button_add_meeting);
        buttonEditCard = view.findViewById(R.id.three_dots_button);
        buttonEditCard.setOnClickListener(view1 -> popupMenu(view1));
        buttonAddMeeting.setOnClickListener(view1 -> showAddMeetingDialog());

        mondayDay.setOnClickListener(view1 -> {
                    showMeetingsForSpecificDayOfTheWeek("Monday");
                    highlightClickedDay(mondayDay);
                }
        );
        tuesdayDay.setOnClickListener(view1 -> {
                    showMeetingsForSpecificDayOfTheWeek("Tuesday");
                    highlightClickedDay(tuesdayDay);
                }
        );
        wednesdayDay.setOnClickListener(view1 -> {
            showMeetingsForSpecificDayOfTheWeek("Wednesday");
            highlightClickedDay(wednesdayDay);
        });
        thursdayDay.setOnClickListener(view1 -> {
            showMeetingsForSpecificDayOfTheWeek("Thursday");
            highlightClickedDay(thursdayDay);
        });
        fridayDay.setOnClickListener(view1 -> {
            showMeetingsForSpecificDayOfTheWeek("Friday");
            highlightClickedDay(fridayDay);
        });

        saturdayDay.setOnClickListener(view1 -> {
            showMeetingsForSpecificDayOfTheWeek("Saturday");
            highlightClickedDay(saturdayDay);
        });
        sundayDay.setOnClickListener(view1 -> {
            showMeetingsForSpecificDayOfTheWeek("Sunday");
            highlightClickedDay(sundayDay);
        });
        buttonShowAllMeetings.setOnClickListener(view1 ->
        {
            loadAllMeetings();
            resetBackgroundColorOnDay();
        });
        buttonMeetingsToday.setOnClickListener(view1 -> {
            try {
                showMeetingsForToday();
                resetBackgroundColorOnDay();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
        buttonMeetingsTomorrow.setOnClickListener(view1 -> {
            try {
                showMeetingsForTomorrow();
                resetBackgroundColorOnDay();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void highlightClickedDay(TextView currDay) {
        resetBackgroundColorOnDay();
        currDay.setBackgroundColor(getResources().getColor(R.color.red));
    }

    private void resetBackgroundColorOnDay() {
        mondayDay.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tuesdayDay.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        wednesdayDay.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        thursdayDay.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        fridayDay.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        saturdayDay.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        sundayDay.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    /**
     * This method shows meetings for a specific day of the week by taking that day of the week
     * in as a parameter. So if you want to find the meetings for Monday, click "M" on the UI and
     * it'll display mondays meetings
     * @param dayOfWeek day of the week that the meeting should show up for
     */
    private void showMeetingsForSpecificDayOfTheWeek(String dayOfWeek) {
        meetingsList.clear();
        List<Meeting> meetings;
        if (dayOfWeek == null) {
            meetings = meetingsDatabase.getAllMeetings();
        } else {
            try {
                meetings = meetingsDatabase.getMeetingsForDay(dayOfWeek);
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
        }
        meetingsList.addAll(meetings);
        adapter.notifyDataSetChanged();
    }

    /**
     * Shows meetings that are scheduled for the current date today
     * @throws ParseException
     */
    private void showMeetingsForToday() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simple = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String todaysDate = simple.format(calendar.getTime());
        showMeetingsForDate(todaysDate);
    }

    /**
     * This method shows meetings for tomorrow, so when the user clicks the "Meetings Tomorrow"
     * button, it will display all the meetings 1 after the current calendar date
     * @throws ParseException
     */
    private void showMeetingsForTomorrow() throws ParseException {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        SimpleDateFormat simple = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String tomorrowsDate = simple.format(calendar.getTime());
        showMeetingsForDate(tomorrowsDate);
    }

    /**
     * Grabs the meetings for any day
     * @param date
     */
    private void showMeetingsForDate(String date) {
        meetingsList.clear();
        List<Meeting> meetings;
        try {
            meetings = meetingsDatabase.getMeetingsForDate(date);
            meetingsList.addAll(meetings);
            adapter.notifyDataSetChanged();
        } catch (ParseException e) {
//            Toast.makeText(this, "Failed cuz of: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
        }
    }

    /**
     * This method brings up a popup menu that gives the following options to the user:
     * 1. Push todays meetings to next weekday
     * 2. Clear all meetings
     * 3. Clear all meetings today
     * @param view current view
     */
    private void popupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(getContext(), view);
        popupMenu.inflate(R.menu.popup_menu_topbar);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
    }

    /**
     * This method handles the menu clicks the user does on the following options:
     * 1. Push todays meetings to next weekday
     * 2. Clear all meetings
     * 3. Clear all meetings today
     * @param menuItem
     * @return
     */
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.push_back_meetings_to_next_week) {
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
            String todayDateStr = sdf.format(new Date());

            meetingsDatabase.updateMeetingDatesForToday(todayDateStr);
            loadAllMeetings();
//            Toast.makeText(MainActivity.this, "MSG: " + nextWeekdayStr, Toast.LENGTH_LONG).show();
            return true;
        } else if (menuItem.getItemId() == R.id.action_delete_all_meetings) {
            meetingsDatabase.deleteEveryMeeting();
            loadAllMeetings();
            return true;
        } else if (menuItem.getItemId() == R.id.action_clear_all_meetings_today) {
            Calendar currDayOfWeek = Calendar.getInstance();
            int dayOfTheWeek = currDayOfWeek.get(Calendar.DAY_OF_WEEK);
            String[] monToSunday = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
            String monToSundayConverted = monToSunday[dayOfTheWeek - 1];
            List<Meeting> meetingsForToday = null;
            try {
                meetingsForToday = meetingsDatabase.getMeetingsForDay(monToSundayConverted);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            if (!meetingsForToday.isEmpty()) {
                meetingsDatabase.deleteMeetingsForDay(monToSundayConverted);
                loadAllMeetings();
//                Toast.makeText(this, meetingsForToday.size() + " meetings deleted", Toast.LENGTH_LONG).show();
            } else {
//                Toast.makeText(this, "There is no meeting(s) to clear for today!", Toast.LENGTH_LONG).show();
            }
            return true;
        }
        return false;
    }

    /**
     * Loads all the meetings from the database, so when the app is opened up, it iterates through
     * the database and retrieves all meetings
     */
    private void loadAllMeetings() {
        meetingsList.clear();
        try {
            List<Meeting> meetings = meetingsDatabase.getAllMeetings();
            meetingsList.addAll(meetings);
            adapter.notifyDataSetChanged();
//            Toast.makeText(this, "Num of meetings loaded: " + meetingsList.size(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(this, "ERROR trying to load meetings: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


//    // code taken from android documentation: https://developer.android.com/develop/ui/views/components/pickers

    /**
     * This method is responsible for the entire add meeting dialog. When the "+ Add Meeting" Button
     * is clicked, this gets triggered. I've set an onclick within the xml file for the Add meeting button.
     * For the calendar, date, and time pickers, I've used the android documentation which provided
     * a fair bit of the code.
     * https://developer.android.com/develop/ui/views/components/pickers
     */
    public void showAddMeetingDialog() {
        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        View viewOfDialog = layoutInflater.inflate(R.layout.dialog_add_meeting, null);
        AlertDialog.Builder popup = new AlertDialog.Builder(getContext());
        popup.setView(viewOfDialog);
        meetingName = viewOfDialog.findViewById(R.id.edit_event_name);
        meetingContacts = viewOfDialog.findViewById(R.id.edit_event_category);
        startDate = viewOfDialog.findViewById(R.id.start_date);
        startTime = viewOfDialog.findViewById(R.id.start_time);
        endDate = viewOfDialog.findViewById(R.id.end_date);
        endTime = viewOfDialog.findViewById(R.id.end_time);
        buttonCancel = viewOfDialog.findViewById(R.id.button_cancel);
        buttonOK = viewOfDialog.findViewById(R.id.button_ok);
        buttonAddContactToMeeting = viewOfDialog.findViewById(R.id.add_contact_button);
        // setting the calendar fields to the current date for the user beforehand
        Calendar theDateToday = Calendar.getInstance();
        theDateToday.set(Calendar.HOUR_OF_DAY, 0);
        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        String formattedDate = format.format(theDateToday.getTime());
        startDate.setText(formattedDate);
        endDate.setText(formattedDate);
        AlertDialog alertDialog = popup.create();

        startDate.setOnClickListener(view -> {
            Calendar c = Calendar.getInstance();
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);
            int day = c.get(Calendar.DAY_OF_MONTH);

            //https://developer.android.com/develop/ui/views/components/pickers
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Calendar selectedDate = Calendar.getInstance();
                            selectedDate.set(year, month, dayOfMonth);

                            // formats the selected date into a word format
                            SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
                            String formattedDate = format.format(selectedDate.getTime());
                            startDate.setText(formattedDate);
                        }
                    }, year, month, day);
            datePickerDialog.show();
        });

        endDate.setOnClickListener(view -> {
            Calendar c = Calendar.getInstance();
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            Calendar selectedDate = Calendar.getInstance();
                            selectedDate.set(year, month, dayOfMonth);

                            SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
                            String formattedDate = format.format(selectedDate.getTime());
                            endDate.setText(formattedDate);
                        }
                    }, year, month, day);
            datePickerDialog.show();
        });

        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE, minute);

                                // this is to help format it as such -> "h:mm a" formats hour, minute, am/pm
                                SimpleDateFormat format = new SimpleDateFormat("h:mm a", Locale.getDefault());
                                String formattedTime = format.format(c.getTime());

                                startTime.setText(formattedTime);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        endTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                            c.set(Calendar.MINUTE, minute);
                            SimpleDateFormat format1 = new SimpleDateFormat("h:mm a", Locale.getDefault());
                            String formattedTime = format1.format(c.getTime());

                            endTime.setText(formattedTime);
                        }
                    }, hour, minute, false);
            timePickerDialog.show();
        });


        buttonAddContactToMeeting.setOnClickListener(view -> {

            if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACT_PERMISSION_REQUEST);
            } else {
                displayPhoneContacts();
            }
        });

        buttonCancel.setOnClickListener(view -> {
            alertDialog.dismiss();
        });

        buttonOK.setOnClickListener(view -> {

            String date = startDate.getText().toString();
            String dateStart = "Starts: " + startDate.getText() + " " + startTime.getText().toString() + "  ";
            String dateEnd = "Ends: " + endDate.getText() + " " + endTime.getText().toString() + "  ";
            String time = startTime.getText().toString() + " - " + endTime.getText().toString();
            String description = meetingName.getText().toString();
            String contacts = "Invited: " + meetingContacts.getText().toString();

            Toast.makeText(getContext(), "CONTACTS: " + contacts, Toast.LENGTH_LONG).show();

            if (description.equals("")) {
                description = "No title";
            }

            long uniqueIDForMeetingCard = meetingsDatabase.insertMeeting(date, dateStart, dateEnd, time, description, contacts);
//            Meeting meeting = new Meeting(uniqueIDForMeetingCard, dateStart, dateEnd, time, description);
            Meeting meeting = new Meeting(uniqueIDForMeetingCard, dateStart, dateEnd, time, description, contacts);
            meetingsList.add(meeting);
            adapter.notifyItemInserted(meetingsList.size() - 1);

            alertDialog.dismiss();
        });
        alertDialog.show();
    }

    /**
     * This method helps display all the phone contacts within the phone. It does so by
     * Partial code taken from Android documentation: https://developer.android.com/training/contacts-provider/retrieve-names
     */
    private void displayPhoneContacts() {
        String[] projection = {
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        Cursor cursor = getContext().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                null
        );

        List<String> contacts = new ArrayList<>();

        if(cursor.getCount() > 0){
            while (cursor.moveToNext()) {
                int indexForName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int indexForPhoneNum = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String name = cursor.getString(indexForName);
                String phoneNumber = cursor.getString(indexForPhoneNum);
                contacts.add(name + " (" + phoneNumber + ")");
            }
        }

        if (cursor.getCount() > 0) {
            cursor.close();
        }

        AlertDialog.Builder contactsDialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_contacts_dialog, null);
        ListView listView = dialogView.findViewById(R.id.contacts_list_view);

        ArrayAdapter<String> contactsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, contacts);
        listView.setAdapter(contactsAdapter);

        contactsDialogBuilder.setView(dialogView);
        contactsDialogBuilder.setTitle("Select a Contact");
        contactsDialogBuilder.setPositiveButton("Ok", (dialog, id) -> dialog.dismiss());

        AlertDialog contactsDialog = contactsDialogBuilder.create();

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            String currContact = contactsAdapter.getItem(position);
            String currContent = meetingContacts.getText().toString();
            if (!currContent.isEmpty()) {
                currContent += ", ";
            }
            meetingContacts.setText(currContent + currContact);
            contactsDialog.dismiss();
        });
        contactsDialog.show();
    }
}
