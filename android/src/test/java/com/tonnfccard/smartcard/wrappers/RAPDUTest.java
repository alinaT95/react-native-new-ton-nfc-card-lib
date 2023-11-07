package com.tonnfccard.smartcard.wrappers;

import com.tonnfccard.utils.ByteArrayHelper;

import org.junit.Test;

import java.util.Random;

import static com.tonnfccard.utils.ByteArrayHelper.hex;
import static com.tonnfccard.api.utils.StringHelper.randomHexString;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RAPDUTest {

    private Random random = new Random();

    @Test(expected = IllegalArgumentException.class)
    public void RAPDUTest1() {
        byte[] data = null;
        new RAPDU(data);
    }

    @Test(expected = IllegalArgumentException.class)
    public void RAPDUTest2() {
        String data = null;
        new RAPDU(data);
    }

    @Test(expected = IllegalArgumentException.class)
    public void RAPDUTest3() {
        new RAPDU(new byte[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void RAPDUTest4() {
        new RAPDU(new byte[1]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void RAPDUTest5() {
        new RAPDU("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void RAPDUTest6() {
        new RAPDU("1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void RAPDUTest7() {
        new RAPDU("12");
    }

    @Test(expected = IllegalArgumentException.class)
    public void RAPDUTest8() {
        new RAPDU("123");
    }

    @Test
    public void RAPDUTest9() {
        for (int len = 2; len <= 257; len++) {
            byte [] bytes = new byte[len];
            random.nextBytes(bytes);
            RAPDU rapdu = new RAPDU(bytes);
            assertArrayEquals(rapdu.getBytes(), bytes);
            assertArrayEquals(rapdu.getData(), ByteArrayHelper.bSub(bytes, 0, len - 2));
            assertArrayEquals(rapdu.getSW(), ByteArrayHelper.bSub(bytes, len - 2, 2));
            assertEquals(rapdu.getSW1(), bytes[len - 2]);
            assertEquals(rapdu.getSW2(), bytes[len - 1]);
            assertEquals(rapdu.toString(), hex(ByteArrayHelper.bSub(bytes, len - 2, 2)) +
                    (ByteArrayHelper.bSub(bytes, 0, len - 2) != null && ByteArrayHelper.bSub(bytes, 0, len - 2).length > 0
                            ? " '" +   hex(ByteArrayHelper.bSub(bytes, 0, len - 2)) + "'"
                            : ""));
        }

        for (int len = 4; len <= 2*257; len=len+2) {
            String str = randomHexString(len);
            byte [] bytes = ByteArrayHelper.bytes(str);
            RAPDU rapdu = new RAPDU(str);
            assertArrayEquals(rapdu.getBytes(), bytes);
            assertArrayEquals(rapdu.getData(), ByteArrayHelper.bSub(bytes, 0, len/2 - 2));
            assertArrayEquals(rapdu.getSW(), ByteArrayHelper.bSub(bytes, len/2 - 2, 2));
            assertEquals(rapdu.getSW1(), bytes[len/2 - 2]);
            assertEquals(rapdu.getSW2(), bytes[len/2 - 1]);
            assertEquals(rapdu.toString(), hex(ByteArrayHelper.bSub(bytes, len/2 - 2, 2)) +
                    (ByteArrayHelper.bSub(bytes, 0, len/2 - 2) != null && ByteArrayHelper.bSub(bytes, 0, len/2 - 2).length > 0
                            ? " '" +   hex(ByteArrayHelper.bSub(bytes, 0, len/2 - 2)) + "'"
                            : ""));
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void RAPDUTest10() {
        byte [] bytes = new byte[258];
        random.nextBytes(bytes);
        new RAPDU(bytes);
    }

    @Test
    public void RAPDUTest11() {
        for (int len = 2 * 2  + 1; len <= 2 * 257 + 1; len=len+2) {
            try{
                new RAPDU(randomHexString(len));
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void RAPDUTest12() {
        new RAPDU("S0ASJH");
    }

}
