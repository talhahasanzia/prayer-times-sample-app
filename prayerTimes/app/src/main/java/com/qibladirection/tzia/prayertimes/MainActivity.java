package com.qibladirection.tzia.prayertimes;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity implements LocationListener {


    double Latitude;
    double Longitude;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.text);
        textView.setMovementMethod(new ScrollingMovementMethod());

        LocationManager lm = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(MainActivity.this,"GPS Disabled, enable GPS and allow app to use the service.",Toast.LENGTH_LONG).show();
        }
        else {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }
        new dmad().execute("");







    }

    @Override
    public void onLocationChanged(Location location) {
        Latitude=location.getLatitude();
        Longitude=location.getLongitude();
        new dmad().execute("");
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


    class dmad extends AsyncTask<String,Void,String>
   {
       String p="None";

       @Override
       protected String doInBackground(String... params) {

           String finalData="";
           try {

               String lng=Longitude+"";
               String lat=Latitude+"";
               Calendar cal=new GregorianCalendar();
               Date date=cal.getTime();

               long time=date.getTime();



               String month=new SimpleDateFormat("M").format(time);
               String year=new SimpleDateFormat("y").format(time);

               Calendar mCalendar = new GregorianCalendar();
               TimeZone mTimeZone = mCalendar.getTimeZone();
               int mGMTOffset = mTimeZone.getRawOffset();
               long timeUnits=TimeUnit.HOURS.convert(mGMTOffset, TimeUnit.MILLISECONDS);
               String timeZone=timeUnits+"";
                p="year: "+year+" Month: "+month+" TimeZone: "+timeZone;

               Uri.Builder ub=new Uri.Builder();
               ub.scheme("http").authority("api.xhanch.com").appendPath("islamic-get-prayer-time.php")
                       .appendQueryParameter("lng",lng )
                       .appendQueryParameter("lat", lat)
                       .appendQueryParameter("yy",year)
                       .appendQueryParameter("mm",month)
                       .appendQueryParameter("gmt",timeZone)
                       .appendQueryParameter("m","json");

               URL requestUrl = new URL(ub.toString());  // set link
               HttpURLConnection connection = (HttpURLConnection)requestUrl.openConnection(); // create connection
               connection.setRequestMethod("GET");  // set HTTP Method type
               connection.connect();  // connect to server

               int responseCode = connection.getResponseCode(); // get response code from server

               if (responseCode == HttpURLConnection.HTTP_OK) { // check if server says "Ok! i will respond to your request"

                   finalData="";
                   BufferedReader reader = null; //Get buffer ready

                   InputStream inputStream = connection.getInputStream(); // get input stream from server ready

                   if (inputStream == null) { // if there is nothing in stream
                       return "";
                   }
                   reader = new BufferedReader(new InputStreamReader(inputStream));  // else pass stream data to buffer

                   String line;
                   while ((line = reader.readLine()) != null) {  // read each line

                       finalData+=line; // save them to string
                   }

                   if (finalData.length() == 0) { // check if string is empty
                       return "";
                   }


               }
               else {
                   Log.i("Unsuccessful", "Unsuccessful HTTP Response Code: " + responseCode);
               }
           } catch (MalformedURLException e) {
               Log.e("Wrong URL", e.getMessage().toString(), e);
           } catch (IOException e) {
               Log.e("Error", "Error connecting to API", e);
           }

           return finalData;


       }

       @Override
       protected void onPostExecute(String s) {
           super.onPostExecute(s);
           Log.d("Parameters",p);
            String FinalData="";
           try {
               JSONObject prayerData=new JSONObject(s);
               int count=prayerData.length();

               for(int i=0; i<count; i++)
               {

                   String day=i+1+"";
                   JSONObject data=prayerData.getJSONObject(day);
                   String Sunrise=data.getString("sunrise");
                   String Sunset=data.getString("maghrib");
                    FinalData+="\n";
                   FinalData+="Sehr: "+Sunrise+"     |          Iftar: "+Sunset;

               }



           } catch (JSONException e) {
               e.printStackTrace();
           }
           textView.setText(FinalData);
       }
   }


}
