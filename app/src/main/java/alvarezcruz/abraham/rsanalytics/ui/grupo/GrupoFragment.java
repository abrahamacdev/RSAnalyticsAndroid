package alvarezcruz.abraham.rsanalytics.ui.grupo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
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
import java.util.logging.Level;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.adapters.GrupoAdapter;
import alvarezcruz.abraham.rsanalytics.adapters.decorators.EdgeDecorator;
import alvarezcruz.abraham.rsanalytics.model.pojo.Usuario;
import alvarezcruz.abraham.rsanalytics.model.repository.local.UsuarioModel;
import alvarezcruz.abraham.rsanalytics.ui.menuPrincipal.MenuPrincipalActivity;
import alvarezcruz.abraham.rsanalytics.ui.notificaciones.NotificacionesFragment;
import io.reactivex.rxjava3.core.Emitter;
import io.reactivex.rxjava3.core.Observable;

public class GrupoFragment extends Fragment implements View.OnClickListener {

    public static final String TAG_NAME = NotificacionesFragment.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private UsuarioModel usuarioModel;
    private LiveData<ArrayList<Usuario>> ldMiembros;

    private MenuPrincipalActivity menuPrincipalActivity;

    private ConstraintLayout contenedorAnimacion;
    private ConstraintLayout contenedorSinGrupo;
    private ConstraintLayout contenedorListadoMiembros;

    private RecyclerView recyclerView;
    private GrupoAdapter grupoAdapter;

    private AppCompatButton botonCrearGrupo;

    private SwipeRefreshLayout swipeRefreshLayout;


    public GrupoFragment(){}

    public GrupoFragment(MenuPrincipalActivity menuPrincipalActivity, UsuarioModel usuarioModel){
        this.usuarioModel = usuarioModel;
        this.ldMiembros = usuarioModel.getMiembros();
        this.menuPrincipalActivity = menuPrincipalActivity;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_grupo, container, false);

        initViews(view);

        refrescarDatosGrupo();

        return view;

    }

    private void initViews(View view){

        contenedorSinGrupo = view.findViewById(R.id.contenedorSinGrupo);
        contenedorAnimacion = view.findViewById(R.id.contenedorAnimacion);
        contenedorListadoMiembros = view.findViewById(R.id.contenedorListadoMiembrosGrupo);

        botonCrearGrupo = view.findViewById(R.id.botonCrearGrupo);
        botonCrearGrupo.setOnClickListener(this);

        grupoAdapter = new GrupoAdapter(getContext());
        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new EdgeDecorator(getResources().getDimension(R.dimen.lisnot_padding_vertical_recycler)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(grupoAdapter);

        swipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(this::refrescarDatosGrupo);
    }

    private void refrescarDatosGrupo(){

        // Mostramos el indicador del swipeRefreshLayout si no se esta mostrando la
        // animacion de los circulitos
        if (contenedorAnimacion.getVisibility() != View.VISIBLE){
            swipeRefreshLayout.setRefreshing(true);
        }


        // Observamos las notificaciones
        ldMiembros.observe(menuPrincipalActivity, miembros -> {

            logger.log(Level.SEVERE, "YA tenemos respuesta");

            // Escondenmos la animacion por defecto si esta se estra mostrando
            if (contenedorAnimacion.getVisibility() == View.VISIBLE){
                contenedorAnimacion.setVisibility(View.GONE);
            }

            // Si han hecho "swipe", esconderemos el indicador de carga
            if (swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }

            // EL usuario tiene notificaciones
            if (miembros.size() > 0){

                // Actualizamos el listado de notificaciones
                grupoAdapter.actualizarTodosMiembros(miembros);

                // Recargamos el listado de notificaciones y lo mostramos
                contenedorListadoMiembros.setVisibility(View.VISIBLE);
                contenedorSinGrupo.setVisibility(View.GONE);

                // Marcamos las notificaciones como leidas
                usuarioModel.marcarNotificacionesLeidas();
            }

            // No tiene ninguna notificacion
            else {

                // Mostramos el mensaje de "No hay notificaciones aun"
                contenedorSinGrupo.setVisibility(View.VISIBLE);
                contenedorListadoMiembros.setVisibility(View.GONE);
            }
        });

        // Forzamos el recargado del listado de notificaciones
        usuarioModel.recargarMiembrosAsync();
    }

    public void mostrarDialogoCrearGrupo(){

        // Escondemos el indicador de carga
        contenedorAnimacion.setVisibility(View.GONE);

        // Inflamos la vista del dialogo
        LayoutInflater factory = LayoutInflater.from(getActivity());
        final View dialogView = factory.inflate(R.layout.dialogo_resultado_accion, null);

        AppCompatTextView tvResultadoAccion = dialogView.findViewById(R.id.textoResultadoAccion);
        LottieAnimationView lavResultadoAccion = dialogView.findViewById(R.id.animacionResultadoAccion);

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        dialogBuilder.setOnDismissListener((v) -> {
            contenedorListadoNotificaciones.setVisibility(View.VISIBLE);
            usuarioModel.recargarNotificaciones();
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        // Cambiamos el tamaño de la ventana
        dialog.getWindow().setLayout((int) getResources().getDimension(R.dimen.diresacc_ancho_card_dialogo_res_accion), (int) getResources().getDimension(R.dimen.diresacc_alto_card_dialogo_res_accion));

        // Esperamos 3 segundos antes de esconder el dialogo
        Observable.create(Emitter::onComplete)
                .delay(3, TimeUnit.SECONDS)
                .subscribe((o) -> {}, error -> {}, () -> {
                    getActivity().runOnUiThread(() -> {
                        dialog.dismiss();
                    });
                });

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.botonCrearGrupo:
                mostrarDialogoCrearGrupo();
        }

    }
}
