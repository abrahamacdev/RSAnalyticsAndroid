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
import alvarezcruz.abraham.rsanalytics.adapters.ViewHolders.NotificacionInformeViewHolder;
import alvarezcruz.abraham.rsanalytics.adapters.ViewHolders.NotificacionInvGrupoViewHolder;
import alvarezcruz.abraham.rsanalytics.adapters.ViewHolders.NotificacionViewHolder;
import alvarezcruz.abraham.rsanalytics.model.pojo.notificaciones.Notificacion;
import alvarezcruz.abraham.rsanalytics.ui.notificaciones.NotificacionesFragment;

public class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionViewHolder> {

    public static final String TAG_NAME = NotificacionesAdapter.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);


    private ArrayList<Notificacion> notificaciones;
    private Context context;

    public NotificacionesAdapter(Context context){
        notificaciones = new ArrayList<>();
        this.context = context;
    }

    public NotificacionesAdapter(Context context, ArrayList<Notificacion> notificaciones){
        this.notificaciones = notificaciones;
        this.context = context;
    }

    public void actualizarTodasNotificaciones(ArrayList<Notificacion> notificaciones){
        this.notificaciones = notificaciones;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {

        logger.log(Level.SEVERE, "getItemViewType");

        // 0 -> Invitacion  a grupo || 1 -> Informe
        return notificaciones.get(position).getAccion() == null ? 1 : 0;
    }

    @NonNull
    @Override
    public NotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        logger.log(Level.SEVERE, "OnCreateViewHolder  -> " + viewType);

        View itemView = null;

        switch (viewType){

            // Invitacion a grupo
            case 0:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.detalle_notificacion_invitacion_grupo, parent, false);
                return new NotificacionInvGrupoViewHolder(itemView, context);

            // Informe
            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.detalle_notificacion_informes, parent, false);
                return new NotificacionInformeViewHolder(itemView, context);
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return notificaciones.size();
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacionViewHolder holder, int position) {
        holder.ligarNotificacion(notificaciones.get(position));
    }
}
