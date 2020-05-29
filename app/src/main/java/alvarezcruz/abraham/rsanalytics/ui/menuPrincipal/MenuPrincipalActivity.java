package alvarezcruz.abraham.rsanalytics.ui.menuPrincipal;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.logging.Level;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.model.pojo.Usuario;
import alvarezcruz.abraham.rsanalytics.model.repository.local.UsuarioModel;
import alvarezcruz.abraham.rsanalytics.ui.MainActivity;
import alvarezcruz.abraham.rsanalytics.ui.grupo.GrupoFragment;
import alvarezcruz.abraham.rsanalytics.ui.informes.InformesFragment;
import alvarezcruz.abraham.rsanalytics.ui.notificaciones.NotificacionesFragment;
import de.hdodenhof.circleimageview.CircleImageView;

public class MenuPrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String TAG_NAME = MenuPrincipalActivity.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    private AppCompatButton botonLogout;
    private CircleImageView avatar;
    private TextView tvNombre, tvCorreo;

    private UsuarioModel usuarioModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_principal);

        usuarioModel = new ViewModelProvider(this).get(UsuarioModel.class);

        initViews();

        parsearUsuario();
    }

    private void initViews(){

        setupToolbar();

        navigationView = findViewById(R.id.nav_view);
        View navHeader = navigationView.getHeaderView(0);

        // Por defecto seleccionaremos la opcion de "Informes"
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_informes);

        avatar = navHeader.findViewById(R.id.avatar);
        tvNombre = navHeader.findViewById(R.id.textViewNombre);
        tvCorreo = navHeader.findViewById(R.id.textViewCorreo);

        botonLogout = findViewById(R.id.botonDesloguearse);
        botonLogout.setOnClickListener(this::desloguear);
    }

    private void setupToolbar(){

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void parsearUsuario(){

        usuarioModel.getUsuario()
                .subscribe(par -> {

                    int codigo = par.first;

                    if (codigo == 200){

                        Usuario usuario = par.second;

                        tvNombre.setText(usuario.getNombre() + " " + usuario.getPrimerApellido());
                        tvCorreo.setText(usuario.getCorreo());

                        if (usuario.getSexo() == Usuario.Sexo.HOMBRE){
                            avatar.setImageDrawable(getDrawable(R.drawable.ic_avatar_hombre));
                        }
                        else {
                            avatar.setImageDrawable(getDrawable(R.drawable.ic_avatar_mujer));
                        }

                        tvNombre.setVisibility(View.VISIBLE);
                        tvCorreo.setVisibility(View.VISIBLE);
                        avatar.setVisibility(View.VISIBLE);
                    }

                    else {
                        desloguear(null);
                    }

                }, error -> {
                    desloguear(null);
                });
    }



    private void desloguear(View v){
        usuarioModel.eliminarTokenLocalAsync()
            .subscribe(() -> {
                logger.log(Level.SEVERE, "Nos deslogueamos");
                irAlMainActivity();
            });
    }

    private void irAlMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("classfrom", this.getClass().getName());
        startActivity(intent);

        logger.log(Level.SEVERE, "Hasta luego!");

        finish();
    }

    private void mostrarSeccionInformes(){

        FragmentManager fragmentManager = getSupportFragmentManager();
        InformesFragment informesFragment = new InformesFragment();

        fragmentManager.beginTransaction()
                .replace(R.id.contenedorFragmentos, informesFragment, InformesFragment.TAG_NAME)
                .commit();
    }

    private void mostrarSeccionGrupo() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        GrupoFragment grupoFragment = new GrupoFragment(this, usuarioModel);

        fragmentManager.beginTransaction()
                .replace(R.id.contenedorFragmentos, grupoFragment, GrupoFragment.TAG_NAME)
                .commit();

    }

    private void mostrarSeccionNotificaciones() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        NotificacionesFragment notificacionesFragment = new NotificacionesFragment(this, usuarioModel);

        fragmentManager.beginTransaction()
                .replace(R.id.contenedorFragmentos, notificacionesFragment, NotificacionesFragment.TAG_NAME)
                .commit();
    }



    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void cerrarDrawer() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if (navigationView.getCheckedItem().getItemId() == item.getItemId()){
            cerrarDrawer();
            return false;
        }

        switch (item.getItemId()){

            case R.id.nav_informes:
                mostrarSeccionInformes();
                break;

            case R.id.nav_grupo:
                mostrarSeccionGrupo();
                break;

            case R.id.nav_notificaciones:
                mostrarSeccionNotificaciones();
                break;
        }

        cerrarDrawer();

        return true;

    }
}
