package kz.gvsx.json;

import android.annotation.SuppressLint;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import io.noties.markwon.Markwon;

@SuppressLint("StaticFieldLeak")
public class MySingleton {
    private static MySingleton instance;
    private static Context ctx;
    private Markwon markwon;
    private RequestQueue requestQueue;

    private MySingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
        markwon = getMarkwon();
    }

    public static synchronized MySingleton getInstance(Context context) {
        if (instance == null) {
            instance = new MySingleton(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public Markwon getMarkwon() {
        if (markwon == null) {
            markwon = Markwon.create(ctx.getApplicationContext());
        }
        return markwon;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}