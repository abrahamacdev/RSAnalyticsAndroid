package alvarezcruz.abraham.rsanalytics.model.repository.local;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.airbnb.lottie.L;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.model.pojo.Usuario;
import alvarezcruz.abraham.rsanalytics.model.pojo.notificaciones.Notificacion;
import alvarezcruz.abraham.rsanalytics.model.repository.remote.UsuarioRepository;
import alvarezcruz.abraham.rsanalytics.utils.RespuestaInvitacion;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class UsuarioModel extends AndroidViewModel {

    public static final String TAG_NAME = UsuarioModel.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private UsuarioRepository usuarioRepository;

    private MutableLiveData<String> ldTokenLocal;
    private MutableLiveData<Usuario> ldUsuario;
    private MutableLiveData<ArrayList<Notificacion>> ldNotificaciones;
    private MutableLiveData<ArrayList<Usuario>> ldMiembros;

    public UsuarioModel(@NonNull Application application) {
        super(application);

        usuarioRepository = new UsuarioRepository(application.getApplicationContext());

        ldUsuario = new MutableLiveData<>();
        ldTokenLocal = new MutableLiveData<>();
        ldNotificaciones = new MutableLiveData<>();
        ldMiembros = new MutableLiveData<>();
    }

    // --- Token ---
    public String getTokenLocalSync(){

        if (this.ldTokenLocal.getValue() == null){
            Context context = getApplication().getApplicationContext();

            String keyArchivo = getApplication().getResources().getString(R.string.archivo_preferencias_key);
            SharedPreferences sharedPreferences = context.getSharedPreferences(keyArchivo, Context.MODE_PRIVATE);

            String keyToken = getApplication().getResources().getString(R.string.token_key);
            String temp = sharedPreferences.getString(keyToken, null);

            this.ldTokenLocal.setValue(temp);
            return temp;
        }

        return this.ldTokenLocal.getValue();
    }

    public Maybe<String> getTokenLocalAsync(){

        if (this.ldTokenLocal.getValue() == null){

            // No hay ningun token almacenado
            String temp = getTokenLocalSync();

            if (temp == null){
                return Maybe.empty();
            }

            return Maybe.just(temp);
        }

        return Maybe.just(this.ldTokenLocal.getValue());
    }

    public Maybe<Pair<Integer,String>> getTokenRemoto(String correo, String contrasenia){
        return usuarioRepository.realizarLogin(correo,contrasenia);
    }

    public Maybe<Pair<Integer,String>> getTokenRemotoYGuardarEnLocal(String correo, String contrasenia){
        return Maybe.create(emitter -> getTokenRemoto(correo,contrasenia).subscribe(par -> {

                    if (par.first >= 200 && par.first < 300){
                        guardarTokenEnLocal(par.second);
                    }

                    emitter.onSuccess(par);

        }, emitter::onError, emitter::onComplete));
    }

    public void guardarTokenEnLocal(String token){

        String keyArchivo = getApplication().getResources().getString(R.string.archivo_preferencias_key);

        Context context = getApplication().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(keyArchivo, Context.MODE_PRIVATE);

        String keyToken = getApplication().getResources().getString(R.string.token_key);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(keyToken, token);
        editor.commit();


        this.ldTokenLocal.postValue(token);
    }

    public void eliminarTokenLocalSync(){

        logger.log(Level.SEVERE, "ELliminando token");

        String keyArchivo = getApplication().getResources().getString(R.string.archivo_preferencias_key);

        Context context = getApplication().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(keyArchivo, Context.MODE_PRIVATE);

        String keyToken = getApplication().getResources().getString(R.string.token_key);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(keyToken);
        editor.commit();


        this.ldTokenLocal.postValue(null);
    }

    public Completable eliminarTokenLocalAsync(){
        return Completable.create(emitter -> {

            Runnable runnable = () -> { eliminarTokenLocalSync(); };

            Completable.fromRunnable(runnable)
                    .subscribeOn(Schedulers.io())
                    .subscribe(() -> {
                        logger.log(Level.SEVERE,"Eliminado");
                        emitter.onComplete();
                    }, e -> {});

        });
    }
    // -----


    // --- Usuario ---
    public Maybe<Pair<Integer,Usuario>> getUsuario(){

        if (ldUsuario.getValue() == null){
            return recargarUsuario();
        }

        else {
            return Maybe.just(new Pair<>(200,ldUsuario.getValue()));
        }
    }

    public Maybe<Pair<Integer, Usuario>> recargarUsuario(){
        return Maybe.create(emitter -> {
            // Obtendremos los datos del usuario de internet
            getUsuarioRemoto().subscribe(par -> {

                if (par.first == 200){
                    ldUsuario.setValue(par.second);
                }

                emitter.onSuccess(par);

            }, emitter::onError, emitter::onComplete);
        });
    }

    private Maybe<Pair<Integer, Usuario>> getUsuarioRemoto(){
        return Maybe.create(emitter -> {

            String token = getTokenLocalSync();

            // Obtenemos la informacion general del usuario
            usuarioRepository.obtenerInformacionGeneral(token)
                    .subscribe(emitter::onSuccess,
                            emitter::onError,
                            emitter::onComplete);
        });
    }
    // ------


    // --- Notificaciones ---
    public MutableLiveData<ArrayList<Notificacion>> getNotificaciones(){
        return this.ldNotificaciones;
    }

    public Completable recargarNotificacionesAsync(){
        return Completable.create(emitter -> {
            // Obtenemos el listado y lo almacenamos en el live data
            usuarioRepository.obtenerListadoNotificaciones(getTokenLocalSync())
                    .subscribeOn(Schedulers.io())
                    .subscribe(par -> {

                        if (par.first == 200){
                            this.ldNotificaciones.postValue(par.second);
                        }

                        emitter.onComplete();
                    }, emitter::onError, emitter::onComplete);
        });
    }

    public void recargarNotificaciones(){
        // Obtenemos el listado y lo almacenamos en el live data
        usuarioRepository.obtenerListadoNotificaciones(getTokenLocalSync())
                .subscribe(par -> {

                    if (par.first == 200){
                        this.ldNotificaciones.postValue(par.second);
                    }
                });
    }

    public void marcarNotificacionesLeidas(){

        ArrayList<Notificacion> notificaciones = ldNotificaciones.getValue();

        if (notificaciones == null || notificaciones.size() == 0){
            return;
        }

        ArrayList<Integer> idsNotificaciones = new ArrayList<>(notificaciones.size());
        Observable.fromIterable(notificaciones)
                .map(Notificacion::getId)
                .collect(() -> idsNotificaciones, ArrayList::add)
                .subscribe();

        usuarioRepository.marcarNotificacionesLeidas(ldTokenLocal.getValue(), idsNotificaciones);
    }
    // ----------------------

    // --- Grupo ---
    public Maybe<Integer> responderInvitacionGrupo(int idNotificacion, RespuestaInvitacion respuestaInvitacion){
        return usuarioRepository.responderInvitacionGrupo(ldTokenLocal.getValue(), idNotificacion, respuestaInvitacion);
    }

    public MutableLiveData<ArrayList<Usuario>> getMiembros(){
        return this.ldMiembros;
    }

    public void recargarMiembrosAsync(){
        // Obtenemos el listado y lo almacenamos en el live data
        usuarioRepository.obtenerMiembros(this.ldTokenLocal.getValue())
                .subscribeOn(Schedulers.io())
                .subscribe(par -> {

                    if (par.first == 200){
                        this.ldMiembros.postValue(par.second);
                    }

                    // No pertenece a ningun grupo
                    else if (par.first == 400){
                        this.ldMiembros.postValue(new ArrayList<>(0));
                    }


                }, Throwable::printStackTrace);
    }

    public void recargarMiembrosYUsuarioAsync(){
        usuarioRepository.obtenerMiembros(this.ldTokenLocal.getValue())
                .subscribeOn(Schedulers.io())
                .subscribe(par -> {

                    if (par.first == 200){
                        this.ldMiembros.postValue(par.second);
                    }

                    // No pertenece a ningun grupo
                    else if (par.first == 400){
                        this.ldMiembros.postValue(new ArrayList<>(0));
                    }

                    // Recargamos la informacion del usuario
                    usuarioRepository.obtenerInformacionGeneral(this.ldTokenLocal.getValue())
                            .subscribe(par2 -> {
                                if (par2.first == 200){
                                    ldUsuario.setValue(par2.second);
                                }
                            });
                });
    }

    public void recargarMiembros(){
        // Obtenemos el listado y lo almacenamos en el live data
        usuarioRepository.obtenerMiembros(this.ldTokenLocal.getValue())
                .subscribe(par -> {

                    if (par.first == 200){
                        this.ldMiembros.postValue(par.second);
                    }
                });
    }

    public Maybe<Integer> crearGrupoAsync(String nombre){
        return usuarioRepository.crearGrupo(this.ldTokenLocal.getValue(), nombre)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Integer> invitarUsuarioAsync(String correo){
        return usuarioRepository.invitarUsuario(this.ldTokenLocal.getValue(), correo)
                .subscribeOn(Schedulers.io());
    }

    public Maybe<Integer> abandonarGrupoAsync(){
        return usuarioRepository.abandonarGrupo(this.ldTokenLocal.getValue())
                .subscribeOn(Schedulers.io());
    }
    // -------------
}
