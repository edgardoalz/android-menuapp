package xyz.edgardoalz.menuapp.Menu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import xyz.edgardoalz.menuapp.utils.ListAdapter;

public class BooksActivity extends BaseAPI {

    private Button btnSearch;
    private TextView txtName;
    private ArrayList<Book> Books = new ArrayList<>();
    private ListView list;

    private class Book {

        @SerializedName("selfLink")
        public String URL;
        @SerializedName("kind")
        public String Kind;
        @SerializedName("volumeInfo")
        public Info VolumeInfo = new Info();

        public class Info {
            @SerializedName("title")
            public String Title;
            @SerializedName("description")
            public String Description;
            @SerializedName("publishedDate")
            public String Date;
            @SerializedName("authors")
            public ArrayList<String> Authors = new ArrayList<>();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);
        setTitle("Buscador de libros");

        btnSearch = (Button) findViewById(R.id.btnBookSearch);
        txtName = (TextView) findViewById(R.id.txtBookName);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MakeRequest(txtName.getText().toString());
            }
        });
    }

    private void SetListView() {
        list = (ListView) findViewById(R.id.lstBooks);
        list.setAdapter(new ListAdapter(this, R.layout.item_books, Books) {
            @Override
            public void onItem(Object item, View view) {
                Book book = (Book) item;
                TextView upText = (TextView) view.findViewById(R.id.txtTitleBooks);
                upText.setText(book.VolumeInfo.Title);

                TextView lowerText = (TextView) view.findViewById(R.id.txtAbstractBooks);
                String info = "Autor: " + book.VolumeInfo.Authors.get(0);
                if (book.VolumeInfo.Description != null) {
                   info += "\n" + book.VolumeInfo.Description;
                }
                lowerText.setText(info);
            }
        });
    }

    private void MakeRequest(String name){
        name = name.replace(" ", "+");
        String url = "https://www.googleapis.com/books/v1/volumes?q=intitle:" + name +
                "&key=AIzaSyAoFd25PYaPWG7ZyWU622DT41TvevWppZM";

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, "", new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            JSONArray list = response.getJSONArray("items");
                            StringReader reader = new StringReader(list.toString());
                            Type listType = new TypeToken<ArrayList<Book>>(){}.getType();
                            Books = new GsonBuilder().create().fromJson(reader, listType);
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
