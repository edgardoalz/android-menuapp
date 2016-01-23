package xyz.edgardoalz.menuapp.API;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class SingletonAPI {
    private static SingletonAPI instance = null;
    private RequestQueue mRequestQueue;

    private SingletonAPI(Context context) {
        mRequestQueue = Volley.newRequestQueue(context);
    }

    public static SingletonAPI getInstance(Context context) {
        if (instance == null) {
            instance = new SingletonAPI(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

}