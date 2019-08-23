package com.fujitsu.transport.transportapp.view;

import android.Manifest;
import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fujitsu.transport.transportapp.R;
import com.fujitsu.transport.transportapp.base.BaseActivity;
import com.fujitsu.transport.transportapp.base.viewmodel.ViewModelFactory;
import com.fujitsu.transport.transportapp.data.rest.RestRepository;
import com.fujitsu.transport.transportapp.model.Rout;
import com.fujitsu.transport.transportapp.model.Session;
import com.fujitsu.transport.transportapp.model.Waypoint;
import com.fujitsu.transport.transportapp.util.CommonUtils;
import com.fujitsu.transport.transportapp.util.Constants;
import com.fujitsu.transport.transportapp.util.DirectionsJSONParser;
import com.fujitsu.transport.transportapp.util.LocationMangement;
import com.fujitsu.transport.transportapp.util.MyJobScheduler;
import com.fujitsu.transport.transportapp.util.NetworkUtils;
import com.fujitsu.transport.transportapp.viewmodel.MapsViewModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MapsActivity extends BaseActivity<MapsViewModel> implements OnMapReadyCallback {
    @BindView(R.id.rl_end_trip)
    RelativeLayout rl_end_trip;
    @BindView(R.id.tv_btn_text)
    TextView tv_btn_text;

    Rout rout;
    String userType = null;
    @BindView(R.id.tv_rout)
    TextView tvRout;
    @BindView(R.id.iv_back)
    ImageView iv_back;
    @BindView(R.id.pb_maps)
    ProgressBar pb_maps;
    @BindView(R.id.rl_toolbar)
    RelativeLayout rlToolbar;
    @BindView(R.id.progress_circular)
    ProgressBar circular_pb;
    private GoogleMap mMap;
    LocationManager locationManager;
    LocationListener locationListener;
    LocationMangement locationMangement;
    Marker currentLocationMarker;
    Marker userLocation;
    ArrayList<LatLng> markerPoints;
    List<Waypoint> waypointList = new ArrayList<>();
    List<Rout> routList = new ArrayList<>();

    private JobScheduler jobScheduler;
    private JobInfo jobInfo;
    private final int jobid = 101;

    @Inject
    Session session;
    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected int layoutRes() {
        return R.layout.activity_maps;
    }

    @Override
    protected Class<MapsViewModel> viewModelType() {
        return MapsViewModel.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        Log.i("MapSActivity","onCreate called");
        locationMangement = new LocationMangement(getApplicationContext());
        rout = (Rout) getIntent().getSerializableExtra("RoutesObject");
        userType = getIntent().getStringExtra("Role");

        if (rout == null || userType.isEmpty()) {
            this.finish();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.frg_map);
        mapFragment.getMapAsync(this);

        tvRout.setText(rout.getRoute_number());
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               MapsActivity.this.onBackPressed();
            }
        });

        if (userType.equals(Constants.USER_TYPE_USER)) {
            //update driver location at some intervals

            rl_end_trip.setVisibility(View.GONE);

            if (routList.size() < 0)
                routList.clear();

            final Handler handler = new Handler();
            Runnable runnable = new Runnable() {

                @Override
                public void run() {
                    try {
                        if (NetworkUtils.isNetworkAvailable(getApplicationContext()))
                            viewModel.getLocation(rout.getId()).observe(MapsActivity.this, mutableLiveData -> {
                                if (mutableLiveData != null) {
                                    routList = mutableLiveData.getBody();
                                    routList.add(rout);

                                    currentMap();
                                } else {
                                    CommonUtils.showToast(getApplicationContext(), getString(R.string.something_wrong));
                                }
                            });
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        handler.postDelayed(this, 3000);
                    }
                }
            };
            handler.post(runnable);

        } else if (userType.equals(Constants.USER_TYPE_DRIVER)) {
            //upadte device's location at intervals
            rl_end_trip.setVisibility(View.VISIBLE);

            rl_end_trip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                    builder.setTitle("End trip ?");
                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            clearjob();
                        }
                    }) .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();

                }
            });

            ComponentName componentName = new ComponentName(getApplicationContext(), MyJobScheduler.class);

//            JobScheduler jobScheduler =
//                    (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
//            jobScheduler.schedule(new JobInfo.Builder(jobid,
//                    new ComponentName(this, MyJobScheduler.class))
//                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                    .build());


            JobInfo.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder = new JobInfo.Builder(jobid, componentName)
                        .setMinimumLatency(2 * 1000);
            } else {
                builder = new JobInfo.Builder(jobid, componentName)
                        .setPeriodic(2 * 1000);
            }
            builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
            builder.setPersisted(true);
            PersistableBundle data = new PersistableBundle();

            Constants.ROUTE_ID = rout.getId();
            data.putString("curr_lattitude", rout.getCurrent_latitude());
            data.putString("current_longitude", rout.getCurrent_longitude());
            data.putString("start_latitude", rout.getStart_latitude());
            data.putString("start_longitude", rout.getStart_longitude());
            data.putString("routeid", rout.getId());
            data.putString("end_latitude", rout.getEnd_latitude());
            data.putString("end_longitude", rout.getEnd_longitude());
            data.putString("driverid", session.getUserId());
            data.putString("isactive", "1");
            builder.setExtras(data);
            jobInfo = builder.build();
            jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
            schedulejob();

            startTrip();
            checkForGPS();
        }
    }

    @Override
    public void onBackPressed() {
        if (userType.equals(Constants.USER_TYPE_DRIVER)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("You cannot go back before ending this trip.");
            builder.setMessage("Do you want to end this trip ?");
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  clearjob();
                }
            }) .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.show();
        }
        else
            super.onBackPressed();
    }

    private void currentMap() {

        routeMap();
        if (!routList.get(0).getCurrent_latitude().isEmpty() && !routList.get(0).getCurrent_longitude().isEmpty()) {
            LatLng point = new LatLng(Double.parseDouble(routList.get(0).getCurrent_latitude()), Double.parseDouble(routList.get(0).getCurrent_longitude()));
            Log.i("buscurrlatandlon", routList.get(0).getCurrent_latitude() + routList.get(0).getCurrent_longitude());

            if (userLocation != null)
                userLocation.remove();

            userLocation = mMap.addMarker(new MarkerOptions().position(point).title("Bus Current location").icon(BitmapDescriptorFactory.fromResource(R.drawable.carplaceholder)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
        }

    }


    private void checkForGPS(){
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.gps_not_found_title)  // GPS not found
                    .setMessage(R.string.enable_message) // Want to enable?
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .show();
        }
    }

    private void schedulejob() {
        jobScheduler.schedule(jobInfo);

    }

    private void clearjob() {
        showEndingTrip(true);
        jobScheduler.cancelAll();
        viewModel.endTrip(rout.getId(), "0", session.getUserId()).observe(this, mutableLiveData -> {
            if (mutableLiveData != null && mutableLiveData.getBody() !=null && mutableLiveData.getBody().getSUCCESS()) {
                showEndingTrip(false);
                Toast.makeText(getApplicationContext(), "Trip ended", Toast.LENGTH_SHORT).show();
                super.onBackPressed();
            } else {
                showEndingTrip(false);
                Toast.makeText(getApplicationContext(), "some problem occoured while trying to end the trip", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startTrip(){
        viewModel.endTrip(rout.getId(), "1", session.getUserId()).observe(this, mutableLiveData -> {
            if (mutableLiveData != null && mutableLiveData.getBody() !=null && mutableLiveData.getBody().getSUCCESS()) {
//                showEndingTrip(false);
                Toast.makeText(getApplicationContext(), "Trip started", Toast.LENGTH_SHORT).show();
//                super.onBackPressed();
            } else {
//                showEndingTrip(false);
                Toast.makeText(getApplicationContext(), "some problem occoured while trying to start the trip", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEndingTrip(boolean show){
        if(show){
            pb_maps.setVisibility(View.VISIBLE);
            tv_btn_text.setText("Ending trip...");
            rl_end_trip.setClickable(false);
        }
        else {
            pb_maps.setVisibility(View.GONE);
            tv_btn_text.setText("END TRIP");
            rl_end_trip.setClickable(true);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //  googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        mMap = googleMap;
        routeMap();
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng current_location = new LatLng(location.getLatitude(), location.getLongitude());

                if (locationMangement.isDataSaved()) {
                    locationMangement.clearLocation();
                }
                if (currentLocationMarker != null) {
                    currentLocationMarker.remove();

                }
                locationMangement.SaveLocation(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
                currentLocationMarker = mMap.addMarker(new MarkerOptions().position(current_location).title("Bus Current location").icon(BitmapDescriptorFactory.fromResource(R.drawable.carplaceholder)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(current_location));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
                circular_pb.setVisibility(View.GONE);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };



        if (userType.equals(Constants.USER_TYPE_DRIVER)) {
            if (Build.VERSION.SDK_INT < 23) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            } else {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);

                } else {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                    routeMap();
                    Location lastknownlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastknownlocation != null) {
                        if (currentLocationMarker != null) {
                            currentLocationMarker.remove();
                        }

                        currentLocationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(lastknownlocation.getLatitude(), lastknownlocation.getLongitude())).title("Bus Current location").icon(BitmapDescriptorFactory.fromResource(R.drawable.carplaceholder)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lastknownlocation.getLatitude(), lastknownlocation.getLongitude())));
                        ///****

                    }
                }
            }
        }
        else {
            if (Build.VERSION.SDK_INT < 23) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            } else {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 2);

                } else {


                }
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 2){
            startActivity(getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    private void routeMap() {
        markerPoints = new ArrayList<LatLng>();
        if (markerPoints.size() > 10) {
            markerPoints.clear();
        }
        LatLng start = new LatLng(Double.parseDouble(rout.getStart_latitude()), Double.parseDouble(rout.getStart_longitude()));
        LatLng end = new LatLng(Double.parseDouble(rout.getEnd_latitude()), Double.parseDouble(rout.getEnd_longitude()));

        markerPoints.add(start);
        markerPoints.add(end);

        MarkerOptions options = new MarkerOptions();
        MarkerOptions options1 = new MarkerOptions();

        options.position(start);
        options1.position(end);

        List<MarkerOptions> markerOptions = new ArrayList<>();
        markerOptions.add(options);
        markerOptions.add(options1);


        for (Waypoint object : waypointList) {
            LatLng waypoint = new LatLng(Double.parseDouble(object.getLatitude()), Double.parseDouble(object.getLongitude()));
            markerPoints.add(waypoint);
            MarkerOptions opt = new MarkerOptions();
            opt.position(waypoint);
            markerOptions.add(opt);

        }


        /**
         * For the start location, the color of marker is GREEN and
         * for the end location, the color of marker is RED.
         */


        for (int i = 0; i < markerOptions.size(); i++) {

            MarkerOptions m = markerOptions.get(i);
            if (i == 0) {
                m.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).title("Start point");
            } else if (i == 1) {
                m.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).title("End point");
            } else {
                m.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title("Waypoints");

            }
            mMap.addMarker(m);
            mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
        }

        // Add new marker to the Google Map Android API V2
        // Checks, whether start and end locations are captured
        if (markerPoints.size() >= 2) {
            LatLng origin = markerPoints.get(0);
            LatLng dest = markerPoints.get(1);

            // Getting URL to the Google Directions API
            String url = getDirectionsUrl(origin, dest);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Waypoints
        String waypoints = "";
        String apiKey = getString(R.string.google_maps_key);
        for (int i = 2; i < markerPoints.size(); i++) {
            LatLng point = (LatLng) markerPoints.get(i);
            if (i == 2)
                waypoints = "waypoints=";
            waypoints += point.latitude + "," + point.longitude + "|";
        }

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&key=" + apiKey;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        Log.i("url", url);

        return url;
    }

    private void updateMap(Double lat, Double lng) {

    }

    private void createRouteOnMap(Double lat, Double lng) {

    }

    private void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain,
                                               String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(
                    context.getSocketFactory());
        } catch (Exception e) { // should never happen
            e.printStackTrace();
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        trustEveryone();
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            // Log.d("Exception while downloading url", e.toString());
            e.printStackTrace();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    //--------------------ASYNC WORK-----------------------------

    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            if (result != null) {
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(8);
                    lineOptions.color(Color.MAGENTA);
                }

                // Drawing polyline in the Google Map for the i-th route
                if (lineOptions != null) {
                    mMap.addPolyline(lineOptions);
                }
                // mMap.animateCamera( CameraUpdateFactory.zoomTo( 16.0f ) );
            }
        }
    }
}
