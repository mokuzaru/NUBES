package deustodia.app.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import deustodia.app.entities.Bano;
import deustodia.app.nubes.R;

/**
 * Created by Victor Casas on 17/05/2015.
 */
public class AdapterBanos extends BaseAdapter {

    private Activity activity;
    private ArrayList<Bano> data;
    private static LayoutInflater inflater=null;

    public AdapterBanos(Activity a, ArrayList<Bano> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;

        vi = inflater.inflate(R.layout.model_list_bano, null);

        TextView txtNombre = (TextView) vi.findViewById(R.id.txtNombre);
        TextView txtDireccion = (TextView) vi.findViewById(R.id.txtDireccion);
        TextView txtDistancia = (TextView) vi.findViewById(R.id.txtDistancia);

       txtNombre.setText(data.get(position).nombre);
       txtDistancia.setText(((Math.round(data.get(position).distanciaDouble * 100))/100)+" m.");
       txtDireccion.setText(data.get(position).direccion);

        return vi;
    }
}