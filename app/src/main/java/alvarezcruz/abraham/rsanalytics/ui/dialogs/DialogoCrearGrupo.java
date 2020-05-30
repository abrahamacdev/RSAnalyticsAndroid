package alvarezcruz.abraham.rsanalytics.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.model.pojo.Usuario;
import alvarezcruz.abraham.rsanalytics.model.repository.local.UsuarioModel;
import alvarezcruz.abraham.rsanalytics.ui.notificaciones.NotificacionesFragment;
import alvarezcruz.abraham.rsanalytics.utils.Constantes;
import alvarezcruz.abraham.rsanalytics.utils.Utils;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DialogoCrearGrupo extends AlertDialog implements View.OnClickListener {

    public static final String TAG_NAME = DialogoCrearGrupo.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private UsuarioModel usuarioModel;
    private Activity activity;

    private LottieAnimationView animacionCarga;
    private RelativeLayout contenedorDatosGrupo;
    private RelativeLayout contenedorResCreacion;

    private TextInputLayout inputLayoutNombreGrupo;
    private TextInputEditText inputNombreGrupo;
    private AppCompatButton botonEnviarDatos;

    private LottieAnimationView animacionResCreacion;
    private AppCompatTextView tvResultadoAccion;

    public DialogoCrearGrupo(Activity activity, UsuarioModel usuarioModel) {
        super(activity);
        this.activity = activity;
        this.usuarioModel = usuarioModel;

        // Cargamos el layout y los elementos de la vista
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View view = factory.inflate(R.layout.dialogo_crear_grupo, null);
        super.setView(view);
        initViews(view);
    }

    @Override
    public void show() {
        super.show();

        Context context = activity.getApplicationContext();
        this.getWindow().setLayout((int) context.getResources().getDimension(R.dimen.diacregru_ancho_contenedor), (int) context.getResources().getDimension(R.dimen.diacregru_alto_contenedor));
    }

    private void initViews(View view){

        animacionCarga = view.findViewById(R.id.animacionCarga);
        contenedorDatosGrupo = view.findViewById(R.id.contenedorDatosGrupo);
        contenedorResCreacion = view.findViewById(R.id.contenedorResCreacion);

        inputLayoutNombreGrupo = view.findViewById(R.id.inputLayoutNombreGrupo);
        inputNombreGrupo = view.findViewById(R.id.inputNombreGrupo);
        botonEnviarDatos = view.findViewById(R.id.botonEnviarDatosCreacion);

        animacionResCreacion = view.findViewById(R.id.animacionResCreacion);
        tvResultadoAccion = view.findViewById(R.id.textoResultadoAccion);

        botonEnviarDatos.setOnClickListener(this);

    }

    private void mostrarAnimacionCarga(){

        animacionCarga.setVisibility(View.VISIBLE);
        contenedorDatosGrupo.setVisibility(View.GONE);
        contenedorResCreacion.setVisibility(View.GONE);
    }

    private void crearGrupo(){

        // Mostramos la animacion de carga
        mostrarAnimacionCarga();

        // Validamos el nombre del grupo
        if (!validarNombreGrupo()){
            animacionCarga.setVisibility(View.GONE);
            contenedorDatosGrupo.setVisibility(View.VISIBLE);
            return;
        }

        // Enviamos la peticion
        usuarioModel.crearGrupoAsync(inputNombreGrupo.getText().toString().trim())
                .subscribe(status -> {

                    if (status == 200){
                        mostrarResultadoCreacion(0);
                    }

                    else if (status == 409){
                        mostrarResultadoCreacion(1);
                    }

                    else {
                        mostrarResultadoCreacion(2);
                    }


                }, error -> mostrarResultadoCreacion(2));

    }

    private boolean validarNombreGrupo(){

        // Escondemos el teclado y los errores que halla podido tener el usuario
        inputNombreGrupo.clearFocus();
        Utils.esconderTeclado(getContext(), inputNombreGrupo);
        inputLayoutNombreGrupo.setErrorEnabled(false);

        String nombreGrupo = inputNombreGrupo.getText().toString().trim();

        // No ha escrito nada
        if (nombreGrupo.length() == 0){
            inputLayoutNombreGrupo.setErrorEnabled(true);
            inputNombreGrupo.setError(getContext().getString(R.string.diacregru_introduce_nombre));
            return false;
        }

        // La longitud supera el maximo permitido
        if (nombreGrupo.length() > Constantes.LONGITUD_MAX_NOMBRE_GRUPO){
            inputLayoutNombreGrupo.setErrorEnabled(true);
            inputNombreGrupo.setError(getContext().getString(R.string.diacregru_maximo_caracteres));
            return false;
        }

        // COntiene caracteres no validos
        Matcher matcher = Pattern.compile(Constantes.REGEX_NOMBRE_GRUPO).matcher(nombreGrupo);
        if (matcher.find()){
            inputLayoutNombreGrupo.setErrorEnabled(true);
            inputNombreGrupo.setError(getContext().getString(R.string.diacregru_caracteres_no_permitidos));
            return false;
        }

        return true;
    }

    private void mostrarResultadoCreacion(int res){

        animacionCarga.setVisibility(View.GONE);

        logger.log(Level.SEVERE, "Resultado - " + res);

        // Toodo salio bien
        if (res == 0){
            tvResultadoAccion.setText(getContext().getString(R.string.diacregru_creacion_grupo_exitosa));
            animacionResCreacion.setAnimation("animacion_operacion_exitosa.json");
            contenedorResCreacion.setVisibility(View.VISIBLE);
            esconderDialogo();
        }

        // Ya existe un grupo con ese nombre
        else if (res == 1){
            inputLayoutNombreGrupo.setErrorEnabled(true);
            inputNombreGrupo.setError(getContext().getString(R.string.diacregru_existe_nombre_grupo));
            contenedorDatosGrupo.setVisibility(View.VISIBLE);
        }

        // Algo salio mal
        else {
            tvResultadoAccion.setText(getContext().getString(R.string.diacregru_creacion_grupo_fallida));
            animacionResCreacion.setAnimation("animacion_operacion_fallida.json");
            contenedorResCreacion.setVisibility(View.VISIBLE);
            esconderDialogo();
        }
    }


    private void esconderDialogo(){
        Observable.create(ObservableEmitter::onComplete)
                .delay(3, TimeUnit.SECONDS)
                .subscribe((o) -> {}, e -> {}, () -> {
                    activity.runOnUiThread(this::dismiss);
                });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.botonEnviarDatosCreacion:
                crearGrupo();
                break;
        }
    }
}
