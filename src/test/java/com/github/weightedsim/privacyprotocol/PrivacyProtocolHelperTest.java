package com.github.weightedsim.privacyprotocol;

import com.github.SymHomEnc.*;
import org.junit.Test;
import static org.junit.Assert.*;

import java.math.BigInteger;
import java.security.SecureRandom;

public class PrivacyProtocolHelperTest {
    private static final SecureRandom rnd = new SecureRandom();
    private static final SHEParameters param = new SHEParameters(SHEParameters.K0, SHEParameters.K1, SHEParameters.K2);
    private static final SHEPrivateKey sk = param.getSHEPrivateKey();
    private static final SHEPublicParameter pb = param.getSHEPublicParameter();

    private static final int[][] SLESSEMessageVector = {
        {56,65,2,-2, 100, -20, -100},   // m1
        {23,100 ,2, -2, -100, -10, -1000} // m2
    };
    private static final int[] SLESSEResultVector = {
            0,1,1,1,0,1,0
    };

    private static final int[][] DWITHINMessageVector = {
            {56, 65, 2, 2, 30, -100, 20, -100, -1, -30},
            {23, 65 , 90, 18, 140, -10, -10, -10, 20, 100},
            {100, 200, 180, 18, 100, 100, 30, -1, 300, -10}
    };
    private static final boolean[] DWITHINResultVector = {
            false, true, true, true, false, true, false, true, true, false
    };

    private static BigInteger[][] getRandomNumberVector(int len){
        BigInteger[][] result = new BigInteger[2][len];

        BigInteger r1;
        BigInteger r2;
        BigInteger tmp;


        for (int i = 0; i < result[0].length; i++) {

            while (true){
                r1 = new BigInteger(pb.getK1(), rnd);
                r2 = new BigInteger(pb.getK1(), rnd);
                if (r1.compareTo(BigInteger.ZERO) == 1 && r2.compareTo(BigInteger.ZERO) == 1)
                    break;
            }

            // make sure r1 > r2
            switch (r1.compareTo(r2)){
                case 0:
                    r1 = r1.add(BigInteger.ONE);
                    break;
                case 1:
                    break;
                case -1:
                    tmp = r1;
                    r1 = r2;
                    r2 = tmp;
                    break;
            }

            result[0][i] = r1;
            result[1][i] = r2;

        }
        return result;
    }


    @Test
    public void DLESSETest(){

        SHECipher E_m1;
        SHECipher E_m2 ;

        SHECipher p1_result;
        boolean p2_result;
        boolean p3_result;

        SHECipher E_mins_1 = SymHomEnc.enc(-1, sk);

        BigInteger[][] ran_list = getRandomNumberVector(SLESSEResultVector.length);
        int[] result_list = new int[SLESSEResultVector.length];


        // coin_flip = true
        for (int i = 0; i < SLESSEResultVector.length; i++) {
            E_m1 = SymHomEnc.enc(SLESSEMessageVector[0][i], sk);
            E_m2 = SymHomEnc.enc(SLESSEMessageVector[1][i], sk);

            p1_result = PrivacyProtocolHelper.DLESSEPhrase1(E_m1, E_m2, E_mins_1, ran_list[0][i], ran_list[1][i], true,pb);
            p2_result = PrivacyProtocolHelper.DLESSEPhrase2(p1_result, sk);
            p3_result = PrivacyProtocolHelper.DLESSEPhrase3(p2_result, E_mins_1, true, pb);

            result_list[i] = (p3_result)? 1 : 0;
        }
        assertArrayEquals(SLESSEResultVector, result_list);

        // coin_flip = false
        for (int i = 0; i < SLESSEMessageVector.length; i++) {
            E_m1 = SymHomEnc.enc(SLESSEMessageVector[0][i], sk);
            E_m2 = SymHomEnc.enc(SLESSEMessageVector[1][i], sk);

            p1_result = PrivacyProtocolHelper.DLESSEPhrase1(E_m1, E_m2, E_mins_1, ran_list[0][i], ran_list[1][i], false,pb);
            p2_result = PrivacyProtocolHelper.DLESSEPhrase2(p1_result, sk);
            p3_result = PrivacyProtocolHelper.DLESSEPhrase3(p2_result, E_mins_1, false, pb);

            result_list[i] = (p3_result)? 1 : 0;
        }
        assertArrayEquals(SLESSEResultVector, result_list);
    }

    @Test
    public void SLESSETest(){

        SHECipher E_m1;
        SHECipher E_m2 ;

        SHECipher p1_result;
        SHECipher p2_result;
        SHECipher p3_result;

        SHECipher E_mins_1 = SymHomEnc.enc(-1, sk);

        BigInteger[][] ran_list = getRandomNumberVector(SLESSEResultVector.length);
        int[] result_list = new int[SLESSEResultVector.length];


        // coin_flip = true
        for (int i = 0; i < SLESSEResultVector.length; i++) {
            E_m1 = SymHomEnc.enc(SLESSEMessageVector[0][i], sk);
            E_m2 = SymHomEnc.enc(SLESSEMessageVector[1][i], sk);

            p1_result = PrivacyProtocolHelper.SLESSEPhrase1(E_m1, E_m2, E_mins_1, ran_list[0][i], ran_list[1][i], true,pb);
            p2_result = PrivacyProtocolHelper.SLESSEPhrase2(p1_result, sk);
            p3_result = PrivacyProtocolHelper.SLESSEPhrase3(p2_result, E_mins_1, true, pb);

            result_list[i] = SymHomEnc.dec(p3_result, sk).intValue();
        }
        assertArrayEquals(SLESSEResultVector, result_list);

        // coin_flip = false
        for (int i = 0; i < SLESSEMessageVector.length; i++) {
            E_m1 = SymHomEnc.enc(SLESSEMessageVector[0][i], sk);
            E_m2 = SymHomEnc.enc(SLESSEMessageVector[1][i], sk);

            p1_result = PrivacyProtocolHelper.SLESSEPhrase1(E_m1, E_m2, E_mins_1,ran_list[0][i], ran_list[1][i], false,pb);
            p2_result = PrivacyProtocolHelper.SLESSEPhrase2(p1_result,  sk);
            p3_result = PrivacyProtocolHelper.SLESSEPhrase3(p2_result, E_mins_1, false, pb);

            result_list[i] = SymHomEnc.dec(p3_result, sk).intValue();
        }
        assertArrayEquals(SLESSEResultVector, result_list);
    }


    @Test
    public void DWITHINTest(){

        SHECipher E_m1;
        SHECipher E_m2 ;
        SHECipher E_m3 ;

        SHECipher p1_result;
        boolean p2_result;
        boolean p3_result;

        SHECipher E_mins_1 = SymHomEnc.enc(-1, sk);

        BigInteger[][] ran_list = getRandomNumberVector(DWITHINResultVector.length);
        boolean[] result_list = new boolean[DWITHINResultVector.length];


        // coin_flip = true
        for (int i = 0; i < DWITHINResultVector.length; i++) {
            E_m1 = SymHomEnc.enc(DWITHINMessageVector[0][i], sk);
            E_m2 = SymHomEnc.enc(DWITHINMessageVector[1][i], sk);
            E_m3 = SymHomEnc.enc(DWITHINMessageVector[2][i], sk);

            p1_result = PrivacyProtocolHelper.DWITHINPhrase1(E_m1, E_m2, E_m3, E_mins_1, ran_list[0][i], ran_list[1][i], true,pb);
            p2_result = PrivacyProtocolHelper.DWITHINPhrase2(p1_result, sk);
            p3_result = PrivacyProtocolHelper.DWITHINPhrase3(p2_result,true);

            result_list[i] = p3_result;
        }
        assertArrayEquals(DWITHINResultVector, result_list);

        // coin_flip = false
        for (int i = 0; i < DWITHINResultVector.length; i++) {
            E_m1 = SymHomEnc.enc(DWITHINMessageVector[0][i], sk);
            E_m2 = SymHomEnc.enc(DWITHINMessageVector[1][i], sk);
            E_m3 = SymHomEnc.enc(DWITHINMessageVector[2][i], sk);

            p1_result = PrivacyProtocolHelper.DWITHINPhrase1(E_m1, E_m2, E_m3, E_mins_1, ran_list[0][i], ran_list[1][i], false,pb);
            p2_result = PrivacyProtocolHelper.DWITHINPhrase2(p1_result, sk);
            p3_result = PrivacyProtocolHelper.DWITHINPhrase3(p2_result,false);

            result_list[i] = p3_result;
        }
        assertArrayEquals(DWITHINResultVector, result_list);
    }

}
