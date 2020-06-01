package alvarezcruz.abraham.rsanalytics.ui.informes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

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

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.adapters.GrupoAdapter;
import alvarezcruz.abraham.rsanalytics.adapters.decorators.EdgeDecorator;
import alvarezcruz.abraham.rsanalytics.model.pojo.Informe;
import alvarezcruz.abraham.rsanalytics.model.repository.local.UsuarioModel;
import alvarezcruz.abraham.rsanalytics.ui.dialogs.CustomDialogsListener;
import alvarezcruz.abraham.rsanalytics.ui.dialogs.DialogoAbandonarGrupo;
import alvarezcruz.abraham.rsanalytics.ui.dialogs.DialogoCrearInforme;
import alvarezcruz.abraham.rsanalytics.ui.menuPrincipal.MenuPrincipalActivity;

public class InformesFragment extends Fragment {

    public static final String TAG_NAME = InformesFragment.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private UsuarioModel usuarioModel;
    private LiveData<ArrayList<Informe>> ldInformes;

    private MenuPrincipalActivity menuPrincipalActivity;

    private ConstraintLayout contenedorAnimacion;
    private ConstraintLayout contenedorSinInformes;
    private ConstraintLayout contenedorListadoInformes;

    private LottieAnimationView animacionSinInformes;

    private RecyclerView recyclerView;
    private GrupoAdapter grupoAdapter;

    private AppCompatButton botonCrearInforme;
    private FloatingActionButton fabCrearInforme;

    private SwipeRefreshLayout swipeRefreshLayout;

    public InformesFragment(){}

    public InformesFragment(MenuPrincipalActivity menuPrincipalActivity, UsuarioModel usuarioModel){
        this.usuarioModel = usuarioModel;
        this.ldInformes = usuarioModel.getInformes();
        this.menuPrincipalActivity = menuPrincipalActivity;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_informes, container, false);

        initViews(view);

        refrescarInformes();

        return view;

    }

    private void initViews(View view){

        contenedorSinInformes = view.findViewById(R.id.contenedorSinInformes);
        contenedorAnimacion = view.findViewById(R.id.contenedorAnimacion);
        contenedorListadoInformes = view.findViewById(R.id.contenedorListadoInformes);

        animacionSinInformes = view.findViewById(R.id.animacionSinInformes);

        fabCrearInforme = view.findViewById(R.id.fabCrearInforme);
        botonCrearInforme = view.findViewById(R.id.botonCrearInforme);

        fabCrearInforme.setOnClickListener(this::mostrarDialogoCrearInforme);
        botonCrearInforme.setOnClickListener(this::mostrarDialogoCrearInforme);


        recyclerView = view.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new EdgeDecorator(getResources().getDimension(R.dimen.lisnot_padding_vertical_recycler)));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(grupoAdapter);

        swipeRefreshLayout = view.findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(this::refrescarInformes);
    }

    private void refrescarInformes(){

        // Mostramos el indicador del swipeRefreshLayout si no se esta mostrando la
        // animacion de los circulitos
        if (contenedorAnimacion.getVisibility() != View.VISIBLE){
            swipeRefreshLayout.setRefreshing(true);
        }

        // Observamos los informes
        ldInformes.observe(menuPrincipalActivity, informes -> {

            // Escondenmos la animacion por defecto si esta se estra mostrando
            if (contenedorAnimacion.getVisibility() == View.VISIBLE){
                contenedorAnimacion.setVisibility(View.GONE);
            }

            // Si han hecho "swipe", esconderemos el indicador de carga
            if (swipeRefreshLayout.isRefreshing()){
                swipeRefreshLayout.setRefreshing(false);
            }

            // EL usuario tiene informes
            if (informes.size() > 0){
                actualizarListadoInformes(informes);
            }

            // No tiene ningun informe
            else {

                // Mostramos el mensaje de "No hay notificaciones aun"
                contenedorSinInformes.setVisibility(View.VISIBLE);
                animacionSinInformes.setFrame(0);
                animacionSinInformes.playAnimation();
                contenedorListadoInformes.setVisibility(View.GONE);
            }
        });

        // Forzamos el recargado del listado de informmes
        usuarioModel.recargarInformesAsync();
    }

    public void actualizarListadoInformes(ArrayList<Informe> informes){

        // Actualizamos el listado de notificaciones
        //grupoAdapter.actualizarTodosMiembros(informes);

        // Recargamos el listado de notificaciones y lo mostramos
        contenedorSinInformes.setVisibility(View.GONE);
        contenedorListadoInformes.setVisibility(View.VISIBLE);
    }

    public void mostrarDialogoCrearInforme(View v){

        CustomDialogsListener customDialogsListener = new CustomDialogsListener();
        customDialogsListener.avisarSoloPrimero();
        customDialogsListener.setOnCancelConsumer(i -> {});
        customDialogsListener.setOnDismissConsumer(i -> {
            contenedorListadoInformes.setVisibility(View.GONE);
            contenedorAnimacion.setVisibility(View.VISIBLE);
            usuarioModel.recargarInformesAsync();
        });

        // Creamos el dialogo y le establecemos los listeners anteriores
        DialogoCrearInforme dialogoCrearInforme = new DialogoCrearInforme(getActivity(), usuarioModel);
        dialogoCrearInforme.setCanceledOnTouchOutside(true);
        dialogoCrearInforme.setOnDismissListener(customDialogsListener);
        dialogoCrearInforme.setOnCancelListener(customDialogsListener);
        dialogoCrearInforme.show();
    }


}
