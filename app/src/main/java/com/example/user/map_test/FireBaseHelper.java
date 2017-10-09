package com.example.user.map_test;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by user on 8/8/2017.
 */

public class FireBaseHelper {
    private DatabaseReference mDatabase;
    GoogleMap mMap;
    Context c;

    public FireBaseHelper(Context context,GoogleMap map) {
        this.mDatabase = mDatabase;
        c = context;
        mMap = map;
    }

    public void insertTOFireBase(String lat, String lang){
        mDatabase = FirebaseDatabase.getInstance().getReference().child("User_02");
        mDatabase.child("Lat").setValue(lat);
        mDatabase.child("Long").setValue(lang);
    }

    public void retriveData(){

        mDatabase = FirebaseDatabase.getInstance().getReference().child("User_01");

        mDatabase.addValueEventListener(new ValueEventListener() {
            String lat,lang;
            LatLng user_02;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lat = dataSnapshot.child("Lat").getValue().toString();
                lang = dataSnapshot.child("Long").getValue().toString();
                user_02 = new LatLng(Double.parseDouble(lat),Double.parseDouble(lang));
                MarkerOptions options = new MarkerOptions();

                options.position(user_02).title("User 01")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                mMap.addMarker(options);
                //mMap.setMyLocationEnabled(true);

                //Toast.makeText(c, "Retrived Data: "+lat, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });


    }

}
