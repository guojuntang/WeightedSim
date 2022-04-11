package com.github.weightedsim;

import com.github.SymHomEnc.*;
import com.github.davidmoten.rtreemulti.RTree;
import com.github.davidmoten.rtreemulti.geometry.Point;
import com.github.weightedsim.encryptedrtree.EncryptedLeaf;
import com.github.weightedsim.encryptedrtree.EncryptedRTree;
import com.github.weightedsim.encryptedrtree.geometry.EncryptedRectangle;
import com.github.weightedsim.entities.AssistServer;
import com.github.weightedsim.entities.OutsourceServer;
import com.github.weightedsim.privacyprotocol.DLESSProtocol;
import com.github.weightedsim.privacyprotocol.DWITHINProtocol;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SearchTest {
    private static SHEParameters param = new SHEParameters(SHEParameters.K0, SHEParameters.K1, SHEParameters.K2);
    private static SHEPrivateKey sk = param.getSHEPrivateKey();
    private static final SHEPublicParameter pb = param.getSHEPublicParameter();
    private static final SHECipher E_mins_1= SymHomEnc.enc(-1, sk);
    private static final OutsourceServer s1 = new OutsourceServer(pb, E_mins_1);
    private static final AssistServer s2 = new AssistServer(sk);
    private static final DLESSProtocol less_protocol = new DLESSProtocol(s1, s2);
    private static final DWITHINProtocol within_protocol = new DWITHINProtocol(s1, s2);


    @Test
    public void searchTest1(){
        RTree<Integer, Point> tree = RTree.create();
        tree = tree.add(0, Point.create(59.0, 91.0))
                .add(1, Point.create(86.0, 14.0))
                .add(2, Point.create(36.0, 60.0))
                .add(3, Point.create(57.0, 36.0))
                .add(4, Point.create(14.0, 37.0));
        EncryptedRTree<Integer> encryptedRTree = new EncryptedRTree<Integer>(tree, sk);
        int[] maxes = {50, 80};
        int[] mins = {13, 23};
        EncryptedRectangle encryptedRectangle = new EncryptedRectangle(maxes, mins, sk);
        List<EncryptedLeaf<Integer>> result = encryptedRTree.search(encryptedRectangle, less_protocol, within_protocol);
        Set<Integer> actual = new HashSet<>();
        Set<Integer> expected = new HashSet<>();
        //expected results
        expected.add(2);
        expected.add(4);

        for (int i = 0; i < result.size(); i++) {
            actual.add(result.get(i).getValue());
        }

        assertEquals(expected, actual);
    }


    @Test
    public void searchTest2(){
        RTree<Integer, Point> tree = RTree.star().create();
        tree = tree.add(0, Point.create(28.0, 19.0))
                .add(1, Point.create(29.0, 4.0))
                .add(2, Point.create(10.0, 63.0))
                .add(3, Point.create(34.0, 85.0))
                .add(4, Point.create(62.0, 45.0));
        EncryptedRTree<Integer> encryptedRTree = new EncryptedRTree<Integer>(tree, sk);
        int[] maxes = {50, 50};
        int[] mins = {10, 10};
        EncryptedRectangle encryptedRectangle = new EncryptedRectangle(maxes, mins, sk);
        List<EncryptedLeaf<Integer>> result = encryptedRTree.search(encryptedRectangle, less_protocol, within_protocol);
        Set<Integer> actual = new HashSet<>();
        Set<Integer> expected = new HashSet<>();
        //expected results
        expected.add(0);

        for (int i = 0; i < result.size(); i++) {
            actual.add(result.get(i).getValue());
        }

        assertEquals(expected, actual);
    }
}
