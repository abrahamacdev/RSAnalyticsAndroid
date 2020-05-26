package alvarezcruz.abraham.rsanalytics.model.repository.local;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.logging.Level;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.model.pojo.Usuario;
import alvarezcruz.abraham.rsanalytics.model.repository.remote.UsuarioRepository;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.CompletableEmitter;
import io.reactivex.rxjava3.core.CompletableOnSubscribe;
import io.reactivex.rxjava3.core.Maybe;

public class UsuarioModel extends AndroidViewModel {

    public static final String TAG_NAME = UsuarioModel.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private UsuarioRepository usuarioRepository;

    private MutableLiveData<String> ldTokenLocal;
    private MutableLiveData<Usuario> ldUsuario;

    public UsuarioModel(@NonNull Application application) {
        super(application);

        usuarioRepository = new UsuarioRepository(application.getApplicationContext());

        ldUsuario = new MutableLiveData<>();
        ldTokenLocal = new MutableLiveData<>();

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

        String keyArchivo = getApplication().getResources().getString(R.string.archivo_preferencias_key);

        Context context = getApplication().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(keyArchivo, Context.MODE_PRIVATE);

        String keyToken = getApplication().getResources().getString(R.string.token_key);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(keyToken);
        editor.commit();


        this.ldTokenLocal.postValue(null);
    }

    public Completable eliminarTokenLocalASync(){
        return Completable.create(emitter -> {
            eliminarTokenLocalSync();
            emitter.onComplete();
        });
    }
    // -----


    // --- Usuario ---
    public Maybe<Usuario> getUsuario(){
        return Maybe.create(emitter -> {

            // No hay ningun valor almacenado
            if (this.ldUsuario.getValue() == null){

                // Obtendremos los datos del usuario de internet
                getUsuarioRemoto().subscribe(usuario -> {

                    this.ldUsuario.setValue(usuario);
                    emitter.onSuccess(usuario);

                }, emitter::onError, emitter::onComplete);
            }

            else {
                emitter.onSuccess(this.ldUsuario.getValue());
            }
        });
    }

    private Maybe<Usuario> getUsuarioRemoto(){
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
}
