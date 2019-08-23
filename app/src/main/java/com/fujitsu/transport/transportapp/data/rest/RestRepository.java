package com.fujitsu.transport.transportapp.data.rest;


import com.fujitsu.transport.transportapp.model.Rout;
import com.fujitsu.transport.transportapp.model.RoutResponse;
import com.fujitsu.transport.transportapp.model.StopTripResponse;
import com.fujitsu.transport.transportapp.model.TokenUpdateResponse;
import com.fujitsu.transport.transportapp.model.UserDetails;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Single;

public class RestRepository {

    private final RestService restService;

    @Inject
    public RestRepository(RestService restService) {
        this.restService = restService;
    }

    public Single<RoutResponse> getRoutes(){
        return restService.getRoutes();
    }

    public Single<RoutResponse> getActiveRoutes(){
        return restService.getActiveRoutes();
    }

    public Single<RoutResponse> getVehicleLocation(String routeid){
        return restService.getVehicleLocation(routeid);
    }

    public Single<UserDetails> login(String username, String password){
        return restService.login(username,password);
    }

    public Single<UserDetails> registerUser(String role,String number,String email, String lname, String fname, String pass, String uname){
        return restService.registerUser(role, number, email, lname, fname, pass, uname);
    }

    public Single<TokenUpdateResponse> updateToken(String userid, String routeid){
        return restService.updateToken(userid,routeid);
    }

    public Single<Boolean> updateRoute( String driverid,
                                        String routeid,
                                        String currlat,
                                        String curlon,
                                        String startlatitude,
                                        String startlongitude,
                                        String endlatitude,
                                        String endlongitude,
                                        String active){
        return restService.updateRoute(driverid, routeid, currlat, curlon, startlatitude, startlongitude, endlatitude, endlongitude, active);
    }

    public  Single<StopTripResponse> endTrip(String routeid, String isactive, String driverid){
        return restService.endTrip(routeid,isactive,driverid);
    }

    public Single<RoutResponse> get_active_route_for_driver (String driverid){
        return restService.get_active_route_for_driver(driverid);
    }
}
