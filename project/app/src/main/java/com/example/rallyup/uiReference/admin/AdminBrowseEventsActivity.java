package com.example.rallyup.uiReference.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.rallyup.FirestoreCallbackListener;
import com.example.rallyup.FirestoreController;
import com.example.rallyup.R;
import com.example.rallyup.firestoreObjects.Event;

import java.util.List;

public class AdminBrowseEventsActivity extends AppCompatActivity implements FirestoreCallbackListener {
    ListView listView;
    ImageButton backBtn;
    List<Event> events;
    FirestoreController controller;

    AdminEventAdapter adminEventAdapter;

    /**
     * Upon getting the list of events, it will set the necessary adapters
     * @param events a list of event objects
     */
    @Override
    public void onGetEvents(List<Event> events){
        adminEventAdapter = new AdminEventAdapter(AdminBrowseEventsActivity.this, events);
        listView.setAdapter(adminEventAdapter);
        this.events = events;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_events);
        listView = findViewById(R.id.admin_browse_events_list);
        // real list
        controller = FirestoreController.getInstance();

        controller.getAdminEvents(this);

        // back button
        backBtn = findViewById(R.id.admin_events_back_button);
        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(), AdminHomepageActivity.class);  // placeholder for attendee opener
            startActivity(intent);
        });
    }
}