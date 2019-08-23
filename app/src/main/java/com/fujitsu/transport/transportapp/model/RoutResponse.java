package com.fujitsu.transport.transportapp.model;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "SUCCESS",
        "routs"
})
public class RoutResponse {

    @JsonProperty("SUCCESS")
    private Boolean SUCCESS;
    @JsonProperty("routs")
    private List<Rout> routs = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public RoutResponse() {
    }

    /**
     *
     * @param routs
     * @param sUCCESS
     */
    public RoutResponse(Boolean sUCCESS, List<Rout> routs) {
        super();
        this.SUCCESS = sUCCESS;
        this.routs = routs;
    }

    @JsonProperty("SUCCESS")
    public Boolean getSUCCESS() {
        return SUCCESS;
    }

    @JsonProperty("SUCCESS")
    public void setSUCCESS(Boolean sUCCESS) {
        this.SUCCESS = sUCCESS;
    }

    @JsonProperty("routs")
    public List<Rout> getRouts() {
        return routs;
    }

    @JsonProperty("routs")
    public void setRouts(List<Rout> routs) {
        this.routs = routs;
    }

}