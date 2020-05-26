package alvarezcruz.abraham.rsanalytics.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.model.pojo.Usuario;
import alvarezcruz.abraham.rsanalytics.model.repository.local.UsuarioModel;
import alvarezcruz.abraham.rsanalytics.ui.login.LoginFragment;
import alvarezcruz.abraham.rsanalytics.ui.menuPrincipal.MenuPrincipalActivity;
import alvarezcruz.abraham.rsanalytics.ui.registro.RegistroFragment;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.MaybeObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    public static final String TAG_NAME = MainActivity.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private UsuarioModel usuarioModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usuarioModel = new ViewModelProvider(this).get(UsuarioModel.class);

        // Comprobamos si estamos logueados
        comprobarEstadoUsuario();
    }



    private void comprobarEstadoUsuario(){

        usuarioModel.getTokenLocalAsync()
                .subscribe(new MaybeObserver<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {}

                    @Override
                    public void onSuccess(@NonNull String s) {

                        // Tenemos un token en local, obtendremos la informacion general del usuario
                        usuarioModel.getUsuario()
                                .subscribe(usuario ->
                                {
                                    // Tenemos un token valido almacenado en local
                                    irMenuPrincipal(usuario);

                                }, error -> {

                                    // Ocurrio un error, volveremos a la pantalla de loquin
                                    usuarioModel.eliminarTokenLocalSync();
                                    irPantallaLogin();

                                }, () -> {

                                    // Ocurrio un error, volveremos a la pantalla de loquin
                                    usuarioModel.eliminarTokenLocalSync();
                                    irPantallaLogin();

                                });
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {}

                    @Override
                    public void onComplete() {

                        // Tenemos que realizar el login
                        irPantallaLogin();
                    }
                });
    }

    private void irMenuPrincipal(Usuario usuario){
        Intent i = new Intent(this, MenuPrincipalActivity.class);
        Bundle extras = new Bundle();
        extras.putSerializable("usuario", usuario);
        startActivity(i);
        finish();
    }

    private void irPantallaRegistro(){
        RegistroFragment registroFragment = new RegistroFragment();
        registroFragment.setOnRegistradoListener(this::irPantallaLogin);
        registroFragment.setOnRegistradoYLogueadoListener(this::irMenuPrincipal);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmento_principal, registroFragment, LoginFragment.TAG_NAME)
                .addToBackStack(RegistroFragment.TAG_NAME)
                .commit();
    }

    private void irPantallaLogin(){
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setOnRegistrarseListener(this::irPantallaRegistro);
        loginFragment.setOnLogueadoListener(this::irMenuPrincipal);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmento_principal, loginFragment, LoginFragment.TAG_NAME)
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
