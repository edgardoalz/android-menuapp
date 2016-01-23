package xyz.edgardoalz.menuapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import xyz.edgardoalz.menuapp.Menu.BooksActivity;
import xyz.edgardoalz.menuapp.Menu.NewsActivity;
import xyz.edgardoalz.menuapp.Menu.PlacesActivity;
import xyz.edgardoalz.menuapp.Menu.TranslateActivity;
import xyz.edgardoalz.menuapp.Menu.WallpapersActivity;
import xyz.edgardoalz.menuapp.Menu.WeatherActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void btnApp(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }
    public void btnNews(View view) {
        btnApp(NewsActivity.class);
    }
    public void btnWallpapers(View view) {
        btnApp(WallpapersActivity.class);
    }
    public void btnBooks(View view) {
        btnApp(BooksActivity.class);
    }
    public void btnWeather(View view) {
        btnApp(WeatherActivity.class);
    }
    public void btnPlaces(View view) {
        btnApp(PlacesActivity.class);
    }
    public void btnTranslate(View view) {
        btnApp(TranslateActivity.class);
    }
}
