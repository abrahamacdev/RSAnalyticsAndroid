package alvarezcruz.abraham.rsanalytics.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.logging.Logger;

import alvarezcruz.abraham.rsanalytics.R;
import alvarezcruz.abraham.rsanalytics.adapters.viewHolders.grupo.GrupoViewHolder;
import alvarezcruz.abraham.rsanalytics.model.pojo.Usuario;

public class GrupoAdapter extends RecyclerView.Adapter<GrupoViewHolder> {

    public static final String TAG_NAME = GrupoAdapter.class.getName();

    private Logger logger = Logger.getLogger(TAG_NAME);


    private ArrayList<Usuario> miembros;
    private Context context;

    public GrupoAdapter(Context context){
        miembros = new ArrayList<>();
        this.context = context;
    }

    public GrupoAdapter(Context context, ArrayList<Usuario> miembros){
        this.miembros = miembros;
        this.context = context;
    }


    public void actualizarTodosMiembros(ArrayList<Usuario> miembros){
        this.miembros = miembros;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GrupoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.detalle_miembro_grupo, parent, false);

        GrupoViewHolder grupoViewHolder = new GrupoViewHolder(itemView, parent.getContext());

        return grupoViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GrupoViewHolder holder, int position) {
        holder.ligarMiembro(miembros.get(position));
    }

    @Override
    public int getItemCount() {
        return miembros.size();
    }
}
