package com.example.myapplication.model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

public class Sesion {
    private int id;
    private String hash_id;
    private Date time_ini;
    private Date time_fin;

    public Sesion(String hash_id, Date time_ini, Date time_fin) {
        this.hash_id = hash_id;
        this.time_ini = time_ini;
        this.time_fin = time_fin;
    }

    public Sesion() {
        this.hash_id = md5(new String(String.valueOf(id)));
        long ahora = System.currentTimeMillis();
        this.time_ini = new Date(ahora); ;
        this.time_fin = new Date(ahora); ;
    }

    public String md5(String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHash_id() {
        return hash_id;
    }

    public void setHash_id(String hash_id) {
        this.hash_id = hash_id;
    }

    public Date getTime_ini() {
        return time_ini;
    }

    public void setTime_ini(Date time_ini) {
        this.time_ini = time_ini;
    }

    public Date getTime_fin() {
        return time_fin;
    }

    public void setTime_fin(Date time_fin) {
        this.time_fin = time_fin;
    }


}
