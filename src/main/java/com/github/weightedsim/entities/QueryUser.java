package com.github.weightedsim.entities;

import com.github.SymHomEnc.SHEPublicKey;
import com.github.SymHomEnc.SHEPublicParameter;
import com.github.weightedsim.util.AES;
import com.github.weightedsim.util.DataMag;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class QueryUser {
    private SHEPublicKey pk;

    public QueryUser(SHEPublicKey pk){
        this.pk = pk;
    }

    private boolean checkResult(RefinementResult r, SecretKey ssk){
        BigInteger gamma = new BigInteger(AES.decrypt(r.getResultMasks(), ssk));
        // b_pi - gamma == 1
        return ((r.getB_pi().subtract(gamma)).compareTo(BigInteger.ONE) == 0)? true : false;
    }

    private BigInteger[] decryptData(RefinementResult r, SecretKey ssk){
        BigInteger[] x_pi = r.getX_pi();
        List<byte[]> masks= r.getDataMasks();
        BigInteger gamma;
        int size = x_pi.length;

        BigInteger[] result = new BigInteger[size];
        for (int i = 0; i < size; i++) {
            gamma = new BigInteger(AES.decrypt(masks.get(i), ssk));
            result[i] = x_pi[i].subtract(gamma);
        }
        return result;
    }

    public EncryptedToken genEncryptedToken(QueryToken queryToken, DataMag dataMag){
        return new EncryptedToken(queryToken,  pk, dataMag.getData_mag(), dataMag.getW_mag(), dataMag.getTau_mag()) ;
    }

    public List<BigInteger[]> refinement(SecretKey ssk, List<RefinementResult> set){
        List<BigInteger[]> result = new ArrayList<>();
        for (RefinementResult r:
             set) {
            if (checkResult(r, ssk)){
                result.add(decryptData(r, ssk));
            }
        }
        return result;
    }
}
