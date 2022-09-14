package com.example.myapplication;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;

import com.example.myapplication.async.ClientClass;
import com.example.myapplication.conector.DBHelper;
import com.example.myapplication.model.Eventos;
import com.example.myapplication.model.Medicion;
import com.example.myapplication.model.SpinnerEvento;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Random;


public class MapFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public MapFragment() {
    }
    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private Spinner spinner;
    private ArrayList<Medicion> mediciones;
    private static final String TAG = "MapFragment";
    private Button appButton1;
    private GoogleMap gMap;
    private Switch swt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        swt = view.findViewById(R.id.switch1);
        Spinner spinner = view.findViewById(R.id.appSpinner);
        appButton1 = view.findViewById(R.id.appButton1);

        DBHelper dbHelper = new DBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<SpinnerEvento> spinner_data = new ArrayList<>();
        mediciones = dbHelper.get_medicion();
        int contador = 1;

        for(Medicion med:mediciones) {
            String msg="";
            for(Eventos ev:med.getEventos()) {
                msg = ev.getDb_value() + " | " + ev.getFecha();
            }
            spinner_data.add(new SpinnerEvento(contador, msg));
            contador++;
        }
        ArrayAdapter adapter = new ArrayAdapter<SpinnerEvento>(getContext(), androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item, spinner_data);
        spinner.setAdapter(adapter);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.appMapFragment);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                gMap=googleMap;
                Double la=0.0,lo=0.0;
                boolean guarda=true;
                for(Medicion med:mediciones){
                    if(!guarda){
                        break;
                    }
                    guarda=false;
                    for(Eventos ev:med.getEventos()){
                        String[] latlong= ev.getGps().split(",");
                        String[] lat = latlong[0].split(":");
                        String[] lon= latlong[1].split(":");
                        la= Double.parseDouble(lat[1]);
                        lo= Double.parseDouble(lon[1]);

                        if (swt.isChecked()){
                            String[] latlongMovil = ev.getGpsmovil().split(",");
                            String[] latMovil = latlong[0].split(":");
                            String[] lonMovil = latlong[1].split(":");
                            la = Double.parseDouble(latMovil[1]);
                            lo = Double.parseDouble(lonMovil[1]);
                        }
                        LatLng pos = new LatLng(la, lo);
                        gMap.addMarker(new MarkerOptions().position(pos)
                                .title("EC " + ev.getEc() + " | TEMP " + ev.getTiempo()));
                        Log.d(TAG, "");
                    }
                }
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                if(la == 0.0 && lo == 0.0){
                    la = 37.7157957;
                    lo = -0.8025017;
                }
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(la,lo),12f));

            }
        });
        swt.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String device_name = spinner.getSelectedItem().toString();
                gMap.clear();
                Double la=0.0,lo=0.0;

                for(Medicion med:mediciones) {
                    if (device_name.contains(med.getDb_value())) {
                        for (Eventos ev : med.getEventos()) {
                            String[] latlong = ev.getGps().split(",");
                            String[] lat = latlong[0].split(":");
                            String[] lon = latlong[1].split(":");
                            la = Double.parseDouble(lat[1]);
                            lo = Double.parseDouble(lon[1]);
                            if (swt.isChecked()){
                                String[] latlongMovil = ev.getGpsmovil().split(",");
                                String[] latMovil = latlong[0].split(":");
                                String[] lonMovil = latlong[1].split(":");
                                la = Double.parseDouble(latMovil[1]);
                                lo = Double.parseDouble(lonMovil[1]);
                            }
                            LatLng pos = new LatLng(la, lo);
                            gMap.addMarker(new MarkerOptions().position(pos)
                                    .title("EC " + ev.getEc() + " | TEMP " + ev.getTiempo()));
                        }
                    }
                    gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    if (la == 0.0 && lo == 0.0) {
                        la = 37.7157957;
                        lo = -0.8025017;
                    }
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(la, lo), 12f));
                }
            }
        });
        appButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String device_name = spinner.getSelectedItem().toString();
                gMap.clear();
                Double la=0.0,lo=0.0;
                for(Medicion med:mediciones) {
                    if (device_name.contains(med.getDb_value())) {
                        for (Eventos ev : med.getEventos()) {
                            Log.d(TAG, ev.getGps());
                            String[] latlong = ev.getGps().split(",");
                            String[] lat = latlong[0].split(":");
                            String[] lon = latlong[1].split(":");
                            la = Double.parseDouble(lat[1]);
                            lo = Double.parseDouble(lon[1]);
                            if (swt.isChecked()){
                                String[] latlongMovil = ev.getGpsmovil().split(",");
                                String[] latMovil = latlong[0].split(":");
                                String[] lonMovil = latlong[1].split(":");
                                la = Double.parseDouble(latMovil[1]);
                                lo = Double.parseDouble(lonMovil[1]);
                            }
                            LatLng pos = new LatLng(la, lo);
                            gMap.addMarker(new MarkerOptions().position(pos)
                                    .title("EC " + ev.getEc() + " | TEMP " + ev.getTiempo()));
                        }
                    }
                    gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    if (la == 0.0 && lo == 0.0) {
                        la = 37.7157957;
                        lo = -0.8025017;
                    }
                    gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(la, lo), 12f));
                }
            }
        });
        return view;
    }
}