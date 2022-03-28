package com.example.courseproject_new.viewmodel;

import static android.content.ContentValues.TAG;

import static com.example.courseproject_new.helpers.DBHelper.getEventPictureByID;
import static com.example.courseproject_new.utility.DbBitmapUtility.getImage;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.courseproject_new.R;
import com.example.courseproject_new.helpers.DBHelper;
import com.example.courseproject_new.model.Event;
import com.example.courseproject_new.utility.DbBitmapUtility;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditEventActivity extends AppCompatActivity {
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
    private EditText edTextTitle;
    private EditText  edTextDescription;
    private EditText edTextPrice;
    private EditText  edText;
    private EditText  edTextTime;
    private EditText  edEndText;
    private EditText  edTextEndTime;
    private  Button pictureBtn;
    private Button locationBtn;
    private  TextView locationText;
    private ImageView imageView;
    private Button addEventBtn;
    private DatePickerDialog.OnDateSetListener setListener;
    private  DatePickerDialog.OnDateSetListener setEndListener;
    private TimePickerDialog.OnTimeSetListener setTimeListener;
    private TimePickerDialog.OnTimeSetListener setEndTimeListener;
    int SELECT_IMAGE_CODE = 1;
    int PLACE_PICKER_REQUEST = 2;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.edit_event);
        bindings();
        setContent();
        setElements();
        super.onCreate(savedInstanceState);
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

    private void  setElements(){
        edTextTitle.setText(selectedTitle);
        edTextDescription.setText(selectedDescription);
        edTextPrice.setText( String.valueOf(selectedPrice));
        edText.setText(selectedDate);
        edTextTime.setText(selectedTime);
        edEndText.setText(selectedEndDate);
        edTextEndTime.setText(selectedEndTime);
        locationText.setText(selectedAdress);
        selectedPicture.setImageDrawable(setPicture());

    }
    private void setContent(){
        Intent intent = getIntent();
        selectedID = (int) intent.getSerializableExtra("event_id");
        selectedTitle = (String) intent.getSerializableExtra("event_title");
        selectedDescription = (String) intent.getSerializableExtra("event_description");
        selectedDate = (String) intent.getSerializableExtra("event_date");
        selectedTime = (String) intent.getSerializableExtra("event_time");
        selectedEndDate = (String) intent.getSerializableExtra("event_end_date");
        selectedEndTime = (String) intent.getSerializableExtra("event_end_time");
        selectedAdress = (String) intent.getSerializableExtra("event_adress");
        selectedPrice  = (double) intent.getSerializableExtra("price");
    }
    private void editEvent(){
        Bitmap drawable = ((BitmapDrawable) selectedPicture.getDrawable()).getBitmap();
        byte [] currentPicture = DbBitmapUtility.getBytes(drawable);
        Event updatedEvent = new Event(edTextTitle.getText().toString(), edTextDescription.getText().toString(),
                Double.parseDouble(edTextPrice.getText().toString()),
                edText.getText().toString(), edTextTime.getText().toString(),edEndText.getText().toString(),
                edTextEndTime.getText().toString(), currentPicture,
                locationText.getText().toString());
        try {
            DBHelper.updateEvent(db, updatedEvent, selectedID);
            Toast.makeText(this, "Event successfully changed!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка изменения", Toast.LENGTH_SHORT).show();
            Log.d("ERROR editing contact", e.getMessage());
        }
    }

    private void bindings(){
        edTextTitle = findViewById(R.id.updateTitle);
        edTextDescription = findViewById(R.id.updateDescription);
        edTextPrice = findViewById(R.id.editTextPrice);
        edText = findViewById(R.id.updateDate);
        edText.setFocusable(false);
        edText.setKeyListener(null);
        edTextTime = findViewById(R.id.updateTime);
        edTextTime.setFocusable(false);
        edTextTime.setKeyListener(null);
        edEndText = findViewById(R.id.updateEndDate);
        edEndText.setFocusable(false);
        edEndText.setKeyListener(null);
        edTextEndTime = findViewById(R.id.updateEndTime);
        edTextEndTime.setFocusable(false);
        edTextEndTime.setKeyListener(null);
        pictureBtn = findViewById(R.id.update_picture);
        imageView = findViewById(R.id.updateImage);
        locationBtn = findViewById(R.id.updateSetNewLocation);
        locationBtn.setFocusable(false);
        locationBtn.setKeyListener(null);
        locationText = findViewById(R.id.updateLocation);
        addEventBtn = findViewById(R.id.updateEvent);
        addEventBtn.setOnClickListener(view ->{editEvent();});
        selectedPicture = findViewById(R.id.updateImage);

        //Location picker
        locationBtn.setOnClickListener(v ->{
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    Intent intent = builder.build(EditEventActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
        });

        //Picture picker
        pictureBtn.setOnClickListener(v ->{
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Title"),SELECT_IMAGE_CODE);
        });

        //Date picker
        edText.setOnClickListener(v ->{
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(EditEventActivity.this, android.R.style.Theme_DeviceDefault_Dialog, setListener,
                    year, month, day);
            datePickerDialog.show();
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month +1;
                Log.d(TAG,"onDateSet: dd/mm/yyyy: " + dayOfMonth + "/" + month + "/"+ year);
                String pickedDay = Integer.toString(dayOfMonth);;
                String pickedMonth = Integer.toString(month);
                if( pickedDay.length() == 1){
                    pickedDay = "0" + pickedDay;
                }
                if( pickedMonth.length() == 1){
                    pickedMonth = "0" + pickedMonth;
                }
                String date = pickedDay + "." + pickedMonth + "." + year;
                edText.setText(date);
            }
        };

        edEndText.setOnClickListener(v ->{
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditEventActivity.this, android.R.style.Theme_DeviceDefault_Dialog,
                        setEndListener, year, month, day);
                datePickerDialog.show();
        });

        setEndListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month +1;
                Log.d(TAG,"onDateSet: dd/mm/yyyy: " + dayOfMonth + "/" + month + "/"+ year);
                String pickedDay = Integer.toString(dayOfMonth);;
                String pickedMonth = Integer.toString(month);
                if( pickedDay.length() == 1){
                    pickedDay = "0" + pickedDay;
                }
                if( pickedMonth.length() == 1){
                    pickedMonth = "0" + pickedMonth;
                }
                String date = pickedDay + "." + pickedMonth + "." + year;
                edEndText.setText(date);
            }
        };

        //Time picker
        edTextTime.setOnClickListener(v ->{
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditEventActivity.this ,android.R.style.Theme_DeviceDefault_Dialog, setTimeListener,
                        hour, minute, android.text.format.DateFormat.is24HourFormat(getApplicationContext()));
                timePickerDialog.show();
        });

        edTextEndTime.setOnClickListener(v ->{
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditEventActivity.this,android.R.style.Theme_DeviceDefault_Dialog, setEndTimeListener,
                        hour, minute, android.text.format.DateFormat.is24HourFormat(getApplicationContext()));
                timePickerDialog.show();
        });

        setTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int startHour, int minute) {
                String min = Integer.toString(minute);
                String hour= Integer.toString(startHour);
                if(min.length() == 1){
                    min = "0"+min;
                }
                if(hour.length() == 1){
                    hour = "0"+hour;
                }
                String time = hour + ":" + min;
                edTextTime.setText(time);
            }
        };

        setEndTimeListener  = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int endHour, int minute) {
                String min = Integer.toString(minute);
                String hour= Integer.toString(endHour);
                if(min.length() == 1){
                    min = "0"+min;
                }
                if(hour.length() == 1){
                    hour = "0"+hour;
                }
                String time = hour + ":" + min;
                edTextEndTime.setText(time);
            }
        };

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_IMAGE_CODE){
            Uri uri = data.getData();
            imageView.setImageURI(uri);
        }
        if (requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == EditEventActivity.RESULT_OK){

                try {
                    Place place = PlacePicker.getPlace(getApplicationContext(),data);
                    StringBuilder stringBuilder = new StringBuilder();
                    Double latitude = Double.valueOf(place.getLatLng().latitude);
                    Double longitude = Double.valueOf(place.getLatLng().longitude);
                    Geocoder geocoder;
                    geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                    List<Address> addresses;
                    addresses = geocoder.getFromLocation(latitude, longitude,1 );
                    String locationAdress = addresses.get(0).getAddressLine(0);
                    locationText.setText(locationAdress);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
