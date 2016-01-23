package xyz.edgardoalz.menuapp.Menu;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import xyz.edgardoalz.menuapp.API.BaseAPI;
import xyz.edgardoalz.menuapp.R;
import xyz.edgardoalz.menuapp.utils.DownloadImageTask;

public class WeatherActivity extends BaseAPI {

    private ImageView imgWeather;
    private TextView txtLarge, txtMedium, txtSmall;
    private Weather objWeather = new Weather();

    private class Weather {

        @SerializedName("weather")
        public ArrayList<InfoWeather> Info = new ArrayList<>();
        @SerializedName("main")
        public MainWeather Main = new MainWeather();
        @SerializedName("sys")
        public SysWeather Sys = new SysWeather();
        @SerializedName("name")
        public String Name;

        public class InfoWeather {
            @SerializedName("description")
            public String Description;
            @SerializedName("icon")
            public String Icon;
        }

        private class MainWeather {
            @SerializedName("temp")
            public double Temp;
            @SerializedName("humidity")
            public int Humidity;
            @SerializedName("temp_min")
            public double TempMin;
            @SerializedName("temp_max")
            public double TempMax;
        }

        private class SysWeather {
            @SerializedName("country")
            public String Country;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wheather);
        setTitle("Clima local");

        txtLarge = (TextView) findViewById(R.id.txtWeatherLarge);
        txtMedium = (TextView) findViewById(R.id.txtWeatherMedium);
        txtSmall = (TextView) findViewById(R.id.txtSmall);
        imgWeather = (ImageView) findViewById(R.id.imgIconWeather);

         /* Use the LocationManager class to obtain GPS locations */
        LocationManager mlocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener mlocListener = new MyLocationListener();

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
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
    }

    public class MyLocationListener implements LocationListener {
        private boolean request = false;

        @Override
        public void onLocationChanged(Location loc) {
            if (!request) {
                MakeRequest(loc.getLatitude(), loc.getLongitude());
                request = true;
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Para utilizar esta opción activar el GPS", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText( getApplicationContext(), "GPS activo", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    private void MakeRequest(double latitud, double longitud) {
        String api = "http://api.openweathermap.org/data/2.5/weather?lat=" +
                Double.toString(latitud) + "&lon=" + Double.toString(longitud) +
                "&appid=1814bf7824aaf116cc35793f52dab79b&lang=es&units=metric";

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, api, "", new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            StringReader reader = new StringReader(response.toString());
                            Type type = new TypeToken<Weather>(){}.getType();
                            objWeather = new GsonBuilder().create().fromJson(reader, type);
                            // TODO Definir metodo para asignar los valores

                            txtLarge.setText(objWeather.Name + ", " + objWeather.Sys.Country);
                            txtMedium.setText(objWeather.Info.get(0).Description);
                            txtSmall.setText("Temperatura: " + Double.toString(objWeather.Main.Temp) + " C\n" +
                                            "Humedad: " + Double.toString(objWeather.Main.Humidity)
                            );

                            String imgURL = "http://openweathermap.org/img/w/"+ objWeather.Info.get(0).Icon +".png";
                            new DownloadImageTask(imgURL, imgWeather).execute();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });
        Volley.newRequestQueue(this).add(jsonRequest);
    }
}
