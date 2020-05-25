package alvarezcruz.abraham.rsanalytics.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
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

        comprobarEstadoUsuario();
    }



    private void comprobarEstadoUsuario(){

        usuarioModel.getTokenLocal()
                .subscribe(new MaybeObserver<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {}

                    @Override
                    public void onSuccess(@NonNull String s) {

                        // Tenemos un token valido almacenado en local
                        irMenuPrincipal();
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

    private void irMenuPrincipal(){
        Intent i = new Intent(this, MenuPrincipalActivity.class);
        startActivity(i);
    }

    private void irPantallaRegistro(){
        RegistroFragment registroFragment = new RegistroFragment();
        registroFragment.setOnRegistradoListener(this::irMenuPrincipal);

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
}
