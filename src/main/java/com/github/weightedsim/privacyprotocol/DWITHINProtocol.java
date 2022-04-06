package com.github.weightedsim.privacyprotocol;

import com.github.SymHomEnc.SHECipher;

public class DWITHINProtocol {
    private DWITHINProtocol(){

    }

    /**
     * Check m1 <= m2 <= m3 by s1 and s2
     * @param E_m1
     * @param E_m2
     * @param E_m3
     * @param s1
     * @param s2
     * @return directly return the result
     */
    public static boolean run(SHECipher E_m1, SHECipher E_m2, SHECipher E_m3, OutsourceServerDWITHINHandler s1, AssistServerDWITHINHandler s2){
        SHECipher result1 = s1.phrase1(E_m1, E_m2, E_m3);
        boolean result2= s2.phrase2(result1);
        return s1.phrase3(result2);
    }
}
