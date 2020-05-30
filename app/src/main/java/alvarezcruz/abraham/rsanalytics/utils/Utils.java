package alvarezcruz.abraham.rsanalytics.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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

    public static String mes2Texto(int num){

        switch (num){

            case 1:
                return "Enero";

            case 2:
                return "Febrero";

            case 3:
                return "Marzo";

            case 4:
                return "Abril";

            case 5:
                return "Mayo";

            case 6:
                return "Junio";

            case 7:
                return "Julio";

            case 8:
                return "Agosto";

            case 9:
                return "Septiembre";

            case 10:
                return "Octubre";

            case 11:
                return "Noviembre";

            case 12:
                return "Diciembre";
        }

        return "";
    }

    public static void esconderTeclado(Context context, View view){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
