package com.github.weightedsim;

import com.github.SymHomEnc.*;

import com.github.weightedsim.encryptedrtree.geometry.EncryptedPoint;
import com.github.weightedsim.encryptedrtree.geometry.EncryptedRectangle;
import com.github.weightedsim.entities.AssistServer;
import com.github.weightedsim.entities.OutsourceServer;
import com.github.weightedsim.privacyprotocol.DLESSProtocol;
import com.github.weightedsim.privacyprotocol.DWITHINProtocol;
import org.junit.Test;
import static org.junit.Assert.*;

public class GeometryTest {
    private static final SHEParameters param = new SHEParameters(SHEParameters.K0, SHEParameters.K1, SHEParameters.K2);
    private static final SHEPrivateKey sk = param.getSHEPrivateKey();
    private static final SHEPublicParameter pb = param.getSHEPublicParameter();
    private static final SHECipher E_mins_1= SymHomEnc.enc(-1, sk);
    private static final OutsourceServer s1 = new OutsourceServer(pb, E_mins_1);
    private static final AssistServer s2 = new AssistServer(sk);
    private static final DLESSProtocol less_protocol = new DLESSProtocol(s1, s2);
    private static final DWITHINProtocol within_protocol = new DWITHINProtocol(s1, s2);

    @Test
    public void testIntersects() {
        int[] maxes0 = {86, 37};
        int[] mins0 = {14, 14};

        int[] maxes1 = {50, 80};
        int[] mins1 = {13, 23};

        EncryptedRectangle a = new EncryptedRectangle(maxes0, mins0, sk);
        EncryptedRectangle b = new EncryptedRectangle(maxes1, mins1, sk);
        assertTrue(a.intersect(b, less_protocol));
        assertTrue(b.intersect(a, less_protocol));
    }

    @Test
    public void testIntersectsNoRectangleContainsCornerOfAnother() {
        int[] maxes0 = {50, 50};
        int[] mins0 = {10, 10};

        int[] maxes1 = {34, 85};
        int[] mins1 = {28, 4};

        EncryptedRectangle a = new EncryptedRectangle(maxes0, mins0, sk);
        EncryptedRectangle b = new EncryptedRectangle(maxes1, mins1, sk);
        assertTrue(a.intersect(b, less_protocol));
        assertTrue(b.intersect(a, less_protocol));
    }

    @Test
    public void testIntersectsOneRectangleContainsTheOther() {
        int[] maxes0 = {50, 50};
        int[] mins0 = {10, 10};

        int[] maxes1 = {40, 40};
        int[] mins1 = {20, 20};

        EncryptedRectangle a = new EncryptedRectangle(maxes0, mins0, sk);
        EncryptedRectangle b = new EncryptedRectangle(maxes1, mins1, sk);
        assertTrue(a.intersect(b, less_protocol));
        assertTrue(b.intersect(a, less_protocol));
    }

    @Test
    public void testIntersectsOneRectangleReturnsTrueDespiteZeroArea() {
        int[] maxes0 = {50, 50};
        int[] mins0 = {10, 50};

        int[] maxes1 = {60, 60};
        int[] mins1 = {20, 20};

        EncryptedRectangle a = new EncryptedRectangle(maxes0, mins0, sk);
        EncryptedRectangle b = new EncryptedRectangle(maxes1, mins1, sk);
        assertTrue(a.intersect(b, less_protocol));
        assertTrue(b.intersect(a, less_protocol));
    }


    @Test
    public void testNoOverlapping() {
        int[] maxes0 = {50, 50};
        int[] mins0 = {10, 10};

        int[] maxes1 = {100, 100};
        int[] mins1 = {60, 60};

        EncryptedRectangle a = new EncryptedRectangle(maxes0, mins0, sk);
        EncryptedRectangle b = new EncryptedRectangle(maxes1, mins1, sk);
        assertFalse(a.intersect(b, less_protocol));
        assertFalse(b.intersect(a, less_protocol));
    }

    @Test
    public void testContains() {
        int[] maxes = {30, 40};
        int[] mins = {10, 20};

        int[] x = {20, 30};

        EncryptedRectangle r = new EncryptedRectangle(maxes, mins, sk);
        EncryptedPoint p = new EncryptedPoint(x, sk);
        assertTrue(p.intersect(r, within_protocol));
    }

    @Test
    public void testContainsReturnsFalseWhenLessThanMinY() {
        int[] maxes = {30, 40};
        int[] mins = {10, 20};

        int[] x = {20, 19};

        EncryptedRectangle r = new EncryptedRectangle(maxes, mins, sk);
        EncryptedPoint p = new EncryptedPoint(x, sk);
        assertFalse(p.intersect(r, within_protocol));
    }

    @Test
    public void testContainsReturnsFalseWhenGreaterThanMaxY() {
        int[] maxes = {30, 40};
        int[] mins = {10, 20};

        int[] x = {20, 41};

        EncryptedRectangle r = new EncryptedRectangle(maxes, mins, sk);
        EncryptedPoint p = new EncryptedPoint(x, sk);
        assertFalse(p.intersect(r, within_protocol));
    }

    @Test
    public void testContainsReturnsFalseWhenGreaterThanMaxX() {
        int[] maxes = {30, 40};
        int[] mins = {10, 20};

        int[] x = {31, 30};

        EncryptedRectangle r = new EncryptedRectangle(maxes, mins, sk);
        EncryptedPoint p = new EncryptedPoint(x, sk);
        assertFalse(p.intersect(r, within_protocol));
    }

    @Test
    public void testContainsReturnsFalseWhenLessThanMinX() {
        int[] maxes = {30, 40};
        int[] mins = {10, 20};

        int[] x = {9, 30};

        EncryptedRectangle r = new EncryptedRectangle(maxes, mins, sk);
        EncryptedPoint p = new EncryptedPoint(x, sk);
        assertFalse(p.intersect(r, within_protocol));
    }


}
