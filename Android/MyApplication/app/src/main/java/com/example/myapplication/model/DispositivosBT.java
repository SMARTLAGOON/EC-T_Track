package com.example.myapplication.model;

public class DispositivosBT {
    int index;

    @Override
    public String toString() {
        return this.value;
    }

    String value;

    public DispositivosBT(int index, String value) {
        this.index = index;
        this.value = value;
    }

    public DispositivosBT() {
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
