package com.example.user.map_test;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

/**
 * Created by user on 8/1/2017.
 */

public class DownParse extends AsyncTask<String, Void, String> {

    //@Override
    Context context;
    LatLng s,d;
    DataBaseHelper dh;
    public DownParse(Context c,LatLng so,LatLng des,DataBaseHelper db){
        context = c;
        s = so;
        d = des;
        dh = db;
    }
    protected String doInBackground(String... url) {
        String data = "";

        try {
            data = downloadUrl(url[0]);
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        ParserTask2 parserTask = new ParserTask2(context,s,d,dh);


        parserTask.execute(result);

    }

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

    private class ParserTask2 extends AsyncTask<String,String,HashMap<String,String>>{
        Context mContext;
        LatLng s,d;
        DataBaseHelper dh;
        //FireBaseHelper fb;
        public ParserTask2(Context c,LatLng so,LatLng des,DataBaseHelper db){
            mContext = c;
            s = so;
            d = des;
            dh = db;
        }

        @Override
        protected HashMap<String,String> doInBackground(String... jsonData) {
            DirectionsJSONParser dir = new DirectionsJSONParser();
            HashMap<String,String> data = null;
            data = dir.parseDistence(jsonData[0]);
            return data;
        }

        @Override
        protected void onPostExecute(HashMap<String,String> list){
            //list.get("duration");
            String lat = String.valueOf(s.latitude);
            boolean res;
            Toast.makeText(mContext, "Duration :"+list.get("duration")+"  Distence:"+list.get("distance"), Toast.LENGTH_LONG).show();
            res = dh.insertData(String.valueOf(s.latitude),String.valueOf(s.longitude),String.valueOf(d.latitude),String.valueOf(d.longitude),list.get("duration"),list.get("distance"));
            //fb = new FireBaseHelper();
            //fb.insertTOFireBase(String.valueOf(s.latitude),String.valueOf(s.longitude));
            if(res == true)
                Toast.makeText(mContext,"DATA INSERTED",Toast.LENGTH_LONG).show();
            else
                Toast.makeText(mContext,"DATA IS NOT INSERTED",Toast.LENGTH_LONG).show();
        }



    }
}
