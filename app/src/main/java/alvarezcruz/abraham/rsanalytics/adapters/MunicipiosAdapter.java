package alvarezcruz.abraham.rsanalytics.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MunicipiosAdapter extends ArrayAdapter<String> {

    private ArrayList<String> municipios;
    private Context context;



    public MunicipiosAdapter(@NonNull Context context) {
        super(context, android.R.layout.simple_expandable_list_item_1);
        this.municipios = new ArrayList<>();
        this.context = context;
    }

    @Override
    public int getCount() {
        return municipios.size();
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return municipios.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        try {
            if (convertView == null) {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                view = inflater.inflate(android.R.layout.simple_expandable_list_item_1, parent, false);
            }

            String municipio = getItem(position);
            TextView nombre = (TextView) view.findViewById(android.R.id.text1);
            nombre.setText(municipio);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    public void actualizarTodos(ArrayList<String> nuevosMunicipios){
        this.municipios.clear();

        if(nuevosMunicipios.size() > 3){
            this.municipios.addAll(nuevosMunicipios.subList(0,3));
        }

        else {
            this.municipios.addAll(nuevosMunicipios);
        }

        this.notifyDataSetChanged();
    }

    public boolean contiene(String municipio){
        return municipios.contains(municipio);
    }
}
