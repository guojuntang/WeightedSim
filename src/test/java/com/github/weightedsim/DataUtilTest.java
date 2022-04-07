package com.github.weightedsim;

import org.junit.Test;
import static org.junit.Assert.*;
import com.github.weightedsim.util.DataUtil;

public class DataUtilTest {
    final static double EPSILON = 0.0000001d;
    @Test
    public void weightedDisTest(){
        double[] a = {1, 2, 3, 4};
        double[] b = {2, 3, 4, 5};
        double[] w = {0.1, 0.5 ,0.3, 0.1};

        double result = DataUtil.weightedDis(a, b, w);
        assertEquals(1.0, result, EPSILON);
    }

    @Test(expected = RuntimeException.class)
    public void lengthExceptionTest(){
        double[] a = {1, 2, 3, 4};
        double[] b = {2, 3, 4};
        double[] w = {0.1, 0.5 ,0.3, 0.1};

        DataUtil.weightedDis(a, b, w);
    }

    @Test(expected = RuntimeException.class)
    public void weightVectorException(){
        double[] a = {1, 2, 3, 4};
        double[] b = {2, 3, 4, 4};
        double[] w = {0.1, 0.5 ,0.3, 0.2};

        DataUtil.weightedDis(a, b, w);
    }
}
