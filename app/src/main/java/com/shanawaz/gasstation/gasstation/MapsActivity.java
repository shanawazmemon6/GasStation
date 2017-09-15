package com.shanawaz.gasstation.gasstation;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.LocaleList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int PROIMITY_RADIUS = 10000;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Marker currentMarker;
    private Location last_location;
    public static final int REQUEST_LOCATION_CODE=99;
    List<Address> locatAddresses=new ArrayList<>();
    List<HashMap<String,String>> listOfGas;
    HashMap<String,String> googlePlace;

    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;

    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    Location loc = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            checkLocationPremission();

        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       switch (requestCode){
           case REQUEST_LOCATION_CODE:
               if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                   if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                       if(googleApiClient==null){
                           buildGoogleAppClient();
                       }
                       mMap.setMyLocationEnabled(true);
                   }
                   else {
                       Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();

                   }

           }

       }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            buildGoogleAppClient();
            mMap.setMyLocationEnabled(true);
            LocationManager  mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
          loc=mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            zoomIn(loc.getLatitude(),loc.getLongitude());

        }
          final MarkerOptions markerOptions=new MarkerOptions();
        Button search=(Button)findViewById(R.id.search);
        final EditText location_search=(EditText)findViewById(R.id.location) ;
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String location=location_search.getText().toString();

                if(!location_search.equals("")){

                    if(currentMarker!=null){
                        currentMarker.remove();
                    }
                    mMap.clear();
                    Geocoder geocoder=new Geocoder(getApplicationContext());
                    try {
                        locatAddresses=geocoder.getFromLocationName(location,5);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                  for (int i=0;i<locatAddresses.size();i++){
                      Address myaddress=locatAddresses.get(i);
                      LatLng latLng=new LatLng(myaddress.getLatitude(),myaddress.getLongitude());
                      String gasStation="gas_station";
                      String url=getUrl(myaddress.getLatitude(),myaddress.getLongitude(),gasStation);
                      Object dataTransfer[]=new Object[2];
                      dataTransfer[0]=mMap;
                      dataTransfer[1]=url;
                      final NearByGasStation nearByGasStation=new NearByGasStation();
                      nearByGasStation.execute(dataTransfer);

                       markerOptions.position(latLng);
                    currentMarker=  mMap.addMarker(markerOptions);
                      mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                  }
                }



            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                mMap.clear();
                String gasStation="gas_station";
                String url=getUrl(loc.getLatitude(),loc.getLongitude(),gasStation);
                Object dataTransfer[]=new Object[2];
                dataTransfer[0]=mMap;
                dataTransfer[1]=url;
                final NearByGasStation nearByGasStation=new NearByGasStation();
                nearByGasStation.execute(dataTransfer);

                LatLng lati=new LatLng(loc.getLatitude(),loc.getLongitude());

                mMap.moveCamera(CameraUpdateFactory.newLatLng(lati));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(12));
                return true;
            }
        });
        String gasStation="gas_station";
        String url=getUrl(loc.getLatitude(),loc.getLongitude(),gasStation);
        Object dataTransfer[]=new Object[2];
        dataTransfer[0]=mMap;
        dataTransfer[1]=url;
        final NearByGasStation nearByGasStation=new NearByGasStation();
        nearByGasStation.execute(dataTransfer);

         Button navigate=(Button)findViewById(R.id.navigate);
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(MapsActivity.this);
                builderSingle.setTitle("Navigate");
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MapsActivity.this, android.R.layout.select_dialog_singlechoice);
              listOfGas= nearByGasStation.getGetListOfGas();
                for(int i=0;i<listOfGas.size();i++){
                    googlePlace=listOfGas.get(i);
                    double lat=Double.valueOf(googlePlace.get("latitude"));
                    double lng=Double.valueOf(googlePlace.get("longitude"));
                    float results[]=new float[10];
                       Location.distanceBetween(loc.getLatitude(),loc.getLongitude(),lat,lng,results);
                    DecimalFormat df = new DecimalFormat("###.##");

                    arrayAdapter.add(googlePlace.get("placename")+"  "+df.format(results[0]/1000)+"kms");
                }
                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        googlePlace=listOfGas.get(which);


                        AlertDialog.Builder builderInner = new AlertDialog.Builder(MapsActivity.this);
                        builderInner.setMessage(strName);
                        builderInner.setTitle("Your Selected Item is");
                        builderInner.setPositiveButton("Track", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int which) {
                                StringBuilder googleDirectionUrl=new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");

                                googleDirectionUrl.append("origin="+loc.getLatitude()+","+loc.getLongitude());

                                googleDirectionUrl.append("&destination="+googlePlace.get("latitude")+","+googlePlace.get("longitude"));
                                googleDirectionUrl.append("&key="+"AIzaSyCK6JSWVtKj1NTy2bfBAtyRHEk1Adnwlt4");
                                mMap.clear();

                                LatLng latl= new LatLng(Double.valueOf(googlePlace.get("latitude")),Double.valueOf(googlePlace.get("longitude")));

                                String url_direction=googleDirectionUrl.toString();
                                Object dataTransfer[]=new Object[3];
                                dataTransfer[0]=mMap;
                                dataTransfer[1]=url_direction;
                                dataTransfer[2]=latl;
                                GetDirectionData getDirectionData=new GetDirectionData();
                                getDirectionData.execute(dataTransfer);


                                dialog.dismiss();
                            }
                        });
                        builderInner.show();
                    }
                });
                builderSingle.show();

                }
        });


    }




    private String getUrl(double latitude, double longitude, String gasStation) {
       StringBuilder googlePlaceUrl=new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlaceUrl.append("location="+latitude+","+longitude);
        googlePlaceUrl.append("&radius="+PROIMITY_RADIUS);
        googlePlaceUrl.append("&type="+gasStation);
        googlePlaceUrl.append("&sensor=true");
        googlePlaceUrl.append("&key="+"AIzaSyAcEFO7VJmyykB0sBiKdA_yzbK3_-hKwqk");
        return googlePlaceUrl.toString();


    }

    protected synchronized void buildGoogleAppClient() {
        googleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
        googleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);

        }

    }

    @Override
    public void onConnectionSuspended(int i) {


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mMap.clear();
        last_location=location;

        if(currentMarker!=null){
            currentMarker.remove();
        }

        LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
        MarkerOptions markerOptions=new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentMarker=mMap.addMarker(markerOptions);
       zoomIn(location.getLatitude(),location.getLongitude());



        if (googleApiClient!=null){

            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
        }




    }

    public  boolean checkLocationPremission(){

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){

            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);

            }
            else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION_CODE);

            }
            return false;
        }
        else {
            return true;
        }



    }
    public void zoomIn(double Lat, double Long) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(Lat,
                Long));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(12);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

    }


}
