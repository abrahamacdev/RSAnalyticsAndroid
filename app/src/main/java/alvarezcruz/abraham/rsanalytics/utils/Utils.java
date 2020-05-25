package alvarezcruz.abraham.rsanalytics.utils;

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

}
