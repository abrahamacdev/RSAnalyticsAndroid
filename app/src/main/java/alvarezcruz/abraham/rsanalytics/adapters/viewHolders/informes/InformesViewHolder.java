package alvarezcruz.abraham.rsanalytics.adapters.viewHolders.informes;

import android.text.Html;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.model.pojo.Informe;
import alvarezcruz.abraham.rsanalytics.ui.informes.InformeListener;
import alvarezcruz.abraham.rsanalytics.utils.Utils;

public class InformesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private AppCompatTextView tvTitulo, tvFecha;
    private AppCompatImageView ivEstado;

    private InformeListener.OnInformeClickListener onClickListener;

    private Informe informe;

    public InformesViewHolder(@NonNull View itemView) {
        super(itemView);

        tvTitulo = itemView.findViewById(R.id.textViewMunicipio);
        tvFecha = itemView.findViewById(R.id.textViewFechaSolicitud);
        ivEstado = itemView.findViewById(R.id.imagenEstado);

        itemView.setOnClickListener(this);
    }

    public void ligarInforme(Informe informe){

        this.informe = informe;

        // Preparamos los datos de la fecha de solicitud
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(informe.getFechaSolicitud());
        String minutos = String.format("%02d", calendar.get(Calendar.MINUTE));
        String hora =  String.format("%02d", calendar.get(Calendar.HOUR_OF_DAY));

        String p2Fecha = itemView.getContext().getResources().getString(R.string.detinf_texto_fecha_p2);
        String p3Fecha = itemView.getContext().getResources().getString(R.string.detinf_texto_fecha_p3);

        // Creamos el texto que se establecera como fecha
        String fecha = itemView.getContext().getResources().getString(R.string.detinf_texto_fecha_p1) + " ";
        fecha += calendar.get(Calendar.DAY_OF_MONTH) + " " + p2Fecha + " ";
        fecha += Utils.mes2Texto(calendar.get(Calendar.MONTH)) + " " + p2Fecha + " ";
        fecha += calendar.get(Calendar.YEAR) + " " + p3Fecha + " ";
        fecha += hora + ":" + minutos;

        // Establecemos ls fecha
        tvFecha.setText(fecha);

        // Establecemos el titulo
        tvTitulo.setText(Html.fromHtml(itemView.getContext().getResources().getString(R.string.detinf_titulo) + " " + "<b>" + informe.getMunicipio() + "</b>"));

        // Imagen de "carga"
        if (informe.isPendiente()){
            ivEstado.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_cargando));
        }
        // Imagen "descargar"
        else {
            ivEstado.setImageDrawable(itemView.getContext().getDrawable(R.drawable.ic_descarga));
        }

    }

    public void setOnClickListener(InformeListener.OnInformeClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onClick(View v) {
        if (onClickListener != null){
            onClickListener.onClick(informe);
        }
    }
}
