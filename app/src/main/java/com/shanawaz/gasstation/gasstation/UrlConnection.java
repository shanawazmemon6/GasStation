package com.shanawaz.gasstation.gasstation;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlConnection {

    public String readUrl(String url) throws IOException {

        String data="";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            URL url_local=new URL(url);
             urlConnection= (HttpURLConnection) url_local.openConnection();
            urlConnection.connect();

             inputStream=urlConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer=new StringBuffer();
            String line="";
            while ((line=bufferedReader.readLine())!=null){
                stringBuffer.append(line);

            }
            data=stringBuffer.toString();
            bufferedReader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
finally {
            inputStream.close();
            urlConnection.disconnect();

        }

        return data;



    }


}
