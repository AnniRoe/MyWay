package com.example.madlo.mywaytest1;

import android.provider.BaseColumns;

/**
 * Created by madlo on 10.01.2018.
 */

public class DbContract {
    private DbContract() {

    }

    static class WayTable implements BaseColumns {
        public static final String TABLE_NAME = "way_table";
        public static final String COLUMN_NAME_TRANSPORT = "means_of_transport";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TRACKNUMBER = "track_number";


    }

    public static final String SQL_CREATE_WAY_TABLE =
            "CREATE TABLE " + WayTable.TABLE_NAME + " (" +
                    WayTable._ID + " INTEGER PRIMARY KEY," +
                    WayTable.COLUMN_NAME_TRANSPORT + " TEXT," +
                    WayTable.COLUMN_NAME_DURATION + " TEXT," +
                    WayTable.COLUMN_NAME_DISTANCE + " TEXT," +
                    WayTable.COLUMN_NAME_DATE + " TEXT," +
                    WayTable.COLUMN_NAME_TRACKNUMBER + " TEXT)";

}