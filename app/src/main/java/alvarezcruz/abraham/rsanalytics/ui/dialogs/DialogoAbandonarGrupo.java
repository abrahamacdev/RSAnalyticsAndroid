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
import androidx.appcompat.widget.AppCompatTextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.button.MaterialButton;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.model.repository.local.UsuarioModel;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;

public class DialogoAbandonarGrupo extends AlertDialog {

    public static final String TAG_NAME = DialogoAbandonarGrupo.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private UsuarioModel usuarioModel;
    private Activity activity;
    private boolean responsable;

    private LottieAnimationView animacionCarga;
    private RelativeLayout contenedorMensaje;
    private RelativeLayout contenedorResAbandono;

    private AppCompatTextView tvAvisoPequenio;
    private AppCompatButton botonCancelar;
    private AppCompatButton botonAbandonar;

    public DialogoAbandonarGrupo(Activity activity, UsuarioModel usuarioModel, boolean responsable){
        super(activity);
        this.activity = activity;
        this.usuarioModel = usuarioModel;
        this.responsable = responsable;

        // Cargamos el layout y los elementos de la vista
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View view = factory.inflate(R.layout.dialogo_abandono_grupo, null);
        super.setView(view);
        initViews(view);
    }

    @Override
    public void show() {
        super.show();

        Context context = activity.getApplicationContext();
        this.getWindow().setLayout((int) context.getResources().getDimension(R.dimen.diaabagru_ancho_contenedor), (int) context.getResources().getDimension(R.dimen.diaabagru_alto_contenedor));
    }

    private void initViews(View view){

        // Contenedores
        animacionCarga = view.findViewById(R.id.animacionCarga);
        contenedorMensaje = view.findViewById(R.id.contenedorMensaje);
        contenedorResAbandono = view.findViewById(R.id.contenedorResAbandono);

        // Elementos interaccion
        tvAvisoPequenio = view.findViewById(R.id.textoAvisoPequenio);
        botonCancelar = view.findViewById(R.id.botonCancelar);
        botonAbandonar = view.findViewById(R.id.botonAbandonar);

        if (responsable){
            tvAvisoPequenio.setVisibility(View.VISIBLE);
        }

        botonCancelar.setOnClickListener((v) -> this.cancel());
        botonAbandonar.setOnClickListener(this::abandonar);
    }

    private void mostrarError(){

        animacionCarga.setVisibility(View.GONE);
        contenedorResAbandono.setVisibility(View.VISIBLE);

        Observable.create(ObservableEmitter::onComplete)
                .delay(3, TimeUnit.SECONDS)
                .subscribe((o) -> {}, e -> {}, () -> {
                    activity.runOnUiThread(this::dismiss);
                });
    }

    private void abandonar(View v){

        animacionCarga.setVisibility(View.VISIBLE);
        contenedorMensaje.setVisibility(View.GONE);

        usuarioModel.abandonarGrupoAsync()
                .subscribe(status -> {

                    if (status == 200){
                        this.dismiss();
                    }

                    else {
                        mostrarError();
                    }

                }, error -> mostrarError());

    }

}
