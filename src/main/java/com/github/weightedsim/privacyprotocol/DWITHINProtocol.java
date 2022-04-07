package com.github.weightedsim.privacyprotocol;

import com.github.SymHomEnc.SHECipher;
import com.github.weightedsim.entities.AssistServer;
import com.github.weightedsim.entities.OutsourceServer;

public class DWITHINProtocol extends PrivacyProtocol{
    public DWITHINProtocol(OutsourceServer s1, AssistServer s2){
        super(s1, s2);
    }

    /**
     * Check m1 <= m2 <= m3 by s1 and s2
     * @param E_m1
     * @param E_m2
     * @param E_m3
     * @return directly return the result
     */
    public boolean run(SHECipher E_m1, SHECipher E_m2, SHECipher E_m3) {
        return run_helper(E_m1, E_m2, E_m3, new OutsourceServerDWITHINHandler(getS1()), new AssistServerDWITHINHandler(getS2()));
    }


    private static boolean run_helper(SHECipher E_m1, SHECipher E_m2, SHECipher E_m3, OutsourceServerDWITHINHandler s1, AssistServerDWITHINHandler s2){
        SHECipher result1 = s1.phrase1(E_m1, E_m2, E_m3);
        boolean result2= s2.phrase2(result1);
        return s1.phrase3(result2);
    }
}
