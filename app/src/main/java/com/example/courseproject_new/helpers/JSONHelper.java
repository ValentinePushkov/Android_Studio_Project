package com.example.courseproject_new.helpers;

import android.content.Context;
import android.widget.EditText;

import com.example.courseproject_new.model.Event;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class JSONHelper {
    private static final String FILE_NAME = "Events.json";
    private EditText startDate;
    private EditText endDate;

    public static ArrayList<Event> getEventsJSON(Context context) {
        if(!isExist(context))
            return new ArrayList<>();

        Gson gson = new Gson();
        File file = new File(context.getFilesDir(), FILE_NAME);

        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);

            DataItems events = gson.fromJson(isr, DataItems.class);

            fis.close();
            isr.close();
            return events.getEvents();
        } catch (Exception e) { }

        return new ArrayList<>();
    }

    public static void saveEventsJSON(Context context, ArrayList<Event> events,String startDate, String endDate) throws ParseException {
        File file = new File(context.getFilesDir(), FILE_NAME);
        Gson gson = new Gson();
        DataItems dataItems = new DataItems();
        ArrayList<Event> newEvents = new ArrayList<Event>();
        for(Event event:events){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-dd");
            LocalDate date = LocalDate.parse(event.getDate(), formatter);
            if((date.isAfter(LocalDate.parse(startDate,formatter))
                    && date.isBefore(LocalDate.parse(endDate, formatter))) || (date.equals(startDate) && date.equals(endDate))){
                newEvents.add(event);
            }
        }
        dataItems.setEvents(newEvents);
        String jsonStr = gson.toJson(dataItems);

        try {
            FileOutputStream fos = new FileOutputStream(file, false);

            fos.write(jsonStr.getBytes());
            fos.close();
        } catch (Exception e) { }
    }

    private static boolean isExist(Context context) {
        File file = new File(context.getFilesDir(), FILE_NAME);
        return file.exists();
    }

    private static class DataItems {
        private ArrayList<Event> events;

        public ArrayList<Event> getEvents() { return events; }
        public void setEvents(ArrayList<Event> events) { this.events = events; }
    }
}
