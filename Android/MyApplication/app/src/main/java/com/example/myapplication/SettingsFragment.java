package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.location.Location;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.myapplication.async.ClientClass;
import com.example.myapplication.conector.DBHelper;
import com.example.myapplication.model.DispositivosBT;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class SettingsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;
    private Button appButtonConnect;
    private Button appButtonDesconnect;
    private static final String TAG = "SettingsFragment";


    public SettingsFragment() {
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
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

    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    static final int STATE_MESSAGE_RECEIVED = 5;
    private Spinner spinner;
    BluetoothAdapter mBlueAdapter;
    Set<BluetoothDevice> devices;
    BluetoothDevice[] btArray;
    Handler handler;
    ClientClass cliente_bt;
    TextView appDebug;
    ArrayList<DispositivosBT> spinner_data;
    String identificador;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    @SuppressLint("MissingPermission")
    public void discover(){
        if (mBlueAdapter.isEnabled()) {
            devices = mBlueAdapter.getBondedDevices();

            spinner_data = new ArrayList<>();
            btArray=new BluetoothDevice[devices.size()];

            int index=0;
            for (BluetoothDevice device : devices) {

                btArray[index]= device;
                spinner_data.add(new DispositivosBT(index, device.getName()));
                index++;
            }
        }
        else {
            Log.d(TAG, "Bluetooth apagado");
        }
    }

    DBHelper dbHelper;
    SQLiteDatabase db;
    private FusedLocationProviderClient fusedLocationClient;
    double lat = 0.0;
    double lon = 0.0;
    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());

        dbHelper= new DBHelper(getActivity());
        db = dbHelper.getWritableDatabase();
        Spinner spinner = view.findViewById(R.id.appSpinnerBT);
        appButtonConnect = view.findViewById(R.id.appButtonConnect);
        appButtonDesconnect = view.findViewById(R.id.appButtonDesconnect);
        appDebug = view.findViewById(R.id.appDebug);
        mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
        discover();
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            lat = location.getLatitude();
                            lon = location.getLongitude();
                        }
                    }
                });
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what)
                {
                    case STATE_LISTENING:
                        Log.d(TAG, "cancel: STATE_LISTENING.");
                        break;
                    case STATE_CONNECTING:
                        Log.d(TAG, "cancel: STATE_CONNECTING.");
                        break;
                    case STATE_CONNECTED:
                        Log.d(TAG, "cancel: STATE_CONNECTED.");
                        break;
                    case STATE_CONNECTION_FAILED:
                        Log.d(TAG, "cancel: STATE_CONNECTION_FAILED.");
                        break;
                    case STATE_MESSAGE_RECEIVED:
                        byte[] readBuff= (byte[]) msg.obj;
                        String tempMsg=new String(readBuff,0,msg.arg1);

                        String[] values = tempMsg.split(";");
                        Boolean checkInsert=false;
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String strDate = sdf.format(c.getTime());
                        if(values.length==8){
                            checkInsert = true;
                            if(checkInsert){
                                checkInsert = dbHelper.insertsondadata( strDate, "TRUE", values[5], "TRUE", values[6], identificador, values[0], values[3], values[4], "lat:" + values[1] + "," + "lon:" + values[2], "lat:" + String.valueOf(lat) + "," + "lon:" + String.valueOf(lon)) ;
                            }else{
                                checkInsert = dbHelper.insertsondadata( strDate, "FALSE", values[5], "TRUE", values[6], identificador, values[0], values[3], values[4], values[1] + "," + values[2], "lat:" + String.valueOf(lat) + "," + "lon:" + String.valueOf(lon)) ;
                            }

                        }
                        if(values.length >= 7) {
                            tempMsg = "Mensaje RAW: \n" + tempMsg + "\n";
                            tempMsg += "Mensaje Formateado:\n";
                            tempMsg += "Código GPS: " + values[0] + "\n";
                            tempMsg += "Sonda Lat: " + values[1] + "\n";
                            tempMsg += "Sonda Lon: " + values[2] + "\n";
                            tempMsg += "Sonda AnalogAverage: " + values[3] + "\n";
                            tempMsg += "Sonda averageVoltage: " + values[4] + "\n";
                            tempMsg += "Sonda Temperature: " + values[5] + "\n";
                            tempMsg += "Sonda EC: " + values[6] + "\n";
                            tempMsg += "Movil Lat: " + String.valueOf(lat) + "\n";
                            tempMsg += "Movil Lon: " + String.valueOf(lon) + "\n";
                        }
                        appDebug.setText(tempMsg);
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + msg.what);
                }
            }
        };

        if(cliente_bt!=null){
            appButtonConnect.setBackgroundColor(Color.RED);
        }
        ArrayAdapter adapter = new ArrayAdapter<DispositivosBT>(getContext(), androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item, spinner_data);
        spinner.setAdapter(adapter);
        appButtonConnect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String device_name = spinner.getSelectedItem().toString();
                if (cliente_bt==null && device_name.contains("HC-0")) {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String strDate = sdf.format(c.getTime());
                    int min = 10000;
                    int max = 99999;
                    int random = new Random().nextInt((max - min) + 1) + min;

                    identificador="R-" + String.valueOf(random);
                    int index = spinner.getSelectedItemPosition();
                    appButtonConnect.setBackgroundColor(Color.RED);
                    cliente_bt = new ClientClass(btArray[index], handler);
                    cliente_bt.start();
                }
            }
        });
        appButtonDesconnect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(cliente_bt!=null){
                    cliente_bt.stop_th();
                    cliente_bt = null;
                    int blue = Color.parseColor("#03A9F4");
                    appButtonConnect.setBackgroundColor(blue);
                    mBlueAdapter = BluetoothAdapter.getDefaultAdapter();
                    discover();
                    ArrayAdapter adapter = new ArrayAdapter<DispositivosBT>(getContext(), androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item, spinner_data);
                    spinner.setAdapter(adapter);
                }
            }
        });
        return view;
    }
}