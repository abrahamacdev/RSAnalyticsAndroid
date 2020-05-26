package alvarezcruz.abraham.rsanalytics.utils;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;

import org.json.JSONException;
import org.json.JSONObject;

public class Utils {

    public static void anadirAlJSON(JSONObject jsonObject, String key, Object object){
        try {
            jsonObject.put(key,object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static Object obtenerDelJSON(JSONObject jsonObject, String key){
        try {
            return jsonObject.get(key);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> void anadirPeticionACola(RequestQueue requestQueue, Request<T> request, int maxIntentos){

        if (maxIntentos >= 1){
            request.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    maxIntentos - 1,
                    0));
        }

        else {
            request.setRetryPolicy(new DefaultRetryPolicy(0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        }

        requestQueue.add(request);
    }

}
