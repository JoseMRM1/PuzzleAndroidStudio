package com.example.puzzledroid_java.utils;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Toast;

import com.example.puzzledroid_java.BuildConfig;

public class QueryHandler extends AsyncQueryHandler {
    private static final String TAG = "QueryHandler";

    private static final String[] CALENDAR_PROJECTION = new String[] {
                CalendarContract.Calendars._ID
            };

    private static final int CALENDAR_ID_INDEX = 0;

    private static final int CALENDAR = 0;
    private static final int EVENT    = 1;
    private static final int REMINDER = 2;

    private static QueryHandler queryHandler;

    public QueryHandler(ContentResolver cr) {
        super(cr);
    }
    public static void insertEvent(Context context, long startTime,
                                   long endTime, String title)
    {
        ContentResolver resolver = context.getContentResolver();

        if (queryHandler == null)
            queryHandler = new QueryHandler(resolver);

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startTime);
        values.put(CalendarContract.Events.DTEND, endTime);
        values.put(CalendarContract.Events.TITLE, title);

        if (BuildConfig.DEBUG)
            Log.d(TAG, "Calendar query start");

        queryHandler.startQuery(CALENDAR, values, CalendarContract.Calendars.CONTENT_URI,
                CALENDAR_PROJECTION, null, null, null);
        Toast.makeText(context.getApplicationContext(), "Holis", Toast.LENGTH_SHORT).show();
    }
}
