package alvarezcruz.abraham.rsanalytics.ui.login;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.model.repository.local.UsuarioModel;
import alvarezcruz.abraham.rsanalytics.model.repository.remote.UsuarioRepository;
import alvarezcruz.abraham.rsanalytics.ui.MainActivity;
import alvarezcruz.abraham.rsanalytics.utils.Constantes;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginFragment extends Fragment {

    public static final String TAG_NAME = LoginFragment.class.getName();

    private UsuarioModel usuarioModel;

    private Runnable onRegistrarseListener;
    private Runnable onLogueadoListener;

    private TextInputEditText inputCorreo;
    private TextInputEditText inputContrasenia;
    private AppCompatTextView textViewErrorCorreo;
    private AppCompatTextView textViewErrorContrasenia;
    private MaterialCardView animacionCargando;
    private MaterialCardView cardViewInputs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container,  false);

        usuarioModel = new ViewModelProvider(this).get(UsuarioModel.class);

        Button botonLogin = view.findViewById(R.id.botonLogin);
        AppCompatTextView botonRegistrarse = view.findViewById(R.id.botonRegistrarse);
        textViewErrorCorreo =  view.findViewById(R.id.errorCorreo);
        textViewErrorContrasenia =  view.findViewById(R.id.errorContrasenia);
        inputCorreo = view.findViewById(R.id.inputCorreo);
        inputContrasenia = view.findViewById(R.id.inputContrasenia);

        animacionCargando = view.findViewById(R.id.cargaLogin);
        cardViewInputs = view.findViewById(R.id.cardViewInputs);

        botonLogin.setOnClickListener(this::loguearse);
        botonRegistrarse.setOnClickListener(this::registrarse);

        return view;
    }


    public void setOnRegistrarseListener(Runnable onRegistrarseListener){
        this.onRegistrarseListener = onRegistrarseListener;
    }

    public void setOnLogueadoListener(Runnable onLogueadoListener){
        this.onLogueadoListener = onLogueadoListener;
    }


    private void registrarse(View v){
        if (onRegistrarseListener != null){
            onRegistrarseListener.run();
        }
    }

    private void loguearse(View v){

        esconderTextosError();

        String correo = inputCorreo.getText().toString().trim();
        String contrasenia = inputContrasenia.getText().toString().trim();

        if (!comprobarCamposValidos(contrasenia, correo)){
            return;
        }

        mostrarCarga();

        // Obtenemos el token del servidor y lo guardamos
        usuarioModel.getTokenRemotoYGuardarEnLocal(correo,contrasenia)
                .subscribeOn(Schedulers.newThread())
                .subscribe(par -> {

                    esconderCarga();

                    // 200
                    if (par.first >= 200 && par.first < 300){
                        if (onLogueadoListener != null){
                            onLogueadoListener.run();
                        }
                    }

                    // 400
                    if (par.first >= 400 && par.first < 500){

                        Snackbar snackbar = Snackbar.make(getView(), getString(R.string.fraglog_error_prueba_otra_combinacion),
                                Snackbar.LENGTH_LONG);
                        snackbar.setTextColor(getResources().getColor(R.color.colorSecondary));
                        snackbar.show();
                    }

                    // 500
                    if (par.first >= 500 && par.first < 600){
                        Snackbar snackbar = Snackbar.make(getView(), getString(R.string.fraglog_error_inesperado),
                                Snackbar.LENGTH_LONG);
                        snackbar.setTextColor(getResources().getColor(R.color.colorSecondary));
                        snackbar.show();
                    }

                }, error -> {
                    error.printStackTrace();
                });

    }


    private void mostrarCarga(){
        animacionCargando.setVisibility(View.VISIBLE);
        cardViewInputs.setVisibility(View.GONE);
    }

    private void esconderCarga(){
        animacionCargando.setVisibility(View.GONE);
        cardViewInputs.setVisibility(View.VISIBLE);
    }

    private void esconderTextosError(){
        textViewErrorCorreo.setVisibility(View.INVISIBLE);
        textViewErrorCorreo.setText("");
        textViewErrorContrasenia.setVisibility(View.INVISIBLE);
        textViewErrorContrasenia.setText("");
    }

    private boolean comprobarCamposValidos(String contrasenia, String correo){

        if (correo.length() == 0){
            textViewErrorCorreo.setText(getString(R.string.fraglog_error_introduzca_correo));
            textViewErrorCorreo.setVisibility(View.VISIBLE);
            return false;
        }

        if (contrasenia.length() == 0){
            textViewErrorContrasenia.setText(getString(R.string.fraglog_error_introduzca_contrasenia));
            textViewErrorContrasenia.setVisibility(View.VISIBLE);
            return false;
        }

        // Comprobamos que el correo sea valido
        Pattern patronCorreo = Pattern.compile(Constantes.REGEX_CORREO);
        Matcher matcherCorreo = patronCorreo.matcher(correo);
        if (!matcherCorreo.find()){
            textViewErrorCorreo.setText(getString(R.string.fraglog_error_correo_invalido));
            textViewErrorCorreo.setVisibility(View.VISIBLE);
            return false;
        }

        return true;
    }
}

