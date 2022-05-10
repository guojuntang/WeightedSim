package com.github.weightedsim;

import com.github.SymHomEnc.*;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import com.github.weightedsim.encryptedrtree.EncryptedLeaf;
import com.github.weightedsim.entities.*;
import com.github.weightedsim.privacyprotocol.DLESSProtocol;
import com.github.weightedsim.privacyprotocol.DWITHINProtocol;
import com.github.weightedsim.privacyprotocol.SLESSEProtocol;
import com.github.weightedsim.util.DataUtil;
import org.junit.Test;

import javax.xml.crypto.Data;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class RefinementTest {
    private static SHEParameters param = new SHEParameters(1200, 30, 80);
    private static SHEPrivateKey sk = param.getSHEPrivateKey();
    private static final SHEPublicParameter pb = param.getSHEPublicParameter();
    private static final SHEPublicKey pk = param.getSHEPublicKey();
    private static final SHECipher E_mins_1= SymHomEnc.enc(-1, sk);
    private static final OutsourceServer s1 = new OutsourceServer(pb, E_mins_1);
    private static final AssistServer s2 = new AssistServer(sk);
    private static final DLESSProtocol less_protocol = new DLESSProtocol(s1, s2);
    private static final DWITHINProtocol within_protocol = new DWITHINProtocol(s1, s2);
    private static final SLESSEProtocol lesse_protocol = new SLESSEProtocol(s1, s2);

    @Test
    public void RefinementTest(){
        Rectangle r = Rectangle.create(0, 0, 0, 0);

        int[] a0 = {200000, 200000, 300000};
        int[] a1 = {120000, 230000, 440000};
        int[] a2 = {510000, 130000, 600000};
        int[] a3 = {300000, 780000, 260000};
        int size;

        List<int[]> int_vector = new ArrayList<>();
        int_vector.add(a0);
        int_vector.add(a1);
        int_vector.add(a2);
        int_vector.add(a3);

        // create double vector
        List<double[]> double_vector = new ArrayList<>();
        for (int[] i:
             int_vector) {
            size = i.length;
            double[] tmp = new double[size];
            for (int j = 0; j < size; j++) {
                tmp[j] = (double) i[j];
            }
            double_vector.add(tmp);
        }

        SHECipher[] c0 = DataUtil.encryptedVector(a0, sk);
        SHECipher[] c1 = DataUtil.encryptedVector(a1, sk);
        SHECipher[] c2 = DataUtil.encryptedVector(a2, sk);
        SHECipher[] c3 = DataUtil.encryptedVector(a3, sk);

        List<EncryptedLeaf<SHECipher[]>> list = new ArrayList<>();
        list.add(new EncryptedLeaf<SHECipher[]>(c0, r, sk));
        list.add(new EncryptedLeaf<SHECipher[]>(c1, r, sk));
        list.add(new EncryptedLeaf<SHECipher[]>(c2, r, sk));
        list.add(new EncryptedLeaf<SHECipher[]>(c3, r, sk));


        double[] q ={300000, 300000, 300000};
        double[] w = {0.4, 0.3, 0.3};
        double tau = 100000.0;

        double[] p1 = {60, 60, 60};
        double[] p2 = {15, 15, 60};


        QueryUser q1 = new QueryUser(pk);

        QueryToken queryToken = new QueryToken(q, w, tau);

        //rectangle maxes = {40, 25}, mins = {20, 5}
        EncryptedToken encryptedToken = new EncryptedToken(queryToken, pk, 1, 100 ,10) ;

        s1.setUpOutsource(null, less_protocol, within_protocol, lesse_protocol);

        List<RefinementCandidate> result1 = s1.refinement(encryptedToken, list);
        List<RefinementResult> result2 = s2.refinement(result1);
        List<BigInteger[]> result3 = q1.refinement(encryptedToken.getSsk(), result2);

        //for (double[] a:
        //     double_vector) {
        //    System.out.println(DataUtil.checkWeightedQuery(queryToken, a));
        //}

        assertEquals(1, result3.size());
        assertArrayEquals(double_vector.get(0), DataUtil.bigIntVectorToDoubleVector(result3.get(0), 1), 0.0001);

    }

}
