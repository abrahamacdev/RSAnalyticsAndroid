package alvarezcruz.abraham.rsanalytics.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.adapters.viewHolders.informes.InformesViewHolder;
import alvarezcruz.abraham.rsanalytics.adapters.viewHolders.notificaciones.NotificacionInvGrupoViewHolder;
import alvarezcruz.abraham.rsanalytics.model.pojo.Informe;

public class InformesAdapter extends RecyclerView.Adapter<InformesViewHolder> {

    public static final String TAG_NAME = InformesAdapter.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private ArrayList<Informe> informes;
    private Context context;

    public InformesAdapter(Context context){
        this.context = context;
        this.informes = new ArrayList<>();
    }

    public InformesAdapter(Context context, ArrayList<Informe> informes){
        this.context = context;
        this.informes = informes;
    }

    public void actualizarTodosInformes(ArrayList<Informe> informes){
        this.informes = informes;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InformesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // Invitacion a grupo
        View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.detalle_informe, parent, false);

        InformesViewHolder informesViewHolder = new InformesViewHolder(itemView);

        return informesViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull InformesViewHolder holder, int position) {

        holder.ligarInforme(informes.get(position));
    }

    @Override
    public int getItemCount() {
        return informes.size();
    }
}
