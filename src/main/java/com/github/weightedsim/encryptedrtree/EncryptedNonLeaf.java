package com.github.weightedsim.encryptedrtree;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPrivateKey;
import com.github.davidmoten.rtreemulti.Node;
import com.github.davidmoten.rtreemulti.geometry.HasGeometry;
import com.github.weightedsim.encryptedrtree.geometry.EncryptedGeometry;
import com.github.weightedsim.encryptedrtree.geometry.EncryptedRectangle;
import com.github.weightedsim.privacyprotocol.PrivacyProtocol;

import java.util.ArrayList;
import java.util.List;


/**
 * Encrypted from Nonleaf and Leaf in multi-tree
 */
public class EncryptedNonLeaf extends EncryptedNode{
    private List<EncryptedNode> children;

    public List<EncryptedNode> getChildren() {
        return children;
    }

    public EncryptedNode getChild(int i){
        return children.get(i);
    }

    public boolean addChild(EncryptedNode a){
        return children.add(a);
    }

    @Override
    public boolean test(EncryptedRectangle r, PrivacyProtocol protocol) {
        return getMbr().intersect(r, protocol);
    }

    public EncryptedNonLeaf(EncryptedRectangle mbr){
        super(mbr);
    }

    public EncryptedNonLeaf(HasGeometry geo, SHEPrivateKey sk){
        super(geo, sk);
        this.children = new ArrayList<>();
    }

    @Override
    public boolean isLeaf() {
        return false;
    }
}
