package com.example.myapplication;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.myapplication.conector.DBHelper;
import com.example.myapplication.model.Eventos;
import com.example.myapplication.model.Medicion;
import com.example.myapplication.model.SpinnerEvento;
import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Chart2Fragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public static Chart2Fragment newInstance(String param1, String param2) {
        Chart2Fragment fragment = new Chart2Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public Chart2Fragment() {
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
    private Button appButton1;
    private static final String TAG = "Chart2Fragment";
    private LineChart lincha;
    private ArrayList<Medicion> mediciones;
    private ArrayList<SpinnerEvento> spinner_data;
    private ArrayList<Entry> datas;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chart2, container, false);
        lincha = view.findViewById(R.id.appLinearChart);
        appButton1 = view.findViewById(R.id.appButton1);
        spinner = view.findViewById(R.id.appSpinner);
        DBHelper dbHelper = new DBHelper(getActivity());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        mediciones = dbHelper.get_medicion();

        spinner_data = new ArrayList<>();

        lincha.setTouchEnabled(true);
        lincha.setPinchZoom(true);
        datas = new ArrayList<>();


        int contador = 1;

        boolean guarda = false;
        for(Medicion med:mediciones) {
            String msg="";
            int count = 0;
            for(Eventos ev:med.getEventos()) {
                msg = ev.getDb_value() + " | " + ev.getFecha();
                if(!guarda){
                    datas.add(new Entry(count, (float) Float.parseFloat(ev.getTiempo())));
                    count++;
                }
            }
            guarda=true;
            spinner_data.add(new SpinnerEvento(contador, msg));
            contador++;
        }

        ArrayAdapter adapter = new ArrayAdapter<SpinnerEvento>(getContext(), androidx.constraintlayout.widget.R.layout.support_simple_spinner_dropdown_item, spinner_data);
        spinner.setAdapter(adapter);


        LineDataSet linDataSet  = new LineDataSet(datas, "Sonda");
        linDataSet.setValueTextColor(Color.BLACK);
        linDataSet.setValueTextSize(10f);
        linDataSet.setValueFormatter(new MyValueFormatter());
        LineData barData = new LineData(linDataSet);
        lincha.setData(barData);
        lincha.getDescription().setText("Ultimos Eventos");
        lincha.animateY(1000);
        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(lincha);
        XAxis xAxis = lincha.getXAxis();

        xAxis.setValueFormatter(xAxisFormatter);
        appButton1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String device_name = spinner.getSelectedItem().toString();
                lincha.clear();
                datas.clear();
                for(Medicion med:mediciones) {
                    if (device_name.contains(med.getDb_value())) {
                        int count =0;
                        for (Eventos ev : med.getEventos()) {
                            Log.d(TAG, ev.getEc());
                            datas.add(new Entry(count, (float) Float.parseFloat(ev.getEc())));
                            Log.d(TAG, "");
                            count++;
                        }


                        LineDataSet linDataSet  = new LineDataSet(datas, "Sonda");
                        linDataSet.setValueTextColor(Color.BLACK);
                        linDataSet.setValueTextSize(16f);
                        linDataSet.setValueFormatter(new MyValueFormatter());

                        LineData barData = new LineData(linDataSet);
                        lincha.setData(barData);
                        lincha.getDescription().setText("Ultimos Eventos");
                        lincha.animateY(1000);
                        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(lincha);
                        XAxis xAxis = lincha.getXAxis();

                        xAxis.setValueFormatter(xAxisFormatter);
                    }
                }
            }
        });



        return view;
    }
    public class DayAxisValueFormatter extends ValueFormatter {
        private final BarLineChartBase<?> chart;
        public DayAxisValueFormatter(BarLineChartBase<?> chart) {
            this.chart = chart;
        }
        @Override
        public String getFormattedValue(float value) {
            return "Medida " + String.valueOf((int)value);
        }
    }
    public class MyValueFormatter extends ValueFormatter {

        private DecimalFormat mFormat;

        public MyValueFormatter() {
            mFormat = new DecimalFormat("#0.00"); // use two decimal
        }

        @Override
        public String getFormattedValue(float value) {
            return mFormat.format(value);
        }
    }
}