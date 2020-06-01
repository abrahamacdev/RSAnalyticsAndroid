package alvarezcruz.abraham.rsanalytics.ui.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputLayout;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.adapters.MunicipiosAdapter;
import alvarezcruz.abraham.rsanalytics.model.repository.local.UsuarioModel;
import alvarezcruz.abraham.rsanalytics.utils.TipoContrato;
import alvarezcruz.abraham.rsanalytics.utils.TipoInmueble;
import alvarezcruz.abraham.rsanalytics.utils.Utils;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class DialogoCrearInforme extends AlertDialog {

    public static final String TAG_NAME = DialogoCrearGrupo.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private UsuarioModel usuarioModel;
    private Activity activity;

    private LottieAnimationView animacionCarga;
    private RelativeLayout contenedorDatosInforme;
    private RelativeLayout contenedorResCreacion;

    private MaterialSpinner spnTipoInmueble, spnTipoContrato;
    private TextInputLayout tilTextoMunicipio;
    private AppCompatAutoCompleteTextView actvMunicipio;
    private MunicipiosAdapter municipiosAdapter;
    private LottieAnimationView animacionCargaTexto;
    private AppCompatButton botonEnviarDatos;

    private LottieAnimationView animacionResCreacion;
    private AppCompatTextView tvResultadoAccion;

    // Nos ayudara a saber si ha escogido uno de los municipios de sugerencia
    private String municipioSugerenciaSeleccionado;

    public DialogoCrearInforme(Activity activity, UsuarioModel usuarioModel) {
        super(activity);
        this.activity = activity;
        this.usuarioModel = usuarioModel;

        // Cargamos el layout y los elementos de la vista
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View view = factory.inflate(R.layout.dialogo_crear_informe, null);
        super.setView(view);
        initViews(view);
    }

    @Override
    public void show() {
        super.show();

        Context context = activity.getApplicationContext();
        this.getWindow().setLayout((int) context.getResources().getDimension(R.dimen.diacreinf_ancho_contenedor), (int) context.getResources().getDimension(R.dimen.diacreinf_alto_contenedor));
    }

    private void initViews(View view){

        // Adapter para las sugestiones del AutoCompleteTV Municipio
        municipiosAdapter = new MunicipiosAdapter(activity);

        // Layouts principales
        animacionCarga = view.findViewById(R.id.animacionCarga);
        contenedorDatosInforme = view.findViewById(R.id.contenedorDatosInforme);
        contenedorResCreacion = view.findViewById(R.id.contenedorResCreacion);

        // Inputs del dialogo
        spnTipoInmueble = view.findViewById(R.id.spinnerTipoInmueble);
        spnTipoInmueble.setItems(activity.getResources().getStringArray(R.array.diacreinf_opciones_tipos_inmuebles));

        spnTipoContrato = view.findViewById(R.id.spinnerTipoContrato);
        spnTipoContrato.setItems(activity.getResources().getStringArray(R.array.diacreinf_opciones_tipos_contratos));

        tilTextoMunicipio = view.findViewById(R.id.inputLayoutMunicipio);
        actvMunicipio = view.findViewById(R.id.autoCompleteMunicipio);
        actvMunicipio.setAdapter(municipiosAdapter);
        actvMunicipio.addTextChangedListener(getMunicipioTextWatcher());
        actvMunicipio.setOnItemClickListener(this::municipioSugerenciaSeleccionado);

        animacionCargaTexto = view.findViewById(R.id.animacionCargaTexto);
        botonEnviarDatos = view.findViewById(R.id.botonEnviarDatosCreacion);
        botonEnviarDatos.setOnClickListener(this::enviarSolicitud);

        // Respuesta peticion
        animacionResCreacion = view.findViewById(R.id.animacionResCreacion);
        tvResultadoAccion = view.findViewById(R.id.textoResultadoAccion);
    }

    private void mostrarAnimacionCarga(){

        animacionCarga.setVisibility(View.VISIBLE);
        contenedorDatosInforme.setVisibility(View.GONE);
        contenedorResCreacion.setVisibility(View.GONE);
    }

    private void enviarSolicitud(View v){

        // Mostramos la animacion de carga
        mostrarAnimacionCarga();

        // Validamos el nombre del grupo
        if (!validarCamposSolicitud()){
            animacionCarga.setVisibility(View.GONE);
            contenedorDatosInforme.setVisibility(View.VISIBLE);
            return;
        }

        TipoContrato tipoContrato = TipoContrato.conId(spnTipoContrato.getSelectedIndex() + 1);
        TipoInmueble tipoInmueble = TipoInmueble.conId(spnTipoInmueble.getSelectedIndex() + 1);

        // Enviamos la peticion
        usuarioModel.solicitarInforme(tipoContrato, tipoInmueble, municipioSugerenciaSeleccionado)
                .subscribe(status -> {

                    if (status == 200){
                        mostrarResultadoCreacion(true);
                    }

                    else {
                        mostrarResultadoCreacion(false);
                    }


                }, error -> mostrarResultadoCreacion(false));

    }


    private boolean validarCamposSolicitud(){

        // Escondemos el teclado y los errores que halla podido tener el usuario
        tilTextoMunicipio.setError(null);

        // No ha seleccionado una sugerencia de municipio
        if (municipioSugerenciaSeleccionado == null){
            tilTextoMunicipio.setError(activity.getString(R.string.diacreinf_seleccione_municipio));
            return false;
        }

        return true;
    }

    private void mostrarResultadoCreacion(boolean exitoso){

        animacionCarga.setVisibility(View.GONE);

        // Toodo salio bien
        if (exitoso){
            tvResultadoAccion.setText(getContext().getString(R.string.diacreinf_solicitud_exitosa));
            animacionResCreacion.setAnimation("animacion_operacion_exitosa.json");
            contenedorResCreacion.setVisibility(View.VISIBLE);
            esconderDialogo();
        }

        // Algo salio mal
        else {
            tvResultadoAccion.setText(getContext().getString(R.string.diacreinf_solicitud_fallida));
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


    private void municipioSugerenciaSeleccionado(AdapterView<?> adapterView, View view, int position, long id){
        String municipio = (String) adapterView.getItemAtPosition(position);
        this.municipioSugerenciaSeleccionado = municipio;
    }

    private TextWatcher getMunicipioTextWatcher(){
        return new TextWatcher() {

            private static final int CODIGO_MENSAJE = 100;
            private static final int RETARDO_ENTRE_MENSAJES = 750;

            private Disposable subscripcion;
            private Observer<Pair<Integer, ArrayList<String>>> observer = new Observer<Pair<Integer, ArrayList<String>>>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {
                    subscripcion = d;
                }

                @Override
                public void onNext(@NonNull Pair<Integer, ArrayList<String>> integerArrayListPair) {
                    activity.runOnUiThread(() -> {

                        boolean hayDisponibles = false;

                        // Si hubo resultados actualizaremos la lista
                        if (integerArrayListPair.first  == 200){
                            municipiosAdapter.actualizarTodos(integerArrayListPair.second);
                            Utils.esconderTeclado(activity, actvMunicipio);
                            hayDisponibles = true;
                        }

                        // Escondemos el indicador de carga del texto
                        if (animacionCargaTexto != null) {
                            animacionCargaTexto.setVisibility(View.GONE);
                        }

                        if (!hayDisponibles){
                            tilTextoMunicipio.setErrorEnabled(true);
                            tilTextoMunicipio.setError(activity.getString(R.string.diacreinf_sugerencias_no_disponibles));
                        }

                    });
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onComplete() {}
            };

            private final Handler mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    realizarConsulta(((SpannableStringBuilder) msg.obj).toString());
                }
            };

            private void realizarConsulta(String patron){
                // Cancelamos la subscripcion anterior
                if (subscripcion != null && !subscripcion.isDisposed()){
                    subscripcion.dispose();
                }

                // Consultamos los municipios
                usuarioModel.consultarMunicipios(patron)
                        .subscribe(observer);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (!municipiosAdapter.contiene(s.toString())){
                    // Tendra que seleccionar una sugerencia para poder realizar el informe
                    municipioSugerenciaSeleccionado = null;

                    if (s.length() >= 2){
                        animacionCargaTexto.setVisibility(View.VISIBLE);
                        mHandler.removeMessages(CODIGO_MENSAJE);
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(CODIGO_MENSAJE, s), RETARDO_ENTRE_MENSAJES);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

}