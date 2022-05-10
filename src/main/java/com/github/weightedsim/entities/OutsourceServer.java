package com.github.weightedsim.entities;

import com.github.SymHomEnc.SHECipher;
import com.github.SymHomEnc.SHEPublicParameter;
import com.github.SymHomEnc.SymHomEnc;
import com.github.weightedsim.encryptedrtree.EncryptedLeaf;
import com.github.weightedsim.encryptedrtree.EncryptedRTree;
import com.github.weightedsim.encryptedrtree.geometry.EncryptedRectangle;
import com.github.weightedsim.privacyprotocol.DLESSProtocol;
import com.github.weightedsim.privacyprotocol.DWITHINProtocol;
import com.github.weightedsim.privacyprotocol.SLESSEProtocol;
import com.github.weightedsim.util.AES;
import com.github.weightedsim.util.DataUtil;
import javafx.util.Pair;

import javax.crypto.SecretKey;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class OutsourceServer<T> {
    private final SHEPublicParameter pb;
    private final SHECipher E_mins_1;
    private final SecureRandom rnd = new SecureRandom();
    private EncryptedRTree<T> encryptedRTree;
    private DLESSProtocol dlessProtocol;
    private DWITHINProtocol dwithinProtocol;
    private SLESSEProtocol slesseProtocol;

    public OutsourceServer(SHEPublicParameter pb, SHECipher E_mins_1){
        this.pb = pb;
        this.E_mins_1 = E_mins_1;
    }

    public void setUpOutsource(EncryptedRTree<T> tree, DLESSProtocol dless, DWITHINProtocol dwithin, SLESSEProtocol slesse){
        this.encryptedRTree = tree;
        this.dlessProtocol = dless;
        this.dwithinProtocol = dwithin;
        this.slesseProtocol = slesse;
    }

    public List<EncryptedLeaf<T>> search(EncryptedRectangle r){
        if (encryptedRTree == null)
            throw new RuntimeException("Filtration: empty encrypted tree");
        return encryptedRTree.search(r, dlessProtocol, dwithinProtocol);
    }

    public List<EncryptedLeaf<T>> filtration(EncryptedToken token){
        EncryptedRectangle r = token.getEncryptedRectangle();
        return search(r);
    }

    private SHECipher calEncryptedWeightedEuclideanDisSquare(SHECipher[] x, SHECipher[] q, SHECipher[] w){
        return DataUtil.calEncryptedWeightedEuclideanDis(x, q, w, E_mins_1, pb);
    }

    // TODO: multithreading
    private Pair<SHECipher[], List<byte[]>> maskEncryptedData(SHECipher[] x, SecretKey aesKey){
        int size = x.length;
        SHECipher[] x_vector = new SHECipher[size];
        List<byte[]> gamma_vector = new ArrayList<>();
        BigInteger gamma;
        for (int i = 0; i < size; i++) {
            gamma = new BigInteger(pb.getK1(), rnd);
            x_vector[i] = SymHomEnc.hm_add(x[i], gamma, pb);
            gamma_vector.add(AES.encrypt(gamma.toByteArray(), aesKey));
        }
        return new Pair<>(x_vector, gamma_vector);
    }

    private Pair<SHECipher, byte[]> maskResultBit(SHECipher b, SecretKey aesKey){
        BigInteger gamma = new BigInteger(pb.getK1(), rnd);
        SHECipher b_pi = SymHomEnc.hm_add(b, gamma, pb);
        byte[] gamma_cipher = AES.encrypt(gamma.toByteArray(), aesKey);
        return new Pair<>(b_pi, gamma_cipher);
    }

    public List<RefinementCandidate> refinement_cipher(EncryptedToken token, List<SHECipher[]> candidate_set) {
        List<RefinementCandidate> result = new ArrayList<>();
        SHECipher result_bit;
        SHECipher encryptedWeightedDis;
        //Todo: multithreading
        for (SHECipher[] x: candidate_set) {
            encryptedWeightedDis = calEncryptedWeightedEuclideanDisSquare(x, token.getEncryptedQ(), token.getEncryptedW());
            result_bit = slesseProtocol.run(encryptedWeightedDis, token.getEncryptedTauSquare());
            result.add(new RefinementCandidate(maskEncryptedData(x, token.getSsk()), maskResultBit(result_bit, token.getSsk())));
        }
        return result;
    }

    public List<RefinementCandidate> refinement(EncryptedToken token, List<EncryptedLeaf<SHECipher[]>> candidate_set){
        List<RefinementCandidate> result = new ArrayList<>();
        SHECipher result_bit;
        SHECipher encryptedWeightedDis;
        //Todo: multithreading
        for (EncryptedLeaf<SHECipher[]> x: candidate_set) {
            encryptedWeightedDis = calEncryptedWeightedEuclideanDisSquare(x.getValue(), token.getEncryptedQ(), token.getEncryptedW());
            result_bit = slesseProtocol.run(encryptedWeightedDis, token.getEncryptedTauSquare());
            result.add(new RefinementCandidate(maskEncryptedData(x.getValue(), token.getSsk()), maskResultBit(result_bit, token.getSsk())));
        }
        return result;
    }

    public SHEPublicParameter getPb() {
        return pb;
    }

    public SHECipher getE_mins_1() {
        return E_mins_1;
    }
}
