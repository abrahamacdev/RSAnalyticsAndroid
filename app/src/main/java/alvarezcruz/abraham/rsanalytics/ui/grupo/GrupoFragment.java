package alvarezcruz.abraham.rsanalytics.ui.grupo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.joaquimley.faboptions.FabOptions;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.adapters.GrupoAdapter;
import alvarezcruz.abraham.rsanalytics.adapters.decorators.EdgeDecorator;
import alvarezcruz.abraham.rsanalytics.model.pojo.Usuario;
import alvarezcruz.abraham.rsanalytics.model.repository.local.UsuarioModel;
import alvarezcruz.abraham.rsanalytics.ui.dialogs.CustomDialogsListener;
import alvarezcruz.abraham.rsanalytics.ui.dialogs.DialogoAbandonarGrupo;
import alvarezcruz.abraham.rsanalytics.ui.dialogs.DialogoInvitacionGrupo;
import alvarezcruz.abraham.rsanalytics.ui.dialogs.DialogoCrearGrupo;
import alvarezcruz.abraham.rsanalytics.ui.menuPrincipal.MenuPrincipalActivity;
import alvarezcruz.abraham.rsanalytics.ui.notificaciones.NotificacionesFragment;

public class GrupoFragment extends Fragment implements View.OnClickListener {

    public static final String TAG_NAME = NotificacionesFragment.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private UsuarioModel usuarioModel;
    private LiveData<ArrayList<Usuario>> ldMiembros;

    private MenuPrincipalActivity menuPrincipalActivity;

    private ConstraintLayout contenedorAnimacion;
    private ConstraintLayout contenedorSinGrupo;
    private ConstraintLayout contenedorListadoMiembros;

    private LottieAnimationView animacionSinGrupo;

    private RecyclerView recyclerView;
    private GrupoAdapter grupoAdapter;

    private AppCompatButton botonCrearGrupo;
    private FloatingActionButton botonMiembro;
    private FabOptions botonResponsable;

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

        animacionSinGrupo = view.findViewById(R.id.animacionSinGrupo);

        botonMiembro = view.findViewById(R.id.fabMiembro);
        botonResponsable = view.findViewById(R.id.fabResponsable);
        botonCrearGrupo = view.findViewById(R.id.botonCrearGrupo);

        botonMiembro.setOnClickListener(this);
        botonResponsable.setOnClickListener(this);
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
                mostrarFabAdecuado();
                actualizarListadoMiembros(miembros);
            }

            // No tiene ninguna notificacion
            else {

                // Mostramos el mensaje de "No hay notificaciones aun"
                contenedorSinGrupo.setVisibility(View.VISIBLE);
                animacionSinGrupo.setFrame(0);
                animacionSinGrupo.playAnimation();
                contenedorListadoMiembros.setVisibility(View.GONE);
            }
        });

        // Forzamos el recargado del listado de notificaciones
        usuarioModel.recargarMiembrosYUsuarioAsync();
    }

    public void actualizarListadoMiembros(ArrayList<Usuario> miembros){

        // Actualizamos el listado de notificaciones
        grupoAdapter.actualizarTodosMiembros(miembros);

        // Recargamos el listado de notificaciones y lo mostramos
        contenedorListadoMiembros.setVisibility(View.VISIBLE);
        contenedorSinGrupo.setVisibility(View.GONE);

        // Marcamos las notificaciones como leidas
        usuarioModel.marcarNotificacionesLeidas();
    }

    public void mostrarFabAdecuado(){

        this.usuarioModel.recargarUsuario()
                .subscribe(par -> {

                    logger.log(Level.SEVERE, "Vamos a mostrar el fab adecuado (" + par.first + " - " + par.second.isResponsable() + ")");

                    if (par.first == 200){

                        boolean esResponsable = par.second.isResponsable();

                        if (esResponsable){
                            botonMiembro.setVisibility(View.GONE);
                            botonResponsable.setVisibility(View.VISIBLE);
                        }
                        else {
                            botonResponsable.setVisibility(View.GONE);
                            botonMiembro.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        botonResponsable.setVisibility(View.GONE);
                        botonMiembro.setVisibility(View.GONE);
                    }

                }, error -> error.printStackTrace());

    }

    public void mostrarDialogoCrearGrupo(){

        DialogoCrearGrupo dialogoCrearGrupo = new DialogoCrearGrupo(getActivity(), usuarioModel);
        dialogoCrearGrupo.setCanceledOnTouchOutside(true);
        dialogoCrearGrupo.setOnDismissListener((dialogInterface) -> {
            contenedorSinGrupo.setVisibility(View.GONE);
            contenedorAnimacion.setVisibility(View.VISIBLE);
            usuarioModel.recargarMiembrosYUsuarioAsync();
        });
        dialogoCrearGrupo.show();
    }

    public void mostrarDialogoAbandonoGrupo(boolean esResponsable){

        // Custom listener para evitar la recarga innecesaria de datos
        CustomDialogsListener customDialogsListener = new CustomDialogsListener();
        customDialogsListener.avisarSoloPrimero();
        customDialogsListener.setOnCancelConsumer(i -> {});
        customDialogsListener.setOnDismissConsumer(i -> {
            contenedorListadoMiembros.setVisibility(View.GONE);
            contenedorAnimacion.setVisibility(View.VISIBLE);
            usuarioModel.recargarMiembrosYUsuarioAsync();
        });

        // Creamos el dialogo y le establecemos los listeners anteriores
        DialogoAbandonarGrupo dialogoAbandonarGrupo = new DialogoAbandonarGrupo(getActivity(), usuarioModel, esResponsable);
        dialogoAbandonarGrupo.setCanceledOnTouchOutside(true);
        dialogoAbandonarGrupo.setOnDismissListener(customDialogsListener);
        dialogoAbandonarGrupo.setOnCancelListener(customDialogsListener);
        dialogoAbandonarGrupo.show();
    }

    public void mostrarDialogoInvitacion(){

        // Custom listener para evitar la recarga innecesaria de datos
        CustomDialogsListener customDialogsListener = new CustomDialogsListener();
        customDialogsListener.avisarSoloPrimero();
        customDialogsListener.setOnCancelConsumer(i -> {});
        customDialogsListener.setOnDismissConsumer(i -> {
            contenedorListadoMiembros.setVisibility(View.GONE);
            contenedorAnimacion.setVisibility(View.VISIBLE);
            usuarioModel.recargarMiembrosYUsuarioAsync();
        });

        DialogoInvitacionGrupo dialogoInvitacionGrupo = new DialogoInvitacionGrupo(getActivity(), usuarioModel);
        dialogoInvitacionGrupo.setCanceledOnTouchOutside(true);
        dialogoInvitacionGrupo.setOnCancelListener(customDialogsListener);
        dialogoInvitacionGrupo.setOnDismissListener(customDialogsListener);
        dialogoInvitacionGrupo.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            // DIalogo creacion grupo
            case R.id.botonCrearGrupo:
                mostrarDialogoCrearGrupo();
                break;

            // Dialogo abandono grupo (miembro)
            case R.id.fabMiembro:
                mostrarDialogoAbandonoGrupo(false);
                break;

            // Dialogo abandono grupo (responsable)
            case R.id.fabResponsableAbandonar:
                mostrarDialogoAbandonoGrupo(true);
                break;

            // Dialogo invitacion
            case R.id.fabResponsableInvitar:
                mostrarDialogoInvitacion();
                break;
        }
    }
}
