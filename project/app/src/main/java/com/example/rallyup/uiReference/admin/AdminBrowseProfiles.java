package com.example.rallyup.uiReference.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.example.rallyup.R;

/**
 * This class contains an activity that allows an admin to browse user profiles to be modified
 */
public class AdminBrowseProfiles extends AppCompatActivity {

    ImageButton backBtn;

    /**
     * Initializes the browse profiles activity when it is launched
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_browse_profiles);

        backBtn = findViewById(R.id.admin_profiles_back_button);

        backBtn.setOnClickListener(view -> {
            Intent intent = new Intent(getBaseContext(), AdminHomepageActivity.class);  // placeholder for attendee opener
            startActivity(intent);
        });
    }
}