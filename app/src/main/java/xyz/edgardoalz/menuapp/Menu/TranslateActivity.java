package xyz.edgardoalz.menuapp.Menu;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

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

public class TranslateActivity extends BaseAPI {

    private Button btnTranslate;
    private TextView txtResult;
    private RadioButton rdENtoES, rdEStoEN;
    private EditText txtTranslate;
    private Translation translation = new Translation();

    private class Translation {

        @SerializedName("code")
        private int Code;
        @SerializedName("lang")
        private String Lang;
        @SerializedName("text")
        public ArrayList<String> Text = new ArrayList<>();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translate);
        setTitle("Traductor");

        btnTranslate = (Button) findViewById(R.id.btnTranslate);
        txtTranslate = (EditText) findViewById(R.id.txtTraslate);
        txtResult = (TextView) findViewById(R.id.txtResult);
        rdENtoES = (RadioButton) findViewById(R.id.rdENtoES);
        rdEStoEN = (RadioButton) findViewById(R.id.rdEStoEN);
        rdENtoES.setChecked(true);

        btnTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String lang = "en-es";
                if (rdEStoEN.isChecked()) {
                    lang = "es-en";
                }
                MakeRequest(txtTranslate.getText().toString(), lang);
            }
        });
    }

    private void MakeRequest(String text, String lang) {
        String url = "https://translate.yandex.net/api/v1.5/tr.json/translate" +
                "?key=trnsl.1.1.20160122T074424Z.94b9ab3bfb582921.a70d5a13bee6ced5380bf52a0428e4c6df03e677" +
                "&text=" + text.replace(" ", "+") + "&lang=" + lang;

        JsonObjectRequest jsonRequest = new JsonObjectRequest
                (Request.Method.GET, url, "", new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // the response is already constructed as a JSONObject!
                        try {
                            StringReader reader = new StringReader(response.toString());
                            Type type = new TypeToken<Translation>(){}.getType();
                            translation = new GsonBuilder().create().fromJson(reader, type);
                            // TODO Definir metodo para asignar los valores

                            txtResult.setText(translation.Text.get(0));

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
