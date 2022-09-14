package com.example.myapplication;

import static android.app.ProgressDialog.show;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.conector.DBHelper;

import java.io.File;
import java.io.FileWriter;

public class StyleFragment extends Fragment {
    private boolean status_conexion = false;
    private boolean status_gps = false;
    private boolean status_bt = false;
    private boolean status_ec = false;
    private boolean status_temp = false;
    private boolean status_bd = false;
    private boolean status_service = false;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public StyleFragment() {
    }

    public static StyleFragment newInstance(String param1, String param2) {
        StyleFragment fragment = new StyleFragment();
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

    public boolean isStatus_service() {
        return status_service;
    }

    public void setStatus_service(boolean status_service) {
        this.status_service = status_service;
    }

    public boolean isStatus_bd() {
        return status_bd;
    }

    public void setStatus_bd(boolean status_bd) {
        this.status_bd = status_bd;
    }

    public boolean get_status_conexion(){
        return true;
    }

    public boolean get_status_gps(){
        return true;
    }

    public boolean get_status_bt(){
        return true;
    }

    public boolean get_status_ec(){
        return true;
    }

    public boolean get_status_temp(){
        return true;
    }

    private Button appButton1;

    private Button appButtonExport;
    private Button appButton2;
    private TextView appDebug;
    private TextView textViewStatus;
    @Override
    public void onStart() {
        super.onStart();
        // rest of the code
        status_conexion=false;
        status_temp=false;
        status_bt=false;
        status_ec=false;
        status_bd=false;
        status_service=false;
        status_gps=false;
        textViewStatus.setText(get_sensores());

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_style, container, false);
        textViewStatus = view.findViewById(R.id.appStatusView);
        View view2 = inflater.inflate(R.layout.fragment_settings, container, false);
        appDebug = view2.findViewById(R.id.appDebug);
        appButton1 = view.findViewById(R.id.appButton1);
        appButtonExport = view.findViewById(R.id.appButtonExport);

        appButtonExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                exportarCSV();
            }
        });
        appButton1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                DBHelper dbHelper= new DBHelper(getActivity());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String msg = appDebug.getText().toString();
                String[] values = msg.split(";");
                if(values.length!=8){
                    Toast.makeText(getActivity(), "Mensaje no valido", Toast.LENGTH_LONG).show();
                }else{

                    String  out_msg = get_sensores();
                    if(values[0]=="G"){
                        status_conexion=true;
                    }

                    if(values[6]=="G"){
                        status_temp=true;
                    }

                    if(values[0]=="A"){
                        status_bt=true;
                    }

                    if(values[5]=="G"){
                        status_ec=true;
                    }

                    if(db!=null){
                        status_bd=true;
                    }

                    if(values[0]=="G"){
                        status_service=true;
                    }

                    if(values[1]!="000000"){
                        status_gps=true;
                    }
                    Toast.makeText(getActivity(), "Estado actualizado", Toast.LENGTH_LONG).show();
                }
                if(db==null){
                    Toast.makeText(getActivity(), "Base De Datos Creada", Toast.LENGTH_LONG).show();
                }

                textViewStatus.setText(get_sensores());
            }

        });
        appButton2 = view.findViewById(R.id.appButton2);
        appButton2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                DBHelper dbHelper= new DBHelper(getActivity());
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                if(db!=null){
                    Toast.makeText(getActivity(), "Base De Datos Creada", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity(), "Base De Datos ERROR al crear", Toast.LENGTH_LONG).show();
                }


            }
        });
        textViewStatus.setText(this.get_sensores());
        return view;
    }
    public void exportarCSV() {
        File carpeta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String archivoAgenda = carpeta.toString() + "/" + "Mediciones.csv";
        Toast.makeText(getActivity(), archivoAgenda, Toast.LENGTH_LONG).show();
        try {
            FileWriter fileWriter = new FileWriter(archivoAgenda);

            DBHelper dbHelper= new DBHelper(getActivity());

            SQLiteDatabase DB = dbHelper.getWritableDatabase();
            Cursor fila = DB.rawQuery("select * from t_mediciones" , null);

            if(fila != null && fila.getCount() != 0) {

                fileWriter.append("ID");
                fileWriter.append(";");
                fileWriter.append("FECHA");
                fileWriter.append(";");
                fileWriter.append("CONEXION");
                fileWriter.append(";");
                fileWriter.append("TEMP");
                fileWriter.append(";");
                fileWriter.append("BT");
                fileWriter.append(";");
                fileWriter.append("EC");
                fileWriter.append(";");
                fileWriter.append("DB");
                fileWriter.append(";");
                fileWriter.append("GOOGLE");
                fileWriter.append(";");
                fileWriter.append("VE");
                fileWriter.append(";");
                fileWriter.append("VA");
                fileWriter.append(";");
                fileWriter.append("GPS");
                fileWriter.append(";");
                fileWriter.append("GPSMOVIL");
                fileWriter.append("\n");

                fila.moveToFirst();
                do {
                    fileWriter.append(fila.getString(0));
                    fileWriter.append(";");
                    fileWriter.append(fila.getString(1));
                    fileWriter.append(";");
                    fileWriter.append(fila.getString(2));
                    fileWriter.append(";");
                    fileWriter.append(fila.getString(3));
                    fileWriter.append(";");
                    fileWriter.append(fila.getString(4));
                    fileWriter.append(";");
                    fileWriter.append(fila.getString(5));
                    fileWriter.append(";");
                    fileWriter.append(fila.getString(6));
                    fileWriter.append(";");
                    fileWriter.append(fila.getString(7));
                    fileWriter.append(";");
                    fileWriter.append(fila.getString(8));
                    fileWriter.append(";");
                    fileWriter.append(fila.getString(9));
                    fileWriter.append(";");
                    fileWriter.append(fila.getString(10));
                    fileWriter.append(";");
                    fileWriter.append(fila.getString(11));
                    fileWriter.append("\n");

                } while(fila.moveToNext());
            } else {
                Toast.makeText(getActivity(), "No hay registros.", Toast.LENGTH_LONG).show();
            }

            DB.close();
            fileWriter.close();
            Toast.makeText(getActivity(), "SE CREO EL ARCHIVO CSV EXITOSAMENTE", Toast.LENGTH_LONG).show();

        } catch (Exception e) { }
    }
    public String get_sensores(){
        return   "$> El sensor de conexion: \n$>  " + Boolean.toString(this.status_conexion) + "....................................\n" +
                 "$> El sensor de temp: \n$>  " + Boolean.toString(this.status_temp) + "....................................\n" +
                 "$> El sensor de BT: \n$>  " + Boolean.toString(this.status_bt) + "....................................\n" +
                 "$> El sensor de EC: \n$>  " + Boolean.toString(this.status_ec) + "....................................\n" +
                 "$> El sensor de GPS: \n$>  " + Boolean.toString(this.status_gps) + "....................................";
    }

    public boolean getStatus_conexion() {
        return status_conexion;
    }

    public void setStatus_conexion(boolean status_conexion) {
        this.status_conexion = status_conexion;
    }

    public boolean getStatus_gps() {
        return status_gps;
    }

    public void setStatus_gps(boolean status_gps) {
        this.status_gps = status_gps;
    }

    public boolean getStatus_bt() {
        return status_bt;
    }

    public void setStatus_bt(boolean status_bt) {
        this.status_bt = status_bt;
    }

    public boolean getStatus_ec() {
        return status_ec;
    }

    public void setStatus_ec(boolean status_ec) {
        this.status_ec = status_ec;
    }

    public boolean getStatus_temp() {
        return status_temp;
    }

    public void setStatus_temp(boolean status_temp) {
        this.status_temp = status_temp;
    }

}