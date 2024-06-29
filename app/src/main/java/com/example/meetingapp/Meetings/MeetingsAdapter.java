package com.example.meetingapp.Meetings;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meetingapp.R;

import java.util.List;


/**
 * This class is responsible for managing the list of meetings that is displayed which is a
 * RecyclerView. Basically, it binds every Meeting object to the RecyclerView's item and allows
 * for an easy way of handling the users specified interactions such as deleting a meeting
 *
 * I based my class design off the official Android documentation which was very helpful.
 * https://developer.android.com/develop/ui/views/layout/recyclerview
 */
public class MeetingsAdapter extends RecyclerView.Adapter<MeetingsAdapter.MeetingViewHolder> {
    private static List<Meeting> meetingsList;
    public class MeetingViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
        public TextView meetingDateStart, meetingTime, meetingDescription, meetingDateEnd, meetingContacts;
        ImageButton buttonEditCard;
        public MeetingViewHolder(View view) {
            super(view);
            meetingDateStart = view.findViewById(R.id.meeting_date_start);
            meetingDateEnd = view.findViewById(R.id.meeting_date_end);
            meetingTime = view.findViewById(R.id.meeting_time);
            meetingDescription = view.findViewById(R.id.meeting_description);
            buttonEditCard = view.findViewById(R.id.edit_button_card);
            buttonEditCard.setOnClickListener(this);
            meetingContacts = view.findViewById(R.id.contacts_invited_field);
        }
        @Override
        public void onClick(View view) {
            popupMenu(view);
        }
        private void popupMenu(View view) {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
            popupMenu.inflate(R.menu.popup_menu);
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (item.getItemId() == R.id.action_delete_meeting) {
                int adapterPos = getAdapterPosition();
                long meetingId = meetingsList.get(adapterPos).getUniqueID();
                MeetingsDatabase dbHelper = new MeetingsDatabase(itemView.getContext());
                dbHelper.deleteSingleMeeting(meetingId);
                meetingsList.remove(adapterPos);
                notifyItemRemoved(adapterPos);
                return true;
            } else if (item.getItemId() == 1) {
                return true;
            }
            return false;
        }
    }
    public MeetingsAdapter(List<Meeting> meetingsList) {
        this.meetingsList = meetingsList;
    }

    @Override
    public MeetingViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.meeting_item_card, viewGroup, false);
        return new MeetingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MeetingViewHolder viewHolder, int position) {
        Meeting meeting = meetingsList.get(position);
        viewHolder.meetingDateStart.setText(meeting.getDate());
        viewHolder.meetingDateEnd.setText(meeting.getDateEnd());
        viewHolder.meetingTime.setText(meeting.getTime());
        viewHolder.meetingDescription.setText(meeting.getDescription());
        viewHolder.meetingContacts.setText(meeting.getContacts());
    }

    @Override
    public int getItemCount() {
        return meetingsList.size();
    }

}
