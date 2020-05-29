package alvarezcruz.abraham.rsanalytics.adapters.viewHolders.notificaciones;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.model.pojo.notificaciones.Notificacion;

public class NotificacionInformeViewHolder extends AbstractNotificacionViewHolder {

    private AppCompatTextView tvFechaInforme;

    public NotificacionInformeViewHolder(@NonNull View itemView, Context context) {
        super(itemView, context);

        tvFechaInforme = itemView.findViewById(R.id.textViewFechaSolicitud);
    }

    @Override
    public void ligarNotificacion(Notificacion notificacion) {
        tvFechaInforme.setText(notificacion.getMensaje());
    }
}
