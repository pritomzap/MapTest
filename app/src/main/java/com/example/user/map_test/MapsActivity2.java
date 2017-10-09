package com.example.user.map_test;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity2 extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;
    public LocationManager locationManager;
    private LatLng destLoc = null;
    public LatLng myloc = null;
    public int setmap = 0;
    public static Button btn_go;
    public static Button btnDB;
    public DataBaseHelper dbh;
    //private DatabaseReference mDatabase;
    private FireBaseHelper fireBaseHelper;
    private Button firebase;
    //public ArrayList<String> retrivedata = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps2);
        dbh = new DataBaseHelper(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                2000, 1, this);
        double l1 = Double.parseDouble(getIntent().getExtras().getString("latitude"));
        double l2 = Double.parseDouble(getIntent().getExtras().getString("longitude"));
        myloc = new LatLng(l1,l2);

        Toast.makeText(getBaseContext(), "My Location: Latitude" + getIntent().getExtras().getString("latitude") + " Longitude" + getIntent().getExtras().getString("longitude"), Toast.LENGTH_LONG).show();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = myloc;
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);
                mMap.addMarker(options);
                destLoc = latLng;
                setmap = 1;
            }
        });
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
        MarkerOptions options = new MarkerOptions();

        // Setting the position of the marker
        options.position(myloc);
        mMap.addMarker(options);
        mMap.setMyLocationEnabled(true);

        mMap.setTrafficEnabled(true);
        onbuttonGO();
        viewAll();
        firebasebtn();


    }

    public void onbuttonGO(){
        btn_go = (Button)findViewById(R.id.button_go);
        btn_go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //LatLng origin = myloc;
                //LatLng dest = destLoc;
                //HashMap<String,String> values = null;
                // Getting URL to the Google Directions API
                String url = getDirectionsUrl(myloc,destLoc);

                DownloadTask downloadTask = new DownloadTask();

                // Start downloading json data from Google Directions API
                downloadTask.execute(url);

                DownParse dp = new DownParse(getBaseContext(),myloc,destLoc,dbh);
                dp.execute(url);

                //values = dp.returnvalues();
               // Toast.makeText(getBaseContext(), values.get("duration") +"  "+values.get("distance"), Toast.LENGTH_LONG).show();

            }
        });
    }

    public void firebasebtn(){
        firebase = (Button)findViewById(R.id.firebase);
        //retrivedata = new ArrayList<>();
        firebase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //mDatabase = FirebaseDatabase.getInstance().getReference();
                //mDatabase.child("Lat").setValue("INSERT");
                fireBaseHelper = new FireBaseHelper(getBaseContext(),mMap);
                fireBaseHelper.insertTOFireBase(String.valueOf(myloc.latitude),String.valueOf(myloc.longitude));
                fireBaseHelper.retriveData();

            }
        });
    }
    public void viewAll() {
        btnDB = (Button)findViewById(R.id.button_db);
        btnDB.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Cursor res = dbh.getAllData();
                        if(res.getCount() == 0) {
                            // show message
                            Toast.makeText(getBaseContext(), "Nothing was Found", Toast.LENGTH_LONG).show();
                            return;
                        }

                        StringBuffer buffer = new StringBuffer();
                        while (res.moveToNext()) {
                            buffer.append("Trip_ID :"+ res.getString(0)+"\n");
                            buffer.append("Source_Lat :"+ res.getString(1)+"\n");
                            buffer.append("Source_Long :"+ res.getString(2)+"\n");
                            buffer.append("Destination_Lat :"+ res.getString(3)+"\n\n");
                            buffer.append("Destination_Long :"+ res.getString(4)+"\n");
                            buffer.append("Duration :"+ res.getString(5)+"\n");
                            buffer.append("Distence :"+ res.getString(6)+"\n");

                        }

                        // Show all data
                        showMessage("Data",buffer.toString());
                    }
                }
        );
    }

    public void showMessage(String title,String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }


    @Override
    public void onLocationChanged(Location location) {
        if (setmap == 0) {
            destLoc = new LatLng(location.getLatitude(), location.getLongitude());
        }
        else
            destLoc = destLoc;
        MarkerOptions m = new MarkerOptions();
        m.position(destLoc);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(destLoc));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        String msg = "New Latitude: " + location.getLatitude()
                + "New Longitude: " + location.getLongitude();

        Toast.makeText(getBaseContext(), "Destination :"+msg, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getBaseContext(), "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
        Toast.makeText(getBaseContext(), "Gps is turned off!! ",
                Toast.LENGTH_SHORT).show();
    }


    //copy start

    public void onMapSearch(View view) throws IOException {
        if(myloc != null){
            //mMap.clear();
            EditText locationSearch = (EditText) findViewById(R.id.editText);
            String location = locationSearch.getText().toString();
            List<Address> addressList = null;

            if (location != null || !location.equals("")) {
                Geocoder geocoder = new Geocoder(this);
                try {
                    addressList = geocoder.getFromLocationName(location, 1);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = addressList.get(0);
                LatLng temp = new LatLng(address.getLatitude(),address.getLongitude());
                //destLoc = new LatLng(address.getLatitude(), address.getLongitude());
                //mMap.addMarker(new MarkerOptions().position(destLoc).title("Marker"));
                mMap.animateCamera(CameraUpdateFactory.newLatLng(temp));

            }
        }
        else{
            Toast.makeText(getBaseContext(), "Get GPS Location First",
                    Toast.LENGTH_SHORT).show();
        }

    }


    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


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

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.GREEN);
                lineOptions.geodesic(true);

            }

// Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=driving";
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

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
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    //copy end


}
