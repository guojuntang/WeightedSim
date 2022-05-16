package com.github.weightedsim.util;

import com.github.weightedsim.util.DataUtil;

import java.util.ArrayList;
import java.util.List;

public class PivotUtil {
    // The pivot is abandoned
    private PivotUtil(){
        // No instance
    }

    public static List<double[]> choosePivots(List<double[]> dataList, int k) {
        List<double[]> pivots = new ArrayList<double[]>();
        int n = dataList.size();

        // choose the first pivot
        int index = 0;
        double dis = sumDouble(dataList.get(index));

        for(int i = 2; i < n; i++) {

            double distance = sumDouble(dataList.get(i));
            if(distance <= dis) {
                index = i;
                dis = distance;
            }

        }

        pivots.add(dataList.get(index));

        // choose another pivots
        for(int i = 1; i < k; i++) {
            int chosenIndex = chooseOnePivot(dataList, pivots);
            pivots.add(dataList.get(chosenIndex));
        }

        return pivots;

    }

    public static double sumDouble(double[] a) {

        int d = a.length;
        double sum = 0;
        for(int i = 0; i < d; i++) {
            sum = sum + a[i];
        }

        return sum;
    }

    public static int chooseOnePivot(List<double[]> dataList, List<double[]> pivots) {

        int index = 0;
        double dis = 0;

        for(int i = 0; i < dataList.size(); i++) {

            double distance = computeRecordPivotsDis(dataList.get(i), pivots);

            if(distance > dis) {

                dis = distance;
                index = i;
            }
        }

        return index;

    }

    public static double computeRecordPivotsDis(double[] record, List<double[]> pivots) {

        double dis = 0;
        int len = pivots.size();
        for(int i = 0; i < len; i++) {
            dis = dis + DataUtil.negativeInf(record, pivots.get(i));
        }

        return dis;

    }

}
