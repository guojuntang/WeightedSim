package com.github.weightedsim.encryptedrtree;

import com.github.SymHomEnc.SHEParameters;
import com.github.SymHomEnc.SHEPrivateKey;
import com.github.SymHomEnc.SHEPublicParameter;
import com.github.davidmoten.rtreemulti.*;
import com.github.davidmoten.rtreemulti.geometry.Geometry;
import com.github.davidmoten.rtreemulti.geometry.Point;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import javafx.util.Pair;

import java.lang.reflect.Type;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Stack;

public class EncryptedRTree {
    // current node and the encrypted parent
    private final Stack<Pair<Node, EncryptedNode>> stack = new Stack<>();
    private EncryptedNode root;

    public EncryptedNode getRoot() {
        return root;
    }

    public EncryptedRTree(RTree rTree, SHEPrivateKey sk){
        buildTree(rTree, sk);
    }

    // built by dfs
    private void buildTree(RTree<?, ? extends Geometry> rTree, SHEPrivateKey sk){
        Node<?, ? extends Geometry> rRoot = rTree.root().get();
        stack.push(new Pair<>(rRoot, null));
        NonLeaf nonLeaf;
        Leaf leaf;
        Node node;
        Pair<Node, EncryptedNode> current;
        EncryptedNode parent;
        EncryptedNode cur_build;
        int size;
        do {
            current = stack.pop();
            parent = current.getValue();
            node = current.getKey();
            // build nonleaf
            cur_build = new EncryptedNonLeaf(node, sk);
            if (!node.isLeaf()){
                // add the next nodes to the stack
                nonLeaf = (NonLeaf) node;
                size = nonLeaf.children().size();
                for (int i = size - 1; i >= 0 ; i--) {
                    stack.push(new Pair<>(nonLeaf.child(i), cur_build));
                }
            }else {
                // build leaf, actually the encryptedLeaf nodes are entries in multi-rtree
                leaf = (Leaf) node;
                size = leaf.entries().size();
                for (int i = 0; i < size; i++) {
                    // create encryptedLeaf and bind to cur_build
                    ((EncryptedNonLeaf)cur_build).addChild(new EncryptedLeaf<>(leaf.entry(i).value(), leaf.entry(i), sk));
                }
            }
            if (parent != null){
                ((EncryptedNonLeaf)parent).addChild(cur_build);
            }else {
                this.root = cur_build;
            }
            // root node
        }while (!stack.isEmpty());
    }


}
