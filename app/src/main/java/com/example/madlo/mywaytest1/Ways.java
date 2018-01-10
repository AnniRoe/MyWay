package com.example.madlo.mywaytest1;

/**
 * Created by madlo on 10.01.2018.
 */

public class Ways {
    // Im folgenden die privaten Member für die jeweiligen Daten
    private String transport;
    private String duration;
    private String distance;
    private String date;

    public String getTransport() {
        return transport;
    }

    public void setTransport(String transport) {
        this.transport = transport;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    private String trackingNumber;

    //Für die Getter und Setter Alt+Einfg druecken und auswaehlen
}
