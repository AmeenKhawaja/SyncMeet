package com.example.meetingapp.Meetings;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.meetingapp.Meetings.Meeting;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/*
    Name: Ameen Khawaja
    ID: 6935688
    Email: ak19nu@brocku.ca
*/
/**
 * This class is responsible for managing the database. It stores and retrieves meetings
 * A portion of code has been taken from Android SQLite documentation and here specifically is where I took code from:
 * https://developer.android.com/training/data-storage/sqlite#java
 */
public class MeetingsDatabase extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;
    private static final String COLUMN_START_DATE = "start_date";
    private static final String COLUMN_END_DATE = "end_date";
    public static final String DATABASE_NAME = "MeetingApp.db";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_CONTACTS = "contacts";
    private static final String TABLE_MEETINGS = "meetings";

    public MeetingsDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This method creates all the columns and the table for the database that will store meetings
     * @param db database that is created
     */
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_MEETINGS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_DATE + " TEXT,"
                + COLUMN_START_DATE + " TEXT,"
                + COLUMN_END_DATE + " TEXT,"
                + COLUMN_TIME + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_CONTACTS + " TEXT" + ")");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEETINGS);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * This method is responsible for inserting a meeting into the database. It takes all the
     * properties of a meeting and puts it in its respective column within the meetings db
     *
     * @param date        date of meeting
     * @param startDate   start date of meeting
     * @param endDate     end date of meeting
     * @param time        time meeting will occur
     * @param description description of the meeting, essentially the title of the meeting
     * @param contacts    contacts invited to the meeting
     * @return
     */
    public long insertMeeting(String date, String startDate, String endDate, String time, String description, String contacts) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_START_DATE, startDate);
        values.put(COLUMN_END_DATE, endDate);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_CONTACTS, contacts);
        long id = db.insert(TABLE_MEETINGS, null, values);
        db.close();
        return id;
    }

    /**
     * This method returns all the meetings that exist in the database by iterating through the
     * entire database and adding each meeting to an arraylist of type meeting.
     * I took a lot of the code from the android documentation, and modified it to my needs.
     * Here: https://developer.android.com/training/data-storage/sqlite#java
     *
     * @return all the meetings that are in the database
     */
    public List<Meeting> getAllMeetings() {
        List<Meeting> meetings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                COLUMN_ID,
                COLUMN_DATE,
                COLUMN_START_DATE,
                COLUMN_END_DATE,
                COLUMN_TIME,
                COLUMN_DESCRIPTION,
                COLUMN_CONTACTS
        };

        Cursor cursor = db.query(
                TABLE_MEETINGS,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            long uniqueIDForMeetingCard = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String itemDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
            String itemDateStart = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE));
            String itemDateEnd = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE));
            String itemTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));
            String itemDescription = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            String itemContacts = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACTS));

            Meeting meeting = new Meeting(uniqueIDForMeetingCard, itemDateStart, itemDateEnd, itemTime, itemDescription, itemContacts);
            meetings.add(meeting);
        }
        db.close();
        return meetings;
    }


    /**
     * This method gets meetings for a specific day of the week. For example, the M T W T F S S
     * buttons on the top of the meetings view is so that when the user clicks any of those days,
     * they can see the meetings that will happen on that date
     * I took a lot of the code from the android documentation, and modified it to my needs.
     * Here: https://developer.android.com/training/data-storage/sqlite#java
     *
     * @param dayOfWeek Specific day of the week where meetings will show up on that day.
     * @return a list of meetings for a specific day of the week
     * @throws ParseException
     */
    public List<Meeting> getMeetingsForDay(String dayOfWeek) throws ParseException {
        List<Meeting> meetings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                COLUMN_ID,
                COLUMN_DATE,
                COLUMN_START_DATE,
                COLUMN_END_DATE,
                COLUMN_TIME,
                COLUMN_DESCRIPTION,
                COLUMN_CONTACTS
        };
        Cursor cursor = db.query(
                TABLE_MEETINGS,
                projection,
                null,
                null,
                null,
                null,
                null
        );
        while (cursor.moveToNext()) {
            String itemDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATE));
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
            Date date = simpleDateFormat.parse(itemDate);
            calendar.setTime(date);
            String dayOfWeekInDB = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
            if (dayOfWeekInDB.equalsIgnoreCase(dayOfWeek)) {
                long uniqueIDForMeetingCard = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String itemDateStart = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE));
                String itemDateEnd = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE));
                String itemTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));
                String itemDescription = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                String itemContacts = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACTS));
                Meeting meeting = new Meeting(uniqueIDForMeetingCard, itemDateStart, itemDateEnd, itemTime, itemDescription, itemContacts);
                meetings.add(meeting);
            }
        }

        db.close();
        return meetings;
    }

    /**
     * This method gets meetings for a specific date of the week. For example, the M T W T F S S
     * buttons on the top of the meetings view is so that when the user clicks any of those days,
     * they can see the meetings that will happen on that date
     *
     * @param specificDate Specific day of the week where meetings will show up on that day.
     * @return a list of meetings for a specific day of the week
     * @throws ParseException
     */
    public List<Meeting> getMeetingsForDate(String specificDate) throws ParseException {
        List<Meeting> meetings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] projection = {
                COLUMN_ID,
                COLUMN_DATE,
                COLUMN_START_DATE,
                COLUMN_END_DATE, COLUMN_TIME,
                COLUMN_DESCRIPTION,
                COLUMN_CONTACTS};
        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        SimpleDateFormat dbFormat = new SimpleDateFormat("'Starts: 'EEEE, MMMM d, yyyy", Locale.getDefault());
        Cursor cursor = db.query(
                TABLE_MEETINGS,
                projection,
                COLUMN_START_DATE + " LIKE ?",
                new String[]{"%" + specificDate + "%"},
                null,
                null,
                null
        );
            while (cursor.moveToNext()) {
                String itemDateStart = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE));
                Date dateStart = dbFormat.parse(itemDateStart);
                if (dateStart != null && format.format(dateStart).equals(specificDate)) {
                    long uniqueIDForMeetingCard = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
                    String itemDateEnd = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE));
                    String itemTime = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIME));
                    String itemDescription = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                    String itemContacts = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACTS));
                    Meeting meeting = new Meeting(uniqueIDForMeetingCard, itemDateStart, itemDateEnd, itemTime, itemDescription, itemContacts);
                    meetings.add(meeting);
                }
            }
        db.close();
        return meetings;
    }

    /**
     * This method deletes a single meeting within the recycler view, where each meeting is assigned
     * a unique id. It deletes the meeting based off this unique column id.
     * A resource that helped me with this was the android documentation for sqllite, it has a section
     * just for this: https://developer.android.com/training/data-storage/sqlite
     *
     * @param id delete meetings based off unique id that each meeting is assigned
     */
    public void deleteSingleMeeting(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        db.delete(TABLE_MEETINGS, selection, selectionArgs);
        db.close();
    }

    /**
     * Deletes meetings for the current day selected, in this case, the drop down menu where it says
     * "Clear all meetings today", it deletes all the meetings of the current day.
     *
     * @param dayOfWeek day of the week that we want to delete the meetings for.
     */
    public void deleteMeetingsForDay(String dayOfWeek) {
        SQLiteDatabase db = this.getWritableDatabase();
        String dateFormat = "%" + dayOfWeek + "%";
        db.delete(TABLE_MEETINGS, COLUMN_DATE + " LIKE ?", new String[]{dateFormat});
        db.close();
    }


    /**
     * Deletes every meeting in the database by dropping the entire table
     */
    public void deleteEveryMeeting() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MEETINGS, null, null);
        db.close();
    }

    /**
     * This method deletes the expired meetings automatically from the database and as a result
     * will not show up on the view. This method is called only on app start up.
     */
    public void deleteExpiredMeetings() {
        SQLiteDatabase db = this.getWritableDatabase();
        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy h:mm a", Locale.getDefault());
        Calendar current = Calendar.getInstance();

        Cursor cursor = db.query(TABLE_MEETINGS, new String[]{COLUMN_ID, COLUMN_END_DATE}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String dateEnd = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE));

            String dateAndTime = dateEnd.replace("Ends: ", "").trim();
            try {
                Date endDate = format.parse(dateAndTime);
                if (endDate != null && endDate.before(current.getTime())) {
                    db.delete(TABLE_MEETINGS, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        db.close();
    }

    /**
     * This method is responsible for updating the meeting dates for today. The drop down menu that
     * says "Push todays meetings to next weekday" is handled by this. So, when the user pushes the
     * dates to the next weekday, then this method reads the new values in those fields, and updates
     * the database.
     *
     * @param todaysDate the current meeting date being passed in.
     */
    public void updateMeetingDatesForToday(String todaysDate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String dateTodayStart = "Starts: " + todaysDate;
        String dateTodayEnd = "Ends: " + todaysDate;
        String nextWeekdayDate = findNextAvailableWeekday();
        String nextWeekdayStart = "Starts: " + nextWeekdayDate;
        String nextWeekdayEnd = "Ends: " + nextWeekdayDate;
        // i've added this to update both the start and end dates for today's meetings
        Cursor cursor = db.query(TABLE_MEETINGS, new String[]{COLUMN_ID, COLUMN_START_DATE, COLUMN_END_DATE},
                COLUMN_START_DATE + " LIKE ?", new String[]{dateTodayStart + "%"},
                null, null, null);
        while (cursor.moveToNext()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String startDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_DATE));
            String endDate = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_DATE));
            startDate = startDate.replace(dateTodayStart, nextWeekdayStart);
            endDate = endDate.replace(dateTodayEnd, nextWeekdayEnd);
            values.put(COLUMN_START_DATE, startDate);
            values.put(COLUMN_END_DATE, endDate);
            db.update(TABLE_MEETINGS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
            values.clear();
        }
        cursor.close();
        db.close();
    }

    /**
     * This method finds the next available week day. It checks if the day of the week is
     * saturday or sunday, and if it is, then move the meeting by 2 days and 1 day respectively,
     * since we cannot have a meeting on a weekend.
     * @return returns a string value of the next available week day.
     */
    private String findNextAvailableWeekday() {
        SimpleDateFormat dateFormatForDatabase = new SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 7);
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            calendar.add(Calendar.DATE, 2); // Move to Monday
        }
        if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            calendar.add(Calendar.DATE, 1); // Move to Monday
        }
        return dateFormatForDatabase.format(calendar.getTime());
    }
}