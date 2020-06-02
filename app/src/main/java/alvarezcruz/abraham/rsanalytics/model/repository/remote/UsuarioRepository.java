package alvarezcruz.abraham.rsanalytics.model.repository.remote;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Pair;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.model.pojo.Informe;
import alvarezcruz.abraham.rsanalytics.model.pojo.Usuario;
import alvarezcruz.abraham.rsanalytics.model.pojo.notificaciones.Notificacion;
import alvarezcruz.abraham.rsanalytics.utils.Constantes;
import alvarezcruz.abraham.rsanalytics.utils.RespuestaInvitacion;
import alvarezcruz.abraham.rsanalytics.utils.Utils;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;

import static android.content.Context.DOWNLOAD_SERVICE;

public class UsuarioRepository {

    public final static String TAG_NAME = UsuarioRepository.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private Context context;
    private RequestQueue requestQueue;

    public UsuarioRepository(Context context){
        this.context = context;
        requestQueue = Volley.newRequestQueue(context);
    }


    // --- Usuario ---
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
    // ---------------

    // --- Informacion general ---
    public Maybe<Pair<Integer, Usuario>> obtenerInformacionGeneral(String token){
        return Maybe.create(emitter -> {

            String url = Constantes.URL_SERVER + Constantes.RUTA_USUARIO + Constantes.INFORMACION_GENERAL_USUARIO_ENDPOINT;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {

                    String nombre = (String) Utils.obtenerDelJSON(response, "nombre");
                    String primerApellido = (String) Utils.obtenerDelJSON(response, "primerApellido");
                    String correo = (String) Utils.obtenerDelJSON(response, "correo");
                    String sexo = (String) Utils.obtenerDelJSON(response, "genero");
                    boolean esResponsable = response.optBoolean("esResponsable", false);

                    emitter.onSuccess(new Pair<>(200, new Usuario(nombre, primerApellido, correo, esResponsable, sexo)));

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
    // ---------------------------

    // --- Notificaciones ---
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

    public Completable marcarNotificacionesLeidas(String token, List<Integer> idsNotificaciones){
        return Completable.create(emitter -> {

            logger.log(Level.SEVERE, "Vamos a marcar como leidas las notificaciones: " + idsNotificaciones);

            String url = Constantes.URL_SERVER + Constantes.RUTA_USUARIO + Constantes.RUTA_NOTIFICACIONES + Constantes.MARCAR_NOTIFICACIONES_LEIDAS_ENDPOINT;

            JSONObject params = new JSONObject();
            params.put("idsNotificaciones", idsNotificaciones);

            // Realizamos la peticion
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                    response -> {

                        emitter.onComplete();

                    }, emitter::onError) {
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
    // ----------------------

    // --- Grupos ---
    public Maybe<Integer> responderInvitacionGrupo(String token, int idNotificacion, RespuestaInvitacion respuestaInvitacion){
        return Maybe.create(emitter -> {

            String url = Constantes.URL_SERVER + Constantes.RUTA_GRUPO + Constantes.RUTA_INVITACION;

            if (respuestaInvitacion == RespuestaInvitacion.ACEPTAR){
                url += Constantes.ACEPTAR_INVITACION_ENDPOINT;
            }
            else {
                url += Constantes.RECHAZAR_INVITACION_ENDPOINT;
            }

            JSONObject params = new JSONObject();
            params.put("idNotificacion", idNotificacion);

            // Realizamos la peticion
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                    response -> {

                        emitter.onSuccess(200);

                    }, error -> {

                        int status = 500;

                        if (error.networkResponse != null){
                            status = error.networkResponse.statusCode;
                        }

                        emitter.onSuccess(status);
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

    public Maybe<Pair<Integer, ArrayList<Usuario>>> obtenerMiembros(String token){
        return Maybe.create(emitter -> {

            String url = Constantes.URL_SERVER + Constantes.RUTA_GRUPO + Constantes.DATOS_GENERALES_GRUPO_ENDPOINT;

            // Realizamos la peticion
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {

                        logger.log(Level.SEVERE, response.toString());

                        try {

                            JSONObject jsonGrupo = (JSONObject) Utils.obtenerDelJSON(response, "grupo");

                            JSONArray miembros = jsonGrupo.getJSONArray("miembros");

                            ArrayList<Usuario> listadoMiembros = new ArrayList(miembros.length());

                            for (int i=0; i<miembros.length(); i++){
                                listadoMiembros.add(Usuario.miembroFromJson(miembros.getJSONObject(i)));
                            }

                            emitter.onSuccess(new Pair<>(200, listadoMiembros));

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

    public Maybe<Integer> crearGrupo(String token, String nombreGrupo){
        return Maybe.create(emitter -> {

            String url = Constantes.URL_SERVER + Constantes.RUTA_GRUPO + Constantes.REGISTRO_GRUPO_ENDPOINT;

            JSONObject params = new JSONObject();
            params.put("nombreGrupo", nombreGrupo);

            // Realizamos la peticion
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                    response -> {

                        emitter.onSuccess(200);

                    }, error -> {

                        int status = 500;

                        if (error.networkResponse != null){
                            status = error.networkResponse.statusCode;
                        }

                        emitter.onSuccess(status);
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

    public Maybe<Integer> invitarUsuario(String token, String correo){
        return Maybe.create(emitter -> {

            String url = Constantes.URL_SERVER + Constantes.RUTA_GRUPO + Constantes.INVITAR_USUARIO_ENDPOINT;

            JSONObject params = new JSONObject();
            params.put("invitado", correo);

            // Realizamos la peticion
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                    response -> {

                        emitter.onSuccess(200);

                    }, error -> {

                        int status = 500;

                        if (error.networkResponse != null){
                            status = error.networkResponse.statusCode;
                        }

                        emitter.onSuccess(status);
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

    public Maybe<Integer> abandonarGrupo(String token){
        return Maybe.create(emitter -> {

            String url = Constantes.URL_SERVER + Constantes.RUTA_GRUPO + Constantes.ABANDONAR_GRUPO_ENDPOINT;

            // Realizamos la peticion
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {

                        emitter.onSuccess(200);

                    }, error -> {

                        int status = 500;

                        if (error.networkResponse != null){
                            status = error.networkResponse.statusCode;
                        }

                        emitter.onSuccess(status);
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", token);
                    return params;
                }
            };

            Utils.anadirPeticionACola(requestQueue, stringRequest, 1);
        });
    }
    // --------------

    // --- Informes ---
    public Maybe<Pair<Integer,ArrayList<Informe>>> obtenerInformes(String token){
        return Maybe.create(emitter -> {

            String url = Constantes.URL_SERVER + Constantes.RUTA_INFORMES + Constantes.LISTAR_INFORMES_ENDPOINT;

            // Realizamos la peticion
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    response -> {

                        // Obtenemos el listado de informes
                        JSONArray jsonArrayInformes = (JSONArray) Utils.obtenerDelJSON(response, "informes");
                        ArrayList<Informe> informes = new ArrayList<>(jsonArrayInformes.length());

                        // Parseamos cada informe a un POJO
                        for (int i=0; i<jsonArrayInformes.length(); i++){
                            informes.add(Informe.fromJson(jsonArrayInformes.optJSONObject(i)));
                        }

                        // Pasamos el listado parseado
                        emitter.onSuccess(new Pair<>(200, informes));

                    }, error -> {

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

    public Observable<Pair<Integer, ArrayList<String>>> consultarMunicipios(String token, String patron){
        return Observable.create(emitter -> {
            String url = Constantes.URL_SERVER + Constantes.RUTA_MUNICIPIOS + Constantes.BUSCAR_MUNICIPIO;

            JSONObject params = new JSONObject();
            params.put("palabraClave", patron);

            // Realizamos la peticion
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                    response -> {


                        // Obtenemos el listado de informes
                        JSONArray jsonArrayMunicipios = (JSONArray) Utils.obtenerDelJSON(response, "municipios");
                        ArrayList<String> municipios = new ArrayList<>(jsonArrayMunicipios.length());

                        // Parseamos cada informe a un POJO
                        for (int i=0; i<jsonArrayMunicipios.length(); i++){
                            municipios.add(jsonArrayMunicipios.optString(i,""));
                        }

                        // Pasamos el listado parseado
                        emitter.onNext(new Pair<>(200, municipios));

                    }, error -> {

                        int status = 500;

                        if (error.networkResponse != null){
                            status = error.networkResponse.statusCode;
                        }

                        emitter.onNext(new Pair<>(status, null));
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

    public Maybe<Integer> solicitarInforme(int idTipoContrato, int idTipoInmueble, String municipio, String token){
        return Maybe.create(emitter -> {
            String url = Constantes.URL_SERVER + Constantes.RUTA_INFORMES + Constantes.SOLICITAR_INFORME_ENDPOINT;

            JSONObject params = new JSONObject();
            params.put("municipio", municipio);
            params.put("idTipoContrato", idTipoContrato);
            params.put("idTipoInmueble", idTipoInmueble);

            // Realizamos la peticion
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                    response -> {

                        // Pasamos el listado parseado
                        emitter.onSuccess(200);

                    }, error -> {

                    error.printStackTrace();

                    int status = 500;

                    if (error.networkResponse != null){
                        status = error.networkResponse.statusCode;
                    }

                    emitter.onSuccess(status);
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

    public void descargarInforme(Informe informe, String token){
        String url = Constantes.URL_SERVER + Constantes.RUTA_INFORMES + Constantes.DESCARGAR_INFORME_ENDPOINT + "?id=" + informe.getId();

        File destino = new File(Environment.getExternalStorageDirectory() + "/" + Environment.DIRECTORY_DOWNLOADS + "/" + informe.getNombreArchivo());

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
            .addRequestHeader("Authorization", token)
            .addRequestHeader("Content-Type", "application/pdf;charset=UTF-8")
            .addRequestHeader("Cache-Control", "no-cache")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationUri(Uri.fromFile(destino))
            .setMimeType("application/pdf")
            .setTitle(context.getResources().getString(R.string.frainf_desc_inf));

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
    // ----------------
}
