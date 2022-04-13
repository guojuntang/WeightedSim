package com.github.weightedsim.entities;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPublicKey;
import com.github.SymHomEnc.SymHomEnc;
import com.github.weightedsim.encryptedrtree.geometry.EncryptedRectangle;
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
    private SHECipher EncryptedTau;
    private EncryptedRectangle encryptedRectangle;
    private static final SecureRandom rnd = new SecureRandom();
    private SecretKey ssk;

    // todo: set up ssk
    public EncryptedToken(QueryToken token, List<double[]> pivots, SHEPublicKey pk, int data_mag, int w_mag, int tau_mag){
        int len = token.getDimension();
        if (pivots.get(0).length != len){
            throw new RuntimeException("Encrypted Token: length error.");
        }
        this.EncryptedQ = encryptedVectorHelper(len, token.getQ(), pk, data_mag);
        this.EncryptedW = encryptedVectorHelper(len, token.getW(), pk, w_mag);
        this.EncryptedTau = encryptedTauHelper(token.getTau(), pk, tau_mag);
        this.encryptedRectangle = encryptedRectangleHelper(token.getQ(), token.getTau(), pivots, pk, data_mag);
        this.ssk = secretKeyHelper(AES_KEY_SIZE);
    }

    private static SecretKey secretKeyHelper(int keySize){
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(keySize, rnd);
            return keyGenerator.generateKey();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static SHECipher encryptedTauHelper(double tau, SHEPublicKey pk, int tau_mag){
        return SymHomEnc.enc(DataUtil.doubleToBigInt(tau, tau_mag), pk );
    }

    private static EncryptedRectangle encryptedRectangleHelper(double[] q, double tau, List<double[]> pivots, SHEPublicKey pk, int data_mag){
        int rectangle_dimension = pivots.size();
        BigInteger[] maxes = new BigInteger[rectangle_dimension];
        BigInteger[] mins =  new BigInteger[rectangle_dimension];
        for (int i = 0; i <rectangle_dimension ; i++) {
            mins[i] = DataUtil.doubleToBigInt(DataUtil.negativeInf(q, pivots.get(i)) - tau, data_mag);
            maxes[i] = DataUtil.doubleToBigInt(DataUtil.negativeInf(q, pivots.get(i)) + tau, data_mag);
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

    public SHECipher getEncryptedTau() {
        return EncryptedTau;
    }

    public SHECipher[] getEncryptedQ() {
        return EncryptedQ;
    }

    public SHECipher[] getEncryptedW() {
        return EncryptedW;
    }
}
