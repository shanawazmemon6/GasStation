package com.shanawaz.gasstation.gasstation;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class NearByGasStation extends AsyncTask<Object,String,String> {

    String gasStationData;
    GoogleMap googleMap;
    String url;
    List<HashMap<String,String>> getListOfGas;


    @Override
    protected String doInBackground(Object... objects) {
        googleMap= (GoogleMap) objects[0];
        url= (String) objects[1];
        UrlConnection urlConnection=new UrlConnection();
        try {
            gasStationData=urlConnection.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return gasStationData;
    }

    @Override
    protected void onPostExecute(String s) {
        Log.d("url",""+s);

        List<HashMap<String,String>> nearByGasStationList=null;
        Parsing parsing=new Parsing();
        nearByGasStationList=parsing.parse(s);

        showNearByGasStation(nearByGasStationList);
        setGetListOfGas(nearByGasStationList);

    }

    private void showNearByGasStation(List<HashMap<String,String>>nearByGasStation){
        for(int i=0;i<nearByGasStation.size();i++){
            MarkerOptions markerOptions=new MarkerOptions();
            HashMap<String,String> googlePlace=nearByGasStation.get(i);
            String placeName=googlePlace.get("placename");
            String vicinity=googlePlace.get("vicinity");

            double lat=Double.valueOf(googlePlace.get("latitude"));


        double lng=Double.valueOf(googlePlace.get("longitude"));
            LatLng latLng=new LatLng(lat,lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName+""+vicinity);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            googleMap.addMarker(markerOptions);





        }
    }

    public List<HashMap<String, String>> getGetListOfGas() {
        return getListOfGas;
    }

    public void setGetListOfGas(List<HashMap<String, String>> getListOfGas) {
        this.getListOfGas = getListOfGas;
    }
}
