package com.example.courseproject_new.fragments;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.courseproject_new.R;
import com.example.courseproject_new.helpers.DBHelper;
import com.example.courseproject_new.helpers.JSONHelper;
import com.example.courseproject_new.model.Event;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
public class ImportEventFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private Button export;
    private Button importBtn;
    private EditText startDate;
    private EditText endDate;
    private SQLiteDatabase db;
    private static final int REQUEST_CODE_READ_CONTACTS = 3;
    private static boolean READ_WRITE_CONTACTS_GRANTED = false;
    private String mParam1;
    private String mParam2;
    private DatePickerDialog.OnDateSetListener setStartDateListener;
    private DatePickerDialog.OnDateSetListener setEndDateListener;

    public ImportEventFragment() {
        // Required empty public constructor
    }
    public static ImportEventFragment newInstance(String param1, String param2) {
        ImportEventFragment fragment = new ImportEventFragment();
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
        View  root = inflater.inflate(R.layout.fragment_import_event, container, false);
        db = new DBHelper(getContext()).getReadableDatabase();
        bindings(root);
        return root;
    }

    private void bindings(View view){

        //Import event
        importBtn = view.findViewById(R.id.importButton);
        importBtn.setOnClickListener(v -> {
            ArrayList<Event> importedContacts = JSONHelper.getEventsJSON(getContext());
            for (Event event: importedContacts){
                try {
                    DBHelper.addEvent(db, event);
                    Toast.makeText(getContext(), "Events are imported from JSON", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error adding event!", Toast.LENGTH_SHORT).show();
                    Log.d("ERROR adding event", e.getMessage());
                }
            }
        });

        //Pick start date
        startDate = view.findViewById(R.id.textViewStartDate);
        startDate.setFocusable(false);
        startDate.setKeyListener(null);
        startDate.setOnClickListener(v ->{
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog, setStartDateListener, year, month, day);
            datePickerDialog.show();
        });

        setStartDateListener = new DatePickerDialog.OnDateSetListener() {
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
                startDate.setText(date);
            }
        };

        //Pick end date
        endDate = view.findViewById(R.id.textViewEndDate);
        endDate.setFocusable(false);
        endDate.setKeyListener(null);
        endDate.setOnClickListener(v ->{
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), android.R.style.Theme_DeviceDefault_Dialog, setEndDateListener, year, month, day);
            datePickerDialog.show();
        });

        setEndDateListener = new DatePickerDialog.OnDateSetListener() {
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
                endDate.setText(date);
            }
        };
        //Export
        export = view.findViewById(R.id.exportButton);
        export.setOnClickListener(v ->{
            Cursor events = DBHelper.getEvents(db);
            ArrayList<Event> eventsToSave = new ArrayList<>();
            while (events.moveToNext()) {
                Integer id = events.getInt(events.getColumnIndexOrThrow("EVENT_ID"));
                String title = events.getString(events.getColumnIndexOrThrow("TITLE"));
                String description = events.getString(events.getColumnIndexOrThrow("DESCRIPTION"));
                double price = events.getDouble(events.getColumnIndexOrThrow("PRICE"));
                String date = events.getString(events.getColumnIndexOrThrow("DATE"));
                String time = events.getString(events.getColumnIndexOrThrow("TIME"));
                String endDate = events.getString(events.getColumnIndexOrThrow("ENDDATE"));
                String endTime = events.getString(events.getColumnIndexOrThrow("ENDTIME"));
                byte [] picture = events.getBlob(events.getColumnIndexOrThrow("PICTURE"));
                String location = events.getString(events.getColumnIndexOrThrow("LOCATION"));
                Event event = new Event(id, title, description,date,time,endDate,endTime, picture, location, price);
                eventsToSave.add(event);
            }

            try {
                if(startDate.getText().toString() != null && endDate.getText().toString() != null){
                    JSONHelper.saveEventsJSON(getContext(), eventsToSave,   startDate.getText().toString(), endDate.getText().toString());
                }else{
                    Toast.makeText(getContext(), "Fill in all fields!", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Toast.makeText(getContext(), "Events are exported to JSON", Toast.LENGTH_SHORT).show();
        });
    }
}