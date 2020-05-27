package alvarezcruz.abraham.rsanalytics.ui.notificaciones;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.adapters.NotificacionesAdapter;
import alvarezcruz.abraham.rsanalytics.model.pojo.notificaciones.Notificacion;
import alvarezcruz.abraham.rsanalytics.model.repository.local.UsuarioModel;
import alvarezcruz.abraham.rsanalytics.ui.menuPrincipal.MenuPrincipalActivity;

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
        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
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

}
