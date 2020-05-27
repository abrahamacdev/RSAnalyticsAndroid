package alvarezcruz.abraham.rsanalytics.adapters.ViewHolders;

import android.content.Context;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import alvarezcruz.abraham.rsanalytics.model.pojo.notificaciones.Notificacion;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;

public abstract class NotificacionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    protected View.OnClickListener onClickListener;
    protected Consumer<Pair<AccionNotificacion, Object>> onAccionListener;
    private Context context;

    public NotificacionViewHolder(@NonNull View itemView, Context context) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.context = context;
    }

    public abstract void ligarNotificacion(Notificacion notificacion);

    public void setOnClickListener(View.OnClickListener onClickListener){
        this.onClickListener = onClickListener;
    }

    public void setOnAccionListener(Consumer<Pair<AccionNotificacion, Object>> onAccionListener){
        this.onAccionListener = onAccionListener;
    }

    @Override
    public void onClick(View v) {
        if (onClickListener != null){
            onClickListener.onClick(v);
        }
    }


    public Context getContext() {
        return context;
    }
}
