package com.fujitsu.transport.transportapp.model;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "driver_id",
        "current_latitude",
        "current_longitude",
        "start_latitude",
        "start_longitude",
        "end_latitude",
        "end_longitude",
        "is_active",
        "route_number",
        "waypoints"
})
public class Rout implements Serializable {

    @JsonProperty("id")
    private String id;
    @JsonProperty("driver_id")
    private String driver_id;
    @JsonProperty("current_latitude")
    private String current_latitude;
    @JsonProperty("current_longitude")
    private String current_longitude;
    @JsonProperty("start_latitude")
    private String start_latitude;
    @JsonProperty("start_longitude")
    private String start_longitude;
    @JsonProperty("end_latitude")
    private String end_latitude;
    @JsonProperty("end_longitude")
    private String end_longitude;
    @JsonProperty("is_active")
    private String is_active;
    @JsonProperty("route_number")
    private String route_number;
    @JsonProperty("waypoints")
    private List<Waypoint> waypoints = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public Rout() {
    }

    /**
     *
     * @param isActive
     * @param id
     * @param routeNumber
     * @param endLatitude
     * @param startLongitude
     * @param currentLongitude
     * @param startLatitude
     * @param driver_id
     * @param currentLatitude
     * @param waypoints
     * @param endLongitude
     */
    public Rout(String id, String driver_id, String currentLatitude, String currentLongitude, String startLatitude, String startLongitude, String endLatitude, String endLongitude, String isActive, String routeNumber, List<Waypoint> waypoints) {
        super();
        this.id = id;
        this.driver_id = driver_id;
        this.current_latitude = currentLatitude;
        this.current_longitude = currentLongitude;
        this.start_latitude = startLatitude;
        this.start_longitude = startLongitude;
        this.end_latitude = endLatitude;
        this.end_longitude = endLongitude;
        this.is_active = isActive;
        this.route_number = routeNumber;
        this.waypoints = waypoints;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("driver_id")
    public String getDriver_id() {
        return driver_id;
    }

    @JsonProperty("driver_id")
    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    @JsonProperty("current_latitude")
    public String getCurrent_latitude() {
        return current_latitude;
    }

    @JsonProperty("current_latitude")
    public void setCurrent_latitude(String current_latitude) {
        this.current_latitude = current_latitude;
    }

    @JsonProperty("current_longitude")
    public String getCurrent_longitude() {
        return current_longitude;
    }

    @JsonProperty("current_longitude")
    public void setCurrent_longitude(String current_longitude) {
        this.current_longitude = current_longitude;
    }

    @JsonProperty("start_latitude")
    public String getStart_latitude() {
        return start_latitude;
    }

    @JsonProperty("start_latitude")
    public void setStart_latitude(String start_latitude) {
        this.start_latitude = start_latitude;
    }

    @JsonProperty("start_longitude")
    public String getStart_longitude() {
        return start_longitude;
    }

    @JsonProperty("start_longitude")
    public void setStart_longitude(String start_longitude) {
        this.start_longitude = start_longitude;
    }

    @JsonProperty("end_latitude")
    public String getEnd_latitude() {
        return end_latitude;
    }

    @JsonProperty("end_latitude")
    public void setEnd_latitude(String end_latitude) {
        this.end_latitude = end_latitude;
    }

    @JsonProperty("end_longitude")
    public String getEnd_longitude() {
        return end_longitude;
    }

    @JsonProperty("end_longitude")
    public void setEnd_longitude(String end_longitude) {
        this.end_longitude = end_longitude;
    }

    @JsonProperty("is_active")
    public String getIs_active() {
        return is_active;
    }

    @JsonProperty("is_active")
    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    @JsonProperty("route_number")
    public String getRoute_number() {
        return route_number;
    }

    @JsonProperty("route_number")
    public void setRoute_number(String route_number) {
        this.route_number = route_number;
    }

    @JsonProperty("waypoints")
    public List<Waypoint> getWaypoints() {
        return waypoints;
    }

    @JsonProperty("waypoints")
    public void setWaypoints(List<Waypoint> waypoints) {
        this.waypoints = waypoints;
    }

}