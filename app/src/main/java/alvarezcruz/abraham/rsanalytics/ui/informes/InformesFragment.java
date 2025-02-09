package alvarezcruz.abraham.rsanalytics.ui.informes;

import android.Manifest;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.adapters.InformesAdapter;
import alvarezcruz.abraham.rsanalytics.adapters.decorators.EdgeDecorator;
import alvarezcruz.abraham.rsanalytics.model.pojo.Informe;
import alvarezcruz.abraham.rsanalytics.model.repository.local.UsuarioModel;
import alvarezcruz.abraham.rsanalytics.ui.dialogs.CustomDialogsListener;
import alvarezcruz.abraham.rsanalytics.ui.dialogs.DialogoCrearInforme;
import alvarezcruz.abraham.rsanalytics.ui.menuPrincipal.MenuPrincipalActivity;

public class InformesFragment extends Fragment implements MultiplePermissionsListener {

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
    private InformesAdapter informesAdapter;

    private AppCompatButton botonCrearInforme;
    private FloatingActionButton fabCrearInforme;

    private SwipeRefreshLayout swipeRefreshLayout;

    private boolean puedeDescargarInformes = false;

    public InformesFragment(){}

    public InformesFragment(MenuPrincipalActivity menuPrincipalActivity, UsuarioModel usuarioModel){
        this.usuarioModel = usuarioModel;
        this.ldInformes = usuarioModel.getInformes();
        this.menuPrincipalActivity = menuPrincipalActivity;
        this.informesAdapter = new InformesAdapter(getActivity());
        informesAdapter.setOnClickListener(this::descargarInforme);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_informes, container, false);

        Dexter.withContext(getContext())
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(this)
                .check();

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
        recyclerView.setAdapter(informesAdapter);

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
        informesAdapter.actualizarTodosInformes(informes);

        // Recargamos el listado de notificaciones y lo mostramos
        contenedorSinInformes.setVisibility(View.GONE);
        contenedorListadoInformes.setVisibility(View.VISIBLE);
    }

    public void mostrarDialogoCrearInforme(View v){

        CustomDialogsListener customDialogsListener = new CustomDialogsListener();
        customDialogsListener.avisarSoloPrimero();
        customDialogsListener.setOnCancelConsumer(i -> { refrescarInformes(); });
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

    public void descargarInforme(Informe i){
        if (puedeDescargarInformes){
            usuarioModel.descargarInforme(i);
        }
    }

    @Override
    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
        puedeDescargarInformes = multiplePermissionsReport.areAllPermissionsGranted();
    }


    @Override
    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
        MultiplePermissionsListener dialogMultiplePermissionsListener =
                DialogOnAnyDeniedMultiplePermissionsListener.Builder
                        .withContext(getContext())
                        .withTitle(getResources().getString(R.string.frainf_titulo_permisos_disco))
                        .withMessage(getResources().getString(R.string.frainf_desc_permisos_disco))
                        .withButtonText(android.R.string.ok)
                        .build();
    }
}
