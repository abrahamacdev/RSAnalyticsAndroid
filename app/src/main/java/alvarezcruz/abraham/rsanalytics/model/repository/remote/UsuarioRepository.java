package alvarezcruz.abraham.rsanalytics.model.repository.remote;

import android.content.Context;
import android.util.JsonReader;
import android.util.Pair;

import com.airbnb.lottie.L;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.model.pojo.Usuario;
import alvarezcruz.abraham.rsanalytics.model.pojo.notificaciones.Notificacion;
import alvarezcruz.abraham.rsanalytics.utils.Constantes;
import alvarezcruz.abraham.rsanalytics.utils.Utils;
import io.reactivex.rxjava3.core.Maybe;

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


            Utils.anadirPeticionACola(requestQueue, jsonObjectRequest, 1);
        });
    }

    public Maybe<Pair<Integer,JSONObject>> realizarRegistro(HashMap<String, Object> params){
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

                        emitter.onSuccess(new Pair(200, new JSONObject()));

                    },  error -> {

                        int status = 500;

                        if (error.networkResponse != null){
                            status = error.networkResponse.statusCode;
                        }

                        String data = new String(error.networkResponse.data);
                        JSONObject res = new JSONObject();
                        try {
                            res = new JSONObject(data);
                            emitter.onSuccess(new Pair<>(status, res));
                            return;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Utils.anadirAlJSON(res, "msg", context.getString(R.string.fraglog_error_inesperado));
                        emitter.onSuccess(new Pair<>(status, res));
            });

            Utils.anadirPeticionACola(requestQueue, jsonObjectRequest, 1);
        });
    }


    public Maybe<Pair<Integer, Usuario>> obtenerInformacionGeneral(String token){
        return Maybe.create(emitter -> {

            String url = Constantes.URL_SERVER + Constantes.RUTA_USUARIO + Constantes.INFORMACION_GENERAL_USUARIO_ENDPOINT;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {

                String nombre = (String) Utils.obtenerDelJSON(response, "nombre");
                String primerApellido = (String) Utils.obtenerDelJSON(response, "primerApellido");
                String correo = (String) Utils.obtenerDelJSON(response, "correo");
                String sexo = (String) Utils.obtenerDelJSON(response, "genero");

                emitter.onSuccess(new Pair<>(200, new Usuario(nombre, primerApellido, correo, sexo)));

            }, error -> {

                int status = 500;

                if (error.networkResponse != null){
                    status = error.networkResponse.statusCode;
                }

                emitter.onSuccess(new Pair<>(status, null));

            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", token);
                    return params;
                }
            };

            // Añadimos la peticion a la cola
            Utils.anadirPeticionACola(requestQueue, jsonObjectRequest, 1);
        });
    }


    public Maybe<Pair<Integer, ArrayList<Notificacion>>> obtenerListadoNotificaciones(String token){
        return Maybe.create(emitter -> {

            String url = Constantes.URL_SERVER + Constantes.RUTA_USUARIO + Constantes.RUTA_NOTIFICACIONES;

            // Realizamos la peticion
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {

                        try {

                            Object tempJsonArray = Utils.obtenerDelJSON(response, "notificaciones");

                            JSONArray jsonArray = new JSONArray();

                            if (tempJsonArray.getClass().getName().equals(String.class.getName())){
                                jsonArray = new JSONArray((String) tempJsonArray);
                            }
                            else {
                                jsonArray = (JSONArray) tempJsonArray;
                            }


                            ArrayList<Notificacion> notificaciones = new ArrayList<>(jsonArray.length());

                            for (int i=0; i<jsonArray.length(); i++){
                                notificaciones.add(new Notificacion((JSONObject) jsonArray.get(i)));
                            }

                            emitter.onSuccess(new Pair<>(200, notificaciones));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        emitter.onSuccess(new Pair<>(200, null));

                    },  error -> {

                        error.printStackTrace();

                        int status = 500;

                        if (error.networkResponse != null){
                            status = error.networkResponse.statusCode;
                        }

                        emitter.onSuccess(new Pair<>(status, null));
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", token);
                    return params;
                }
            };

            Utils.anadirPeticionACola(requestQueue, jsonObjectRequest, 1);

        });
    }

}
