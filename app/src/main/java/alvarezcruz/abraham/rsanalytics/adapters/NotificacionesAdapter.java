package alvarezcruz.abraham.rsanalytics.adapters;

import android.content.Context;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.adapters.viewHolders.notificaciones.AccionNotificacion;
import alvarezcruz.abraham.rsanalytics.adapters.viewHolders.notificaciones.NotificacionInformeViewHolder;
import alvarezcruz.abraham.rsanalytics.adapters.viewHolders.notificaciones.NotificacionInvGrupoViewHolder;
import alvarezcruz.abraham.rsanalytics.adapters.viewHolders.notificaciones.AbstractNotificacionViewHolder;
import alvarezcruz.abraham.rsanalytics.model.pojo.notificaciones.Notificacion;
import io.reactivex.rxjava3.functions.Consumer;

public class NotificacionesAdapter extends RecyclerView.Adapter<AbstractNotificacionViewHolder> {

    public static final String TAG_NAME = NotificacionesAdapter.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);

    private Consumer<Pair<Notificacion, Pair<AccionNotificacion, Object>>> onAccionListener;

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

        // 0 -> Invitacion  a grupo || 1 -> Informe
        return notificaciones.get(position).getAccion() == null ? 1 : 0;
    }

    @NonNull
    @Override
    public AbstractNotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        AbstractNotificacionViewHolder abstractNotificacionViewHolder = null;
        View itemView = null;

        switch (viewType){

            // Invitacion a grupo
            case 0:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.detalle_notificacion_invitacion_grupo, parent, false);

                abstractNotificacionViewHolder = new NotificacionInvGrupoViewHolder(itemView, context);

                if (onAccionListener != null){
                    abstractNotificacionViewHolder.setOnAccionListener(onAccionListener);
                }

                return abstractNotificacionViewHolder;

            // Informe
            case 1:
                itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.detalle_notificacion_informes, parent, false);

                abstractNotificacionViewHolder = new NotificacionInformeViewHolder(itemView, context);

                if (onAccionListener != null){
                    abstractNotificacionViewHolder.setOnAccionListener(onAccionListener);
                }

                return abstractNotificacionViewHolder;
        }

        return null;
    }

    @Override
    public int getItemCount() {
        return notificaciones.size();
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractNotificacionViewHolder holder, int position) {
        holder.ligarNotificacion(notificaciones.get(position));
    }

    public void setOnAccionListener(Consumer<Pair<Notificacion, Pair<AccionNotificacion, Object>>> onAccionListener){
        this.onAccionListener = onAccionListener;
    }

}
