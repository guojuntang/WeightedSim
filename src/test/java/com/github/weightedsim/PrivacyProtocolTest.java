package com.github.weightedsim;

import com.github.SymHomEnc.*;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import com.github.weightedsim.entities.AssistServer;
import com.github.weightedsim.entities.EncryptedToken;
import com.github.weightedsim.entities.OutsourceServer;
import com.github.weightedsim.entities.QueryToken;
import com.github.weightedsim.privacyprotocol.DLESSProtocol;
import com.github.weightedsim.privacyprotocol.DWITHINProtocol;
import com.github.weightedsim.privacyprotocol.SLESSEProtocol;
import com.github.weightedsim.util.DataUtil;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class PrivacyProtocolTest {
    private static final SHEParameters param = new SHEParameters(SHEParameters.K0, SHEParameters.K1, 60);
    private static final SHEPrivateKey sk = param.getSHEPrivateKey();
    private static final SHEPublicParameter pb = param.getSHEPublicParameter();
    private static final SHEPublicKey pk = param.getSHEPublicKey();
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
    public void SLESSEWithEncryptedToken(){
        SLESSEProtocol protocol = new SLESSEProtocol(s1, s2);
        double[] q = {30.12, 30.34, 30.45, 30.76};
        double[] w = {0.1, 0.5 ,0.3, 0.1};
        double tau = 30;


        double[] p1 = {60.00, 60.00, 60.00, 60.00};
        double[] p2 = {60.50, 60.99, 60.89, 60.79};



        QueryToken queryToken = new QueryToken(q, w, tau);
        EncryptedToken encryptedToken = new EncryptedToken(queryToken,  pk, 100, 100, 1000);

        SHECipher[] c1 = DataUtil.convertAndEncryptVector(p1, 100, sk);
        SHECipher[] c2 = DataUtil.convertAndEncryptVector(p2, 100, sk);

        SHECipher d1 = DataUtil.calEncryptedWeightedEuclideanDis(c1, encryptedToken.getEncryptedQ(), encryptedToken.getEncryptedW(), E_mins_1, pb);
        SHECipher d2 = DataUtil.calEncryptedWeightedEuclideanDis(c2, encryptedToken.getEncryptedQ(), encryptedToken.getEncryptedW(), E_mins_1, pb);

        assertEquals(1, SymHomEnc.dec(protocol.run(d1, encryptedToken.getEncryptedTauSquare()), sk).intValue());
        assertEquals(0, SymHomEnc.dec(protocol.run(d2, encryptedToken.getEncryptedTauSquare()), sk).intValue());


    }

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
