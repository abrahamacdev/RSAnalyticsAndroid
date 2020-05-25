package alvarezcruz.abraham.rsanalytics.model.repository.remote;

import android.content.Context;
import android.util.Pair;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.utils.Constantes;
import alvarezcruz.abraham.rsanalytics.utils.Utils;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeEmitter;
import io.reactivex.rxjava3.core.MaybeOnSubscribe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.subjects.PublishSubject;

public class UsuarioRepository {

    public final static String TAG_NAME = UsuarioRepository.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private Context context;
    private RequestQueue requestQueue;

    public UsuarioRepository(Context context){
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }

    public Maybe<Pair<Integer, String>> realizarLogin(String correo, String contrasenia){

        logger.log(Level.SEVERE, "Vamos a hacer login");

        return Maybe.create(emitter -> {

            logger.log(Level.SEVERE, "stamos dentro");

            String url = Constantes.URL_SERVER + Constantes.RUTA_USUARIO + Constantes.LOGIN_ENDPOINT;

            // Parametros de la peticion
            JSONObject jsonObject = new JSONObject();
            Utils.anadirAlJSON(jsonObject, "correo", correo);
            Utils.anadirAlJSON(jsonObject, "contrasenia", contrasenia);

            // Realizamos la peticion
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                    response -> {

                        String token = (String) Utils.obtenerDelJSON(jsonObject, "token");
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

}
