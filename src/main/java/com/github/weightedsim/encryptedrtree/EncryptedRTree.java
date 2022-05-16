package com.github.weightedsim.encryptedrtree;

import com.github.SymHomEnc.SHEPrivateKey;
import com.github.davidmoten.rtreemulti.*;
import com.github.davidmoten.rtreemulti.geometry.Geometry;
import com.github.weightedsim.encryptedrtree.geometry.EncryptedRectangle;
import com.github.weightedsim.privacyprotocol.DLESSProtocol;
import com.github.weightedsim.privacyprotocol.DWITHINProtocol;
import com.github.weightedsim.util.Pair;

import java.util.*;

public class EncryptedRTree<T> {
    // current node and the encrypted parent
    private final Stack<Pair<Node<T, ?>, EncryptedNode>> stack = new Stack<>();
    private final Queue<EncryptedNode> queue = new LinkedList<>();
    private EncryptedNode root;

    public EncryptedNode getRoot() {
        return root;
    }

    public EncryptedRTree(RTree<T, ?> rTree, SHEPrivateKey sk){
        buildTree(rTree, sk);
    }

    public List<EncryptedLeaf<T>> search(EncryptedRectangle r, DLESSProtocol dless, DWITHINProtocol dwithin){
        List<EncryptedLeaf<T>> result = new ArrayList<>();
        EncryptedNode node;
        EncryptedNonLeaf nonLeaf;
        int size;
        queue.add(root);
        while (!queue.isEmpty()){
            node = queue.poll();
            // leaf node, check if it is inside the rectangle
            if (node.isLeaf()){
                if (node.test(r, dwithin)){
                    result.add((EncryptedLeaf<T>) node);
                }
            }else{
                if (node.test(r, dless)){
                    nonLeaf = (EncryptedNonLeaf) node;
                    size = nonLeaf.getChildren().size();
                    for (int i = 0; i < size; i++) {
                        queue.add(nonLeaf.getChild(i));
                    }
                }
            }
        }
        return result;
    }


    // built by dfs
    private void buildTree(RTree<T, ? extends Geometry> rTree, SHEPrivateKey sk){
        Node<T, ? extends Geometry> rRoot = rTree.root().get();
        stack.push(new Pair<>(rRoot, null));
        NonLeaf<T, ?> nonLeaf;
        Leaf<T, ?> leaf;
        Node<T, ?> node;
        Pair<Node<T, ?>, EncryptedNode> current;
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
                nonLeaf = (NonLeaf<T, ?>) node;
                size = nonLeaf.children().size();
                for (int i = size - 1; i >= 0 ; i--) {
                    stack.push(new Pair<>(nonLeaf.child(i), cur_build));
                }
            }else {
                // build leaf, actually the encryptedLeaf nodes are entries in multi-rtree
                leaf = (Leaf<T, ?>) node;
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
