package com.github.weightedsim.privacyprotocol;

import com.github.SymHomEnc.SHECipher;

public class SLESSEProtocol{
    private SLESSEProtocol(){
        // No instance
    }

    /**
     * Check whether m1 <= m2 by s1 and s2
     * @param E_m1
     * @param E_m2
     * @param s1
     * @param s2
     * @return SHE Ciphertext of result
     */
    public SHECipher run(SHECipher E_m1, SHECipher E_m2, OutsourceServerSLESSEHandler s1, AssistServerSLESSEHandler s2) {
        SHECipher result1 = s1.phrase1(E_m1, E_m2);
        SHECipher result2 = s2.phrase2(result1);
        return s1.phrase3(result2);
    }
}
