package com.tonnfccard.smartcard.apdu;

import com.tonnfccard.smartcard.cryptoUtils.HmacHelper;
import com.tonnfccard.smartcard.wrappers.CAPDU;

import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static com.tonnfccard.smartcard.TonWalletAppletConstants.*;

import static com.tonnfccard.smartcard.apdu.TonWalletAppletApduCommands.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TonWalletAppletApduCommandsTest {

    @Before
    public void initiateHmacHelper() throws Exception {
        HmacHelper hmacHelperMock = mock(HmacHelper.class);
        when(hmacHelperMock.computeMac((byte[])any())).thenReturn(new byte[HMAC_SHA_SIG_SIZE]);
        TonWalletAppletApduCommands.setHmacHelper(hmacHelperMock);
    }

    /**
     VERIFY_PASSWORD

     CLA: 0xB0
     INS: 0x92
     P1: 0x00
     P2: 0x00
     LC: 0x90
     Data: 128 bytes of unencrypted activation password | 16 bytes of IV for AES128 CBC

     */
    @Test
    public void getVerifyPasswordAPDUTest1() {
        byte[] passwordBytes = new byte[PASSWORD_SIZE];
        byte[] initialVector = new byte[IV_SIZE];
        CAPDU apdu = TonWalletAppletApduCommands.getVerifyPasswordAPDU(passwordBytes, initialVector);
        assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
        assertEquals(apdu.getIns(), INS_VERIFY_PASSWORD);
        assertEquals(apdu.getP1(),0x00);
        assertEquals(apdu.getP2(), 0x00);
        assertEquals(apdu.getData().length, PASSWORD_SIZE + IV_SIZE);
        assertEquals(apdu.getLe(), -1);
    }

    @Test
    public void getVerifyPasswordAPDUTest2() {
        byte[] initialVector = new byte[IV_SIZE];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i == PASSWORD_SIZE) continue;
            try {
                TonWalletAppletApduCommands.getVerifyPasswordAPDU(new byte[i], initialVector);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test()
    public void getVerifyPasswordAPDUTest3() {
        byte[] passwordBytes = new byte[PASSWORD_SIZE];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i == IV_SIZE) continue;
            try {
                TonWalletAppletApduCommands.getVerifyPasswordAPDU(passwordBytes, new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getVerifyPasswordAPDUTest4() {
        byte[] passwordBytes = new byte[PASSWORD_SIZE + 1];
        byte[] initialVector = new byte[IV_SIZE + 1];
        TonWalletAppletApduCommands.getVerifyPasswordAPDU(passwordBytes, initialVector);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getVerifyPasswordAPDUTest5() {
        TonWalletAppletApduCommands.getVerifyPasswordAPDU(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getVerifyPasswordAPDUTest6() {
        TonWalletAppletApduCommands.getVerifyPasswordAPDU(null, new byte[IV_SIZE]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getVerifyPasswordAPDUTest7() {
        TonWalletAppletApduCommands.getVerifyPasswordAPDU(new byte[PASSWORD_SIZE], null);
    }

    /***
     VERIFY_PIN

     CLA: 0xB0
     INS: 0xA2
     P1: 0x00
     P2: 0x00
     LC: 0x44
     Data: 4 bytes of PIN | 32 bytes of sault | 32 bytes of mac
     */
    @Test
    public void getVerifyPinAPDUTest1() throws Exception {
        byte[] pinBytes = new byte[PIN_SIZE];
        byte[] sault = new byte[SAULT_LENGTH];
        CAPDU apdu = TonWalletAppletApduCommands.getVerifyPinAPDU(pinBytes, sault);
        assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
        assertEquals(apdu.getIns(), INS_VERIFY_PIN);
        assertEquals(apdu.getP1(), 0x00);
        assertEquals(apdu.getP2(), 0x00);
        assertEquals(apdu.getData().length, PIN_SIZE + SAULT_LENGTH + HMAC_SHA_SIG_SIZE);
        assertEquals(apdu.getLe(), -1);
    }

    @Test
    public void getVerifyPinAPDUTest2() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i == PIN_SIZE) continue;
            try {
                TonWalletAppletApduCommands.getVerifyPinAPDU(new byte[i], sault);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test
    public void getVerifyPinAPDUTest3() throws Exception {
        byte[] pinBytes = new byte[PIN_SIZE ];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i == SAULT_LENGTH) continue;
            try {
                TonWalletAppletApduCommands.getVerifyPinAPDU(pinBytes, new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getVerifyPinAPDUTest4() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH + 1];
        byte[] pinBytes = new byte[PIN_SIZE + 1];
        TonWalletAppletApduCommands.getVerifyPinAPDU(pinBytes, sault);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getVerifyPinAPDUTest5() throws Exception {
        TonWalletAppletApduCommands.getVerifyPinAPDU(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getVerifyPinAPDUTest6() throws Exception {
        TonWalletAppletApduCommands.getVerifyPinAPDU(null,  new byte[SAULT_LENGTH]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getVerifyPinAPDUTest7() throws Exception {
        TonWalletAppletApduCommands.getVerifyPinAPDU(new byte[PIN_SIZE], null);
    }

    /***
     SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH

     CLA: 0xB0
     INS: 0xA5
     P1: 0x00
     P2: 0x00
     LC: APDU data length
     Data: messageLength (2bytes)| message | sault (32 bytes) | mac (32 bytes)
     LE: 0x40
     */
    @Test
    public void getSignShortMessageWithDefaultPathAPDUTest1() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        for (int dataLen = 1 ; dataLen <= DATA_FOR_SIGNING_MAX_SIZE ; dataLen++) {
            byte[] dataForSigning = new byte[dataLen];
            CAPDU apdu = TonWalletAppletApduCommands.getSignShortMessageWithDefaultPathAPDU(dataForSigning, sault);
            assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
            assertEquals(apdu.getIns(), INS_SIGN_SHORT_MESSAGE_WITH_DEFAULT_PATH);
            assertEquals(apdu.getP1(), 0x00);
            assertEquals(apdu.getP2(), 0x00);
            assertEquals(apdu.getData().length, 0x02 + dataLen + SAULT_LENGTH + HMAC_SHA_SIG_SIZE);
            assertEquals(apdu.getLe(), SIG_LEN);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSignShortMessageWithDefaultPathAPDUTest2() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        for (int i = DATA_FOR_SIGNING_MAX_SIZE + 1; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            try {
                TonWalletAppletApduCommands.getSignShortMessageWithDefaultPathAPDU(new byte[i], sault);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
        TonWalletAppletApduCommands.getSignShortMessageWithDefaultPathAPDU(new byte[0], sault);
    }

    @Test
    public void getSignShortMessageWithDefaultPathAPDUTest3() throws Exception {
        byte[] dataForSigning = new byte[1];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i == SAULT_LENGTH) continue;
            try {
                TonWalletAppletApduCommands.getSignShortMessageWithDefaultPathAPDU(dataForSigning, new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }


    @Test(expected = IllegalArgumentException.class)
    public void getSignShortMessageWithDefaultPathAPDUTest4() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH + 1];
        byte[] dataForSigning = new byte[DATA_FOR_SIGNING_MAX_SIZE + 1];
        TonWalletAppletApduCommands.getSignShortMessageWithDefaultPathAPDU(dataForSigning, sault);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSignShortMessageWithDefaultPathAPDUTest5() throws Exception {
        TonWalletAppletApduCommands.getSignShortMessageWithDefaultPathAPDU(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSignShortMessageWithDefaultPathAPDUTest6() throws Exception {
        TonWalletAppletApduCommands.getSignShortMessageWithDefaultPathAPDU(null, new byte[SAULT_LENGTH]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSignShortMessageWithDefaultPathAPDUTest7() throws Exception {
        TonWalletAppletApduCommands.getSignShortMessageWithDefaultPathAPDU(new byte[DATA_FOR_SIGNING_MAX_SIZE], null);
    }

    /***
     SIGN_SHORT_MESSAGE

     CLA: 0xB0
     INS: 0xA3
     P1: 0x00
     P2: 0x00
     LC: APDU data length
     Data: messageLength (2bytes)| message | indLength (1 byte, > 0, <= 10) | ind | sault (32 bytes) | mac (32 bytes)
     LE: 0x40

     */
    @Test
    public void getSignShortMessagAPDUTest1() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        Random random = new Random();
        for (int dataLen = 1 ; dataLen <= DATA_FOR_SIGNING_MAX_SIZE_FOR_CASE_WITH_PATH ; dataLen++) {
            byte[] dataForSigning = new byte[dataLen];
            int hdIndexLen = random.nextInt(MAX_IND_SIZE) + 1;
            byte[] hdIndex = new byte[hdIndexLen];
            CAPDU apdu = TonWalletAppletApduCommands.getSignShortMessageAPDU(dataForSigning, hdIndex, sault);
            assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
            assertEquals(apdu.getIns(), INS_SIGN_SHORT_MESSAGE);
            assertEquals(apdu.getP1(), 0x00);
            assertEquals(apdu.getP2(), 0x00);
            assertEquals(apdu.getData().length, 0x02 + dataLen + 0x01 + hdIndexLen + SAULT_LENGTH + HMAC_SHA_SIG_SIZE);
            assertEquals(apdu.getLe(), SIG_LEN);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSignShortMessageAPDUTest2() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        byte[] hdIndex = new byte[MAX_IND_SIZE];
        for (int i = DATA_FOR_SIGNING_MAX_SIZE_FOR_CASE_WITH_PATH + 1; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            try {
                TonWalletAppletApduCommands.getSignShortMessageAPDU(new byte[i], hdIndex, sault);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
        TonWalletAppletApduCommands.getSignShortMessageAPDU(new byte[0], hdIndex, sault);
    }

    @Test
    public void getSignShortMessageAPDUTest3() throws Exception {
        byte[] dataForSigning = new byte[1];
        byte[] hdIndex = new byte[1];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i == SAULT_LENGTH) continue;
            try {
                TonWalletAppletApduCommands.getSignShortMessageAPDU(dataForSigning, hdIndex, new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSignShortMessageAPDUTest4() throws Exception {
        byte[] dataForSigning = new byte[1];
        byte[] sault = new byte[SAULT_LENGTH];;
        for (int i = MAX_IND_SIZE + 1; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            try {
                TonWalletAppletApduCommands.getSignShortMessageAPDU(dataForSigning, new byte[i], sault);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
        TonWalletAppletApduCommands.getSignShortMessageAPDU(dataForSigning, new byte[0], sault);
    }


    @Test(expected = IllegalArgumentException.class)
    public void getSignShortMessageAPDUTest5() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH + 1];
        byte[] dataForSigning = new byte[DATA_FOR_SIGNING_MAX_SIZE + 1];
        byte[] hdIndex = new byte[MAX_IND_SIZE + 1];
        TonWalletAppletApduCommands.getSignShortMessageAPDU(dataForSigning, hdIndex, sault);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSignShortMessageAPDUTest6() throws Exception {
        TonWalletAppletApduCommands.getSignShortMessageAPDU(null, new byte[DATA_FOR_SIGNING_MAX_SIZE], new byte[MAX_IND_SIZE]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSignShortMessageAPDUTest7() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH ];
        byte[] dataForSigning = new byte[DATA_FOR_SIGNING_MAX_SIZE];
        TonWalletAppletApduCommands.getSignShortMessageAPDU(dataForSigning, null, sault);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getSignShortMessageAPDUTest8() throws Exception {
        byte[] dataForSigning = new byte[DATA_FOR_SIGNING_MAX_SIZE];
        byte[] hdIndex = new byte[MAX_IND_SIZE];
        TonWalletAppletApduCommands.getSignShortMessageAPDU(dataForSigning, hdIndex, null);
    }

    /***
     GET_PUBLIC_KEY

     CLA: 0xB0
     INS: 0xA0
     P1: 0x00
     P2: 0x00
     LC: Number of decimal places in ind
     Data: Ascii encoding of ind decimal places
     LE: 0x20

     */
    @Test
    public void getPublicKeyAPDUTest1() throws Exception {
        for (int hdIndexLen = 1 ; hdIndexLen <= MAX_IND_SIZE ; hdIndexLen++) {
            byte[] hdIndex = new byte[hdIndexLen];
            CAPDU apdu = TonWalletAppletApduCommands.getPublicKeyAPDU(hdIndex);
            assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
            assertEquals(apdu.getIns(), INS_GET_PUBLIC_KEY);
            assertEquals(apdu.getP1(), 0x00);
            assertEquals(apdu.getP2(), 0x00);
            assertEquals(apdu.getData().length, hdIndexLen);
            assertEquals(apdu.getLe(), PUBLIC_KEY_LEN);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPublicKeyAPDUTest2() throws Exception {
        for (int i = MAX_IND_SIZE + 1; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            try {
                TonWalletAppletApduCommands.getPublicKeyAPDU(new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
        TonWalletAppletApduCommands.getPublicKeyAPDU(new byte[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPublicKeyAPDUTest3() throws Exception {
        TonWalletAppletApduCommands.getPublicKeyAPDU(null);
    }

    /***
     RESET_KEYCHAIN

     CLA: 0xB0
     INS: 0xBC
     P1: 0x00
     P2: 0x00
     LC: 0x40
     Data: sault (32 bytes) | mac (32 bytes)

     */

    @Test
    public void getResetKeyChainAPDUTest1() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        CAPDU apdu = TonWalletAppletApduCommands.getResetKeyChainAPDU(sault);
        assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
        assertEquals(apdu.getIns(), INS_RESET_KEYCHAIN);
        assertEquals(apdu.getP1(), 0x00);
        assertEquals(apdu.getP2(), 0x00);
        assertEquals(apdu.getData().length, SAULT_LENGTH + HMAC_SHA_SIG_SIZE);
        assertEquals(apdu.getLe(), -1);
    }

    @Test
    public void getResetKeyChainAPDUTest2() throws Exception {
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  SAULT_LENGTH) continue;
            try {
                TonWalletAppletApduCommands.getResetKeyChainAPDU(new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getResetKeyChainAPDUTest3() throws Exception {
        TonWalletAppletApduCommands.getResetKeyChainAPDU(null);
    }

    /***
     GET_NUMBER_OF_KEYS

     CLA: 0xB0
     INS: 0xB8
     P1: 0x00
     P2: 0x00
     LC: 0x40
     Data: sault (32 bytes) | mac (32 bytes)
     LE: 0x02

     */

    @Test
    public void getNumberOfKeysAPDUTest1() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        CAPDU apdu = TonWalletAppletApduCommands.getNumberOfKeysAPDU(sault);
        assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
        assertEquals(apdu.getIns(), INS_GET_NUMBER_OF_KEYS);
        assertEquals(apdu.getP1(), 0x00);
        assertEquals(apdu.getP2(), 0x00);
        assertEquals(apdu.getData().length, SAULT_LENGTH + HMAC_SHA_SIG_SIZE);
        assertEquals(apdu.getLe(), GET_NUMBER_OF_KEYS_LE);
    }

    @Test
    public void getNumberOfKeysAPDUTest2() throws Exception {
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  SAULT_LENGTH) continue;
            try {
                TonWalletAppletApduCommands.getNumberOfKeysAPDU(new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNumberOfKeysAPDUTest3() throws Exception {
        TonWalletAppletApduCommands.getNumberOfKeysAPDU(null);
    }

    /***
     GET_OCCUPIED_STORAGE_SIZE

     CLA: 0xB0
     INS: 0xBA
     P1: 0x00
     P2: 0x00
     LC: 0x40
     Data: sault (32 bytes) | mac (32 bytes)
     LE: 0x02

     */

    @Test
    public void getGetOccupiedSizeAPDUTest1() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        CAPDU apdu = TonWalletAppletApduCommands.getGetOccupiedSizeAPDU(sault);
        assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
        assertEquals(apdu.getIns(), INS_GET_OCCUPIED_STORAGE_SIZE);
        assertEquals(apdu.getP1(), 0x00);
        assertEquals(apdu.getP2(), 0x00);
        assertEquals(apdu.getData().length, SAULT_LENGTH + HMAC_SHA_SIG_SIZE);
        assertEquals(apdu.getLe(), GET_OCCUPIED_SIZE_LE);
    }

    @Test
    public void getGetOccupiedSizeAPDUTest2() throws Exception {
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  SAULT_LENGTH) continue;
            try {
                TonWalletAppletApduCommands.getGetOccupiedSizeAPDU(new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getGetOccupiedSizeAPDUTest3() throws Exception {
        TonWalletAppletApduCommands.getGetOccupiedSizeAPDU(null);
    }

    /***
     GET_FREE_STORAGE_SIZE

     CLA: 0xB0
     INS: 0xB9
     P1: 0x00
     P2: 0x00
     LC: 0x40
     Data: sault (32 bytes) | mac (32 bytes)
     LE: 0x02

     */

    @Test
    public void getGetFreeSizeAPDUTest1() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        CAPDU apdu = TonWalletAppletApduCommands.getGetFreeSizeAPDU(sault);
        assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
        assertEquals(apdu.getIns(), INS_GET_FREE_STORAGE_SIZE);
        assertEquals(apdu.getP1(), 0x00);
        assertEquals(apdu.getP2(), 0x00);
        assertEquals(apdu.getData().length, SAULT_LENGTH + HMAC_SHA_SIG_SIZE);
        assertEquals(apdu.getLe(), GET_FREE_SIZE_LE);
    }

    @Test
    public void getGetFreeSizeAPDUTest2() throws Exception {
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  SAULT_LENGTH) continue;
            try {
                TonWalletAppletApduCommands.getGetFreeSizeAPDU(new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getGetFreeSizeAPDUTest3() throws Exception {
        TonWalletAppletApduCommands.getGetFreeSizeAPDU(null);
    }

    /***
     CHECK_AVAILABLE_VOL_FOR_NEW_KEY

     CLA: 0xB0
     INS: 0xB3
     P1: 0x00
     P2: 0x00
     LC: 0x42
     Data: length of new key (2 bytes) | sault (32 bytes) | mac (32 bytes)

     */

    @Test
    public void getCheckAvailableVolForNewKeyAPDUTest1() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        CAPDU apdu = TonWalletAppletApduCommands.getCheckAvailableVolForNewKeyAPDU(MAX_KEY_SIZE_IN_KEYCHAIN, sault);
        assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
        assertEquals(apdu.getIns(), INS_CHECK_AVAILABLE_VOL_FOR_NEW_KEY);
        assertEquals(apdu.getP1(), 0x00);
        assertEquals(apdu.getP2(), 0x00);
        assertEquals(apdu.getData().length, 0x02 + SAULT_LENGTH + HMAC_SHA_SIG_SIZE);
        assertEquals(apdu.getLe(), -1);
    }

    @Test
    public void getCheckAvailableVolForNewKeyAPDUTest2() throws Exception {
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  SAULT_LENGTH) continue;
            try {
                TonWalletAppletApduCommands.getCheckAvailableVolForNewKeyAPDU(MAX_KEY_SIZE_IN_KEYCHAIN, new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getCheckAvailableVolForNewKeyAPDUTest3() throws Exception {
        TonWalletAppletApduCommands.getCheckAvailableVolForNewKeyAPDU(MAX_KEY_SIZE_IN_KEYCHAIN, null);
    }

    /***
     CHECK_KEY_HMAC_CONSISTENCY

     CLA: 0xB0
     INS: 0xB0
     P1: 0x00
     P2: 0x00
     LC: 0x60
     Data: keyMac (32 bytes) | sault (32 bytes) | mac (32 bytes)

     */

    @Test
    public void getCheckKeyHmacConsistencyAPDUTest1() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        byte[] keyHmac = new byte[HMAC_SHA_SIG_SIZE];
        CAPDU apdu = TonWalletAppletApduCommands.getCheckKeyHmacConsistencyAPDU(keyHmac, sault);
        assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
        assertEquals(apdu.getIns(), INS_CHECK_KEY_HMAC_CONSISTENCY);
        assertEquals(apdu.getP1(), 0x00);
        assertEquals(apdu.getP2(), 0x00);
        assertEquals(apdu.getData().length, SAULT_LENGTH + 2 * HMAC_SHA_SIG_SIZE);
        assertEquals(apdu.getLe(), -1);
    }

    @Test
    public void  getCheckKeyHmacConsistencyAPDUTest2() throws Exception {
        byte[] keyHmac = new byte[HMAC_SHA_SIG_SIZE];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  SAULT_LENGTH) continue;
            try {
                TonWalletAppletApduCommands.getCheckKeyHmacConsistencyAPDU(keyHmac, new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test
    public void  getCheckKeyHmacConsistencyAPDUTest3() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  HMAC_SHA_SIG_SIZE) continue;
            try {
                TonWalletAppletApduCommands.getCheckKeyHmacConsistencyAPDU(new byte[i], sault);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void  getCheckKeyHmacConsistencyAPDUTest4() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH + 1];
        byte[] keyHmac = new byte[HMAC_SHA_SIG_SIZE + 1];
        TonWalletAppletApduCommands.getCheckKeyHmacConsistencyAPDU(keyHmac, sault);
    }

    @Test(expected = IllegalArgumentException.class)
    public void  getCheckKeyHmacConsistencyAPDUTest5() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        TonWalletAppletApduCommands.getCheckKeyHmacConsistencyAPDU(null, sault);
    }

    @Test(expected = IllegalArgumentException.class)
    public void  getCheckKeyHmacConsistencyAPDUTest6() throws Exception {
        byte[] keyHmac = new byte[HMAC_SHA_SIG_SIZE];
        TonWalletAppletApduCommands.getCheckKeyHmacConsistencyAPDU(keyHmac, null);
    }

    /***
     INITIATE_CHANGE_OF_KEY

     CLA: 0xB0
     INS: 0xB5
     P1: 0x00
     P2: 0x00
     LC: 0x42
     Data: index of key (2 bytes) | sault (32 bytes) | mac (32 bytes)

     */

    @Test
    public void getInitiateChangeOfKeyAPDUTest1() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        byte[] index = new byte[KEYCHAIN_KEY_INDEX_LEN];
        CAPDU apdu = TonWalletAppletApduCommands.getInitiateChangeOfKeyAPDU(index, sault);
        assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
        assertEquals(apdu.getIns(), INS_INITIATE_CHANGE_OF_KEY);
        assertEquals(apdu.getP1(), 0x00);
        assertEquals(apdu.getP2(), 0x00);
        assertEquals(apdu.getData().length, KEYCHAIN_KEY_INDEX_LEN + SAULT_LENGTH + HMAC_SHA_SIG_SIZE);
        assertEquals(apdu.getLe(), -1);
    }

    @Test
    public void  getInitiateChangeOfKeyAPDUTest2() throws Exception {
        byte[] index = new byte[KEYCHAIN_KEY_INDEX_LEN];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  SAULT_LENGTH) continue;
            try {
                TonWalletAppletApduCommands.getInitiateChangeOfKeyAPDU(index, new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test
    public void  getInitiateChangeOfKeyAPDUTestt3() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  KEYCHAIN_KEY_INDEX_LEN) continue;
            try {
                TonWalletAppletApduCommands.getInitiateChangeOfKeyAPDU(new byte[i], sault);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void  getInitiateChangeOfKeyAPDUTest4() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH + 1];
        byte[] index = new byte[KEYCHAIN_KEY_INDEX_LEN + 1];
        TonWalletAppletApduCommands.getInitiateChangeOfKeyAPDU(index, sault);
    }

    @Test(expected = IllegalArgumentException.class)
    public void  getInitiateChangeOfKeyAPDUTest5() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        TonWalletAppletApduCommands.getInitiateChangeOfKeyAPDU(null, sault);
    }

    @Test(expected = IllegalArgumentException.class)
    public void  getInitiateChangeOfKeyAPDUTest6() throws Exception {
        byte[] index = new byte[KEYCHAIN_KEY_INDEX_LEN];
        TonWalletAppletApduCommands.getInitiateChangeOfKeyAPDU(index, null);
    }


    /***
     INITIATE_DELETE_KEY

     CLA: 0xB0
     INS: 0xB7
     P1: 0x00
     P2: 0x00
     LC: 0x42
     Data: key  index (2 bytes) | sault (32 bytes) | mac (32 bytes)
     LE: 0x02

     */

    @Test
    public void getInitiateDeleteOfKeyAPDUTest1() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        byte[] index = new byte[KEYCHAIN_KEY_INDEX_LEN];
        CAPDU apdu = TonWalletAppletApduCommands.getInitiateDeleteOfKeyAPDU(index, sault);
        assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
        assertEquals(apdu.getIns(), INS_INITIATE_DELETE_KEY);
        assertEquals(apdu.getP1(), 0x00);
        assertEquals(apdu.getP2(), 0x00);
        assertEquals(apdu.getData().length, KEYCHAIN_KEY_INDEX_LEN + SAULT_LENGTH + HMAC_SHA_SIG_SIZE);
        assertEquals(apdu.getLe(), INITIATE_DELETE_KEY_LE);
    }

    @Test
    public void  getInitiateDeleteOfKeyAPDUTest2() throws Exception {
        byte[] index = new byte[KEYCHAIN_KEY_INDEX_LEN];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  SAULT_LENGTH) continue;
            try {
                TonWalletAppletApduCommands.getInitiateDeleteOfKeyAPDU(index, new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test
    public void  getInitiateDeleteOfKeyAPDUTest3() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  KEYCHAIN_KEY_INDEX_LEN) continue;
            try {
                TonWalletAppletApduCommands.getInitiateDeleteOfKeyAPDU(new byte[i], sault);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void  getInitiateDeleteOfKeyAPDUTest4() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH + 1];
        byte[] index = new byte[KEYCHAIN_KEY_INDEX_LEN + 1];
        TonWalletAppletApduCommands.getInitiateDeleteOfKeyAPDU(index, sault);
    }

    @Test(expected = IllegalArgumentException.class)
    public void  getInitiateDeleteOfKeyAPDUTest5() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        TonWalletAppletApduCommands.getInitiateDeleteOfKeyAPDU(null, sault);
    }

    @Test(expected = IllegalArgumentException.class)
    public void  getInitiateDeleteOfKeyAPDUTest6() throws Exception {
        byte[] index = new byte[KEYCHAIN_KEY_INDEX_LEN];
        TonWalletAppletApduCommands.getInitiateDeleteOfKeyAPDU(index, null);
    }


    /***
     GET_KEY_INDEX_IN_STORAGE_AND_LEN

     CLA: 0xB0
     INS: 0xB1
     P1: 0x00
     P2: 0x00
     LC: 0x60
     Data: hmac of key (32 bytes) | sault (32 bytes) | mac (32 bytes)
     LE: 0x04

     */

    @Test
    public void getGetIndexAndLenOfKeyInKeyChainAPDUTest1() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        byte[] keyHmac = new byte[HMAC_SHA_SIG_SIZE];
        CAPDU apdu = TonWalletAppletApduCommands.getGetIndexAndLenOfKeyInKeyChainAPDU(keyHmac, sault);
        assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
        assertEquals(apdu.getIns(), INS_GET_KEY_INDEX_IN_STORAGE_AND_LEN);
        assertEquals(apdu.getP1(), 0x00);
        assertEquals(apdu.getP2(), 0x00);
        assertEquals(apdu.getData().length, SAULT_LENGTH + 2 * HMAC_SHA_SIG_SIZE);
        assertEquals(apdu.getLe(), GET_KEY_INDEX_IN_STORAGE_AND_LEN_LE);
    }

    @Test
    public void  getGetIndexAndLenOfKeyInKeyChainAPDUTest2() throws Exception {
        byte[] keyHmac = new byte[HMAC_SHA_SIG_SIZE];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  SAULT_LENGTH) continue;
            try {
                TonWalletAppletApduCommands.getGetIndexAndLenOfKeyInKeyChainAPDU(keyHmac, new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test
    public void  getGetIndexAndLenOfKeyInKeyChainAPDUTest3() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  HMAC_SHA_SIG_SIZE) continue;
            try {
                TonWalletAppletApduCommands.getGetIndexAndLenOfKeyInKeyChainAPDU(new byte[i], sault);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getGetIndexAndLenOfKeyInKeyChainAPDUTest4() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH + 1];
        byte[] keyHmac = new byte[HMAC_SHA_SIG_SIZE + 1];
        TonWalletAppletApduCommands.getGetIndexAndLenOfKeyInKeyChainAPDU(keyHmac, sault);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getGetIndexAndLenOfKeyInKeyChainAPDUTest5() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        TonWalletAppletApduCommands.getGetIndexAndLenOfKeyInKeyChainAPDU(null, sault);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getGetIndexAndLenOfKeyInKeyChainAPDUTest6() throws Exception {
        byte[] keyHmac = new byte[HMAC_SHA_SIG_SIZE];
        TonWalletAppletApduCommands.getGetIndexAndLenOfKeyInKeyChainAPDU(keyHmac, null);
    }

    /***
     DELETE_KEY_CHUNK

     CLA: 0xB0
     INS: 0xBE
     P1: 0x00
     P2: 0x00
     LC: 0x40
     Data: sault (32 bytes) | mac (32 bytes)
     LE: 0x01

     */

    @Test
    public void getDeleteKeyChunkAPDUTest1() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        CAPDU apdu = TonWalletAppletApduCommands.getDeleteKeyChunkAPDU(sault);
        assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
        assertEquals(apdu.getIns(), INS_DELETE_KEY_CHUNK);
        assertEquals(apdu.getP1(), 0x00);
        assertEquals(apdu.getP2(), 0x00);
        assertEquals(apdu.getData().length, SAULT_LENGTH + HMAC_SHA_SIG_SIZE);
        assertEquals(apdu.getLe(), DELETE_KEY_CHUNK_LE);
    }

    @Test
    public void getDeleteKeyChunkAPDUTest2() throws Exception {
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  SAULT_LENGTH) continue;
            try {
                TonWalletAppletApduCommands.getDeleteKeyChunkAPDU(new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getDeleteKeyChunkAPDUTest3() throws Exception {
        TonWalletAppletApduCommands.getDeleteKeyChunkAPDU( null);
    }

    /***
     DELETE_KEY_RECORD

     CLA: 0xB0
     INS: 0xBF
     P1: 0x00
     P2: 0x00
     LC: 0x40
     Data: sault (32 bytes) | mac (32 bytes)
     LE: 0x01

     */

    @Test
    public void getDeleteKeyRecordAPDUTest1() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        CAPDU apdu = TonWalletAppletApduCommands.getDeleteKeyRecordAPDU(sault);
        assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
        assertEquals(apdu.getIns(), INS_DELETE_KEY_RECORD);
        assertEquals(apdu.getP1(), 0x00);
        assertEquals(apdu.getP2(), 0x00);
        assertEquals(apdu.getData().length, SAULT_LENGTH + HMAC_SHA_SIG_SIZE);
        assertEquals(apdu.getLe(), DELETE_KEY_RECORD_LE);
    }

    @Test
    public void getDeleteKeyRecordAPDUTest2() throws Exception {
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  SAULT_LENGTH) continue;
            try {
                TonWalletAppletApduCommands.getDeleteKeyRecordAPDU(new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getDeleteKeyRecordAPDUTest3() throws Exception {
        TonWalletAppletApduCommands.getDeleteKeyRecordAPDU( null);
    }

    /***
     GET_HMAC

     CLA: 0xB0
     INS: 0xBB
     P1: 0x00
     P2: 0x00
     LC: 0x42
     Data: index of key (2 bytes) | sault (32 bytes) | mac (32 bytes)
     LE: 0x22

     */

    @Test
    public void getGetHmacAPDUTest1() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        byte[] index = new byte[KEYCHAIN_KEY_INDEX_LEN];
        CAPDU apdu = TonWalletAppletApduCommands.getGetHmacAPDU(index, sault);
        assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
        assertEquals(apdu.getIns(), INS_GET_HMAC);
        assertEquals(apdu.getP1(), 0x00);
        assertEquals(apdu.getP2(), 0x00);
        assertEquals(apdu.getData().length, KEYCHAIN_KEY_INDEX_LEN + SAULT_LENGTH + HMAC_SHA_SIG_SIZE);
        assertEquals(apdu.getLe(), GET_HMAC_LE);
    }

    @Test
    public void  getGetHmacAPDUTest2() throws Exception {
        byte[] index = new byte[KEYCHAIN_KEY_INDEX_LEN];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  SAULT_LENGTH) continue;
            try {
                TonWalletAppletApduCommands.getGetHmacAPDU(index, new byte[i]);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test
    public void  getGetHmacAPDUTest3() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  KEYCHAIN_KEY_INDEX_LEN) continue;
            try {
                TonWalletAppletApduCommands.getGetHmacAPDU(new byte[i], sault);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getGetHmacAPDUTest4() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH + 1];
        byte[] index = new byte[KEYCHAIN_KEY_INDEX_LEN + 1];
        TonWalletAppletApduCommands.getGetHmacAPDU(index, sault);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getGetHmacAPDUTest5() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        TonWalletAppletApduCommands.getGetHmacAPDU(null, sault);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getGetHmacAPDUTest6() throws Exception {
        byte[] index = new byte[KEYCHAIN_KEY_INDEX_LEN];
        TonWalletAppletApduCommands.getGetHmacAPDU(index, null);
    }

    /***
     GET_KEY_CHUNK

     CLA: 0xB0
     INS: 0xB2
     P1: 0x00
     P2: 0x00
     LC: 0x44
     Data: key  index (2 bytes) | startPos (2 bytes) | sault (32 bytes) | mac (32 bytes)
     LE: Key chunk length

     */

    @Test
    public void getGetKeyChunkAPDUTest1() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        byte[] index = new byte[KEYCHAIN_KEY_INDEX_LEN];
        int le = DATA_PORTION_MAX_SIZE;
        CAPDU apdu = TonWalletAppletApduCommands.getGetKeyChunkAPDU(index, (short)0x00, sault, (byte)le);
        assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
        assertEquals(apdu.getIns(), INS_GET_KEY_CHUNK);
        assertEquals(apdu.getP1(), 0x00);
        assertEquals(apdu.getP2(), 0x00);
        assertEquals(apdu.getData().length, 0x02 + KEYCHAIN_KEY_INDEX_LEN + SAULT_LENGTH + HMAC_SHA_SIG_SIZE);
        assertEquals(apdu.getLe(), le);
    }

    @Test
    public void  getGetKeyChunkAPDUTest2() throws Exception {
        byte[] index = new byte[KEYCHAIN_KEY_INDEX_LEN];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  SAULT_LENGTH) continue;
            try {
                TonWalletAppletApduCommands.getGetKeyChunkAPDU(index, (short)0x00, new byte[i], (byte) 0x01);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test
    public void  getGetKeyChunkAPDUTest3() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE ; i++ ) {
            if (i ==  KEYCHAIN_KEY_INDEX_LEN) continue;
            try {
                TonWalletAppletApduCommands.getGetKeyChunkAPDU(new byte[i], (short)0x00, sault, (byte) 0x02);
                fail();
            }
            catch (IllegalArgumentException e){}
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getGetKeyChunkAPDUTest4() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH + 1];
        byte[] index = new byte[KEYCHAIN_KEY_INDEX_LEN + 1];
        TonWalletAppletApduCommands.getGetKeyChunkAPDU(index, (short)0x00, sault, (byte) 0x03);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getGetKeyChunkAPDUTest5() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        TonWalletAppletApduCommands.getGetKeyChunkAPDU(null, (short)0x00, sault, (byte) 0x03);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getGetKeyChunkAPDUTest6() throws Exception {
        byte[] index = new byte[KEYCHAIN_KEY_INDEX_LEN];
        TonWalletAppletApduCommands.getGetKeyChunkAPDU(index, (short)0x00, null, (byte) 0x03);
    }

    /***
     ADD_KEY_CHUNK

     CLA: 0xB0
     INS: 0xB4
     P1: 0x00 (START_OF_TRANSMISSION), 0x01 or 0x02 (END_OF_TRANSMISSION)
     P2: 0x00
     LC:
     if (P1 = 0x00 OR  0x01): 0x01 +  length of key chunk + 0x40
     if (P1 = 0x02): 0x60

     Data:
     if (P1 = 0x00 OR  0x01): length of key chunk (1 byte) | key chunk | sault (32 bytes) | mac (32 bytes)
     if (P1 = 0x02): hmac of key (32 bytes) | sault (32 bytes) | mac (32 bytes)

     LE: if (P1 = 0x02): 0x02

     */

    @Test
    public void getAddKeyChunkAPDUTest1() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        for  (byte p1 = 0; p1 < 2; p1 ++) {
            for  (int keyChunkLen = 1; keyChunkLen <= DATA_PORTION_MAX_SIZE; keyChunkLen ++) {
                byte[] keyChunk = new byte[keyChunkLen];
                CAPDU apdu = TonWalletAppletApduCommands.getAddKeyChunkAPDU(p1, keyChunk, sault);
                assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
                assertEquals(apdu.getIns(), INS_ADD_KEY_CHUNK);
                assertEquals(apdu.getP1(), p1);
                assertEquals(apdu.getP2(), 0x00);
                assertEquals(apdu.getData().length, 0x01 + keyChunkLen + SAULT_LENGTH + HMAC_SHA_SIG_SIZE);
                assertEquals(apdu.getLe(), -1);
            }
        }

        byte p1 = 0x02;
        byte[] keyMac = new byte[HMAC_SHA_SIG_SIZE];
        CAPDU apdu = TonWalletAppletApduCommands.getAddKeyChunkAPDU(p1, keyMac,  sault);
        assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
        assertEquals(apdu.getIns(), INS_ADD_KEY_CHUNK);
        assertEquals(apdu.getP1(), p1);
        assertEquals(apdu.getP2(), 0x00);
        assertEquals(apdu.getData().length, SAULT_LENGTH +  2 * HMAC_SHA_SIG_SIZE);
        assertEquals(apdu.getLe(), SEND_CHUNK_LE);
    }

    @Test
    public void  getAddKeyChunkAPDUTest2() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        for  (byte p1 = 0; p1 < 2; p1 ++) {
            try {
                TonWalletAppletApduCommands.getAddKeyChunkAPDU(p1, new byte[0], sault);
                fail();
            } catch (IllegalArgumentException e) {
            }
            for (int i = DATA_PORTION_MAX_SIZE + 1; i <= APDU_DATA_MAX_SIZE; i++) {
                try {
                    TonWalletAppletApduCommands.getAddKeyChunkAPDU(p1, new byte[i], sault);
                    fail();
                } catch (IllegalArgumentException e) {
                }
            }
        }

        for (int i = 0; i <= APDU_DATA_MAX_SIZE; i++) {
            if (i == HMAC_SHA_SIG_SIZE) continue;
            try {
                TonWalletAppletApduCommands.getAddKeyChunkAPDU((byte)0x02, new byte[i], sault);
                fail();
            } catch (IllegalArgumentException e) {
            }
        }
    }

    @Test
    public void  getAddKeyChunkAPDUTes3() throws Exception {
        byte[] keyChunk = new byte[DATA_PORTION_MAX_SIZE];
        for  (byte p1 = 0; p1 < 2; p1 ++) {
            for (int i = 0; i <= APDU_DATA_MAX_SIZE; i++) {
                if (i == SAULT_LENGTH) continue;
                try {
                    TonWalletAppletApduCommands.getAddKeyChunkAPDU(p1, keyChunk, new byte [i]);
                    fail();
                } catch (IllegalArgumentException e) {
                }
            }
        }

        byte[] mac = new byte[HMAC_SHA_SIG_SIZE];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE; i++) {
            if (i == SAULT_LENGTH) continue;
            try {
                TonWalletAppletApduCommands.getAddKeyChunkAPDU((byte)0x02, mac, new byte [i]);
                fail();
            } catch (IllegalArgumentException e) {
            }
        }
    }

    @Test
    public void  getAddKeyChunkAPDUTest4() throws Exception {
        for  (byte p1 = 0; p1 < 2; p1 ++) {
            try {
                TonWalletAppletApduCommands.getAddKeyChunkAPDU(p1, new byte[DATA_PORTION_MAX_SIZE + 1], new byte[SAULT_LENGTH + 1]);
                fail();
            } catch (IllegalArgumentException e) {
            }
        }
        try {
            TonWalletAppletApduCommands.getAddKeyChunkAPDU((byte) 0x02, new byte[HMAC_SHA_SIG_SIZE + 1], new byte[SAULT_LENGTH + 1]);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAddKeyChunkAPDUTest5() throws Exception {
        TonWalletAppletApduCommands.getAddKeyChunkAPDU((byte) 0x00, null, new byte[SAULT_LENGTH]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAddKeyChunkAPDUTest6() throws Exception {
        TonWalletAppletApduCommands.getAddKeyChunkAPDU((byte) 0x00, new byte[DATA_PORTION_MAX_SIZE], null);
    }

    /***
     CHANGE_KEY_CHUNK

     CLA: 0xB0
     INS: 0xB6
     P1: 0x00 (START_OF_TRANSMISSION), 0x01 or 0x02 (END_OF_TRANSMISSION)
     P2: 0x00
     LC:
     if (P1 = 0x00 OR  0x01): 0x01 +  length of key chunk + 0x40
     if (P1 = 0x02): 0x60

     Data:
     if (P1 = 0x00 OR  0x01): length of key chunk (1 byte) | key chunk | sault (32 bytes) | mac (32 bytes)
     if (P1 = 0x02): hmac of key (32 bytes) | sault (32 bytes) | mac (32 bytes)

     LE: if (P1 = 0x02): 0x02

     */

    @Test
    public void getChangeKeyChunkAPDUTest1() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];

        byte le = (byte)DATA_PORTION_MAX_SIZE;
        for  (byte p1 = 0; p1 < 2; p1 ++) {
            for  (int keyChunkLen = 1; keyChunkLen <= DATA_PORTION_MAX_SIZE; keyChunkLen ++) {
                byte[] keyChunk = new byte[keyChunkLen];
                CAPDU apdu = TonWalletAppletApduCommands.getChangeKeyChunkAPDU(p1, keyChunk, sault);
                assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
                assertEquals(apdu.getIns(), INS_CHANGE_KEY_CHUNK);
                assertEquals(apdu.getP1(), p1);
                assertEquals(apdu.getP2(), 0x00);
                assertEquals(apdu.getData().length, 0x01 + keyChunkLen + SAULT_LENGTH + HMAC_SHA_SIG_SIZE);
                assertEquals(apdu.getLe(), -1);
            }
        }

        byte p1 = 0x02;
        byte[] keyMac = new byte[HMAC_SHA_SIG_SIZE];
        CAPDU apdu = TonWalletAppletApduCommands.getChangeKeyChunkAPDU(p1, keyMac,  sault);
        assertEquals(apdu.getCla(), WALLET_APPLET_CLA);
        assertEquals(apdu.getIns(), INS_CHANGE_KEY_CHUNK);
        assertEquals(apdu.getP1(), p1);
        assertEquals(apdu.getP2(), 0x00);
        assertEquals(apdu.getData().length, SAULT_LENGTH +  2 * HMAC_SHA_SIG_SIZE);
        assertEquals(apdu.getLe(), SEND_CHUNK_LE);
    }

    @Test
    public void  getChangeKeyChunkAPDUTest2() throws Exception {
        byte[] keyChunk = new byte[DATA_PORTION_MAX_SIZE];
        for  (byte p1 = 0; p1 < 2; p1 ++) {
            for (int i = 0; i <= APDU_DATA_MAX_SIZE; i++) {
                if (i == SAULT_LENGTH) continue;
                try {
                    TonWalletAppletApduCommands.getChangeKeyChunkAPDU(p1, keyChunk, new byte [i]);
                    fail();
                } catch (IllegalArgumentException e) {
                }
            }
        }
        byte[] mac = new byte[HMAC_SHA_SIG_SIZE];
        for (int i = 0; i <= APDU_DATA_MAX_SIZE; i++) {
            if (i == SAULT_LENGTH) continue;
            try {
                TonWalletAppletApduCommands.getChangeKeyChunkAPDU((byte)0x02, mac, new byte [i]);
                fail();
            } catch (IllegalArgumentException e) {
            }
        }
    }

    @Test
    public void  getChangeKeyChunkAPDUTest3() throws Exception {
        byte[] sault = new byte[SAULT_LENGTH];
        for  (byte p1 = 0; p1 < 2; p1 ++) {
            try {
                TonWalletAppletApduCommands.getChangeKeyChunkAPDU(p1, new byte[0], sault);
                fail();
            } catch (IllegalArgumentException e) {
            }
            for (int i = DATA_PORTION_MAX_SIZE + 1; i <= APDU_DATA_MAX_SIZE; i++) {
                try {
                    TonWalletAppletApduCommands.getChangeKeyChunkAPDU(p1, new byte[i], sault);
                    fail();
                } catch (IllegalArgumentException e) {
                }
            }
        }

        for (int i = 0; i <= APDU_DATA_MAX_SIZE; i++) {
            if (i == HMAC_SHA_SIG_SIZE) continue;
            try {
                TonWalletAppletApduCommands.getChangeKeyChunkAPDU((byte)0x02, new byte[i], sault);
                fail();
            } catch (IllegalArgumentException e) {
            }
        }
    }

    @Test
    public void  getChangeKeyChunkAPDUTes4() throws Exception {
        for  (byte p1 = 0; p1 < 2; p1 ++) {
            try {
                TonWalletAppletApduCommands.getChangeKeyChunkAPDU(p1, new byte[DATA_PORTION_MAX_SIZE + 1], new byte[SAULT_LENGTH + 1]);
                fail();
            } catch (IllegalArgumentException e) {
            }
        }
        try {
            TonWalletAppletApduCommands.getChangeKeyChunkAPDU((byte) 0x02, new byte[HMAC_SHA_SIG_SIZE + 1], new byte[SAULT_LENGTH + 1]);
            fail();
        } catch (IllegalArgumentException e) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getChangeKeyChunkAPDUTest5() throws Exception {
        TonWalletAppletApduCommands.getChangeKeyChunkAPDU((byte) 0x00, null, new byte[SAULT_LENGTH]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getChangeKeyChunkAPDUTest6() throws Exception {
        TonWalletAppletApduCommands.getChangeKeyChunkAPDU((byte) 0x00, new byte[DATA_PORTION_MAX_SIZE], null);
    }

}
