package alvarezcruz.abraham.rsanalytics.ui.registro;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.model.repository.local.UsuarioModel;
import alvarezcruz.abraham.rsanalytics.model.repository.remote.UsuarioRepository;
import alvarezcruz.abraham.rsanalytics.utils.Constantes;

public class RegistroFragment extends Fragment {

    public static final String TAG_NAME = RegistroFragment.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private UsuarioRepository usuarioRepository;
    private UsuarioModel usuarioModel;

    private Runnable onRegistradoListener;
    private Runnable onRegistradoYLogueadoListener;

    private TextInputEditText inputNombre, inputPrimerApellido, inputSegundoApellido;
    private RadioGroup radioGroupGenero;
    private TextInputEditText inputTelefono;
    private TextInputEditText inputCorreo;
    private TextInputEditText inputContrasenia, inputRepetirContrasenia;

    private AppCompatTextView errorNombre, errorPrimerApellido, errorSegundoApellido;
    private AppCompatTextView errorGenero;
    private AppCompatTextView errorTelefono;
    private AppCompatTextView errorCorreo;
    private AppCompatTextView errorContrasenia, errorRepetirContrasenia;

    private AppCompatButton botonRegistrarse;

    private MaterialCardView cardViewInputs, cardViewCarga;
    private ScrollView scrollView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registro, container, false);

        usuarioRepository = new UsuarioRepository(getContext());
        usuarioModel = new ViewModelProvider(this).get(UsuarioModel.class);

        cargarViews(view);

        return view;
    }

    public void cargarViews(View view){

        // Inputs
        inputNombre = view.findViewById(R.id.inputNombre);
        inputPrimerApellido = view.findViewById(R.id.inputPrimerApellido);
        inputSegundoApellido = view.findViewById(R.id.inputSegundoApellido);
        radioGroupGenero = view.findViewById(R.id.radioGroupGenero);
        inputTelefono = view.findViewById(R.id.inputTelefono);
        inputCorreo = view.findViewById(R.id.inputCorreo);
        inputContrasenia = view.findViewById(R.id.inputContrasenia);
        inputRepetirContrasenia = view.findViewById(R.id.inputRepetirContrasenia);

        // Mensajes error
        errorNombre = view.findViewById(R.id.errorNombre);
        errorPrimerApellido = view.findViewById(R.id.errorPrimerApellido);
        errorSegundoApellido = view.findViewById(R.id.errorSegundoApellido);
        errorGenero = view.findViewById(R.id.errorGenero);
        errorTelefono = view.findViewById(R.id.errorTelefono);
        errorCorreo = view.findViewById(R.id.errorCorreo);
        errorContrasenia = view.findViewById(R.id.errorContrasenia);
        errorRepetirContrasenia = view.findViewById(R.id.errorRepetirContrasenia);

        // Boton Registrarse
        botonRegistrarse = view.findViewById(R.id.botonRegistrarse);
        botonRegistrarse.setOnClickListener(this::registrarse);

        // CardViews
        cardViewInputs = view.findViewById(R.id.cardViewInputs);
        cardViewCarga = view.findViewById(R.id.cardViewCarga);

        // ScrollView
        scrollView = view.findViewById(R.id.scrollView);
    }

    private void registrarse(View v){

        esconderTextosError();

        mostrarCarga();

        Pair<Boolean,HashMap<String,Object>> paramsCamposReg = comprobarCamposRegistro();

        // Alguno de los campos esta mal o falta algun campo
        if (!paramsCamposReg.first){
            esconderCarga();
            return;
        }

        usuarioRepository.realizarRegistro(paramsCamposReg.second)
                .subscribe(codStatus -> {

                    // Ocurrio un error
                    if (codStatus != 200){

                        esconderCarga();

                        Snackbar snackbar = Snackbar.make(getView(), getString(R.string.fraglog_error_inesperado),
                                Snackbar.LENGTH_LONG);
                        snackbar.setTextColor(getResources().getColor(R.color.colorSecondary));
                        snackbar.show();
                    }

                    // Realizaremos login automatico
                    else  {

                        String correo = (String) paramsCamposReg.second.get("correo");
                        String contrasenia = (String) paramsCamposReg.second.get("contrasenia");

                        usuarioModel.getTokenRemotoYGuardarEnLocal(correo, contrasenia)
                                .subscribe(par -> {

                                    esconderCarga();

                                    // 2xx
                                    if (par.first >= 200 && par.first < 300){
                                        if (onRegistradoYLogueadoListener != null){
                                            onRegistradoYLogueadoListener.run();
                                        }
                                    }

                                    // Desconocido
                                    else {
                                        if (onRegistradoListener != null){
                                            onRegistradoListener.run();
                                        }
                                    }
                                });
                    }

                }, error -> {
                    error.printStackTrace();
                });

    }

    private void mostrarCarga(){
        cardViewCarga.setVisibility(View.VISIBLE);
        cardViewInputs.setVisibility(View.GONE);
    }

    private void esconderCarga(){
        cardViewCarga.setVisibility(View.GONE);
        cardViewInputs.setVisibility(View.VISIBLE);
    }

    private void esconderTextosError(){

        errorNombre.setText("");
        errorNombre.clearFocus();
        errorNombre.setVisibility(View.INVISIBLE);

        errorPrimerApellido.setText("");
        errorPrimerApellido.clearFocus();
        errorPrimerApellido.setVisibility(View.INVISIBLE);

        errorSegundoApellido.setText("");
        errorSegundoApellido.clearFocus();
        errorSegundoApellido.setVisibility(View.INVISIBLE);

        errorGenero.setText("");
        errorGenero.clearFocus();
        errorGenero.setVisibility(View.INVISIBLE);

        errorTelefono.setText("");
        errorTelefono.clearFocus();
        errorTelefono.setVisibility(View.INVISIBLE);

        errorCorreo.setText("");
        errorCorreo.clearFocus();
        errorCorreo.setVisibility(View.INVISIBLE);

        errorContrasenia.setText("");
        errorContrasenia.clearFocus();
        errorContrasenia.setVisibility(View.INVISIBLE);

        errorRepetirContrasenia.setText("");
        errorRepetirContrasenia.clearFocus();
        errorRepetirContrasenia.setVisibility(View.INVISIBLE);
    }

    private Pair<Boolean, HashMap<String,Object>> comprobarCamposRegistro(){

        HashMap<String,Object> valores = new HashMap<>();

        // Nombre
        String nombre = inputNombre.getText().toString().trim();
        valores.put("nombre", nombre);
        Matcher matcherNombre = Pattern.compile(Constantes.REGEX_NOMBRE_PERSONA).matcher(nombre);
        if (nombre.length() == 0 || !matcherNombre.find()){
            errorNombre.setText(getString(R.string.fragreg_error_introduzca_nombre));
            errorNombre.setVisibility(View.VISIBLE);
            errorNombre.requestFocus();
            scrollView.scrollTo(0,0);
            return new Pair<>(false, null);
        }

        // Primer apellido
        String primerApellido = inputPrimerApellido.getText().toString().trim();
        valores.put("primerApellido", primerApellido);
        if (primerApellido.length() == 0){
            errorPrimerApellido.setText(getString(R.string.fragreg_error_introduzca_primer_apellido));
            errorPrimerApellido.setVisibility(View.VISIBLE);
            errorPrimerApellido.requestFocus();
            scrollView.scrollTo(0, errorPrimerApellido.getScrollY());
            return new Pair<>(false, null);
        }
        else {

            Matcher matcher = Pattern.compile(Constantes.REGEX_NOMBRE_PERSONA).matcher(primerApellido);
            if (!matcher.find()){
                errorPrimerApellido.setText(getString(R.string.fragreg_error_introduzca_apellido_valido));
                errorPrimerApellido.setVisibility(View.VISIBLE);
                errorPrimerApellido.requestFocus();
                scrollView.scrollTo(0, errorPrimerApellido.getScrollY());
                return new Pair<>(false, null);
            }
        }

        // Segundo apellido
        String segundoApellido = inputSegundoApellido.getText().toString().trim();
        valores.put("segundoApellido", segundoApellido);
        if (segundoApellido.length() == 0){
            errorSegundoApellido.setText(getString(R.string.fragreg_error_introduzca_segundo_apellido));
            errorSegundoApellido.setVisibility(View.VISIBLE);
            errorSegundoApellido.requestFocus();
            scrollView.scrollTo(0, errorSegundoApellido.getScrollY());
            return new Pair<>(false, null);
        }
        else {

            Matcher matcher = Pattern.compile(Constantes.REGEX_NOMBRE_PERSONA).matcher(segundoApellido);
            if (!matcher.find()){
                errorSegundoApellido.setText(getString(R.string.fragreg_error_introduzca_apellido_valido));
                errorSegundoApellido.setVisibility(View.VISIBLE);
                errorSegundoApellido.requestFocus();
                scrollView.scrollTo(0, errorSegundoApellido.getScrollY());
                return new Pair<>(false, null);
            }
        }

        // Genero
        int indexGenero = radioGroupGenero.getCheckedRadioButtonId();
        String genero = "";
        if (indexGenero == -1){
            errorGenero.setText(getString(R.string.fragreg_error_introduzca_genero));
            errorGenero.setVisibility(View.VISIBLE);
            errorGenero.requestFocus();
            scrollView.scrollTo(0, radioGroupGenero.getScrollY());
            return new Pair<>(false, null);
        }
        else {
            genero = indexGenero == 0 ? "M" : "H";
            valores.put("genero", genero);
        }


        // Telefono
        String telefono = inputTelefono.getText().toString().trim();
        valores.put("telefono", telefono);
        if (telefono.length() == 0){
            errorTelefono.setText(getString(R.string.fragreg_error_introduzca_telefono));
            errorTelefono.setVisibility(View.VISIBLE);
            errorTelefono.requestFocus();
            return new Pair<>(false, null);
        }
        else {

            Matcher matcher = Pattern.compile(Constantes.REGEX_TELEFONO).matcher(telefono);
            if (!matcher.find()){
                errorTelefono.setText(getString(R.string.fragreg_error_introduzca_telefono_valido));
                errorTelefono.setVisibility(View.VISIBLE);
                errorTelefono.requestFocus();
                return new Pair<>(false, null);
            }
        }

        // Correo
        String correo = inputCorreo.getText().toString().trim();
        valores.put("correo", correo);
        if (correo.length() == 0){
            errorCorreo.setText(getString(R.string.fragreg_error_introduzca_correo));
            errorCorreo.setVisibility(View.VISIBLE);
            errorCorreo.requestFocus();
            return new Pair<>(false, null);
        }
        else {

            Matcher matcher = Pattern.compile(Constantes.REGEX_CORREO).matcher(correo);
            if (!matcher.find()){
                errorCorreo.setText(getString(R.string.fragreg_error_introduzca_correo_valido));
                errorCorreo.setVisibility(View.VISIBLE);
                errorCorreo.requestFocus();
                return new Pair<>(false, null);
            }
        }

        // Contrasenias
        String contrasenia = inputContrasenia.getText().toString().trim();
        valores.put("contrasenia", contrasenia);
        String repetirContrasenia = inputRepetirContrasenia.getText().toString().trim();
        if (contrasenia.length() == 0){
            errorContrasenia.setText(getString(R.string.fragreg_error_introduzca_contrasenia));
            errorContrasenia.setVisibility(View.VISIBLE);
            errorContrasenia.requestFocus();
            return new Pair<>(false, null);
        }

        if (repetirContrasenia.length() == 0){
            errorRepetirContrasenia.setText(getString(R.string.fragreg_error_introduzca_repetir_contrasenia));
            errorRepetirContrasenia.setVisibility(View.VISIBLE);
            errorRepetirContrasenia.requestFocus();
            return new Pair<>(false, null);
        }

        if (!contrasenia.equals(repetirContrasenia)){
            inputRepetirContrasenia.setText("");
            errorRepetirContrasenia.setText(getString(R.string.fragreg_error_introduzca_repetir_contrasenia_concuerden));
            errorRepetirContrasenia.setVisibility(View.VISIBLE);
            errorRepetirContrasenia.requestFocus();
            return new Pair<>(false, null);
        }

        Matcher matcherContrasenia = Pattern.compile(Constantes.REGEX_CONTRASENIA).matcher(contrasenia);
        if (!matcherContrasenia.find()){
            inputContrasenia.setText("");
            inputRepetirContrasenia.setText("");
            errorContrasenia.setText(getString(R.string.fragreg_error_introduzca_contrasenia_valido));
            errorContrasenia.setVisibility(View.VISIBLE);
            errorContrasenia.requestFocus();
            return new Pair<>(false, null);
        }

        return new Pair<>(true, valores);
    }


    public void setOnRegistradoListener(Runnable onRegistradoListener){
        this.onRegistradoListener = onRegistradoListener;
    }

    public void setOnRegistradoYLogueadoListener(Runnable onRegistradoYLogueadoListener){
        this.onRegistradoYLogueadoListener = onRegistradoYLogueadoListener;
    }

}
