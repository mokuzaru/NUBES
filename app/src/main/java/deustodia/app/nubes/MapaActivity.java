package deustodia.app.nubes;

import android.content.Context;
import android.graphics.Camera;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import java.lang.Object.*;

import deustodia.app.adapters.AdapterBanos;
import deustodia.app.conexion.ServidorDeustodia;
import deustodia.app.entities.Bano;

public class MapaActivity extends ActionBarActivity {

    private static final LatLng DAVAO = new LatLng(-12.1215822, -77.029243);
    private GoogleMap map;

    private AsyncListarBanos asyncListarBanos;
    public ListView lstBanos;
    private AdapterBanos adapterBanos;
    private ArrayList<Bano> arrayBanos = new ArrayList<Bano>();
    private ArrayList<Bano> arrayBanosVisible = new ArrayList<Bano>();
    private boolean posicionInicial = false;
    private boolean calculandoDistancia = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa); //

        /*map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapa)).getMap();



        // zoom in the camera to Davao city
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(DAVAO, 15));

        // animate the zoom process
        map.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null);

        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

            @Override
            public void onMyLocationChange(Location arg0) {
                map.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                mostrarMensaje(arg0.getLatitude()+ " --- "+arg0.getLongitude());
            }
        });*/


        lstBanos = (ListView)findViewById(R.id.lstBanosCercanos);
        adapterBanos = new AdapterBanos(this, arrayBanosVisible);
        lstBanos.setAdapter(adapterBanos);

        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapa))
                    .getMap();
            map.setMyLocationEnabled(true);
            // Check if we were successful in obtaining the map.
            if (map != null) {

                map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                    @Override
                    public void onMyLocationChange(Location arg0) {
                        //map.addMarker(new MarkerOptions().position(new LatLng(arg0.getLatitude(), arg0.getLongitude())).title("It's Me!"));
                        //mostrarMensaje(arg0.getLatitude()+ " --- "+arg0.getLongitude());
                        try{
                            if(!calculandoDistancia)
                            calcularBanosCercanos(arg0.getLatitude(),arg0.getLongitude());
                        }catch (Exception e){
                            e.printStackTrace();
                            calculandoDistancia = false;
                        }

                        if(!posicionInicial){
                            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(arg0.getLatitude(),arg0.getLongitude()), 15));
                            map.animateCamera(CameraUpdateFactory.zoomTo(18), 2000, null);
                            posicionInicial = true;
                        }
                    }
                });

            }
        }


        if(isNetworkAvailable())
            cargarListaBanos();
    }

    private void calcularBanosCercanos(Double latitudActual, Double longitudActual){
        calculandoDistancia = true;
        for(Bano b : arrayBanos){
            b.distanciaDouble = distance(new LatLng(b.geoX,b.geoY),new LatLng(latitudActual,longitudActual));
        }

        for(int i = 0; i < arrayBanos.size() -1; i++){
            for(int j = 0; j < arrayBanos.size() -1; j++){
                if(arrayBanos.get(j).distanciaDouble < arrayBanos.get(j + 1).distanciaDouble){
                    Bano b = new Bano();
                    b = arrayBanos.get(j+1);
                    arrayBanos.set(j+1,arrayBanos.get(j));
                    arrayBanos.set(j,b);
                }
            }
        }

        arrayBanosVisible.clear();
        arrayBanosVisible.add(arrayBanos.get(arrayBanos.size()-1));
        arrayBanosVisible.add(arrayBanos.get(arrayBanos.size()-2));
        adapterBanos.notifyDataSetChanged();

        calculandoDistancia = false;
        /*for(int i = 0; i < arreglo.length - 1; i++)
        {
            for(int j = 0; j < arreglo.length - 1; j++)
            {
                if (arreglo[j] < arreglo[j + 1])
                {
                    int tmp = arreglo[j+1];
                    arreglo[j+1] = arreglo[j];
                    arreglo[j] = tmp;
                }
            }
        }*/

    }

    private void cargarListaBanos(){
        asyncListarBanos = new AsyncListarBanos();
        asyncListarBanos.execute();
    }

    private void agregarMarcador(double geoX, double geoY, String nombre, String direccion){
        LatLng lat = new LatLng(geoX,geoY);
        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.img_marker_bano3);

        Marker davao = map.addMarker(new MarkerOptions().position(DAVAO).title(nombre).snippet(direccion));
        davao.setPosition(lat);
        davao.setIcon(icon);
    }

    private class AsyncListarBanos extends AsyncTask<String, Void, String> {
        String n;
        @Override
        protected String doInBackground(String... urls) {
            return ServidorDeustodia.getBanos();
        }

        @Override
        protected void onPostExecute(String result) {
            try{
                JSONArray jArray = new JSONArray(result);
                Bano n; arrayBanos.clear();
                for(int i=0;i<jArray.length();i++){
                    JSONObject json_data = jArray.getJSONObject(i);
                    n  = new Bano();
                    n.id = json_data.getInt("id");
                    n.nombre = json_data.getString("nombre");
                    n.direccion = json_data.getString("direccion");
                    n.geoX = json_data.getDouble("geox");
                    n.geoY = json_data.getDouble("geoy");

                    agregarMarcador(n.geoX,n.geoY,n.nombre,n.direccion);
                    arrayBanos.add(n);
                }

            }
            catch(JSONException e){
                mostrarMensaje("Woops! "+e.toString());
            }
        }

    }

    /*public double calculationByDistance(GeoPoint StartP, GeoPoint EndP) {
        int Radius=6371;//radius of earth in Km
        double lat1 = StartP.getLatitudeE6()/1E6;
        double lat2 = EndP.getLatitudeE6()/1E6;
        double lon1 = StartP.getLongitudeE6()/1E6;
        double lon2 = EndP.getLongitudeE6()/1E6;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult= Radius*c;
        double km=valueResult/1;
        DecimalFormat newFormat = new DecimalFormat("####");
        kmInDec =  Integer.valueOf(newFormat.format(km));
        meter=valueResult%1000;
        meterInDec= Integer.valueOf(newFormat.format(meter));

        return Radius * c;
    }*/

    public static double distance(LatLng StartP, LatLng EndP) {
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2-lat1);
        double dLon = Math.toRadians(lon2-lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.asin(Math.sqrt(a));
        return 6366000 * c;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean res = activeNetworkInfo != null && activeNetworkInfo.isConnected();
        if(!res){
            mostrarMensaje("Necesitas estar conectado a internet");
        }
        return res;
    }

    public void mostrarMensaje(String s){
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}