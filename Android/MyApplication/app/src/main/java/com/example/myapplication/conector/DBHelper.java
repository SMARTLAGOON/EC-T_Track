package com.example.myapplication.conector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.model.Eventos;
import com.example.myapplication.model.Medicion;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NOMBRE = "mediciones.db";
    public static final String TABLE_MEDICIONES = "t_mediciones";
    private Context context;

    public DBHelper(@Nullable Context context){
        super(context, DATABASE_NOMBRE, null, DATABASE_VERSION);
        this.context = context;
    }

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version, @Nullable DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        this.context = context;
    }

    public DBHelper(@Nullable Context context, @Nullable String name, int version, @NonNull SQLiteDatabase.OpenParams openParams) {
        super(context, name, version, openParams);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "CREATE TABLE " + TABLE_MEDICIONES + "(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "fecha TEXT NOT NULL," +
                        "conexion TEXT NOT NULL," +
                        "tiempo TEXT NOT NULL," +
                        "bt TEXT NOT NULL," +
                        "ec TEXT NOT NULL," +
                        "db TEXT NOT NULL," +
                        "google TEXT NOT NULL," +
                        "ve TEXT NOT NULL," +
                        "va TEXT NOT NULL," +
                        "gps TEXT," +
                        "gpsmovil TEXT)"
                );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE " + TABLE_MEDICIONES);
        onCreate(sqLiteDatabase);
    }

    public Cursor getSesiones(String sesion){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("select * from t_mediciones where db=\""+sesion+"\" Order by fecha desc", null);
        return cursor;
    }

    public Cursor getUltimo(String sesion){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery("select * from t_mediciones where db=\""+sesion+"\" Order by fecha desc", null);
        return cursor;
    }

    public Cursor getSesion10(String sesion){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery(" select * from t_mediciones where db=\"" + sesion +"\" order by fecha DESC limit 10" , null);
        return cursor;
    }
    public Cursor getCSV(){
        SQLiteDatabase DB = this.getWritableDatabase();
        Cursor cursor = DB.rawQuery(" select * from t_mediciones" , null);
        return cursor;
    }
    private static final String TAG = "MedicionFragment";


    public ArrayList<Medicion> get_medicion(){

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<Medicion> meds = new ArrayList<>();

        Cursor cursorEventos = null;
        cursorEventos = db.rawQuery(
                "select DISTINCT db from t_mediciones order by fecha DESC limit 4",null
        );

        if(cursorEventos.moveToFirst()){
            do{
                Medicion med = new Medicion();
                String nombre_dispositivo = cursorEventos.getString(0);
                med.setDb_value(nombre_dispositivo);
                ArrayList<Eventos> evs = new ArrayList<>();
                Eventos ev = null;

                Cursor cursorEventos2 = null;
                cursorEventos2 = db.rawQuery(
                        "select * from t_mediciones where db=\""+ nombre_dispositivo +"\" order by fecha DESC limit 10",null
                );

                if(cursorEventos2.moveToFirst()) {
                    do {
                        ev = new Eventos();
                        ev.setFecha(cursorEventos2.getString(1));
                        ev.setConexion(cursorEventos2.getString(2));
                        ev.setTiempo(cursorEventos2.getString(3));
                        ev.setBt(cursorEventos2.getString(4));
                        ev.setEc(cursorEventos2.getString(5));
                        ev.setDb_value(cursorEventos2.getString(6));
                        ev.setGoogle(cursorEventos2.getString(7));
                        ev.setVe(cursorEventos2.getString(8));
                        ev.setVa(cursorEventos2.getString(9));
                        ev.setGps(cursorEventos2.getString(10));
                        ev.setGpsmovil(cursorEventos2.getString(11));
                        evs.add(ev);
                    } while (cursorEventos2.moveToNext());
                    med.setEventos(evs);
                    meds.add(med);
                }
            }while(cursorEventos.moveToNext());
            db.close();
            dbHelper.close();
            return meds;
        }
        db.close();
        dbHelper.close();
        return meds;
    }

    public Boolean insertsondadata(String fecha, String conexion, String tiempo, String bt, String ec, String db, String google, String ve, String va, String gps, String gpsmovil){
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues= new ContentValues();
        contentValues.put("fecha", fecha);
        contentValues.put("conexion", conexion);
        contentValues.put("tiempo", tiempo);
        contentValues.put("bt", bt);
        contentValues.put("ec", ec);
        contentValues.put("db", db);
        contentValues.put("google", google);
        contentValues.put("ve", ve);
        contentValues.put("va", va);
        contentValues.put("gps", gps);
        contentValues.put("gpsmovil", gps);
        long result = DB.insert(TABLE_MEDICIONES, null, contentValues);
        if(result==-1){
            return false;
        }else{
            return true;
        }
    }
}
