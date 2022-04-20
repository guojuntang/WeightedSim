package com.github.weightedsim.entities;

public class QueryToken {
    private int dimension;
    private double[] q;
    private double[] w;
    private double tau;

    public int getDimension() {
        return dimension;
    }

    public double getTau() {
        return tau;
    }

    public double[] getQ() {
        return q;
    }

    public double[] getW() {
        return w;
    }

    public QueryToken(double[] q, double[] w, double tau){
        this.dimension = q.length;
        if (q.length != w.length){
            throw new RuntimeException("Query Token: length error");
        }
        this.q = q;
        // TODO: check summary is equal to 1
        this.w = w;
        this.tau = tau;
    }

}
