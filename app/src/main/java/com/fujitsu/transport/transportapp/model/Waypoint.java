package com.fujitsu.transport.transportapp.model;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "name",
        "waypoint_id",
        "route_id",
        "latitude",
        "longitude",
        "sequence"
})
public class Waypoint {

    @JsonProperty("name")
    private String name;
    @JsonProperty("waypoint_id")
    private String waypointId;
    @JsonProperty("route_id")
    private String routeId;
    @JsonProperty("latitude")
    private String latitude;
    @JsonProperty("longitude")
    private String longitude;
    @JsonProperty("sequence")
    private String sequence;

    /**
     * No args constructor for use in serialization
     *
     */
    public Waypoint() {
    }

    /**
     *
     * @param waypointId
     * @param sequence
     * @param name
     * @param longitude
     * @param routeId
     * @param latitude
     */
    public Waypoint(String name, String waypointId, String routeId, String latitude, String longitude, String sequence) {
        super();
        this.name = name;
        this.waypointId = waypointId;
        this.routeId = routeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sequence = sequence;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("waypoint_id")
    public String getWaypointId() {
        return waypointId;
    }

    @JsonProperty("waypoint_id")
    public void setWaypointId(String waypointId) {
        this.waypointId = waypointId;
    }

    @JsonProperty("route_id")
    public String getRouteId() {
        return routeId;
    }

    @JsonProperty("route_id")
    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    @JsonProperty("latitude")
    public String getLatitude() {
        return latitude;
    }

    @JsonProperty("latitude")
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    @JsonProperty("longitude")
    public String getLongitude() {
        return longitude;
    }

    @JsonProperty("longitude")
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    @JsonProperty("sequence")
    public String getSequence() {
        return sequence;
    }

    @JsonProperty("sequence")
    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

}