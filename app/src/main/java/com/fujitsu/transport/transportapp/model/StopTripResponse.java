package com.fujitsu.transport.transportapp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "SUCCESS",
        "MESSAGE"
})
public class StopTripResponse {

    @JsonProperty("SUCCESS")
    private Boolean SUCCESS;
    @JsonProperty("MESSAGE")
    private String MESSAGE;

    @JsonProperty("SUCCESS")
    public Boolean getSUCCESS() {
        return SUCCESS;
    }

    @JsonProperty("SUCCESS")
    public void setSUCCESS(Boolean sUCCESS) {
        this.SUCCESS = sUCCESS;
    }

    @JsonProperty("MESSAGE")
    public String getMESSAGE() {
        return MESSAGE;
    }

    @JsonProperty("MESSAGE")
    public void setMESSAGE(String mESSAGE) {
        this.MESSAGE = mESSAGE;
    }

}