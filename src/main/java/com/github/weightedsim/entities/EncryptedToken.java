package com.github.weightedsim.entities;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPublicKey;
import com.github.SymHomEnc.SymHomEnc;
import com.github.weightedsim.encryptedrtree.geometry.EncryptedRectangle;
import com.github.weightedsim.util.AES;
import com.github.weightedsim.util.DataUtil;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.List;


public class EncryptedToken {
    private static final int AES_KEY_SIZE = 128;
    private SHECipher[] EncryptedQ;
    private SHECipher[] EncryptedW;
    private SHECipher EncryptedTauSquare;
    private EncryptedRectangle encryptedRectangle;
    private SecretKey ssk;

    public EncryptedToken(QueryToken token, SHEPublicKey pk, int data_mag, int w_mag, int tau_mag){
        int len = token.getDimension();
        this.EncryptedQ = encryptedVectorHelper(len, token.getQ(), pk, data_mag);
        this.EncryptedW = encryptedVectorHelper(len, token.getW(), pk, w_mag);
        this.EncryptedTauSquare = encryptedTauHelper(token.getTau(), pk, tau_mag);
        this.encryptedRectangle = encryptedRectangleHelper(token.getQ(), token.getW(),token.getTau(),  pk, data_mag);
        this.ssk = secretKeyHelper(AES_KEY_SIZE);
    }

    private static SecretKey secretKeyHelper(int keySize){
        return AES.getRandomKey(keySize);
    }

    private static SHECipher encryptedTauHelper(double tau, SHEPublicKey pk, int tau_mag){
        SHECipher a = SymHomEnc.enc(DataUtil.doubleToBigInt(tau, tau_mag), pk );
        return SymHomEnc.hm_mul(a, a, pk.getPublicParameter());
    }

    private static EncryptedRectangle encryptedRectangleHelper(double[] q, double[] w, double tau,  SHEPublicKey pk, int data_mag){
        int rectangle_dimension = q.length;
        BigInteger[] maxes = new BigInteger[rectangle_dimension];
        BigInteger[] mins =  new BigInteger[rectangle_dimension];
        for (int i = 0; i <rectangle_dimension ; i++) {
            double tmp = tau/Math.sqrt(w[i]);
            mins[i] = DataUtil.doubleToBigInt(q[i] - tmp, data_mag);
            maxes[i] = DataUtil.doubleToBigInt(q[i] + tmp, data_mag);
        }
        return new EncryptedRectangle(maxes, mins, pk);
    }

    private static SHECipher[] encryptedVectorHelper(int len, double[] a, SHEPublicKey pk, int mag){
        SHECipher[] result = new SHECipher[len];
        BigInteger[] vector_list = DataUtil.doubleVectorToBigIntVector(a, mag);
        for (int i = 0; i < len; i++) {
            result[i] = SymHomEnc.enc(vector_list[i], pk);
        }
        return result;
    }

    public EncryptedRectangle getEncryptedRectangle() {
        return encryptedRectangle;
    }

    public SHECipher getEncryptedTauSquare() {
        return EncryptedTauSquare;
    }

    public SHECipher[] getEncryptedQ() {
        return EncryptedQ;
    }

    public SecretKey getSsk() {
        return ssk;
    }

    public SHECipher[] getEncryptedW() {
        return EncryptedW;
    }
}
