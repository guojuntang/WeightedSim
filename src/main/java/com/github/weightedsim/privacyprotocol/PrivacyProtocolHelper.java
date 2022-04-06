package com.github.weightedsim.privacyprotocol;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPrivateKey;
import com.github.SymHomEnc.SHEPublicParameter;
import com.github.SymHomEnc.SymHomEnc;

import java.math.BigInteger;

public class PrivacyProtocolHelper {
    private PrivacyProtocolHelper(){
        // No instance
    }
    private static SHECipher garbleCipherPos(SHECipher E_m, SHECipher E_mins_1, BigInteger r1, BigInteger r2, boolean coin_flip, SHEPublicParameter pb){
        return garbleCipher(E_m, E_mins_1, r1, r2, coin_flip, pb);
    }

    private static SHECipher garbleCipherNeg(SHECipher E_m, SHECipher E_mins_1, BigInteger r1, BigInteger r2, boolean coin_flip, SHEPublicParameter pb){
        return garbleCipher(E_m, E_mins_1, r1, r2.negate(), coin_flip, pb);
    }


    private static SHECipher garbleCipher(SHECipher E_m, SHECipher E_mins_1, BigInteger r1, BigInteger r2, boolean coin_flip, SHEPublicParameter pb){
        //  E(s) * (E_m * r1 - r2)
        return (coin_flip)?
                // coin_flip == true -> E(s) = 1
                SymHomEnc.hm_add(SymHomEnc.hm_mul(E_m, r1, pb), r2, pb) :
                // coin_flip == false -> E(s) = -1
                SymHomEnc.hm_mul(SymHomEnc.hm_add(SymHomEnc.hm_mul(E_m, r1, pb), r2, pb), E_mins_1,pb);
    }

    public static SHECipher SLESSEPhrase1(SHECipher E_m1, SHECipher E_m2, SHECipher E_mins_1,BigInteger r1, BigInteger r2, boolean coin_flip, SHEPublicParameter pb){
        // E(m_2 - m_1)
        SHECipher E_x_pi = SymHomEnc.hm_add(E_m1, SymHomEnc.hm_mul(E_m2, E_mins_1, pb),pb);
        return garbleCipherNeg(E_x_pi, E_mins_1, r1, r2, coin_flip, pb);
    }


    public static SHECipher SLESSEPhrase2(SHECipher result, SHEPrivateKey sk){
        BigInteger x = SymHomEnc.dec(result, sk);
        // x > 0
        if(x.compareTo(BigInteger.ZERO) == 1){
            return SymHomEnc.enc(0, sk);
        }else{
            return  SymHomEnc.enc(1, sk);
        }
    }

    public static SHECipher SLESSEPhrase3(SHECipher E_x, SHECipher E_mins_1, boolean coin_flip, SHEPublicParameter pb){
        if (coin_flip){
            return E_x;
        } else{
            // E(-x + 1) -> E(1 - x)
            return SymHomEnc.hm_add(SymHomEnc.hm_mul(E_x, E_mins_1, pb), 1, pb);
        }
    }


    public static SHECipher DLESSPhrase1(SHECipher E_m1, SHECipher E_m2, SHECipher E_mins_1,BigInteger r1, BigInteger r2, boolean coin_flip, SHEPublicParameter pb){
        SHECipher E_x_pi = SymHomEnc.hm_add(E_m1, SymHomEnc.hm_mul(E_m2, E_mins_1, pb),pb);
        return garbleCipherPos(E_x_pi, E_mins_1, r1, r2, coin_flip, pb);
    }


    public static boolean DLESSPhrase2(SHECipher result, SHEPrivateKey sk){
        BigInteger x = SymHomEnc.dec(result, sk);
        // x > 0
        if(x.compareTo(BigInteger.ZERO) == 1){
            return false;
        }else{
            return true;
        }
    }

    public static boolean DLESSPhrase3(boolean x, boolean coin_flip){
        if (coin_flip){
            return x;
        } else{
            // E(-x + 1) -> E(1 - x)
            return !x;
        }
    }


    public static SHECipher DWITHINPhrase1(SHECipher E_m1, SHECipher E_m2, SHECipher E_m3, SHECipher E_mins_1, BigInteger r1, BigInteger r2, boolean coin_flip, SHEPublicParameter pb){
        // E(m_1 - m_2)
        SHECipher a1 = SymHomEnc.hm_add(E_m1, SymHomEnc.hm_mul(E_m2, E_mins_1, pb),pb);
        // E(m_3 - m_2)
        SHECipher a2 = SymHomEnc.hm_add(E_m3, SymHomEnc.hm_mul(E_m2, E_mins_1, pb),pb);
        SHECipher E_x_pi = SymHomEnc.hm_mul(a1, a2, pb);
        return garbleCipherNeg(E_x_pi, E_mins_1, r1, r2, coin_flip, pb);
    }


    public static boolean DWITHINPhrase2(SHECipher result, SHEPrivateKey sk){
        BigInteger x = SymHomEnc.dec(result, sk);
        // x > 0
        if(x.compareTo(BigInteger.ZERO) == 1){
            return false;
        }else{
            return true;
        }
    }

    public static boolean DWITHINPhrase3(boolean x, boolean coin_flip){
        if (coin_flip){
            return x;
        } else{
            return !x;
        }
    }

}
