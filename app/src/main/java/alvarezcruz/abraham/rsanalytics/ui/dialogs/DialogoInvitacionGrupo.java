package alvarezcruz.abraham.rsanalytics.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.model.repository.local.UsuarioModel;
import alvarezcruz.abraham.rsanalytics.utils.Constantes;
import alvarezcruz.abraham.rsanalytics.utils.Utils;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;

public class DialogoInvitacionGrupo extends AlertDialog implements View.OnClickListener {

    public static final String TAG_NAME = DialogoInvitacionGrupo.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private UsuarioModel usuarioModel;
    private Activity activity;


    private LottieAnimationView animacionCarga;
    private RelativeLayout contenedorDatosCorreo;
    private RelativeLayout contenedorResInvitacion;

    private TextInputLayout inputLayoutCorreo;
    private TextInputEditText inputCorreo;
    private AppCompatImageView imagenCorreo;
    private AppCompatButton botonEnviarInvitacion;

    private AppCompatTextView tvCorreo;
    private LottieAnimationView animacionResInvitacion;

    public DialogoInvitacionGrupo(Activity activity, UsuarioModel usuarioModel) {
        super(activity);
        this.activity = activity;
        this.usuarioModel = usuarioModel;

        // Cargamos el layout y los elementos de la vista
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View view = factory.inflate(R.layout.dialogo_invitacion_grupo, null);
        super.setView(view);
        initViews(view);
    }

    @Override
    public void show() {
        super.show();

        Context context = activity.getApplicationContext();
        this.getWindow().setLayout((int) context.getResources().getDimension(R.dimen.diainvgru_ancho_contenedor), (int) context.getResources().getDimension(R.dimen.diainvgru_alto_contenedor));
    }

    private void initViews(View view){

        // Contenedores
        animacionCarga = view.findViewById(R.id.animacionCarga);
        contenedorDatosCorreo = view.findViewById(R.id.contenedorDatosCorreo);
        contenedorResInvitacion = view.findViewById(R.id.contenedorResInvitacion);

        // Campos contenedor de datos
        imagenCorreo = view.findViewById(R.id.imagenCorreo);
        botonEnviarInvitacion = view.findViewById(R.id.botonEnviarInvitacion);
        botonEnviarInvitacion.setOnClickListener(this);
        inputLayoutCorreo = view.findViewById(R.id.inputLayoutCorreoUsuario);
        inputCorreo = view.findViewById(R.id.inputCorreoUsuario);
        inputCorreo.addTextChangedListener(new TextWatcher() {

            private TimerTask task;
            private Timer timer;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                activity.runOnUiThread(() -> validarCorreo(false));
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        // Campos contenedor resultado
        tvCorreo = view.findViewById(R.id.textoResultadoAccion);
        animacionResInvitacion = view.findViewById(R.id.animacionResInvitacion);
    }

    private void mostrarAnimacionCarga(){
        animacionCarga.setVisibility(View.VISIBLE);
        contenedorDatosCorreo.setVisibility(View.GONE);
        contenedorResInvitacion.setVisibility(View.GONE);
    }

    private void invitarUsuario(){

        // Mostramos la animacion de carga
        mostrarAnimacionCarga();

        // Validamos el nombre del grupo
        if (!validarCorreo(true)){
            animacionCarga.setVisibility(View.GONE);
            contenedorDatosCorreo.setVisibility(View.VISIBLE);
            return;
        }

        // Enviamos la peticion
        usuarioModel.invitarUsuarioAsync(inputCorreo.getText().toString().trim())
                .subscribe(status -> {

                    // Se ha realizado la invitacion
                    if (status >= 200 && status < 300){
                        mostrarResultadoInvitacion(0);
                    }

                    // No se encontro a ningun usuario con ese correo
                    else if (status == 404){
                        mostrarResultadoInvitacion(1);
                    }

                    // Error desconocido
                    else {
                        mostrarResultadoInvitacion(2);
                    }


                }, error -> mostrarResultadoInvitacion(2));

    }

    private boolean validarCorreo(boolean desdeBoton){

        // Escondemos el teclado y los errores que halla podido tener el usuario
        inputCorreo.clearFocus();
        inputLayoutCorreo.setErrorEnabled(false);

        String correo = inputCorreo.getText().toString().trim();

        // No ha escrito nada
        if (correo.length() == 0){

            if (desdeBoton){
                inputLayoutCorreo.setErrorEnabled(true);
                inputCorreo.setError(getContext().getString(R.string.diainvgru_introduce_correo_valido));
            }

            imagenCorreo.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_linea));
            return false;
        }

        // COntiene caracteres no validos
        Matcher matcher = Pattern.compile(Constantes.REGEX_CORREO).matcher(correo);
        if (!matcher.find()){

            if (desdeBoton){
                inputLayoutCorreo.setErrorEnabled(true);
                inputCorreo.setError(getContext().getString(R.string.diainvgru_introduce_correo_valido));
            }

            imagenCorreo.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_cruz_roja));
            return false;
        }

        imagenCorreo.setImageDrawable(getContext().getResources().getDrawable(R.drawable.ic_tick));
        return true;
    }

    private void mostrarResultadoInvitacion(int res){

        animacionCarga.setVisibility(View.GONE);

        // Toodo salio bien
        if (res == 0){
            tvCorreo.setText(getContext().getString(R.string.diainvgru_invitacion_exitosa));
            animacionResInvitacion.setAnimation("animacion_operacion_exitosa.json");
            contenedorResInvitacion.setVisibility(View.VISIBLE);
            esconderDialogo();
        }

        // No existe ningun usuario con ese nombre
        else if (res == 1){
            inputLayoutCorreo.setErrorEnabled(true);
            inputCorreo.setError(getContext().getString(R.string.diainvgru_no_existe_usuario));
            contenedorDatosCorreo.setVisibility(View.VISIBLE);
        }

        // Algo salio mal
        else {
            tvCorreo.setText(getContext().getString(R.string.diainvgru_invitacion_fallida));
            animacionResInvitacion.setAnimation("animacion_operacion_fallida.json");
            contenedorResInvitacion.setVisibility(View.VISIBLE);
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

            case R.id.botonEnviarInvitacion:
                Utils.esconderTeclado(activity, inputCorreo);
                invitarUsuario();
                break;
        }
    }
}
