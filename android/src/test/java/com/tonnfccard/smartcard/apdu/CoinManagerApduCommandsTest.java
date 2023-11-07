package com.tonnfccard.smartcard.apdu;

import com.tonnfccard.smartcard.cryptoUtils.HmacHelper;
import com.tonnfccard.smartcard.wrappers.CAPDU;

import org.junit.Before;
import org.junit.Test;

import static com.tonnfccard.smartcard.TonWalletAppletConstants.APDU_DATA_MAX_SIZE;
import static com.tonnfccard.smartcard.TonWalletAppletConstants.HMAC_SHA_SIG_SIZE;
import static com.tonnfccard.smartcard.TonWalletAppletConstants.PIN_SIZE;
import static com.tonnfccard.smartcard.apdu.CoinManagerApduCommands.LABEL_LENGTH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CoinManagerApduCommandsTest {

    @Before
    public void initiateHmacHelper() throws Exception {
        HmacHelper hmacHelperMock = mock(HmacHelper.class);
        when(hmacHelperMock.computeMac((byte[])any())).thenReturn(new byte[HMAC_SHA_SIG_SIZE]);
        TonWalletAppletApduCommands.setHmacHelper(hmacHelperMock);
    }

    @Test
    public void getChangePinAPDUTest1() {
        byte[] oldPinBytes = new byte[PIN_SIZE];
        byte[] newPinBytes = new byte[PIN_SIZE];
        CAPDU apdu = CoinManagerApduCommands.getChangePinAPDU(oldPinBytes, newPinBytes);
        assertEquals(apdu.getCla(), (byte) 0x80);
        assertEquals(apdu.getIns(), (byte)0xCB);
        assertEquals(apdu.getP1(), (byte) 0x80);
        assertEquals(apdu.getP2(), (byte) 0x00);
        assertEquals(apdu.getData().length, 6 + 2 * (PIN_SIZE + 1));
        assertEquals(apdu.getLe(), 0x00);
    }

    @Test
    public void getChangePinAPDUTest2() throws Exception {
        byte[] pin = new byte[PIN_SIZE];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i == PIN_SIZE) continue;
            try {
                CoinManagerApduCommands.getChangePinAPDU(new byte[i], pin);
                fail();
            }
            catch (IllegalArgumentException e){}
            try {
                CoinManagerApduCommands.getChangePinAPDU(pin, new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }

        try {
            CoinManagerApduCommands.getChangePinAPDU(new byte[PIN_SIZE -1], new byte[PIN_SIZE + 1]);
            fail();
        }
        catch (IllegalArgumentException e){}

        try {
            CoinManagerApduCommands.getChangePinAPDU(null, new byte[PIN_SIZE]);
            fail();
        }
        catch (IllegalArgumentException e){}

        try {
            CoinManagerApduCommands.getChangePinAPDU(new byte[PIN_SIZE], null);
            fail();
        }
        catch (IllegalArgumentException e){}

        try {
            CoinManagerApduCommands.getChangePinAPDU(null, null);
            fail();
        }
        catch (IllegalArgumentException e){}
    }

    @Test
    public void getGenerateSeedAPDUTest1() {
        byte[] pinBytes = new byte[PIN_SIZE];
        CAPDU apdu = CoinManagerApduCommands.getGenerateSeedAPDU(pinBytes);
        assertEquals(apdu.getIns(), (byte)0xCB);
        assertEquals(apdu.getP1(), (byte) 0x80);
        assertEquals(apdu.getP2(), (byte) 0x00);
        assertEquals(apdu.getData().length, 6 + (PIN_SIZE + 1));
        assertEquals(apdu.getLe(), 0x00);
    }

    @Test
    public void getGenerateSeedAPDUTest2() throws Exception {
        try {
            CoinManagerApduCommands.getGenerateSeedAPDU(null);
            fail();
        }
        catch (IllegalArgumentException e){}

        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i == PIN_SIZE) continue;
            try {
                CoinManagerApduCommands.getGenerateSeedAPDU(new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test
    public void getSetDeviceLabelAPDUTest1() {
        byte[] label = new byte[LABEL_LENGTH];
        CAPDU apdu = CoinManagerApduCommands.getSetDeviceLabelAPDU(label);
        assertEquals(apdu.getIns(), (byte)0xCB);
        assertEquals(apdu.getP1(), (byte) 0x80);
        assertEquals(apdu.getP2(), (byte) 0x00);
        assertEquals(apdu.getData().length, 5 + (LABEL_LENGTH + 1));
        assertEquals(apdu.getLe(), 0x00);
    }

    @Test
    public void getSetDeviceLabelAPDUTest2() throws Exception {
        try {
            CoinManagerApduCommands.getSetDeviceLabelAPDU(null);
            fail();
        }
        catch (IllegalArgumentException e){}

        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i == LABEL_LENGTH) continue;
            try {
                CoinManagerApduCommands.getSetDeviceLabelAPDU(new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

}
