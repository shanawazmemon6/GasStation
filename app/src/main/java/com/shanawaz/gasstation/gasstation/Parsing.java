package com.shanawaz.gasstation.gasstation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Parsing {

    private HashMap<String,String> getPlace(JSONObject googelPlaceJson){
        HashMap<String,String> googlePlaceMap=new HashMap<>();
        String placeName="-NA-";
        String vicinity="-NA-";
        String latitude="";
        String longitude="";
        String reference="";
        try {
        if(!googelPlaceJson.isNull("name")){

                placeName=googelPlaceJson.getString("name");

        }
            if(!googelPlaceJson.isNull("vicinity")){

                vicinity=googelPlaceJson.getString("vicinity");

            }
            latitude=googelPlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude=googelPlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");
            reference=googelPlaceJson.getString("reference");
            googlePlaceMap.put("placename",placeName);
            googlePlaceMap.put("vicinity",vicinity);
            googlePlaceMap.put("latitude",latitude);
            googlePlaceMap.put("longitude",longitude);
            googlePlaceMap.put("reference",reference);





        } catch (JSONException e) {
            e.printStackTrace();
        }
        return googlePlaceMap;

    }


    private List<HashMap<String,String>> getPlaces(JSONArray jsonArray){
        int count=jsonArray.length();

        List<HashMap<String,String>> gasStationList=new ArrayList<>();
        HashMap<String,String> placeMap;

        for (int i=0;i<count;i++){
            try {
                placeMap=getPlace((JSONObject) jsonArray.get(i));
                gasStationList.add(placeMap);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

return gasStationList;
    }

public List<HashMap<String,String>> parse(String jsonData)
{
    JSONArray jsonArray=null;
    JSONObject jsonObject;
    try {
        jsonObject=new JSONObject(jsonData);
        jsonArray=jsonObject.getJSONArray("results");
    } catch (JSONException e) {
        e.printStackTrace();
    }
     return getPlaces(jsonArray);
}
    public HashMap<String,String> parseDuration(String jsonData)
    {
        JSONArray jsonArray=null;
        JSONObject jsonObject;
        try {
            jsonObject=new JSONObject(jsonData);
            jsonArray=jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getDuration(jsonArray);
    }

    public String[] parseDirection(String jsonData)
    {
        JSONArray jsonArray=null;
        JSONObject jsonObject;
        try {
            jsonObject=new JSONObject(jsonData);
            jsonArray=jsonObject.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return getPaths(jsonArray);
    }


    public String[] getPaths(JSONArray googleStepJson){
        int count=googleStepJson.length();
        String[] ploylines =new String[count];
        for (int i=0;i<count;i++){
            try {
                ploylines[i]=getPath(googleStepJson.getJSONObject(i));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return ploylines;
    }


    public String getPath(JSONObject googlePathJson)
    {
        String polyline="";
        try {
             polyline=googlePathJson.getJSONObject("polyline").getString("points");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return polyline;
    }


    private HashMap<String,String> getDuration(JSONArray jsonArray) {
        HashMap<String,String> googleDirectionMap=new HashMap<>();
        String duration="";
        String distance="";
        try {
            duration=jsonArray.getJSONObject(0).getJSONObject("duration").getString("text");
            distance=jsonArray.getJSONObject(0).getJSONObject("distance").getString("text");
            googleDirectionMap.put("duration",duration);
            googleDirectionMap.put("distance",distance);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return googleDirectionMap;

    }


}
