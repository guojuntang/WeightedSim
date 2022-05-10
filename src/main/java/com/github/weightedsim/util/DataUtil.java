package com.github.weightedsim.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPrivateKey;
import com.github.SymHomEnc.SHEPublicParameter;
import com.github.SymHomEnc.SymHomEnc;
import com.github.davidmoten.rtreemulti.geometry.Point;
import com.github.weightedsim.Main;
import com.github.weightedsim.entities.EncryptedToken;
import com.github.weightedsim.entities.QueryToken;
import com.github.weightedsim.util.PivotUtil;

public class DataUtil {
    final static double EPSILON = 0.00001d;

    private DataUtil(){
        // no instance
    }

    public static Point createPointFromPivots(List<double[]> pivots, double[] a, int magnification){
        int size = pivots.size();
        double[] indexes = new double[size];
        for (int i = 0; i < size; i++) {
            indexes[i] = negativeInf(pivots.get(i), a) * magnification;
        }
        return Point.create(indexes);
    }

    public static Point createPointFromData(double[] a, int magnification){
        for (int i = 0; i < a.length; i++) {
            a[i] = a[i] * magnification;
        }
        return Point.create(a);
    }

    public static List<double[]> readCsvData(String filename){
        try {
            List<double[]> result = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream(filename)));
            String line;
            int size;
            String[] string_buffer;
            while((line = bufferedReader.readLine()) != null){
                string_buffer = line.split(",");
                size = string_buffer.length;
                double[] tmp = new double[size];
                for (int i = 0; i < size; i++) {
                    tmp[i] = Double.valueOf(string_buffer[i]);
                }
                result.add(tmp);
            }
            return result;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }



    public static boolean checkWeightedQuery(QueryToken queryToken, double[] a){
        double weightedDis = DataUtil.weightedDis(a, queryToken.getQ(), queryToken.getW());
        return (weightedDis <= queryToken.getTau());
    }

    public static SHECipher[] convertAndEncryptVector(double[] a, int magnification, SHEPrivateKey sk){
        return encryptedVector(doubleVectorToBigIntVector(a, magnification), sk);
    }

    public static SHECipher[] encryptedVector(BigInteger[] a, SHEPrivateKey sk){
        int size = a.length;
        SHECipher[] result = new SHECipher[size];
        for (int i = 0; i < size; i++) {
            result[i] = SymHomEnc.enc(a[i], sk);
        }
        return result;
    }


    public static SHECipher[] encryptedVector(int[] a, SHEPrivateKey sk){
        int size = a.length;
        SHECipher[] result = new SHECipher[size];
        for (int i = 0; i < size; i++) {
            result[i] = SymHomEnc.enc(a[i], sk);
        }
        return result;
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

    public static double refineWeightedDis(BigInteger a, int magnification){
        double real_result = DataUtil.bigIntToDouble(a, magnification);
        return Math.sqrt(real_result);
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


    public static SHECipher calEncryptedWeightedEuclideanDis(SHECipher[] x, SHECipher[] q, SHECipher[] w, SHECipher E_mins_1, SHEPublicParameter pb){
        int size = x.length;
        if (size != q.length || size != w.length){
            throw new RuntimeException("Encrypted Weighted Euclidean Distance: length error.");
        }
        SHECipher tmp;
        // result = x[0] - q[0]
        SHECipher result = SymHomEnc.hm_add(x[0], SymHomEnc.hm_mul(E_mins_1, q[0], pb), pb);
        // result = w[0] * result * result
        result = SymHomEnc.hm_mul(w[0], SymHomEnc.hm_mul(result, result, pb), pb);
        for (int i = 1; i < size; i++) {
            // tmp = x[i] - q[i]
            tmp = SymHomEnc.hm_add(x[i], SymHomEnc.hm_mul(E_mins_1, q[i], pb), pb);
            // tmp = w[i] * tmp * tmp
            tmp = SymHomEnc.hm_mul(w[i], SymHomEnc.hm_mul(tmp, tmp, pb), pb);
            result = SymHomEnc.hm_add(result, tmp, pb);
        }
        return result;
    }

    public static BigInteger doubleToBigInt(double a, int magnification){
        return BigInteger.valueOf(Math.round(a * magnification));
    }

    public static double bigIntToDouble(BigInteger a, int minification){
        return a.doubleValue() / minification;
    }

    public static double[] bigIntVectorToDoubleVector(BigInteger[] a, int minification){
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = bigIntToDouble(a[i], minification);
        }
        return result;
    }

    public static BigInteger[] doubleVectorToBigIntVector(double[] a, int magnification){
        BigInteger[] result = new BigInteger[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = doubleToBigInt(a[i], magnification);
        }
        return result;
    }



}
