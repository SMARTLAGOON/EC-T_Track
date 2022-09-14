package com.example.myapplication.model;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.metrics.Event;

import androidx.annotation.Nullable;
import com.example.myapplication.conector.DBHelper;

import java.util.ArrayList;

public class Medicion {

    private String db_value;
    private ArrayList<Eventos> eventos;

    public Medicion() {
    }


    public String getDb_value() {
        return db_value;
    }

    public void setDb_value(String db_value) {
        this.db_value = db_value;
    }

    public ArrayList<Eventos> getEventos() {
        return eventos;
    }

    public void setEventos(ArrayList<Eventos> eventos) {
        this.eventos = eventos;
    }

}
