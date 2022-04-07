package com.github.weightedsim;

import com.github.SymHomEnc.*;
import com.github.weightedsim.entities.AssistServer;
import com.github.weightedsim.entities.OutsourceServer;
import com.github.weightedsim.privacyprotocol.DLESSProtocol;
import com.github.weightedsim.privacyprotocol.DWITHINProtocol;
import com.github.weightedsim.privacyprotocol.SLESSEProtocol;
import org.junit.Test;

import static org.junit.Assert.*;

public class PrivacyProtocolTest {
    private static final SHEParameters param = new SHEParameters(SHEParameters.K0, SHEParameters.K1, SHEParameters.K2);
    private static final SHEPrivateKey sk = param.getSHEPrivateKey();
    private static final SHEPublicParameter pb = param.getSHEPublicParameter();
    private static final SHECipher E_mins_1= SymHomEnc.enc(-1, sk);
    private static final OutsourceServer s1 = new OutsourceServer(pb, E_mins_1);
    private static final AssistServer s2 = new AssistServer(sk);

    private static final int[][] SLESSEMessageVector = {
            {56,65,2,-2, 100, -20, -100},   // m1
            {23,100 ,2, -2, -100, -10, -1000} // m2
    };
    private static final int[] SLESSEResultVector = {
            0,1,1,1,0,1,0
    };

    private static final boolean[] DLESSResultVector = {
            false,true,false,false,false,true,false
    };

    private static final int[][] DWITHINMessageVector = {
            {56, 65, 2, 2, 30, -100, 20, -100, -1, -30},
            {23, 65 , 90, 18, 140, -10, -10, -10, 20, 100},
            {100, 200, 180, 18, 100, 100, 30, -1, 300, -10}
    };
    private static final boolean[] DWITHINResultVector = {
            false, true, true, true, false, true, false, true, true, false
    };

    @Test
    public void SLESSETest(){
        SLESSEProtocol protocol = new SLESSEProtocol(s1, s2);
        int[] result_list = new int[SLESSEResultVector.length];
        SHECipher result;
        for (int i = 0; i < SLESSEMessageVector[0].length; i++) {
            result = protocol.run(SymHomEnc.enc(SLESSEMessageVector[0][i], sk), SymHomEnc.enc(SLESSEMessageVector[1][i], sk));
            result_list[i] = SymHomEnc.dec(result, sk).intValue();
        }
        assertArrayEquals(SLESSEResultVector, result_list);
    }

    @Test
    public void DLESSTest(){
        DLESSProtocol protocol = new DLESSProtocol(s1, s2);
        boolean[] result_list = new boolean[DLESSResultVector.length];
        for (int i = 0; i < SLESSEMessageVector[0].length; i++) {
            result_list[i] = protocol.run(SymHomEnc.enc(SLESSEMessageVector[0][i], sk), SymHomEnc.enc(SLESSEMessageVector[1][i], sk));
        }
        assertArrayEquals(DLESSResultVector, result_list);
    }

    @Test
    public void DWITHINTest(){
        DWITHINProtocol protocol = new DWITHINProtocol(s1, s2);
        boolean[] result_list = new boolean[DWITHINResultVector.length];
        for (int i = 0; i < DWITHINMessageVector[0].length; i++) {
            result_list[i] = protocol.run(SymHomEnc.enc(DWITHINMessageVector[0][i], sk), SymHomEnc.enc(DWITHINMessageVector[1][i], sk), SymHomEnc.enc(DWITHINMessageVector[2][i], sk));
        }
        assertArrayEquals(DWITHINResultVector, result_list);
    }


}
