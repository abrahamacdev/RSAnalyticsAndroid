package alvarezcruz.abraham.rsanalytics.ui.notificaciones;

import android.os.Bundle;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.adapters.NotificacionesAdapter;
import alvarezcruz.abraham.rsanalytics.adapters.ViewHolders.AccionNotificacion;
import alvarezcruz.abraham.rsanalytics.adapters.decorators.EdgeDecorator;
import alvarezcruz.abraham.rsanalytics.model.pojo.notificaciones.Notificacion;
import alvarezcruz.abraham.rsanalytics.model.repository.local.UsuarioModel;
import alvarezcruz.abraham.rsanalytics.ui.menuPrincipal.MenuPrincipalActivity;
import alvarezcruz.abraham.rsanalytics.utils.RespuestaInvitacion;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;

public class NotificacionesFragment extends Fragment {

    public static final String TAG_NAME = NotificacionesFragment.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private UsuarioModel usuarioModel;
    private LiveData<ArrayList<Notificacion>> ldNotificaciones;

    private MenuPrincipalActivity menuPrincipalActivity;

    private ConstraintLayout contenedorAnimacion;
    private ConstraintLayout contenedorSinNotificaciones;
    private ConstraintLayout contenedorListadoNotificaciones;

    private RecyclerView recyclerView;
    private NotificacionesAdapter notificacionesAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    public NotificacionesFragment(){}

    public NotificacionesFragment(MenuPrincipalActivity menuPrincipalActivity, UsuarioModel usuarioModel){
        this.usuarioModel = usuarioModel;
        this.ldNotificaciones = usuarioModel.getNotificaciones();
        this.menuPrincipalActivity = menuPrincipalActivity;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notificaciones, container, false);

        initViews(view);

        setupNotificaciones();

        return view;
    }

    private void initViews(View view){

        contenedorSinNotificaciones = view.findViewById(R.id.contenedorSinNotificaciones);
        contenedorAnimacion = view.findViewById(R.id.contenedorAnimacion);
        contenedorListadoNotificaciones = view.findViewById(R.id.contenedorListadoNotificaciones);

        notificacionesAdapter = new NotificacionesAdapter(getContext());
        notificacionesAdapter.setOnAccionListener(onAccionListener());
        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new EdgeDecorator(getResources().getDimension(R.dimen.lisnot_padding_vertical_recycler)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(notificacionesAdapter);

        swipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(this::setupNotificaciones);
    }

    private void setupNotificaciones(){

        // MOstramos el indicador del swipeRefreshLayout si no se esta mostrando la
        // animacion de los circulitos
        if (contenedorAnimacion.getVisibility() != View.VISIBLE){
            swipeRefreshLayout.setRefreshing(true);
        }


        // Observamos las notificaciones
        ldNotificaciones.observe(menuPrincipalActivity, notificaciones -> {

            // Escondenmos la animacion por defecto si esta se estra mostrando
            if (contenedorAnimacion.getVisibility() == View.VISIBLE){
                contenedorAnimacion.setVisibility(View.GONE);
            }

            // Si han hecho "swipe", esconderemos el indicador de carga
            if (swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }

            // EL usuario tiene notificaciones
            if (notificaciones.size() > 0){

                // Actualizamos el listado de notificaciones
                notificacionesAdapter.actualizarTodasNotificaciones(notificaciones);

                // Recargamos el listado de notificaciones y lo mostramos
                contenedorListadoNotificaciones.setVisibility(View.VISIBLE);
                contenedorSinNotificaciones.setVisibility(View.GONE);

                // Marcamos las notificaciones como leidas
                usuarioModel.marcarNotificacionesLeidas();
            }

            // No tiene ninguna notificacion
            else {

                // Mostramos el mensaje de "No hay notificaciones aun"
                contenedorSinNotificaciones.setVisibility(View.VISIBLE);
                contenedorListadoNotificaciones.setVisibility(View.GONE);
            }
        });

        // Forzamos el recargado del listado de notificaciones
        usuarioModel.recargarNotificaciones();
    }

    private Consumer<Pair<Notificacion, Pair<AccionNotificacion, Object>>> onAccionListener(){
        return accionNotificacionObjectPair -> {

            Notificacion notificacion = accionNotificacionObjectPair.first;
            Object respuestaAccion = accionNotificacionObjectPair.second.second;
            AccionNotificacion accionNotificacion = accionNotificacionObjectPair.second.first;

            switch (accionNotificacion){

                case INVITACION_GRUPO:
                    int idNotificacion = notificacion.getId();
                    RespuestaInvitacion respuestaInvitacion = ((boolean) respuestaAccion) ? RespuestaInvitacion.ACEPTAR : RespuestaInvitacion.RECHAZAR;
                    manejarInvitacionGrupo(idNotificacion, respuestaInvitacion);
            }

        };
    }

    private void manejarInvitacionGrupo(int id, RespuestaInvitacion resRespuestaInvitacion){

        contenedorAnimacion.setVisibility(View.VISIBLE);

        usuarioModel.responderInvitacionGrupo(id, resRespuestaInvitacion)
                .subscribe(status -> {

                    if (status == 200){
                        mostrarDialogoResultadoAccion(true);

                    }
                    else {
                        mostrarDialogoResultadoAccion(false);
                    }

                }, error -> {
                    contenedorAnimacion.setVisibility(View.VISIBLE);
                    error.printStackTrace();
                });

    }

    private void mostrarDialogoResultadoAccion(boolean exitosa){

        // Escondemos el indicador de carga
        contenedorAnimacion.setVisibility(View.GONE);

        // Inflamos la vista del dialogo
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View dialogView = factory.inflate(R.layout.dialogo_resultado_accion, null);

        AppCompatTextView tvResultadoAccion = dialogView.findViewById(R.id.textoResultadoAccion);
        LottieAnimationView lavResultadoAccion = dialogView.findViewById(R.id.animacionResultadoAccion);

        // Mostramos el mensaje y la animacion correspondientes
        if (exitosa){
            tvResultadoAccion.setText(R.string.fragnot_adhesion_grupo_exitosa);
            lavResultadoAccion.setAnimation("animacion_operacion_exitosa.json");
        }
        else {
            tvResultadoAccion.setText(R.string.fragnot_adhesion_grupo_fallida);
            lavResultadoAccion.setAnimation("animacion_operacion_fallida.json");
        }

        // Creamos el alertdialog y le pasamos la vista
        final AlertDialog resultadoAccionDialog = new AlertDialog.Builder(getContext()).create();
        resultadoAccionDialog.setView(dialogView);
        resultadoAccionDialog.setCanceledOnTouchOutside(true);
        resultadoAccionDialog.show();

        // Esperamos 3 segundos antes de esconder el dialogo
        Observable.create(emitter -> emitter.onComplete())
                .delay(3, TimeUnit.SECONDS)
                .subscribe((o) -> {}, error -> {}, resultadoAccionDialog::hide);
    }
}
