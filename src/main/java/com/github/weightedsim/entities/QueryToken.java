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

    public QueryToken(double[] q, double[] w, double tau, int dimension){
        if (q.length != w.length && q.length != dimension){
            throw new RuntimeException("Query Token: length error");
        }
        this.q = q;
        this.w = w;
        this.tau = tau;
        this.dimension = dimension;
    }

}
