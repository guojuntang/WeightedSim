package com.github.weightedsim.privacyprotocol;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPublicParameter;

import java.math.BigInteger;
import java.security.SecureRandom;

abstract class OutsourceServerProtocolHandler {
    private static SecureRandom rnd = new SecureRandom();
    private SHEPublicParameter pb;
    private boolean coin;
    private SHECipher E_mins_1;
    /**
     * random number r1,r2
     */
    private BigInteger r1;
    private BigInteger r2;

    public BigInteger getR1() {
        return r1;
    }

    public BigInteger getR2() {
        return r2;
    }

    public boolean getCoin(){
        return coin;
    }

    public SHECipher getE_mins_1() {
        return E_mins_1;
    }

    public SHEPublicParameter getPb() {
        return pb;
    }

    public OutsourceServerProtocolHandler(SHEPublicParameter pb, SHECipher E_mins_1){
        this.pb = pb;
        this.E_mins_1 = E_mins_1;
        flipCoin();
        chooseRandomNumbers();
    }

    private void flipCoin(){
        coin = rnd.nextBoolean();
    }

    private void chooseRandomNumbers(){
        BigInteger tmp;
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
    }

}
