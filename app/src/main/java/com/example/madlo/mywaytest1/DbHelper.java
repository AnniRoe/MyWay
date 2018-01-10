package com.example.madlo.mywaytest1;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by madlo on 29.11.2017.
 */

public class DbHelper extends SQLiteOpenHelper {
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbContract.SQL_CREATE_WAY_TABLE);

    }

    //TODO: USERDATA ERZEUGEN UND IN WAYDATA UMSCHREIBEN
    //Diese Funktion soll die die gespeicherten Wege aus der Datenbank auslesen und in die Liste der Wege der Klasse UserData
    //hinzufuegen
    public void loadWays() {

        //Auslesen von Daten aus einer Tabelle durch SELECT
        //Zuerst einen SQL-Befehl in FOrm eines Strings definieren
        String selectQuery = "SELECT * FROM " + DbContract.WayTable.TABLE_NAME;

        //Damit man Lesezugriff fuer ein Datenbankobjekt hat
        SQLiteDatabase db = this.getReadableDatabase();

        //rawQuery-Methode um einen Cursor auf die Ergebnisliste zu erhalten , als Parameter kriegt
        //er den zuvor definierten selectQuery-String uebergeben
        Cursor cursor = db.rawQuery(selectQuery, null);


        if (cursor.moveToFirst()) {
            do {
                //cursor zeigt auf einen eintrag der ergebnisliste
                //Auslesen der werte aus den spalten

                String transport = cursor.getString(
                        cursor.getColumnIndex(DbContract.WayTable.COLUMN_NAME_TRANSPORT));
                String duration = cursor.getString(
                        cursor.getColumnIndex(DbContract.WayTable.COLUMN_NAME_DURATION));
                String distance = cursor.getString(
                        cursor.getColumnIndex(DbContract.WayTable.COLUMN_NAME_DISTANCE));
                String date = cursor.getString(
                        cursor.getColumnIndex(DbContract.WayTable.COLUMN_NAME_DATE));
                String trackNumber = cursor.getString(
                        cursor.getColumnIndex(DbContract.WayTable.COLUMN_NAME_TRACKNUMBER));

                //Neues Ways Objekt erstellen
                Ways ways = new Ways();

                //Werte des User-Objekts werden auf die ausgelesenen Werte gesetzt
                ways.setTransport(transport);
                ways.setDuration(duration);
                ways.setDistance(distance);
                ways.setDate(date);
                ways.setTrackingNumber(trackNumber);

//TODO: Userdata bzw WayData klasse erstellen
                //Erzeugtes Ways-Objekt der Ways Liste in der WayData Klasse hinzufuegen mittels eigenerstellter Methode
                WayData.getInstance().addWayToList(ways);

            } while (cursor.moveToNext());
        }

        //Cursor Objekt schliessen
        cursor.close();
        db.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //Funktion - Soll beim hinzufuegen eines Nutzers nicht nur in einer Nutzerliste sondern
    //persistent in der Tabelle der Datenbank gespeichert werden
    public void saveWay(Ways ways) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues valuesWays = new ContentValues();
        valuesWays.put(DbContract.WayTable.COLUMN_NAME_TRANSPORT, ways.getTransport());
        valuesWays.put(DbContract.WayTable.COLUMN_NAME_DURATION, ways.getDuration());
        valuesWays.put(DbContract.WayTable.COLUMN_NAME_DISTANCE, ways.getDistance());
        valuesWays.put(DbContract.WayTable.COLUMN_NAME_DATE, ways.getDate());
        valuesWays.put(DbContract.WayTable.COLUMN_NAME_TRACKNUMBER, ways.getTrackingNumber());

        db.insert(DbContract.WayTable.TABLE_NAME, null, valuesWays);
        db.close();
    }


    //Erzeuge eine (versteckte) Klassenvariable vom Typ der eigenen Klasse (also UserData)
    private static DbHelper instance;

    private DbHelper(Context context) {

        super(context, "user_db", null, 1); //Context ist der Variablen-Bezeichner des Parameters

    }

    public static DbHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbHelper(context);
        }
        return instance;
    }

}