package com.shanawaz.gasstation.gasstation;


import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class GetDirectionData extends AsyncTask<Object,String,String> {
    GoogleMap map;
    String url;
    String googleDirectionData;

    LatLng latLng;
    String duration;
    @Override
    protected String doInBackground(Object... objects) {

        map= (GoogleMap) objects[0];
        url= (String) objects[1];
        latLng= (LatLng) objects[2];
        UrlConnection urlConnection=new UrlConnection();
        try {
            googleDirectionData=urlConnection.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return googleDirectionData;


    }

    @Override
    protected void onPostExecute(String s) {
        HashMap<String,String> durationList=null;

        String[] directionList;
        Parsing parsing=new Parsing();
       directionList=parsing.parseDirection(s);
         durationList=parsing.parseDuration(s);
        duration=durationList.get("duration");
        displayDirection(directionList);


    }

    private void displayDirection(String[] directionList) {
        int count=directionList.length;
        for (int i=0;i<count;i++){
            PolylineOptions options=new PolylineOptions();
            options.color(Color.RED);

            options.width(10);
            options.addAll(PolyUtil.decode(directionList[i]));
            map.addPolyline(options);
            MarkerOptions markerOptions=new MarkerOptions();

            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            markerOptions.title(""+duration);
            map.addMarker(markerOptions);
        }
    }


}
