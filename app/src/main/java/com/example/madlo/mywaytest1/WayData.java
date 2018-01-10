package com.example.madlo.mywaytest1;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by madlo on 10.01.2018.
 */


public class WayData {
    //Privater Member vom Typ List<User>
    private List<Ways> waysList;

    public List<Ways> getWaysList() {
        return waysList;
    }

    //Erzeuge eine (versteckte) Klassenvariable vom Typ der eigenen Klasse (also WayData)
    private static WayData instance;

    //Dieser Konstruktor verhindert die Erzeugung des Objkets über andere Methoden
    private WayData() {

        waysList = new ArrayList<>();

    }

    //Methode die den Wege zur WaysList hinzufügt
    public void addWayToList(Ways ways) {
        waysList.add(ways);
    }

    //Zugriffsmethode auf Klassenebene, welches "einmal" ein konkretes Objekt erzeugt und dieses zurueck liefert
    public static WayData getInstance() {
        if (WayData.instance == null) {
            WayData.instance = new WayData();
        }
        return WayData.instance;
    }
}
