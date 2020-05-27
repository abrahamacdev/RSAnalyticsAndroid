package alvarezcruz.abraham.rsanalytics.adapters.ViewHolders;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.logging.Level;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.model.pojo.notificaciones.Notificacion;

public class NotificacionInformeViewHolder extends NotificacionViewHolder {

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
