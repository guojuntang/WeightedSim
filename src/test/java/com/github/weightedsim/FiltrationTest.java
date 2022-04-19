package com.github.weightedsim;

import com.github.SymHomEnc.*;
import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Point;
import com.github.weightedsim.encryptedrtree.EncryptedLeaf;
import com.github.weightedsim.encryptedrtree.EncryptedRTree;
import com.github.weightedsim.entities.AssistServer;
import com.github.weightedsim.entities.EncryptedToken;
import com.github.weightedsim.entities.OutsourceServer;
import com.github.weightedsim.entities.QueryToken;
import com.github.weightedsim.privacyprotocol.DLESSProtocol;
import com.github.weightedsim.privacyprotocol.DWITHINProtocol;
import com.github.weightedsim.privacyprotocol.SLESSEProtocol;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

public class FiltrationTest {
    private static SHEParameters param = new SHEParameters(SHEParameters.K0, SHEParameters.K1, SHEParameters.K2);
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
    public void filtrationTest(){
        RTree<SHECipher, Point> tree = RTree.create();
        SHECipher c0 = SymHomEnc.enc(0, sk);
        SHECipher c1 = SymHomEnc.enc(1, sk);
        SHECipher c2 = SymHomEnc.enc(2, sk);
        SHECipher c3 = SymHomEnc.enc(3, sk);
        SHECipher c4 = SymHomEnc.enc(4, sk);

        tree = tree.add(c0, Point.create(59.0, 91.0))
                .add(c1, Point.create(86.0, 14.0))
                .add(c2, Point.create(36.0, 15.0))
                .add(c3, Point.create(57.0, 36.0))
                .add(c4, Point.create(20.0, 20.0));
        EncryptedRTree<SHECipher> encryptedRTree = new EncryptedRTree<SHECipher>(tree, sk);
        s1.setUpOutsource(encryptedRTree, less_protocol, within_protocol, lesse_protocol);

        double[] q ={30, 30};
        double[] w = {0.5, 0.5};
        double tau = 10.0;

        double[] p1 = {60, 60};
        double[] p2 = {15, 15};

        List<double[]> pivots = new ArrayList<>();
        pivots.add(p1);
        pivots.add(p2);

        QueryToken queryToken = new QueryToken(q, w, tau);

        //rectangle maxes = {40, 25}, mins = {20, 5}
        EncryptedToken encryptedToken = new EncryptedToken(queryToken, pivots, pk, 1, 1 ,1) ;

        Set<SHECipher> actual = new HashSet<>();
        Set<SHECipher> expected = new HashSet<>();

        expected.add(c2);
        expected.add(c4);

        List<EncryptedLeaf<SHECipher>> result = s1.filtration(encryptedToken);
        for (EncryptedLeaf<SHECipher> c:
             result) {
            actual.add(c.getValue());
        }

        assertEquals(expected, actual);



    }
}
