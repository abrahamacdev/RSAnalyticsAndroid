package alvarezcruz.abraham.rsanalytics.adapters.ViewHolders;

import android.content.Context;
import android.os.Build;
import android.text.Html;
import android.util.Pair;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import java.util.logging.Level;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.adapters.NotificacionesAdapter;
import alvarezcruz.abraham.rsanalytics.model.pojo.notificaciones.Accion;
import alvarezcruz.abraham.rsanalytics.model.pojo.notificaciones.Notificacion;

public class NotificacionInvGrupoViewHolder extends NotificacionViewHolder {

    public static final String TAG_NAME = NotificacionInvGrupoViewHolder.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);


    private AppCompatTextView tvMensajeAdhesion;
    private RelativeLayout relativeLayoutBotonAdhesion;

    private AppCompatButton botonAceptarAdhesion, botonRechazarAdhesion;

    public NotificacionInvGrupoViewHolder(@NonNull View itemView, Context context) {
        super(itemView, context);

        tvMensajeAdhesion = itemView.findViewById(R.id.textViewMensajeAdhesion);
        relativeLayoutBotonAdhesion = itemView.findViewById(R.id.contenedorAccionInvitacion);

        botonAceptarAdhesion = itemView.findViewById(R.id.botonConfirmarAdhesion);
        botonRechazarAdhesion = itemView.findViewById(R.id.botonRechazarAdhesion);

        botonAceptarAdhesion.setOnClickListener(this::accionPulsada);
        botonRechazarAdhesion.setOnClickListener(this::accionPulsada);
    }

    @Override
    public void ligarNotificacion(Notificacion notificacion) {

        // Si ya realizamos la accion esconderemos los botones
        if (notificacion.getAccion().isCompletada()){
            relativeLayoutBotonAdhesion.setVisibility(View.GONE);
        }

        String nombreResponsable = " <b>" + notificacion.getEmisor() + "</b>";
        String nombreGrupo = " <b>" + notificacion.getAccion().getNombreGrupo() + "</b> ";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvMensajeAdhesion.setText(Html.fromHtml(getContext().getString(R.string.detnotinvgrup_mensaje_adhesion_p1)
                            + nombreResponsable + getContext().getString(R.string.detnotinvgrup_mensaje_adhesion_p2)
                            + nombreGrupo + getContext().getString(R.string.detnotinvgrup_mensaje_adhesion_p3),
                    Html.FROM_HTML_MODE_COMPACT));
        } else {
            tvMensajeAdhesion.setText(Html.fromHtml(getContext().getString(R.string.detnotinvgrup_mensaje_adhesion_p1) + nombreResponsable
                            + getContext().getString(R.string.detnotinvgrup_mensaje_adhesion_p2) + nombreGrupo
                            + getContext().getString(R.string.detnotinvgrup_mensaje_adhesion_p3)));
        }
    }

    private void accionPulsada(View v){

        int id = v.getId();

        Pair<AccionNotificacion,Object> res = null;

        switch (id){

            case R.id.botonConfirmarAdhesion:
                res = new Pair<>(AccionNotificacion.INVITACION_GRUPO, true);
                break;

            case R.id.botonRechazarAdhesion:
                res = new Pair<>(AccionNotificacion.INVITACION_GRUPO, false);
                break;
        }

        if (super.onAccionListener != null){
            try {
                onAccionListener.accept(res);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

    }
}
