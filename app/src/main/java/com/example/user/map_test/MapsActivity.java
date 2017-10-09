package com.example.user.map_test;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,LocationListener{

    private GoogleMap mMap;
    private LatLng myloc = null,destLoc = null;
    //ArrayList markerPoints= new ArrayList();
    public LocationManager locationManager;
    private static Button btn_next;
    private int setmap = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
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


    }


    public void OnClickButtonListener(){
        btn_next = (Button)findViewById(R.id.button3);

        btn_next.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double l1 = myloc.latitude;
                        Double l2 = myloc.longitude;
                        Intent intent = new Intent("com.example.user.map_test.MapsActivity2");
                        Bundle extra = new Bundle();
                        extra.putString("latitude",l1.toString());
                        extra.putString("longitude",l2.toString());
                        intent.putExtras(extra);
                        startActivity(intent);
                        MapsActivity.this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }
        );
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
        String ms = "Map is ready";
        Toast.makeText(getBaseContext(), ms, Toast.LENGTH_LONG).show();
        // Add a marker in Sydney and move the camera
        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        LatLng Canberra = new LatLng(-35.277249, 149.133542);
        mMap.addMarker(new MarkerOptions()
                .position(Canberra)
                .title("Marker in Canberra Central"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Canberra));

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    mMap.clear();
                    MarkerOptions options = new MarkerOptions();

                    // Setting the position of the marker
                    options.position(latLng);
                    mMap.addMarker(options);
                    myloc = latLng;
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
        mMap.setMyLocationEnabled(true);
        mMap.setTrafficEnabled(true);
        OnClickButtonListener();

    }

    @Override
    public void onLocationChanged(Location location) {
        if(setmap == 0) {
            myloc = new LatLng(location.getLatitude(), location.getLongitude());
            MarkerOptions m = new MarkerOptions();
            m.position(myloc);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myloc));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            String msg = "New Latitude: " + location.getLatitude()
                    + "New Longitude: " + location.getLongitude();

            Toast.makeText(getBaseContext(), "My Location" + msg, Toast.LENGTH_LONG).show();
        }
        else {
            myloc = myloc;
            MarkerOptions m = new MarkerOptions();
            m.position(myloc);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(myloc));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            String msg = "New Latitude: " + location.getLatitude()
                    + "New Longitude: " + location.getLongitude();

            Toast.makeText(getBaseContext(), "My Location" + msg, Toast.LENGTH_LONG).show();
        }
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

}
