package alvarezcruz.abraham.rsanalytics.model.repository.remote;

import android.content.Context;
import android.util.Pair;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.utils.Constantes;
import alvarezcruz.abraham.rsanalytics.utils.Utils;
import io.reactivex.rxjava3.core.Maybe;

public class UsuarioRepository {

    public final static String TAG_NAME = UsuarioRepository.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private RequestQueue requestQueue;

    public UsuarioRepository(Context context){
        requestQueue = Volley.newRequestQueue(context);
    }

    public Maybe<Pair<Integer, String>> realizarLogin(String correo, String contrasenia){
        return Maybe.create(emitter -> {

            String url = Constantes.URL_SERVER + Constantes.RUTA_USUARIO + Constantes.LOGIN_USUARIO_ENDPOINT;

            // Parametros de la peticion
            JSONObject jsonObject = new JSONObject();
            Utils.anadirAlJSON(jsonObject, "correo", correo);
            Utils.anadirAlJSON(jsonObject, "contrasenia", contrasenia);

            // Realizamos la peticion
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    response -> {

                        String token = (String) Utils.obtenerDelJSON(response, "token");
                        emitter.onSuccess(new Pair<>(200, token));

                    },  error -> {

                        int status = 500;

                        if (error.networkResponse != null){
                            status = error.networkResponse.statusCode;
                        }
                        emitter.onSuccess(new Pair<>(status, null));
            });

            requestQueue.add(jsonObjectRequest);
        });
    }

    public Maybe<Integer> realizarRegistro(HashMap<String, Object> params){
        return Maybe.create(emitter -> {

            String url = Constantes.URL_SERVER + Constantes.RUTA_USUARIO + Constantes.REGISTRO_USUARIO_ENDPOINT;

            // Parametros de la peticion
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, Object> entry : params.entrySet()){
                Utils.anadirAlJSON(jsonObject, entry.getKey(), entry.getValue());
            }

            // Realizamos la peticion
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    response -> {
                        emitter.onSuccess(200);

                    },  error -> {

                        int status = 500;

                        if (error.networkResponse != null){
                            status = error.networkResponse.statusCode;
                        }
                        emitter.onSuccess(status);
            });

            requestQueue.add(jsonObjectRequest);
        });
    }

}
