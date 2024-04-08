package com.example.rallyup.uiReference.organizers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.health.connect.datatypes.HeartRateRecord;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.rallyup.FirestoreCallbackListener;
import com.example.rallyup.FirestoreController;
import com.example.rallyup.LocalStorageController;
import com.example.rallyup.R;
import com.example.rallyup.firestoreObjects.Event;
import com.example.rallyup.firestoreObjects.User;
import com.example.rallyup.uiReference.testingClasses.AttListArrayAdapter;
import com.example.rallyup.uiReference.testingClasses.AttendeeStatsClass;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.firebase.firestore.FirestoreRegistrar;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * This class contains the activity for the attendee's info in an event
 * @author Kaye Maranan
 */
public class EventAttendeesInfoActivity extends AppCompatActivity
        implements OnMapReadyCallback, FirestoreCallbackListener {

    ImageButton eventAttBackButton;
    ArrayList<AttendeeStatsClass> dataList;
    private ListView attlist;      // the view that everything will be shown on
    private AttListArrayAdapter attListAdapter;

    // Maps
    private GoogleMap map;

    // Data management controllers.
    FirestoreController fc = FirestoreController.getInstance();
    //LocalStorageController lc = LocalStorageController.getInstance();

    // Colors to be used for the HeatMap gradient
    int[] colors = {
        Color.rgb(102, 225, 0), // green
        Color.rgb(255, 0, 0)    // red
    };

    // percentage of maximum intensity from the first element to the second
    float[] startingPoints = {
            0.2f,
            1f
    };
    
    // Gradient of the HeatMap
    Gradient gradient = new Gradient(colors, startingPoints);

    @Override
    public void onGetUsers(List<User> userList) {
        // LatLngs list for the heatmap
        List<LatLng> latLngs = new ArrayList<>();

        //FirestoreCallbackListener.super.onGetUsers(userList);
        // Iterate through the list and add the LatLng objects into an array
        for(User user:userList){
            if (user.getGeolocation()){
                latLngs.add(new LatLng(user.getLatlong().getLatitude(), user.getLatlong().getLongitude()));
                //Toast.makeText(getBaseContext(), "User LatLng Added!", Toast.LENGTH_SHORT).show();
            }
            //System.out.println("userID: " + user.getId());
            //System.out.println("getGeolocation: " + user.getGeolocation());
        }
        // Add the HeatMap overlay after we received ALL the
        //latLngs.add(new LatLng(-4.0383, 21.7587));
        //latLngs.add(new LatLng(-4.0383, 21.7587));
        addHeatMap(latLngs);
    }

    @Override
    public void onGetEvent(Event event) {
        // If the Event's geolocation is true, then do the map.
        //fc.getCheckedInUserIDs("048ACC2B534046668F6BAA2EA43F170C", this);
        if (event.getGeolocation()){
            //fc.getCheckedInUserIDs(event.getEventID(), this);
            fc.getCheckedInUserIDs2(event.getEventID(), this);
        }
    }

    @Override
    public void onGetLatLngs(List<LatLng> latLngs) {
        // Set the latLngs here into our heatmap
        addHeatMap(latLngs);
        Log.d("EventAttendeesInfoActivity - onGetLatLngs", "latLngs size: " + latLngs.size());
    }

    /**
     * Initializes an event's attendees info activity when it is first launched
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_attendees_info);

        // Get the event ID only works IF it has been passed to this activity
        // WHICH should be from OrganizerEventDetailsActivity
        // And that activity receives its eventID from OrganizerEventListActivity
        String eventID = getIntent().getStringExtra("eventID");
        // Then call the FirestoreController to do something
        // (probably to retrieve the lat longs of users)
        //fc.getEventByID(eventID, this);
        // test event ID: 048ACC2B534046668F6BAA2EA43F170C
        //fc.getCheckedInUserIDs("048ACC2B534046668F6BAA2EA43F170C", this);
        fc.getEventByID("048ACC2B534046668F6BAA2EA43F170C", this);
        //fc.getCheckedInUserIDs2("048ACC2B534046668F6BAA2EA43F170C", this);

        // SupportMapFragment that manages the GoogleMap object
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        eventAttBackButton = findViewById(R.id.event_attendees_back_button);
        eventAttBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), OrganizerEventDetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    // Great reference from StackOverflow:
    // https://stackoverflow.com/questions/49465240/weighted-heat-maps-in-android

    /**
     * Method that adds a Heat Map overlay to a GoogleMaps object
     * Will show a Toast if there are NO latLngs
     * @param latLngs A list of LatLng objects for the HeatMap to mark
     */
    private void addHeatMap(List<LatLng> latLngs){
        if (!latLngs.isEmpty()){
            HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                    .data(latLngs)
                    .gradient(gradient)
                    .build();
            TileOverlay overlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
        } else {
         Toast.makeText(getBaseContext(), "No data points available", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Method that adds a Weighted Heat Map overlay to a GoogleMaps object
     * Will show a Toast if there are NO latLngs
     * @param latLngs A list of WeightedLatLng objects for the HeatMap to mark
     */
    private void addHeatMapWeighted(List<WeightedLatLng> latLngs){
        if (!latLngs.isEmpty()){
            HeatmapTileProvider provider = new HeatmapTileProvider.Builder()
                    .weightedData(latLngs)
                    .gradient(gradient)
                    .build();
            TileOverlay overlay = map.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
        } else {
            Toast.makeText(getBaseContext(), "No data points available", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Overrided method that activates the map
     * @param googleMap The googleMap object that we instantiate
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        if (map != null){
            return;
        }
        map = googleMap;
    }
}