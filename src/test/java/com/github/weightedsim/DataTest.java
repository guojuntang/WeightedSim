package com.github.weightedsim;

import com.github.SymHomEnc.*;
import com.github.weightedsim.entities.AssistServer;
import com.github.weightedsim.entities.OutsourceServer;
import com.github.weightedsim.privacyprotocol.DLESSProtocol;
import com.github.weightedsim.privacyprotocol.DWITHINProtocol;
import com.github.weightedsim.privacyprotocol.SLESSEProtocol;
import com.github.weightedsim.util.AES;
import org.junit.Test;
import static org.junit.Assert.*;
import com.github.weightedsim.util.DataUtil;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class DataTest {
    private static final SHEParameters param = new SHEParameters(SHEParameters.K0, SHEParameters.K1, SHEParameters.K2);
    private static final SHEPrivateKey sk = param.getSHEPrivateKey();
    private static final SHEPublicParameter pb = param.getSHEPublicParameter();
    private static final SHECipher E_mins_1= SymHomEnc.enc(-1, sk);
    private static final double EPSILON = 0.0000001d;

    @Test
    public void weightedDisTest(){
        double[] a = {1, 2, 3, 4};
        double[] b = {2, 3, 4, 5};
        double[] w = {0.1, 0.5 ,0.3, 0.1};

        double result = DataUtil.weightedDis(a, b, w);
        assertEquals(1.0, result, EPSILON);
    }



    @Test
    public void AESTest(){
        SecretKey secretKey = AES.getRandomKey(128);
        BigInteger a = BigInteger.valueOf(321);
        byte[] cipher = AES.encrypt(a.toByteArray(), secretKey);
        byte[] plaintext = AES.decrypt(cipher, secretKey);
        BigInteger result = new BigInteger(plaintext);
        assertEquals(0, result.compareTo(a));
    }

    @Test
    public void AESTest2(){
        SecretKey secretKey = AES.getRandomKey(128);
        String a = "test";
        byte[] cipher = AES.encrypt(a.getBytes(StandardCharsets.UTF_8), secretKey);
        byte[] plaintext = AES.decrypt(cipher, secretKey);
        assertEquals(a, new String(plaintext));

    }

    @Test
    public void encWeightedDisTest(){
        double[] a = {5, 2, 3, 4};
        double[] b = {2, 3, 4, 5};
        double[] w = {0.1, 0.5 ,0.3, 0.1};

        SHECipher[] a0 = {
                SymHomEnc.enc(DataUtil.doubleToBigInt(a[0], 1), sk),
                SymHomEnc.enc(DataUtil.doubleToBigInt(a[1], 1), sk),
                SymHomEnc.enc(DataUtil.doubleToBigInt(a[2], 1), sk),
                SymHomEnc.enc(DataUtil.doubleToBigInt(a[3], 1), sk),
        };


        SHECipher[] b0 = {
                SymHomEnc.enc(DataUtil.doubleToBigInt(b[0], 1), sk),
                SymHomEnc.enc(DataUtil.doubleToBigInt(b[1], 1), sk),
                SymHomEnc.enc(DataUtil.doubleToBigInt(b[2], 1), sk),
                SymHomEnc.enc(DataUtil.doubleToBigInt(b[3], 1), sk),
        };

        SHECipher[] w0 = {
                SymHomEnc.enc(DataUtil.doubleToBigInt(w[0], 100), sk),
                SymHomEnc.enc(DataUtil.doubleToBigInt(w[1], 100), sk),
                SymHomEnc.enc(DataUtil.doubleToBigInt(w[2], 100), sk),
                SymHomEnc.enc(DataUtil.doubleToBigInt(w[3], 100), sk),
        };

        SHECipher result = DataUtil.calEncryptedWeightedEuclideanDis(a0, b0, w0, E_mins_1, pb);
        double real_result = DataUtil.refineWeightedDis(SymHomEnc.dec(result, sk), 100);
        assertEquals(DataUtil.weightedDis(a, b, w), real_result, EPSILON);
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
