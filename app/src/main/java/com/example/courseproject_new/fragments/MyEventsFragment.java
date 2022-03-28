package com.example.courseproject_new.fragments;

import static com.example.courseproject_new.utility.DbBitmapUtility.getImage;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.courseproject_new.R;
import com.example.courseproject_new.viewmodel.SelectedEventActivity;
import com.example.courseproject_new.helpers.DBHelper;
import com.example.courseproject_new.model.Event;

import java.util.ArrayList;
import java.util.Locale;
import java.util.stream.Collectors;

public class MyEventsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private EditText searchEventTitle;
    private ListView viewEvents;
    private Button refreshButton;
    private Button searchButton;
    private Button sortByTitleDesc;
    private Button sortByTitleAsc;
    private Button sortByDateDesc;
    private Button sortByDateAsc;
    private ArrayList<Event> events;
    private EventsListAdapter eventsListAdapterListAdapter;
    private SQLiteDatabase db;

    public MyEventsFragment() {
    }

    public static MyEventsFragment newInstance(String param1, String param2) {
        MyEventsFragment fragment = new MyEventsFragment();
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
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View  root = inflater.inflate(R.layout.fragment_my_events, container, false);
        searchEventTitle = root.findViewById(R.id.edtSearchEventTitle);
        viewEvents = root.findViewById(R.id.listViewEvents);
        db = new DBHelper(getContext()).getReadableDatabase();
        getEvents();
        EventsListAdapter[] eventsListAdapter = {new EventsListAdapter(getContext(), events)};
        viewEvents.setAdapter(eventsListAdapter[0]);
        //Refresh
        refreshButton = root.findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(v -> {
                getEvents();
                eventsListAdapter[0] = new EventsListAdapter(getContext(), events);
                viewEvents.setAdapter(eventsListAdapter[0]);
        });
        //Search
        searchButton = root.findViewById(R.id.btnSearchByEventTitle);
        searchButton.setOnClickListener(v -> {
                eventsListAdapter[0].searchEvents(getSearchEvents());
        });

        //Sort title desc
        sortByTitleDesc = root.findViewById(R.id.sortByTitleDesc);
        sortByTitleDesc.setOnClickListener(v ->{
                getEventsSortedByTitle();
                eventsListAdapter[0] = new EventsListAdapter(getContext(), events);
                viewEvents.setAdapter(eventsListAdapter[0]);
        });
        //Sort title asc
        sortByTitleAsc = root.findViewById(R.id.sortByTitleAsc);
        sortByTitleAsc.setOnClickListener(v -> {
                getEventsSortedByTitleAsc();
                eventsListAdapter[0] = new EventsListAdapter(getContext(), events);
                viewEvents.setAdapter(eventsListAdapter[0]);
        });
        //Sort date desc
        sortByDateDesc = root.findViewById(R.id.sortByDateDesc);
        sortByDateDesc.setOnClickListener(v -> {
                getEventsSortedByDateDesc();
                eventsListAdapter[0] = new EventsListAdapter(getContext(), events);
                viewEvents.setAdapter(eventsListAdapter[0]);
        });

        //Sort date asc
        sortByDateAsc = root.findViewById(R.id.ascDate);
        sortByDateAsc.setOnClickListener(v -> {
                getEventsSortedByDateAsc();
                eventsListAdapter[0] = new EventsListAdapter(getContext(), events);
                viewEvents.setAdapter(eventsListAdapter[0]);
        });

        return root;


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        db.close();
    }

    private  void getEventsSortedByDateDesc(){
        events = new ArrayList<>();

        Cursor cursor = DBHelper.getEventsSortedByDateDesc(db);

        if(cursor.getCount() == 0) return;

        while(cursor.moveToNext()) {
            Integer event_id = cursor.getInt(cursor.getColumnIndexOrThrow("EVENT_ID"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("TITLE"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("DESCRIPTION"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("PRICE"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("TIME"));
            String endDate = cursor.getString(cursor.getColumnIndexOrThrow("ENDDATE"));
            String endTime = cursor.getString(cursor.getColumnIndexOrThrow("ENDTIME"));
            byte [] picture = cursor.getBlob(cursor.getColumnIndexOrThrow("PICTURE"));
            String location = cursor.getString(cursor.getColumnIndexOrThrow("LOCATION"));

            Event event = new Event(event_id, title, description,
                    date, time,endDate,endTime,
                    picture, location, price
            );
            events.removeIf(obj -> obj.getEvent_id() == event.getEvent_id());
            events.add(event);
        }
        cursor.close();
    }

    private  void getEventsSortedByDateAsc(){
        events = new ArrayList<>();

        Cursor cursor = DBHelper.getEventsSortedByDateAsc(db);

        if(cursor.getCount() == 0) return;

        while(cursor.moveToNext()) {
            Integer event_id = cursor.getInt(cursor.getColumnIndexOrThrow("EVENT_ID"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("TITLE"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("DESCRIPTION"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("PRICE"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("TIME"));
            String endDate = cursor.getString(cursor.getColumnIndexOrThrow("ENDDATE"));
            String endTime = cursor.getString(cursor.getColumnIndexOrThrow("ENDTIME"));
            byte [] picture = cursor.getBlob(cursor.getColumnIndexOrThrow("PICTURE"));
            String location = cursor.getString(cursor.getColumnIndexOrThrow("LOCATION"));

            Event event = new Event(event_id, title, description,
                    date, time,endDate,endTime,
                    picture, location, price
            );
            events.removeIf(obj -> obj.getEvent_id() == event.getEvent_id());
            events.add(event);
        }
        cursor.close();
    }

    private  void getEventsSortedByTitleAsc(){
        events = new ArrayList<>();

        Cursor cursor = DBHelper.getEventsSortedByTitleAsc(db);

        if(cursor.getCount() == 0) return;

        while(cursor.moveToNext()) {
            Integer event_id = cursor.getInt(cursor.getColumnIndexOrThrow("EVENT_ID"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("TITLE"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("DESCRIPTION"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("PRICE"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("TIME"));
            String endDate = cursor.getString(cursor.getColumnIndexOrThrow("ENDDATE"));
            String endTime = cursor.getString(cursor.getColumnIndexOrThrow("ENDTIME"));
            byte [] picture = cursor.getBlob(cursor.getColumnIndexOrThrow("PICTURE"));
            String location = cursor.getString(cursor.getColumnIndexOrThrow("LOCATION"));

            Event event = new Event(event_id, title, description,
                    date, time,endDate,endTime,
                    picture, location, price
            );
            events.removeIf(obj -> obj.getEvent_id() == event.getEvent_id());
            events.add(event);
        }
        cursor.close();
    }

    private void getEventsSortedByTitle(){
        events = new ArrayList<>();

        Cursor cursor = DBHelper.getEventsSortedByTitle(db);

        if(cursor.getCount() == 0) return;

        while(cursor.moveToNext()) {
            Integer event_id = cursor.getInt(cursor.getColumnIndexOrThrow("EVENT_ID"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("TITLE"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("DESCRIPTION"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("PRICE"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("TIME"));
            String endDate = cursor.getString(cursor.getColumnIndexOrThrow("ENDDATE"));
            String endTime = cursor.getString(cursor.getColumnIndexOrThrow("ENDTIME"));
            byte [] picture = cursor.getBlob(cursor.getColumnIndexOrThrow("PICTURE"));
            String location = cursor.getString(cursor.getColumnIndexOrThrow("LOCATION"));

            Event event = new Event(event_id, title, description,
                    date, time,endDate,endTime,
                    picture, location, price
            );
            events.removeIf(obj -> obj.getEvent_id() == event.getEvent_id());
            events.add(event);
        }
        cursor.close();
    }

    private void getEvents() {
        events = new ArrayList<>();

        Cursor cursor = DBHelper.getEvents(db);

        if(cursor.getCount() == 0) return;

        while(cursor.moveToNext()) {
            Integer event_id = cursor.getInt(cursor.getColumnIndexOrThrow("EVENT_ID"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("TITLE"));
            String description = cursor.getString(cursor.getColumnIndexOrThrow("DESCRIPTION"));
            double price = cursor.getDouble(cursor.getColumnIndexOrThrow("PRICE"));
            String date = cursor.getString(cursor.getColumnIndexOrThrow("DATE"));
            String time = cursor.getString(cursor.getColumnIndexOrThrow("TIME"));
            String endDate = cursor.getString(cursor.getColumnIndexOrThrow("ENDDATE"));
            String endTime = cursor.getString(cursor.getColumnIndexOrThrow("ENDTIME"));
            byte [] picture = cursor.getBlob(cursor.getColumnIndexOrThrow("PICTURE"));
            String location = cursor.getString(cursor.getColumnIndexOrThrow("LOCATION"));

            Event event = new Event(event_id, title, description,
                   date, time,endDate,endTime,
                    picture, location, price
                  );
           events.removeIf(obj -> obj.getEvent_id() == event.getEvent_id());
           events.add(event);
        }
        cursor.close();
    }
    private ArrayList<Event> getSearchEvents() {
        getEvents();
        String searchStr = searchEventTitle.getText().toString()
                .replace(" ", "")
                .replace("(", "")
                .replace(")", "")
                .replace("-", "")
                .toLowerCase(Locale.ROOT);

        ArrayList<Event> searchEvents;
        searchEvents = ((ArrayList<Event>) events.stream().filter(event -> {
            return event.getTitle()
                    .replace(" ", "")
                    .replace("(", "")
                    .replace(")", "")
                    .replace("-", "").toLowerCase(Locale.ROOT).contains(searchStr)
                    || event.getDate()
                    .toLowerCase(Locale.ROOT)
                    .contains(searchStr)
                    || event.getTime()
                    .toLowerCase(Locale.ROOT)
                    .contains(searchStr);
        }).collect(Collectors.toList()));

        return searchEvents;
    }


    public class EventsListAdapter extends BaseAdapter {

        private final Context context;
        private final ArrayList<Event> events;


        public EventsListAdapter(Context context, ArrayList<Event> events) {
            this.context = context;
            this.events = events;
        }

        @Override
        public int getCount() {
            return events.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public void searchEvents(ArrayList<Event> events) {
            this.events.clear();
            this.events.addAll(events);
            this.notifyDataSetChanged();
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup viewGroup) {
            View view = getLayoutInflater().inflate(R.layout.event_item, null);

            TextView title = view.findViewById(R.id.txtEventItemTitle);
            TextView date = view.findViewById(R.id.txtEventItemDate);
            ImageView picture = view.findViewById(R.id.imageItem);
            byte [] bytePicture = events.get(pos).getPicture();
            Bitmap bitPicture = getImage(bytePicture);
            Drawable drawPicture = new BitmapDrawable(getResources(), bitPicture);

            title.setText(events.get(pos).getTitle());
            date.setText(events.get(pos).getDate());
            picture.setImageDrawable(drawPicture);

            view.setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), SelectedEventActivity.class);
                intent.putExtra("event_id", events.get(pos).getEvent_id());
                intent.putExtra("event_title", events.get(pos).getTitle());
                intent.putExtra("event_description", events.get(pos).getDescription());
                intent.putExtra("event_date", events.get(pos).getDate());
                intent.putExtra("event_time", events.get(pos).getTime());
                intent.putExtra("event_end_date", events.get(pos).getEndDate());
                intent.putExtra("event_end_time", events.get(pos).getEndTime());
                intent.putExtra("event_adress", events.get(pos).getAdress());
                intent.putExtra("price", events.get(pos).getPrice());
                startActivity(intent);
            });

            return view;
        }
    }

}