package com.example.meetingapp.Fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.meetingapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for displaying a list of contacts in the contacts view.
 * I've used partial code from the Android documentation:
 * https://developer.android.com/training/contacts-provider/retrieve-names
 */
public class ContactsFragment extends Fragment {

    ListView listViewContacts;
    List<String> contactsList = new ArrayList<>();
    ArrayAdapter<String> contactsAdapter;
    private static final int READ_CONTACT_PERMISSION_REQUEST = 79;

    // https://developer.android.com/training/contacts-provider/retrieve-names
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);

        listViewContacts = view.findViewById(R.id.list_view_contacts);
        contactsAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, contactsList);
        listViewContacts.setAdapter(contactsAdapter);

        // this asks for permission rights and if the user grants it will display the phone contacts
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACT_PERMISSION_REQUEST);
        } else {
            displayContacts();
        }
        return view;
    }

    /**
     * This method helps display all the phone contacts within the phone. It does so by
     * Partial code taken from Android documentation: https://developer.android.com/training/contacts-provider/retrieve-names
     */
    private void displayContacts() {
        contactsList.clear();
        Cursor cursor = requireContext().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
        if(cursor.getCount() > 0){
            while (cursor.moveToNext()) {
                int indexForName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                int indexForPhoneNum = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String name = cursor.getString(indexForName);
                String phoneNumber = cursor.getString(indexForPhoneNum);
                contactsList.add(name + " (" + phoneNumber + ")");
            }
            cursor.close();
        }
        contactsAdapter.notifyDataSetChanged();
    }

}
