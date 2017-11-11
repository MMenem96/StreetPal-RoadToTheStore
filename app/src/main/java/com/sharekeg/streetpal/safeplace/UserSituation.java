
package com.sharekeg.streetpal.safeplace;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserSituation {

    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lon")
    @Expose
    private double lon;
    @SerializedName("severity")
    @Expose
    private String severity;

    /**
     * No args constructor for use in serialization
     */
    public UserSituation() {
    }

    /**
     * @param lon
     * @param severity
     * @param lat
     */
    public UserSituation(double lat, double lon, String severity) {
        super();
        this.lat = lat;
        this.lon = lon;
        this.severity = severity;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

}
