package com.github.weightedsim.util;

import java.math.BigInteger;
import com.github.weightedsim.util.PivotUtil;

public class DataUtil {
    final static double EPSILON = 0.0000001d;

    private DataUtil(){
        // no instance
    }

    public static double weightedDis(double[] a, double[] b, double[] w) {

        double dis = 0;
        int d = a.length;

        if (d != b.length || d != w.length){
            throw new RuntimeException("WeightedDis: different length.");
        }
        // sumDouble(w) != 1
        if (Math.abs(1.0 - PivotUtil.sumDouble(w)) > EPSILON){
            throw new RuntimeException("WeightedDis: weight vector error");
        }

        for(int i = 0; i < d; i++) {
            dis = dis + w[i]*(a[i] - b[i])*(a[i] - b[i]);
        }

        dis = Math.sqrt(dis);

        return dis;

    }

    public static double negativeInf(double[] a, double[] b) {

        double minDis = Double.MAX_VALUE;
        int d = a.length;

        if (d != b.length ){
            throw new RuntimeException("WeightedDis: different length.");
        }

        for(int i = 0; i < d; i++) {
            if(Math.abs(a[i] - b[i]) < minDis) {
                minDis = Math.abs(a[i] - b[i]);
            }
        }

        return minDis;

    }

    public static BigInteger doubleToBigInt(double a, int magnification){
        return BigInteger.valueOf((long)(a * magnification));
    }

    public static double bigIntToDouble(BigInteger a, int magnification){
        return a.doubleValue() / magnification;
    }

    public static BigInteger[] doubleVectorToBigIntVector(double[] a, int magnification){
        BigInteger[] result = new BigInteger[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = doubleToBigInt(a[i], magnification);
        }
        return result;
    }



}
