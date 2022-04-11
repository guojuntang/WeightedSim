package com.github.weightedsim;

import com.github.SymHomEnc.SHEParameters;
import com.github.SymHomEnc.SHEPrivateKey;
import com.github.SymHomEnc.SymHomEnc;
import com.github.davidmoten.rtreemulti.*;
import com.github.davidmoten.rtreemulti.geometry.Geometry;
import com.github.davidmoten.rtreemulti.geometry.Point;
import com.github.weightedsim.encryptedrtree.EncryptedLeaf;
import com.github.weightedsim.encryptedrtree.EncryptedNode;
import com.github.weightedsim.encryptedrtree.EncryptedNonLeaf;
import org.junit.Test;
import static org.junit.Assert.*;
import com.github.weightedsim.encryptedrtree.EncryptedRTree;

import java.util.LinkedList;
import java.util.Queue;

public class BuildTreeTest {
    private static SHEParameters param = new SHEParameters(SHEParameters.K0, SHEParameters.K1, SHEParameters.K2);
    private static SHEPrivateKey sk = param.getSHEPrivateKey();

    private static void testHelper(RTree<Integer, Point> tree, EncryptedRTree encryptedRTree){
        Queue<Node> queue1 = new LinkedList();
        Queue<EncryptedNode> queue2 = new LinkedList();

        queue1.add(tree.root().get());
        queue2.add(encryptedRTree.getRoot());

        Node node;
        EncryptedNode encryptedNode;
        NonLeaf nonLeaf;
        EncryptedNonLeaf encryptedNonLeaf;
        Leaf leaf;
        Geometry geometry;
        int dimension;
        int children_size;

        while (!queue1.isEmpty()){
            node = queue1.poll();
            encryptedNode = queue2.poll();
            geometry = node.geometry().mbr();
            dimension = geometry.dimensions();

            if (!node.isLeaf()){
                nonLeaf = (NonLeaf)node;
                encryptedNonLeaf = (EncryptedNonLeaf) encryptedNode;

                // check the mbr
                for (int i = 0; i < dimension; i++) {
                    assertEquals((int)geometry.mbr().max(i), SymHomEnc.dec(encryptedNode.getMbr().getMax(i), sk).intValue());
                    assertEquals((int)geometry.mbr().min(i), SymHomEnc.dec(encryptedNode.getMbr().getMin(i), sk).intValue());
                }

                // add children to queue
                children_size = nonLeaf.children().size();
                for (int j = 0; j < children_size; j++) {
                    queue1.add(nonLeaf.child(j));
                    queue2.add(encryptedNonLeaf.getChild(j));
                }
            }else {
                leaf = (Leaf) node;
                encryptedNonLeaf = (EncryptedNonLeaf) encryptedNode;
                // check the parent node
                for (int i = 0; i < dimension; i++) {
                    assertEquals((int)leaf.geometry().mbr().max(i), SymHomEnc.dec(encryptedNonLeaf.getMbr().getMax(i), sk).intValue());
                    assertEquals((int)leaf.geometry().mbr().min(i), SymHomEnc.dec(encryptedNonLeaf.getMbr().getMin(i), sk).intValue());
                }
                // check entries(leaves in encrypted tree)
                children_size = leaf.entries().size();
                for (int i = 0; i < children_size; i++) {
                    Entry<Integer, ?> entry = leaf.entry(i);
                    EncryptedLeaf<Integer> encryptedLeaf = (EncryptedLeaf) encryptedNonLeaf.getChild(i);
                    for (int j = 0; j < dimension; j++) {
                        assertEquals((int)entry.geometry().mbr().max(j), SymHomEnc.dec(encryptedLeaf.getMbr().getMax(j), sk).intValue());
                    }
                    assertEquals(entry.value().intValue(), encryptedLeaf.getValue().intValue());
                }
            }

        }

    }

    @Test
    public void buildTreeFromRStarTreeTest(){
        RTree<Integer, Point> tree = RTree.star().create();
        tree = tree.add(0, Point.create(10, 20))
                .add(1, Point.create(12, 25))
                .add(2, Point.create(109, 69))
                .add(3, Point.create(125, 5))
                .add(4, Point.create(132, 5))
                .add(5, Point.create(90, 134))
                .add(6, Point.create(12, 11))
                .add(7, Point.create(22, 250))
                .add(8, Point.create(2, 240))
                .add(9, Point.create(93, 22))
                .add(10, Point.create(97, 125));


        EncryptedRTree<Integer> encryptedRTree = new EncryptedRTree(tree, sk);
        testHelper(tree, encryptedRTree);
    }

    @Test
    public void buildTreeTest(){
        RTree<Integer, Point> tree = RTree.maxChildren(3).create();
        tree = tree.add(0, Point.create(10, 20))
                .add(1, Point.create(12, 25))
                .add(2, Point.create(109, 69))
                .add(3, Point.create(125, 5))
                .add(4, Point.create(132, 5))
                .add(5, Point.create(90, 134))
                .add(6, Point.create(12, 11))
                .add(7, Point.create(22, 250))
                .add(8, Point.create(2, 240))
                .add(9, Point.create(93, 22))
                .add(10, Point.create(97, 125));


        EncryptedRTree<Integer> encryptedRTree = new EncryptedRTree(tree, sk);
        testHelper(tree, encryptedRTree);
    }
}
