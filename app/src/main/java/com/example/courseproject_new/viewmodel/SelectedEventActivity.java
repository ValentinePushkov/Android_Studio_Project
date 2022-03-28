package com.example.courseproject_new.viewmodel;


import static com.example.courseproject_new.helpers.DBHelper.getEventPictureByID;
import static com.example.courseproject_new.utility.DbBitmapUtility.getImage;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.courseproject_new.R;
import com.example.courseproject_new.helpers.DBHelper;
import com.example.courseproject_new.model.Event;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class SelectedEventActivity extends AppCompatActivity implements OnMapReadyCallback {
    private int selectedID;
    private String selectedTitle;
    private String selectedDescription;
    private double selectedPrice;
    private String selectedDate;
    private String selectedTime;
    private String selectedEndDate;
    private  String selectedEndTime;
    private String selectedAdress;
    private SQLiteDatabase db;
    private ImageView selectedPicture;
    private TextView editSelectedTitle;
    private EditText editSelectedDate;
    private TextView editSelectedDescription;
    private TextView editSelectedPrice;
    private EditText editSelectedTime;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private MapView mMapView;
    protected Event selectedEvent;
    private Button deleteButton;
    private Button editButton;
    private Button notificationButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.selected_event);
        setContentProvider();
        bindings();
        //Set map
        mMapView = findViewById(R.id.selectedEventMap);
        initGoogleMap(savedInstanceState);
        deleteEvent(deleteButton);
        editEvent(editButton);
        createNotificationChannel();
        notify(notificationButton);
        super.onCreate(savedInstanceState);

    }



    public void bindings(){
        //Set picture
        selectedPicture =  findViewById(R.id.selectedEventImage);
        selectedPicture.setImageDrawable(setPicture());
        //Set title
        editSelectedTitle = findViewById(R.id.selectedTitle);
        editSelectedTitle.setText(selectedTitle);
        editSelectedTitle.setFocusable(false);
        editSelectedTitle.setKeyListener(null);
        //Set date
        editSelectedDate = findViewById(R.id.selectedEventDate);
        String date = "\t" + selectedDate + " - " + selectedEndDate;
        editSelectedDate.setText(date);
        editSelectedDate.setFocusable(false);
        editSelectedDate.setKeyListener(null);
        //Set description
        editSelectedDescription = findViewById(R.id.descriptionSelectedItem);
        editSelectedDescription.setText(selectedDescription);
        editSelectedDescription.setFocusable(false);
        editSelectedDescription.setKeyListener(null);
        //Set price
        editSelectedPrice = findViewById(R.id.selectedEventPrice);
        editSelectedPrice.setText("\t"+ selectedPrice + " $");

        editSelectedPrice.setFocusable(false);
        editSelectedPrice.setKeyListener(null);
        //Set time
        editSelectedTime = findViewById(R.id.selectedEventTime);
        String time = "\t" + selectedTime + " - " + selectedEndTime;
        editSelectedTime.setText(time);
        editSelectedTime.setFocusable(false);
        editSelectedTime.setKeyListener(null);
        //Delete event
        deleteButton = findViewById(R.id.deleteButton);
        //Edit event
        editButton = findViewById(R.id.editButton);
        //Notification
        notificationButton = findViewById(R.id.setNotification);
    }

    public Drawable setPicture(){
        db = new DBHelper(getApplicationContext()).getReadableDatabase();
        byte [] picture = null;
        Cursor selectedCursor = getEventPictureByID(db, selectedID);
        while (selectedCursor.moveToNext()) {
            picture = selectedCursor.getBlob(selectedCursor.getColumnIndexOrThrow("PICTURE"));
        }
        Bitmap bitPicture = getImage(picture);
        Drawable drawPicture = new BitmapDrawable(getResources(), bitPicture);
        return drawPicture;
    }

    public void setContentProvider(){
        Intent intent = getIntent();
        selectedID = (int) intent.getSerializableExtra("event_id");
        selectedTitle = (String) intent.getSerializableExtra("event_title");
        selectedDescription = (String) intent.getSerializableExtra("event_description");
        selectedPrice = (double) intent.getSerializableExtra("price");
        selectedDate = (String) intent.getSerializableExtra("event_date");
        selectedTime = (String) intent.getSerializableExtra("event_time");
        selectedEndDate = (String) intent.getSerializableExtra("event_end_date");
        selectedEndTime = (String) intent.getSerializableExtra("event_end_time");
        selectedAdress = (String) intent.getSerializableExtra("event_adress");
    }

    public void deleteEvent(Button button){
        button.setOnClickListener(v -> {
                deleteContactAction();
        });
    }

    public void notify(Button button){
        button.setOnClickListener(v -> {
              NotificationCompat.Builder builder = new NotificationCompat.Builder(SelectedEventActivity.this,
                      String.valueOf(selectedID));
              builder.setContentTitle(selectedTitle);
              builder.setContentText(selectedDescription);
              builder.setSmallIcon(R.drawable.ic_launcher_foreground);
              builder.setAutoCancel(true);
              builder.setStyle(new NotificationCompat.BigTextStyle().bigText(selectedDescription));
                NotificationManagerCompat notificationManager =
                        NotificationManagerCompat.from(SelectedEventActivity.this);
                notificationManager.cancel(selectedID);
                notificationManager.notify(selectedID, builder.build());
        });
        }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "My Channel";
            String description = "My Channel Description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(String.valueOf(selectedID), name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void editEvent(Button button){
        button.setOnClickListener(v -> {
                Intent intent = new Intent(SelectedEventActivity.this, EditEventActivity.class);
                intent.putExtra("event_id", selectedID);
                intent.putExtra("event_title", selectedTitle);
                intent.putExtra("price", selectedPrice);
                intent.putExtra("event_description", selectedDescription);
                intent.putExtra("event_date",selectedDate);
                intent.putExtra("event_time", selectedTime);
                intent.putExtra("event_end_date",  selectedEndDate);
                intent.putExtra("event_end_time", selectedEndTime);
                intent.putExtra("event_adress",  selectedAdress);
                startActivity(intent);
        });
    }


    public LatLng getLocationFromAddress(Context context, String strAddress) {

        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;

        try {
            // May throw an IOException
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }

            Address location = address.get(0);
            p1 = new LatLng(location.getLatitude(), location.getLongitude() );

        } catch (IOException ex) {

            ex.printStackTrace();
        }

        return p1;
    }

    private void initGoogleMap(Bundle savedInstanceState){
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    private void deleteContactAction() {
        try {
            Intent intent = getIntent();
            selectedID = (int) intent.getSerializableExtra("event_id");
            DBHelper.deleteEvent(db, selectedID);

            Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        } catch (Exception e) {
            Toast.makeText(this, "Delete error!", Toast.LENGTH_SHORT).show();
            Log.d("ERROR deleting contact", e.getMessage());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Bundle mapViewBundle = outState.getBundle(MAPVIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAPVIEW_BUNDLE_KEY, mapViewBundle);
        }

        mMapView.onSaveInstanceState(mapViewBundle);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng point = getLocationFromAddress(getApplicationContext(), selectedAdress);
        MarkerOptions options = new MarkerOptions().position(point).title(selectedTitle);
        map.addMarker(options);
        float zoom = 15;
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom));

    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}
