package xyz.edgardoalz.menuapp.Menu;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
import xyz.edgardoalz.menuapp.utils.ListAdapter;

public class NewsActivity extends BaseAPI {

    private class News {

        @SerializedName("url")
        public String URL;
        @SerializedName("section")
        public String Section;
        @SerializedName("title")
        public String Title;
        @SerializedName("abstract")
        public String Abstract;
        @SerializedName("published_date")
        public String Date;
        @SerializedName("media")
        public ArrayList<NewsMedia> Media = new ArrayList<>();

        private class NewsMedia {

            @SerializedName("media-metadata")
            public ArrayList<MediaMetadata> Metadata = new ArrayList<>();

            private class MediaMetadata {
                @SerializedName("url")
                public String URL;
            }
        }
    }

    private Button btnNewsReader;
    private ArrayList<News> Articles = new ArrayList<>();
    private ListView list;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        setTitle("Noticias populares");

        btnNewsReader = (Button) findViewById(R.id.btnNewsReader);

        MakeRequest();

        btnNewsReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeRequest();
            }
        });
    }

    private void SetListView() {
        list = (ListView) findViewById(R.id.lstPlaces);
        list.setAdapter(new ListAdapter(this, R.layout.article_news, Articles) {
            @Override
            public void onItem(Object entrada, View view) {
                TextView upText = (TextView) view.findViewById(R.id.txtTitleNews);
                upText.setText(((News) entrada).Title);

                TextView upDown = (TextView) view.findViewById(R.id.txtAbstractNews);
                upDown.setText(((News) entrada).Abstract);

                // show The Image in a ImageView
                ImageView imgItem = (ImageView) view.findViewById(R.id.imgItemNews);
                String url = ((News) entrada).Media.get(0).Metadata.get(0).URL;
                new DownloadImageTask(url, imgItem).execute();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News news = (News) parent.getItemAtPosition(position);
                    Intent internetIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(news.URL));
                    internetIntent.setComponent(new ComponentName("com.android.browser","com.android.browser.BrowserActivity"));
                    internetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(internetIntent);
                }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SetListView();
    }

    private void MakeRequest(){

        String url = "http://api.nytimes.com/svc/mostpopular/v2/mostviewed/all-sections/30.json" +
                "?api-key=ac06e8da3d870bbbfe7280a1f1449721:0:74070801";

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, "", new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            JSONArray list = response.getJSONArray("results");
                            StringReader reader = new StringReader(list.toString());
                            Type listType = new TypeToken<ArrayList<News>>(){}.getType();
                            Articles = new GsonBuilder().create().fromJson(reader, listType);
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
    }
}



