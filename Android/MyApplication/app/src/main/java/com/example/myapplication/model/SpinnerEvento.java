package com.example.myapplication.model;

public class SpinnerEvento {
    private int id;
    private String value;

    public SpinnerEvento() {
    }
    public SpinnerEvento(int id, String value) {
        this.id = id;
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
