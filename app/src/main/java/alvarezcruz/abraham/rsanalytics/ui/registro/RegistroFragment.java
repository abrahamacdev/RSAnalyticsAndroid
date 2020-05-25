package alvarezcruz.abraham.rsanalytics.ui.registro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;

public class RegistroFragment extends Fragment {

    public static final String TAG_NAME = RegistroFragment.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private Runnable onRegistradoListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_registro, container, false);

        return view;
    }

    public void setOnRegistradoListener(Runnable onRegistradoListener){
        this.onRegistradoListener = onRegistradoListener;
    }
}
