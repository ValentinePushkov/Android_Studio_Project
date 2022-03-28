package com.example.courseproject_new.fragments;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.courseproject_new.R;
import com.example.courseproject_new.helpers.DBHelper;
import com.example.courseproject_new.model.Event;
import com.example.courseproject_new.utility.DbBitmapUtility;
import com.example.courseproject_new.viewmodel.MainActivity;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class NewEventFragment extends Fragment{

    private SQLiteDatabase db;
    private int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private EditText  edTextTitle;
    private EditText  edTextDescription;
    private EditText  edTextPrice;
    private EditText  edText;
    private  EditText  edTextTime;
    private  EditText  edEndText;
    private EditText  edTextEndTime;
    private Button pictureBtn;
    private Button locationBtn;
    private TextView locationText;
    private ImageView  imageView;
    private  Button addEventBtn;
    private Button addToCalendar;
    private DatePickerDialog.OnDateSetListener setListener;
    private DatePickerDialog.OnDateSetListener setEndListener;
    private TimePickerDialog.OnTimeSetListener setTimeListener;
    private TimePickerDialog.OnTimeSetListener setEndTimeListener;
    private int SELECT_IMAGE_CODE = 1;
    private int PLACE_PICKER_REQUEST = 2;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private void toastMessage(String message){
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public NewEventFragment() {
        // Required empty public constructor
    }


    public static NewEventFragment newInstance(String param1, String param2) {
        NewEventFragment fragment = new NewEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  root = inflater.inflate(R.layout.fragment_new_event, container, false);
        bindings(root);
        //ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.GET_ACCOUNTS}, 1);
        //getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return root;
    }

    private  void bindings(View view){
        edTextTitle = view.findViewById(R.id.updateTextTitle);
        edTextDescription = view.findViewById(R.id.updateTextDescription);
        edText = view.findViewById(R.id.updateTextDate);
        edText.setFocusable(false);
        edText.setKeyListener(null);
        edTextTime = view.findViewById(R.id.updateTextTime);
        edTextTime.setFocusable(false);
        edTextTime.setKeyListener(null);
        edEndText = view.findViewById(R.id.updateTextEndDate);
        edEndText.setFocusable(false);
        edEndText.setKeyListener(null);
        edTextEndTime = view.findViewById(R.id.updateTextEndTime);
        edTextEndTime.setFocusable(false);
        edTextEndTime.setKeyListener(null);
        pictureBtn = view.findViewById(R.id.update_picture_button);
        imageView = view.findViewById(R.id.updateImageView);
        locationBtn = view.findViewById(R.id.updateSetLocation);
        locationBtn.setFocusable(false);
        locationBtn.setKeyListener(null);
        locationText = view.findViewById(R.id.updateLocationText);
        locationText.setFocusable(false);
        locationText.setKeyListener(null);
        addToCalendar = view.findViewById(R.id.addEventToCalendar);
        edTextPrice = view.findViewById(R.id.updateTextPrice);

        //Adding event
        addEventBtn = view.findViewById(R.id.updateEventButton);
        addEventBtn.setOnClickListener(v -> {
                    Bitmap drawable = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    byte [] currentPicture = DbBitmapUtility.getBytes(drawable);
                    if(edTextTitle.getText().toString().isEmpty() || edTextDescription.getText().toString().isEmpty() || edText.getText().toString().isEmpty()
                            || edTextTime.getText().toString().isEmpty()  || edEndText.getText().toString().isEmpty() || edTextTime.getText().toString().isEmpty() || imageView.getDrawable() == null
                            || locationText.getText().toString().isEmpty() || edTextPrice.getText().toString().isEmpty()){
                        Context context = getContext();
                        CharSequence text = "Enter all data!";
                        int duration = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }else{
                        Event event = new Event(edTextTitle.getText().toString(), edTextDescription.getText().toString(),
                                Double.parseDouble(edTextPrice.getText().toString()),
                                edText.getText().toString(), edTextTime.getText().toString(),edEndText.getText().toString(),
                                edTextEndTime.getText().toString(), currentPicture,
                                locationText.getText().toString());
                        db = new DBHelper(getContext()).getReadableDatabase();
                        DBHelper.addEvent(db, event);
                        edTextTitle.getText().clear();
                        edTextDescription.getText().clear();
                        edTextPrice.getText().clear();
                        edText.getText().clear();
                        edTextTime.getText().clear();
                        edEndText.getText().clear();
                        edTextEndTime.getText().clear();
                        imageView.setImageResource(android.R.color.transparent);
                        locationText.setText("");
                        Toast.makeText(getContext(), "Event added successfully!", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        //Setting location
        locationBtn.setOnClickListener(v -> {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                Intent intent = builder.build(getActivity());
                startActivityForResult(intent, PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }

        });

        //Picture picker
        pictureBtn.setOnClickListener(v -> {
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
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog, setListener,
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
                String date = year + "-" + pickedMonth + "-" + pickedDay;
                edText.setText(date);
            }
        };



        edEndText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog,
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
                String date = year + "-" + pickedMonth + "-" + pickedDay;
                edEndText.setText(date);
            }
        };

        //Time picker
        edTextTime.setOnClickListener(v ->{
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),android.R.style.Theme_DeviceDefault_Dialog, setTimeListener,
                    hour, minute, android.text.format.DateFormat.is24HourFormat(getContext()));
            timePickerDialog.show();
        });

        edTextEndTime.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),android.R.style.Theme_DeviceDefault_Dialog, setEndTimeListener,
                    hour, minute, android.text.format.DateFormat.is24HourFormat(getContext()));
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

        //Add event to calendar
        addToCalendar.setOnClickListener(v -> {
            if(edText.getText().toString().isEmpty() || edEndText.getText().toString().isEmpty() || edTextEndTime.getText().toString().isEmpty()
                    || edTextTime.getText().toString().isEmpty()){
                Toast.makeText(getContext(), "Pick Start Data, End Data, Start Time And End Time!", Toast.LENGTH_SHORT).show();
            }else{
                //Set start date
                String startDate = edText.getText().toString();
                String[] startDateMassive = startDate.split("-");
                int startDay = Integer.parseInt(startDateMassive[2]);
                int startMonth = Integer.parseInt(startDateMassive[1]);
                int startYear = Integer.parseInt(startDateMassive[0]);
                //Set start time
                String startTime = edTextTime.getText().toString();
                String[] startTimeMassive  = startTime.split(":");
                int startHour = Integer.parseInt(startTimeMassive[0]);
                int startMinute = Integer.parseInt(startTimeMassive[1]);
                //Set end date
                String endDate = edEndText.getText().toString();
                String[] endDateMassive = endDate.split("-");
                int endDay = Integer.parseInt(endDateMassive[2]);
                int endMonth = Integer.parseInt(endDateMassive[1]);
                int endYear = Integer.parseInt(endDateMassive[0]);
                //Set end time
                String endTime = edTextEndTime.getText().toString();
                String[] endTimeMassive  = endTime.split(":");
                int endHour = Integer.parseInt(endTimeMassive[0]);
                int endMinute = Integer.parseInt(endTimeMassive[1]);

                int calenderId= -1;

                String calenderEmaillAddress= getEmail(getContext());
                String[] projection = new String[]{
                        CalendarContract.Calendars._ID,
                        CalendarContract.Calendars.ACCOUNT_NAME};
                ContentResolver cr = getContext().getContentResolver();
                Cursor cursor = cr.query(Uri.parse("content://com.android.calendar/calendars"), projection,
                        CalendarContract.Calendars.ACCOUNT_NAME + "=? and (" +
                                CalendarContract.Calendars.NAME + "=? or " +
                                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME + "=?)",
                        new String[]{calenderEmaillAddress, calenderEmaillAddress,
                                calenderEmaillAddress}, null);

                if (cursor.moveToFirst()) {
                    if (cursor.getString(1).equals(calenderEmaillAddress))
                        calenderId=cursor.getInt(0);
                }
                long startMillis = 0;
                long endMillis = 0;
                Calendar beginTime = Calendar.getInstance();
                beginTime.set(startYear, startMonth, startDay, startHour, startMinute);
                startMillis = beginTime.getTimeInMillis();
                Calendar endTimeCalendar = Calendar.getInstance();
                endTimeCalendar.set(endYear, endMonth, endDay, endHour, endMinute);
                endMillis = endTimeCalendar.getTimeInMillis();
                Calendar cal = Calendar.getInstance();
                TimeZone currentZone = cal.getTimeZone();
                ContentResolver calendarCr = getContext().getContentResolver();
                ContentValues values = new ContentValues();
                values.put(CalendarContract.Events.DTSTART, startMillis);
                values.put(CalendarContract.Events.DTEND, endMillis);
                values.put(CalendarContract.Events.TITLE, edTextTitle.getText().toString());
                values.put(CalendarContract.Events.DESCRIPTION, edTextDescription.getText().toString());
                values.put(CalendarContract.Events.CALENDAR_ID, calenderId);
                values.put(CalendarContract.Events.EVENT_TIMEZONE, currentZone.toString());
                Uri uri = calendarCr.insert(CalendarContract.Events.CONTENT_URI, values);
                Toast.makeText(getContext(), "Event added successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    static String getEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);

        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_IMAGE_CODE){
            Uri uri = data.getData();
            imageView.setImageURI(uri);
        }
        if (requestCode == PLACE_PICKER_REQUEST){
            if (resultCode == getActivity().RESULT_OK){

                try {
                    Place place = PlacePicker.getPlace(getContext(),data);
                    StringBuilder stringBuilder = new StringBuilder();
                    Double latitude = Double.valueOf(place.getLatLng().latitude);
                    Double longitude = Double.valueOf(place.getLatLng().longitude);
                    Geocoder geocoder;
                    geocoder = new Geocoder(getContext(), Locale.getDefault());
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