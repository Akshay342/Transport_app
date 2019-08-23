package com.fujitsu.transport.transportapp.model;


import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "SUCCESS",
        "id",
        "firstname",
        "lastname",
        "email",
        "mobilenumber",
        "role"
})
public class UserDetails implements Serializable{

    @JsonProperty("SUCCESS")
    private Boolean SUCCESS;
    @JsonProperty("id")
    private String id;
    @JsonProperty("firstname")
    private String firstname;
    @JsonProperty("lastname")
    private String lastname;
    @JsonProperty("email")
    private String email;
    @JsonProperty("mobilenumber")
    private String mobilenumber;
    @JsonProperty("role")
    private String role;

    @JsonProperty("SUCCESS")
    public Boolean getSUCCESS() {
        return SUCCESS;
    }

    @JsonProperty("SUCCESS")
    public void setSUCCESS(Boolean sUCCESS) {
        this.SUCCESS = sUCCESS;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("firstname")
    public String getFirstname() {
        return firstname;
    }

    @JsonProperty("firstname")
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    @JsonProperty("lastname")
    public String getLastname() {
        return lastname;
    }

    @JsonProperty("lastname")
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("mobilenumber")
    public String getMobilenumber() {
        return mobilenumber;
    }

    @JsonProperty("mobilenumber")
    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    @JsonProperty("role")
    public String getRole() {
        return role;
    }

    @JsonProperty("role")
    public void setRole(String role) {
        this.role = role;
    }

}