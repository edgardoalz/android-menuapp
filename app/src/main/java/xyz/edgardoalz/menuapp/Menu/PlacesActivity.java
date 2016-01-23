package xyz.edgardoalz.menuapp.Menu;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import xyz.edgardoalz.menuapp.API.BaseAPI;
import xyz.edgardoalz.menuapp.R;
import xyz.edgardoalz.menuapp.utils.ListAdapter;

public class PlacesActivity extends BaseAPI {

    private Button btnSearch;
    private EditText txtName;
    private ArrayList<Place> places = new ArrayList<>();
    private ListView list;
    private Location location;

    private class Place {

        @SerializedName("name")
        public String Name;
        @SerializedName("contact")
        public ContactPlace Contact = new ContactPlace();
        @SerializedName("location")
        public LocationPlace Location = new LocationPlace();

        private class ContactPlace {
            @SerializedName("phone")
            public String Phone;
            @SerializedName("formattedPhone")
            public String FormattedPhone;
        }

        private class LocationPlace {
            @SerializedName("address")
            public String Address;
            @SerializedName("crossStreet")
            public String Street;
            @SerializedName("city")
            public String City;
            @SerializedName("country")
            public String Country;

            @Override
            public String toString() {
                String str = "Calle: ";
                if (this.Address != null) {
                    str += this.Address;
                }
                if (this.City != null) {
                    str += " " + this.City;
                }
                if (this.Country != null) {
                    str += " " + this.Country;
                }
                return str;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        setTitle("Buscador de lugares");

        btnSearch = (Button) findViewById(R.id.btnPlacesSearch);
        txtName = (EditText) findViewById(R.id.txtNamePlace);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeRequest(txtName.getText().toString(), location);
            }
        });

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
                location = loc;
                Toast.makeText(PlacesActivity.this, "Ubicación obtenida", Toast.LENGTH_LONG).show();
                request = true;
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Para utilizar esta opción activar el GPS", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "GPS activo", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }

    private void SetListView() {
        list = (ListView) findViewById(R.id.lstPlaces);
        list.setAdapter(new ListAdapter(this, R.layout.item_places, places) {
            @Override
            public void onItem(Object item, View view) {
                Place place = (Place) item;

                TextView upText = (TextView) view.findViewById(R.id.txtTitleBooks);
                upText.setText(place.Name);

                TextView lowerText = (TextView) view.findViewById(R.id.txtAbstractBooks);
                String info = place.Location.toString();
                if (place.Contact.FormattedPhone != null) {
                    info += "\nTelefono: " + place.Contact.FormattedPhone;
                }
                lowerText.setText(info);
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Place place = (Place) parent.getItemAtPosition(position);
                if (place.Contact.Phone != null) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + place.Contact.Phone));
                    if (ActivityCompat.checkSelfPermission(PlacesActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(callIntent);
                }
            }
        });
    }

    private void MakeRequest(String name, Location location){

        if (location != null) {
            name = name.replace(" ", "+");
            String url = "https://api.foursquare.com/v2/venues/search" +
                    "?client_id=T3QAR4O3VW03BX1OJGSEMUI5CAJFODBUZROCQRQCCIFYN2F2" +
                    "&client_secret=WIATJSUWFU3NEJII1XHC5RRQ5HIQGEKGRFJ3VKBYT4QESCP1" +
                    "&v=20130815&ll=" + Double.toString(location.getLatitude()) + "," +
                    Double.toString(location.getLongitude()) + "&query=" + name;

            JsonObjectRequest jsonRequest = new JsonObjectRequest
                    (Request.Method.GET, url, "", new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // the response is already constructed as a JSONObject!
                            try {
                                response = response.getJSONObject("response");
                                JSONArray list = response.getJSONArray("venues");
                                StringReader reader = new StringReader(list.toString());
                                Type listType = new TypeToken<ArrayList<Place>>(){}.getType();
                                places = new GsonBuilder().create().fromJson(reader, listType);
                                SetListView();

                            } catch (JSONException e) {
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
        } else {
            Toast.makeText(PlacesActivity.this, "Obteniendo la ubicación actual", Toast.LENGTH_LONG).show();
        }
    }
}
