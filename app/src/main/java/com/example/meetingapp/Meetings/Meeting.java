package com.example.meetingapp.Meetings;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
    Name: Ameen Khawaja
    ID: 6935688
    Email: ak19nu@brocku.ca
*/
/**
 * This class is responsible for storing each meeting cards information into a meeting object.
 * It contains a Meeting constructor and a bunch of getter & setters.
 */
public class Meeting{
    /**
     * This is my meeting object, which consists of a unique id, start date, end date, time,
     * description, and contacts.
     * @param uniqueID Each meeting is associated with a unique id number in the database, so when
     *                 the user wants to delete a meeting, it deletes based off the unique ID
     * @param date This is the full date of what the user selects from the date picker dialog.
     * @param dateEnd The end date the user wants the meeting to end on
     * @param time The time of the meeting
     * @param description The description of the meeting which can be anything the user wants
     * @param contacts Stores contacts that are retrieved from the phone contact list.
     */
    public Meeting(long uniqueID, String date, String dateEnd, String time, String description, String contacts){
        this.uniqueID = uniqueID;
        this.date = date;
        this.time = time;
        this.description = description;
        this.dateEnd = dateEnd;
        this.contacts = contacts;
    }

    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }

    private String contacts;
    public Meeting(long uniqueID, String date, String time, String description){
        this.uniqueID = uniqueID;
        this.date = date;
        this.time = time;
        this.description = description;
        this.dateEnd = dateEnd;
    }

    public Meeting(String date, String time, String description) {
        this.date = date;
        this.time = time;
        this.description = description;
    }


    private String dateEnd;
    private String date;
    private String time;

    public long getUniqueID() {
        return uniqueID;
    }

    public void setUniqueID(long uniqueID) {
        this.uniqueID = uniqueID;
    }

    private long uniqueID;

    public String getDate() {
        return date;
    }

    public String getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(String dateEnd) {
        this.dateEnd = dateEnd;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    private String description;



}