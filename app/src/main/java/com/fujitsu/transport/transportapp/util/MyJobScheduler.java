package com.fujitsu.transport.transportapp.util;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import com.fujitsu.transport.transportapp.data.rest.RestService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class MyJobScheduler extends JobService {
    private int jobid;

    private String curr_lattitude;
    private String current_longitude;
    private String start_latitude;
    private String start_longitude;
    private String end_latitude;
    private String end_longitude;
    private String driverid;


    private final String TAG = "JOB_SCHEDULER";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 0;
    private static final float LOCATION_DISTANCE = 0;
    private static boolean isJobRunning = false;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
           // mylocation = new Location(LocationManager.NETWORK_PROVIDER);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
            //mylocation = new Location(LocationManager.NETWORK_PROVIDER);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }



    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.i("updateRoute","onStartJob");
        if(isJobRunning)
            return true;

        isJobRunning =true;

           start_latitude = params.getExtras().getString("start_latitude");
           start_longitude = params.getExtras().getString("start_longitude");
           end_latitude = params.getExtras().getString("end_latitude");
           end_longitude = params.getExtras().getString("end_longitude");
           driverid = params.getExtras().getString("driverid");
          // routeid = params.getExtras().getString("routeid");
        //routeid = Constants.ROUTE_ID;
        if( Constants.ROUTE_ID.equals("0")) {
            isJobRunning = false;
            return isJobRunning;
        }

           Log.i(TAG, "start_latitude : " + start_latitude);
         Log.i(TAG, "start_longitude : " + start_longitude);
        Log.i(TAG, "end_latitude : " + end_latitude);
        Log.i(TAG, "end_longitude : " + end_longitude);
        Log.i(TAG, "driverid : " + driverid);
        Log.i(TAG, "routeid : " +  Constants.ROUTE_ID);

           updateUserLocationToServer();

      /* if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
           scheduleRefresh();
       }else {
           sRefresh();
       }*/

        return isJobRunning;
    }

    private void updateUserLocationToServer(){
        if(!isJobRunning)
            return;
        if(NetworkUtils.isNetworkAvailable(getApplicationContext())) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).readTimeout(40, TimeUnit.SECONDS).build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                    .client(client)
                    .build();

            CompositeDisposable disposable = new CompositeDisposable();
            Log.i(TAG,"route id : " +  Constants.ROUTE_ID);
            disposable.add( retrofit.create(RestService.class).updateRoute(driverid,  Constants.ROUTE_ID, curr_lattitude, current_longitude, start_latitude, start_longitude, end_latitude, end_longitude, "1")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<Boolean>() {
                        @Override
                        public void onSuccess(Boolean value) {
                            Log.i("updateRoute", value.toString());
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                        }
                    }));
        }
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        isJobRunning =false;
        Constants.ROUTE_ID = "0";
        Log.i("updateRoute","onStopJobCalled");
      /*  JobScheduler mJobScheduler = (JobScheduler)getApplicationContext()
                .getSystemService(JOB_SCHEDULER_SERVICE);
        mJobScheduler.cancel(jobid);*/
        return false;
    }
//    private void scheduleRefresh() {
//        JobScheduler mJobScheduler = (JobScheduler)getApplicationContext()
//                .getSystemService(JOB_SCHEDULER_SERVICE);
//        JobInfo.Builder mJobBuilder =
//                new JobInfo.Builder(jobid,
//                        new ComponentName(getPackageName(), MyJobScheduler.class.getName()));
//
//  /* For Android N and Upper Versions */
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            mJobBuilder
//                    .setMinimumLatency(1000) //YOUR_TIME_INTERVAL
//                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
//        }
//
//
//        if (mJobScheduler != null && mJobScheduler.schedule(mJobBuilder.build())
//                <= JobScheduler.RESULT_FAILURE) {
//            //Scheduled Failed/LOG or run fail safe measures
//            Log.d(TAG, "Unable to schedule the service!");
//        }
//    }

//    private void sRefresh() {
//        JobScheduler mJobScheduler = (JobScheduler)getApplicationContext()
//                .getSystemService(JOB_SCHEDULER_SERVICE);
//        JobInfo.Builder mJobBuilder =
//                new JobInfo.Builder(jobid,
//                        new ComponentName(getPackageName(), MyJobScheduler.class.getName()));
//
//            mJobBuilder
//                    .setMinimumLatency(1000) //YOUR_TIME_INTERVAL
//                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
//
//
//
//        if (mJobScheduler != null && mJobScheduler.schedule(mJobBuilder.build())
//                <= JobScheduler.RESULT_FAILURE) {
//            //Scheduled Failed/LOG or run fail safe measures
//            Log.d(TAG, "Unable to schedule the service!");
//        }
//    }

    private class LocationListener implements android.location.LocationListener
    {

        Location mLastLocation;

        public LocationListener(String provider)
        {
            mLastLocation = new Location(provider);
            //mylocation = new Location(provider);

        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.i(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            //mylocation = location;
            curr_lattitude = String.valueOf(location.getLatitude());
            current_longitude = String.valueOf(location.getLongitude());

            updateUserLocationToServer();
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }

}
