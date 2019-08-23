package com.fujitsu.transport.transportapp.data.rest;

import com.fujitsu.transport.transportapp.model.Rout;
import com.fujitsu.transport.transportapp.model.RoutResponse;
import com.fujitsu.transport.transportapp.model.StopTripResponse;
import com.fujitsu.transport.transportapp.model.TokenUpdateResponse;
import com.fujitsu.transport.transportapp.model.UserDetails;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RestService {
    @GET("routs.php/routs")
    Single<RoutResponse> getRoutes();

    @GET("active_routs.php")
    Single<RoutResponse> getActiveRoutes();

    @GET("get_route.php")
    Single<RoutResponse> getVehicleLocation(@Query("routeid") String routeid);

    @GET("login.php")
    Single<UserDetails> login(@Query("username") String username,
                                     @Query("password") String password);

    @GET("register.php")
    Single<UserDetails> registerUser(@Query("role") String role,
                                     @Query("mobilenumber") String mobilenumber,
                                     @Query("email") String email,
                                     @Query("lastname") String lastname,
                                     @Query("firstname") String firstname,
                                     @Query("password") String password,
                                     @Query("username") String username);

    @GET("settokan.php")
    Single<TokenUpdateResponse> updateToken(@Query("userid") String userid, @Query("tokan") String routeid);

    @GET("update_route.php")
    Single<Boolean> updateRoute(@Query("driverid") String driverid,
                              @Query("routeid") String routeid,
                              @Query("currentlatitude") String currentlatitude,
                              @Query("currentlongitude") String currentlongitude,
                              @Query("startlatitude") String startlatitude,
                              @Query("startlongitude") String startlongitude,
                              @Query("endlatitude") String endlatitude,
                              @Query("endlongitude") String endlongitude,
                              @Query("isactive") String active);

    @GET("endtrip.php")
    Single<StopTripResponse> endTrip(@Query("routeid") String routeid, @Query("isactive") String isactive, @Query("driverid") String driverid);

    @GET("get_active_route_for_driver.php")
    Single<RoutResponse> get_active_route_for_driver (@Query("driverid") String driverid);


}
