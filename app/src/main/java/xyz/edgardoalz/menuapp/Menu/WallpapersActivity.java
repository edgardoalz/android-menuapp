package xyz.edgardoalz.menuapp.Menu;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

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
import xyz.edgardoalz.menuapp.utils.DownloadImageTask;

public class WallpapersActivity extends BaseAPI {

    private class Wallpaper {

        @SerializedName("id")
        public String ID;
        @SerializedName("image")
        public Image image;
        @SerializedName("url")
        public String URL;

        private class Image {
            @SerializedName("url")
            public String URL;
            @SerializedName("thumb")
            public ThumbImage Thumb;

            private class ThumbImage {
                @SerializedName("url")
                public String URL;
            }
        }
    }

    private Button btnWallpapersNext;
    private int page = 1;
    private ArrayList<Wallpaper> Wallpapers = new ArrayList<Wallpaper>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpapers);
        setTitle("Fondos de pantalla");

        btnWallpapersNext = (Button) findViewById(R.id.btnWallpapersNext);
        // Ejecutar la funcion al inicio para generar el listado de articulos
        MakeRequest(page);

        btnWallpapersNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                page += 1;
                MakeRequest(page);
            }
        });
    }

    private void SetImages() {
        ArrayList<ImageView> imgs = new ArrayList<>();
        imgs.add((ImageView) findViewById(R.id.wall1));
        imgs.add((ImageView) findViewById(R.id.wall2));
        imgs.add((ImageView) findViewById(R.id.wall3));
        imgs.add((ImageView) findViewById(R.id.wall4));
        imgs.add((ImageView) findViewById(R.id.wall5));
        imgs.add((ImageView) findViewById(R.id.wall6));
        imgs.add((ImageView) findViewById(R.id.wall7));
        imgs.add((ImageView) findViewById(R.id.wall8));
        imgs.add((ImageView) findViewById(R.id.wall9));

        Wallpaper wall;

        for (int i = 0; i < 9; i++) {
            wall = Wallpapers.get(i);
            new DownloadImageTask(wall.image.Thumb.URL, imgs.get(i)).execute();
            imgs.get(i).setOnClickListener(new MyOnClickListener(wall) {
                @Override
                public void onClick(View v) {
                    Intent internetIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(wall.image.URL));
                    internetIntent.setComponent(new ComponentName("com.android.browser","com.android.browser.BrowserActivity"));
                    internetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(internetIntent);
                }
            });
        }
    }

    public class MyOnClickListener implements View.OnClickListener {

        Wallpaper wall;
        public MyOnClickListener(Wallpaper wall) {
            this.wall = wall;
        }

        @Override
        public void onClick(View v) {}
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void MakeRequest(int pagina){

        String url = "https://api.desktoppr.co/1/wallpapers?page=" + Integer.toString(pagina);
        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, "", new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            JSONArray list = response.getJSONArray("response");
                            StringReader reader = new StringReader(list.toString());
                            Type listType = new TypeToken<ArrayList<Wallpaper>>(){}.getType();
                            Wallpapers = new GsonBuilder().create().fromJson(reader, listType);
                            SetImages();

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
    }
}