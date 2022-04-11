package com.github.weightedsim.encryptedrtree;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPrivateKey;
import com.github.davidmoten.rtreemulti.Node;
import com.github.davidmoten.rtreemulti.geometry.Geometry;
import com.github.davidmoten.rtreemulti.geometry.HasGeometry;
import com.github.davidmoten.rtreemulti.geometry.Point;
import com.github.davidmoten.rtreemulti.geometry.Rectangle;
import com.github.weightedsim.encryptedrtree.geometry.EncryptedGeometry;
import com.github.weightedsim.encryptedrtree.geometry.EncryptedPoint;
import com.github.weightedsim.encryptedrtree.geometry.EncryptedRectangle;
import com.github.weightedsim.privacyprotocol.PrivacyProtocol;
import com.github.weightedsim.util.DataUtil;


public abstract class EncryptedNode{
    private EncryptedGeometry mbr;

    public EncryptedNode(EncryptedGeometry mbr){
        this.mbr = mbr;
    }

    public EncryptedNode(HasGeometry geo, SHEPrivateKey sk){
        Geometry geometry = geo.geometry();
        if (geometry instanceof Point){
            this.mbr = new EncryptedPoint(geometry.mbr().maxes(), sk);
        }else if (geometry instanceof Rectangle){
            this.mbr =  new EncryptedRectangle(geometry.mbr().maxes(), geo.geometry().mbr().mins(), sk);
        } else{
            throw new RuntimeException("Encrypted Node: unknown geometry");
        }
        //this.mbr = new EncryptedRectangle(node.geometry().mbr().maxes(), node.geometry().mbr().mins(), sk);
    }

    public EncryptedGeometry getMbr() {
        return mbr;
    }

    public abstract boolean isLeaf();

    public abstract boolean test(EncryptedRectangle r, PrivacyProtocol protocol);

}
