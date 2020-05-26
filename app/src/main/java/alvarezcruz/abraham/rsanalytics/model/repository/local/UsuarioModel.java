package alvarezcruz.abraham.rsanalytics.model.repository.local;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.airbnb.lottie.L;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.model.repository.remote.UsuarioRepository;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.MaybeEmitter;
import io.reactivex.rxjava3.core.MaybeOnSubscribe;

public class UsuarioModel extends AndroidViewModel {

    public static final String TAG_NAME = UsuarioModel.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private UsuarioRepository usuarioRepository;

    private String tokenLocal;

    public UsuarioModel(@NonNull Application application) {
        super(application);

        usuarioRepository = new UsuarioRepository(application.getApplicationContext());
    }

    public Maybe<String> getTokenLocal(){

        if (this.tokenLocal == null){
            Context context = getApplication().getApplicationContext();

            String keyArchivo = getApplication().getResources().getString(R.string.archivo_preferencias_key);
            SharedPreferences sharedPreferences = context.getSharedPreferences(keyArchivo, Context.MODE_PRIVATE);

            String keyToken = getApplication().getResources().getString(R.string.token_key);
            String temp = sharedPreferences.getString(keyToken, null);

            // No hay ningun token almacenado
            if (temp == null){
                return Maybe.empty();
            }

            this.tokenLocal = temp;
        }

        return Maybe.just(this.tokenLocal);
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
        }));
    }

    public void guardarTokenEnLocal(String token){

        String keyArchivo = getApplication().getResources().getString(R.string.archivo_preferencias_key);

        Context context = getApplication().getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(keyArchivo, Context.MODE_PRIVATE);

        String keyToken = getApplication().getResources().getString(R.string.token_key);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(keyToken, token);
        editor.commit();


        this.tokenLocal = token;
    }
}
