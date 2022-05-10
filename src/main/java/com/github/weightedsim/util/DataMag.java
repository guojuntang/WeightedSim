package com.github.weightedsim.util;

/**
 * Data Magnification
 */
public class DataMag {
    private int data_mag;
    private int w_mag;
    private int tau_mag;

    public DataMag(int data_mag, int w_mag, int tau_mag){
        this.data_mag = data_mag;
        this.w_mag = w_mag;
        this.tau_mag = tau_mag;
    }

    public int getData_mag() {
        return data_mag;
    }

    public int getW_mag() {
        return w_mag;
    }

    public int getTau_mag() {
        return tau_mag;
    }
}
