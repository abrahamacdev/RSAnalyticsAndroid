package alvarezcruz.abraham.rsanalytics.adapters.viewHolders.grupo;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Calendar;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.model.pojo.Usuario;
import alvarezcruz.abraham.rsanalytics.utils.Utils;
import de.hdodenhof.circleimageview.CircleImageView;

public class GrupoViewHolder extends RecyclerView.ViewHolder {

    private AppCompatTextView tvNombreMiembro, tvFechaMiembro;
    private CircleImageView imagenAvatarMiembro;

    private Context context;

    public GrupoViewHolder(@NonNull View itemView, Context context) {
        super(itemView);

        this.context = context;
        tvNombreMiembro = itemView.findViewById(R.id.textoNombreMiembro);
        tvFechaMiembro = itemView.findViewById(R.id.textoFechaMiembro);
        imagenAvatarMiembro = itemView.findViewById(R.id.imagenAvatarMiembro);
    }

    public void ligarMiembro(Usuario usuario){

        //Establecemos el nombre del miembro
        tvNombreMiembro.setText(usuario.getNombre());

        // Establecemos el icono del usuario
        if (usuario.getSexo() == Usuario.Sexo.HOMBRE){
            imagenAvatarMiembro.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_avatar_hombre));
        }
        else {
            imagenAvatarMiembro.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_avatar_mujer));
        }

        // Establecemos la fecha de adhesion al grupo del usuario
        if (usuario.getFechaMiembro() > 0){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(usuario.getFechaMiembro());

            String p2 = " " + context.getResources().getString(R.string.detmiegru_texto_fecha_p2) + " ";

            String fecha = context.getResources().getString(R.string.detmiegru_texto_fecha_p1) + " ";
            fecha += calendar.get(Calendar.DAY_OF_MONTH);
            fecha += p2;
            fecha += Utils.mes2Texto(calendar.get(Calendar.MONTH));
            fecha += p2;
            fecha += calendar.get(Calendar.YEAR);

            tvFechaMiembro.setText(fecha);
        }

    }

}
