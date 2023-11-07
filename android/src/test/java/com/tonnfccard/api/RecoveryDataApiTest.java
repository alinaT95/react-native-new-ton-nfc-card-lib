package com.tonnfccard.api;

import android.content.Context;
import android.nfc.NfcAdapter;

import com.facebook.react.bridge.Callback;
import com.tonnfccard.api.nfc.NfcApduRunner;
import com.tonnfccard.utils.ByteArrayHelper;
import com.tonnfccard.smartcard.ErrorCodes;
import com.tonnfccard.smartcard.wrappers.RAPDU;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;


import static com.tonnfccard.smartcard.TonWalletAppletConstants.DATA_RECOVERY_PORTION_MAX_SIZE;
import static com.tonnfccard.smartcard.apdu.TonWalletAppletApduCommands.GET_APPLET_STATE_APDU_LIST;
import static com.tonnfccard.smartcard.apdu.TonWalletAppletApduCommands.GET_RECOVERY_DATA_HASH_APDU;
import static com.tonnfccard.smartcard.apdu.TonWalletAppletApduCommands.GET_RECOVERY_DATA_LEN_APDU;
import static com.tonnfccard.smartcard.apdu.TonWalletAppletApduCommands.IS_RECOVERY_DATA_SET_APDU;
import static com.tonnfccard.smartcard.apdu.TonWalletAppletApduCommands.RESET_RECOVERY_DATA_APDU;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;

import  com.tonnfccard.api.utils.PromiseImpl;


@RunWith(PowerMockRunner.class)
@PrepareForTest({NfcAdapter.class/*, Log.class*/})
public class RecoveryDataApiTest {
  private boolean failFlag;
  private String failMsg;

  private CountDownLatch lock = new CountDownLatch(1);

  private interface CallbackFromCard extends Callback {
  }

  private CallbackFromCard failCalback = new CallbackFromCard() {
    @Override
    public void invoke(Object... args) {
      failFlag = true;
    }
  };

  @Test
  public void getRecoveryDataPositiveTest() throws Exception {
    Context activity = mock(Context.class);

    String recData = "1291231291234512912341291234512912345545129123412912312912345129123412912345129123455451291234121291231291234512912341291234512912345545129112912312912345129123412912345129123455451291234129123129123451291234129123451291234554512912341212912312912345129123412912345129123455451291234129123129123451291234129123412912312912345129123412912345129123455451291234129123129123451291234129123451291234554512912341212912312912345129123412912345129123455451291129123129123451291234129123451291234554512912341291231291234512912341291234512912345545129123412129123129123451291234129123451291234554512912341291231291234512912341291234512912345545129123412912345129123455129123451291234559123451291234551291234a12912345523412912312912345129123012912345129123455451291234129123451291234551291234512912345591234512912145512912345179123455512912345545129123412912345129123455129123451291234559123451291234551291234512912345523412912312912345129123412912345129123455451f91234129123451291234551291234512912345591234512912345512912345129123455";
    byte[] recDataBytes = ByteArrayHelper.bytes(recData);
    System.out.println(recDataBytes.length);
    byte[] len = new byte[2];
    ByteArrayHelper.setShort(len, (short) 0, (short) 512);

    NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);

    when(nfcApduRunner.sendAPDUList(GET_APPLET_STATE_APDU_LIST)).thenReturn(new RAPDU(new byte[]{APP_PERSONALIZED, (byte) 0x90, 0x00}));
    when(nfcApduRunner.transmitCommand(not(eq(GET_RECOVERY_DATA_LEN_APDU)))).thenReturn(
      new RAPDU(ByteArrayHelper.bConcat(ByteArrayHelper.bSub(recDataBytes, 0, DATA_RECOVERY_PORTION_MAX_SIZE), new byte[]{(byte) 0x90, 0x00})),
      new RAPDU(ByteArrayHelper.bConcat(ByteArrayHelper.bSub(recDataBytes, DATA_RECOVERY_PORTION_MAX_SIZE, DATA_RECOVERY_PORTION_MAX_SIZE), new byte[]{(byte) 0x90, 0x00})),
      new RAPDU(ByteArrayHelper.bConcat(ByteArrayHelper.bSub(recDataBytes, 2 * DATA_RECOVERY_PORTION_MAX_SIZE), new byte[]{(byte) 0x90, 0x00}))
    );
    when(nfcApduRunner.transmitCommand(GET_RECOVERY_DATA_LEN_APDU)).thenReturn(new RAPDU(ByteArrayHelper.bConcat(len, new byte[]{(byte) 0x90, 0x00})));

    when(nfcApduRunner.sendTonWalletAppletAPDU(any())).thenCallRealMethod();
    when(nfcApduRunner.sendAPDU(any())).thenCallRealMethod();

    RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity, nfcApduRunner);
    CallbackFromCard resolve = new CallbackFromCard() {
      @Override
      public void invoke(Object... args) {
        System.out.println(args[0]);
        try {
          System.out.println("hey");
          assertEquals(args[0], recData.toUpperCase());
          failFlag = false;
        } catch (AssertionError e) {
          failFlag = true;
          failMsg = e.getMessage();
          e.printStackTrace();
        }
      }
    };

    recoveryDataApi.getRecoveryData(new PromiseImpl(resolve, failCalback));
    lock.await(2000, TimeUnit.MILLISECONDS);
    if (failFlag) fail(failMsg);


  }

  @Test
  public void getRecoveryDataNegativeTest() throws Exception {
    Context activity = mock(Context.class);

    byte[] errCode = new byte[2];
    short[] errorsToTest = new short[]{ErrorCodes.SW_INS_NOT_SUPPORTED, ErrorCodes.SW_WRONG_LENGTH,
      ErrorCodes.SW_RECOVERY_DATA_ALREADY_EXISTS, ErrorCodes.SW_INCORRECT_START_POS_OR_LE};

    for (int i = 0 ; i < errorsToTest.length ; i++) {
      System.out.println("Iteration#" + i);
      NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);
      ByteArrayHelper.setShort(errCode, (short)0, errorsToTest[i]);
      when(nfcApduRunner.sendAPDUList(GET_APPLET_STATE_APDU_LIST)).thenReturn(new RAPDU(new byte[]{APP_PERSONALIZED, (byte) 0x90, 0x00}));
      when(nfcApduRunner.transmitCommand(not(eq(GET_RECOVERY_DATA_LEN_APDU)))).thenReturn(new RAPDU(errCode));
      when(nfcApduRunner.transmitCommand(GET_RECOVERY_DATA_LEN_APDU)).thenReturn(new RAPDU(new byte[]{0x00, (byte) 0xFF,(byte) 0x90, 0x00}));

      when(nfcApduRunner.sendTonWalletAppletAPDU(any())).thenCallRealMethod();
      when(nfcApduRunner.sendAPDU(any())).thenCallRealMethod();

      final int ind = i;
      RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity, nfcApduRunner);
      CallbackFromCard reject = new CallbackFromCard() {
        @Override
        public void invoke(Object... args) {
          System.out.println("Error msg:" + args[0]);
          String err = (String) args[0];
          try {
            assertTrue(err.contains(ErrorCodes.getMsg(errorsToTest[ind])) && err.contains(ByteArrayHelper.hex(errorsToTest[ind])));
            failFlag = false;
          }
          catch (AssertionError e ){
            e.printStackTrace();
            failFlag = true;
            failMsg = e.getMessage();
          }
        }
      };

      recoveryDataApi.getRecoveryData(new PromiseImpl(failCalback, reject));
      lock.await(2000, TimeUnit.MILLISECONDS);
      if (failFlag) fail(failMsg);

    }
  }

  @Test
  public void getRecoveryDataTestForEmptyData() throws Exception {
    Context activity = mock(Context.class);
    NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);
    when(nfcApduRunner.sendTonWalletAppletAPDU(GET_RECOVERY_DATA_LEN_APDU)).thenReturn(new RAPDU(new byte[]{(byte) 0x00, (byte) 0x00, (byte) 0x90, (byte) 0x00}));
    RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity,  nfcApduRunner);

    CallbackFromCard reject = new CallbackFromCard() {
      @Override
      public void invoke(Object... args) {
        System.out.println("Error msg:" + args[0]);
        String err = (String) args[0];
        try {
          assertTrue(err.equals("Recovery data is empty"));
          failFlag = false;
        }
        catch (AssertionError e ){
          e.printStackTrace();
          failFlag = true;
          failMsg = e.getMessage();
        }
      }
    };

    recoveryDataApi.getRecoveryData(new PromiseImpl(failCalback, reject));
    lock.await(2000, TimeUnit.MILLISECONDS);
    if (failFlag) fail(failMsg);
  }

  @Test
  public void getRecoveryDataTestForNegativeDataLength() throws Exception {
    Context activity = mock(Context.class);
    NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);
    when(nfcApduRunner.sendTonWalletAppletAPDU(GET_RECOVERY_DATA_LEN_APDU)).thenReturn(new RAPDU(new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0x90, (byte) 0x00}));
    RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity,  nfcApduRunner);

    CallbackFromCard reject = new CallbackFromCard() {
      @Override
      public void invoke(Object... args) {
        System.out.println("Error msg:" + args[0]);
        String err = (String) args[0];
        try {
          assertTrue(err.contains("Recovery data is corrupted"));
          failFlag = false;
        }
        catch (AssertionError e ){
          e.printStackTrace();
          failFlag = true;
          failMsg = e.getMessage();
        }
      }
    };

    recoveryDataApi.getRecoveryData(new PromiseImpl(failCalback, reject));
    lock.await(2000, TimeUnit.MILLISECONDS);
    if (failFlag) fail(failMsg);
  }


  @Test
  public void addRecoveryDataPositiveTest() throws Exception {
    Context activity = mock(Context.class);
    NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);
    String recoveryData = "4444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555";
    System.out.println(recoveryData.length());
    when(nfcApduRunner.sendTonWalletAppletAPDU(any())).thenReturn(new RAPDU(new byte[]{(byte) 0x90, (byte) 0x00}));
    RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity,  nfcApduRunner);
    CallbackFromCard resolve = new CallbackFromCard() {
      @Override
      public void invoke(Object... args) {
        System.out.println(args[0]);
        try {
          assertEquals(args[0], "done");
          failFlag = false;
        }
        catch (AssertionError e){
          failFlag = true;
          failMsg = e.getMessage();
          e.printStackTrace();
        }
      }
    };
    recoveryDataApi.addRecoveryData(recoveryData, new PromiseImpl(resolve, failCalback));
    lock.await(2000, TimeUnit.MILLISECONDS);
    if (failFlag) fail(failMsg);
  }

  @Test
  public void addRecoveryDataNegativeTest() throws Exception {
    Context activity = mock(Context.class);

    byte[] errCode = new byte[2];
    short[] errorsToTest = new short[]{ErrorCodes.SW_INS_NOT_SUPPORTED, ErrorCodes.SW_WRONG_LENGTH,
      ErrorCodes.SW_RECOVERY_DATA_ALREADY_EXISTS, ErrorCodes.SW_INTEGRITY_OF_RECOVERY_DATA_CORRUPTED};
    String recoveryData = "4444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555444455554444555544445555";

    for (int i = 0 ; i < errorsToTest.length ; i++) {
      System.out.println("Iteration#" + i);
      NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);
      ByteArrayHelper.setShort(errCode, (short)0, errorsToTest[i]);
      when(nfcApduRunner.sendAPDUList(GET_APPLET_STATE_APDU_LIST)).thenReturn(new RAPDU(new byte[]{APP_PERSONALIZED, (byte) 0x90, 0x00}));
      when(nfcApduRunner.transmitCommand(any())).thenReturn(new RAPDU(errCode));
      when(nfcApduRunner.sendTonWalletAppletAPDU(any())).thenCallRealMethod();
      when(nfcApduRunner.sendAPDU(any())).thenCallRealMethod();

      final int ind = i;
      RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity,  nfcApduRunner);
      CallbackFromCard reject = new CallbackFromCard() {
        @Override
        public void invoke(Object... args) {
          System.out.println("Error msg:" + args[0]);
          String err = (String) args[0];
          try {
            assertTrue(err.contains(ErrorCodes.getMsg(errorsToTest[ind])) && err.contains(ByteArrayHelper.hex(errorsToTest[ind])));
            failFlag = false;
          }
          catch (AssertionError e ){
            e.printStackTrace();
            failFlag = true;
            failMsg = e.getMessage();
          }
        }
      };

      recoveryDataApi.addRecoveryData(recoveryData, new PromiseImpl(failCalback, reject));
      lock.await(2000, TimeUnit.MILLISECONDS);
      if (failFlag) fail(failMsg);

    }
  }


  @Test
  public void addRecoveryDataTestIncorrectRecoveryData() throws Exception {
    Context activity = mock(Context.class);
    NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);
    RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity,  nfcApduRunner);

    String s ="522100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111000011110000111100001111";
    System.out.println(s.length());

    CallbackFromCard reject = new CallbackFromCard() {
      @Override
      public void invoke(Object... args) {
        System.out.println("Error msg:" + args[0]);
        String err = (String) args[0];
        try {
          assertTrue(err.equals("Incorrect recoveryData: recoveryData is a hex string of length <= 2 * 1024."));
          failFlag = false;
        }
        catch (AssertionError e ){
          e.printStackTrace();
          failFlag = true;
          failMsg = e.getMessage();
        }
      }
    };

    recoveryDataApi.addRecoveryData(s, new PromiseImpl(failCalback, reject));
    lock.await(2000, TimeUnit.MILLISECONDS);
    if (failFlag) fail(failMsg);
  }

  @Test
  public void addRecoveryDataTestIncorrectRecoveryData2() throws Exception {
    Context activity = mock(Context.class);
    NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);

    RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity,  nfcApduRunner);

    String[] s = new String[] {"A234F", "", "ssa3"};
    for(int i = 0 ; i < s.length; i++){
      CallbackFromCard reject = new CallbackFromCard() {
        @Override
        public void invoke(Object... args) {
          System.out.println("Error msg:" + args[0]);
          String err = (String) args[0];
          try {
            assertTrue(err.equals("Data is not in hex."));
            failFlag = false;
          }
          catch (AssertionError e ){
            e.printStackTrace();
            failFlag = true;
            failMsg = e.getMessage();
          }
        }
      };

      recoveryDataApi.addRecoveryData(s[i], new PromiseImpl(failCalback, reject));
      lock.await(2000, TimeUnit.MILLISECONDS);
      if (failFlag) fail(failMsg);
    }
  }


  @Test
  public void getRecoveryDataLenPositiveTest() throws Exception {
    Context activity = mock(Context.class);
    NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);//NfcApduRunner.getInstance();
    when(nfcApduRunner.sendTonWalletAppletAPDU(GET_RECOVERY_DATA_LEN_APDU)).thenReturn(new RAPDU(ByteArrayHelper.bConcat(new byte[]{(byte) 0x01, (byte) 0x00, (byte) 0x90, (byte) 0x00})));
    RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity,  nfcApduRunner);
    int len = ByteArrayHelper.makeShort(new byte[]{(byte) 0x01, (byte) 0x00}, 0);
    CallbackFromCard resolve = new CallbackFromCard() {
      @Override
      public void invoke(Object... args) {
        System.out.println(args[0]);
        try {
          assertEquals(args[0],Integer.valueOf(len).toString());
          failFlag = false;
        }
        catch (AssertionError e){
          e.printStackTrace();
          failFlag = true;
          failMsg = e.getMessage();
        }
      }
    };
    recoveryDataApi.getRecoveryDataLen(new PromiseImpl(resolve, failCalback));
    lock.await(2000, TimeUnit.MILLISECONDS);
    if (failFlag) fail(failMsg);
  }

  @Test
  public void getRecoveryDataLenNegativeTestForIncorrectResponseLength() throws Exception {
    Context activity = mock(Context.class);
    NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);//NfcApduRunner.getInstance();
    when(nfcApduRunner.sendTonWalletAppletAPDU(GET_RECOVERY_DATA_LEN_APDU)).thenReturn(new RAPDU(ByteArrayHelper.bConcat(new byte[]{(byte) 0x02, (byte) 0x90, (byte) 0x00})));
    RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity,  nfcApduRunner);
    CallbackFromCard reject = new CallbackFromCard() {
      @Override
      public void invoke(Object... args) {
        System.out.println("Error msg:" + args[0]);
        String err = (String) args[0];
        try {
         // assertTrue(err.contains(ERROR_GET_RECOVERY_DATA_LEN_RESPONSE_IS_INCORRECT));
          failFlag = false;
        }
        catch (AssertionError e ){
          e.printStackTrace();
          failFlag = true;
          failMsg = e.getMessage();
        }
      }
    };

    recoveryDataApi.getRecoveryDataLen(new PromiseImpl(failCalback, reject));
    lock.await(2000, TimeUnit.MILLISECONDS);
    if (failFlag) fail(failMsg);
  }

  @Test
  public void getRecoveryDataLenNegativeTest() throws Exception {
    Context activity = mock(Context.class);

    byte[] errCode = new byte[2];
    short[] errorsToTest = new short[]{ErrorCodes.SW_INS_NOT_SUPPORTED, ErrorCodes.SW_WRONG_LENGTH};

    for (int i = 0 ; i < errorsToTest.length ; i++) {
      System.out.println("Iteration#" + i);
      NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);
      ByteArrayHelper.setShort(errCode, (short)0, errorsToTest[i]);
      when(nfcApduRunner.sendAPDUList(GET_APPLET_STATE_APDU_LIST)).thenReturn(new RAPDU(new byte[]{APP_PERSONALIZED, (byte) 0x90, 0x00}));
      when(nfcApduRunner.transmitCommand(GET_RECOVERY_DATA_LEN_APDU)).thenReturn(new RAPDU(errCode));
      when(nfcApduRunner.sendTonWalletAppletAPDU(GET_RECOVERY_DATA_LEN_APDU)).thenCallRealMethod();
      when(nfcApduRunner.sendAPDU(GET_RECOVERY_DATA_LEN_APDU)).thenCallRealMethod();

      final int ind = i;
      RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity,  nfcApduRunner);
      CallbackFromCard reject = new CallbackFromCard() {
        @Override
        public void invoke(Object... args) {
          System.out.println("Error msg:" + args[0]);
          String err = (String) args[0];
          try {
            assertTrue(err.contains(ErrorCodes.getMsg(errorsToTest[ind])) && err.contains(ByteArrayHelper.hex(errorsToTest[ind])));
            failFlag = false;
          }
          catch (AssertionError e ){
            e.printStackTrace();
            failFlag = true;
            failMsg = e.getMessage();
          }
        }
      };

      recoveryDataApi.getRecoveryDataLen(new PromiseImpl(failCalback, reject));
      lock.await(2000, TimeUnit.MILLISECONDS);
      if (failFlag) fail(failMsg);

    }
  }

  @Test
  public void isRecoveryDataSetPositiveTest() throws Exception {
    Context activity = mock(Context.class);
    NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);//NfcApduRunner.getInstance();
    when(nfcApduRunner.sendTonWalletAppletAPDU(IS_RECOVERY_DATA_SET_APDU)).thenReturn(new RAPDU(ByteArrayHelper.bConcat(new byte[]{(byte) 0x01, (byte) 0x90, (byte) 0x00})));
    RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity,  nfcApduRunner);
    CallbackFromCard resolve = new CallbackFromCard() {
      @Override
      public void invoke(Object... args) {
        System.out.println(args[0]);
        try {
          assertEquals(args[0],"true");
          failFlag = false;
        }
        catch (AssertionError e){
          e.printStackTrace();
          failFlag = true;
          failMsg = e.getMessage();
        }
      }
    };
    recoveryDataApi.isRecoveryDataSet(new PromiseImpl(resolve, failCalback));
    lock.await(2000, TimeUnit.MILLISECONDS);
    if (failFlag) fail(failMsg);
  }

  @Test
  public void isRecoveryDataSetNegativeTestForIncorrectResponseLength() throws Exception {
    Context activity = mock(Context.class);
    NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);//NfcApduRunner.getInstance();
    when(nfcApduRunner.sendTonWalletAppletAPDU(IS_RECOVERY_DATA_SET_APDU)).thenReturn(new RAPDU(ByteArrayHelper.bConcat(new byte[]{(byte) 0x90, (byte) 0x00})));
    RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity,  nfcApduRunner);
    CallbackFromCard reject = new CallbackFromCard() {
      @Override
      public void invoke(Object... args) {
        System.out.println("Error msg:" + args[0]);
        String err = (String) args[0];
        try {
        // assertTrue(err.contains(ERROR_IS_RECOVERY_DATA_SET_RESPONSE_IS_INCORRECT));
          failFlag = false;
        }
        catch (AssertionError e ){
          e.printStackTrace();
          failFlag = true;
          failMsg = e.getMessage();
        }
      }
    };
    recoveryDataApi.isRecoveryDataSet(new PromiseImpl(failCalback, reject));
    lock.await(2000, TimeUnit.MILLISECONDS);
    if (failFlag) fail(failMsg);
  }

  @Test
  public void isRecoveryDataSetNegativeTest() throws Exception {
    Context activity = mock(Context.class);

    byte[] errCode = new byte[2];
    short[] errorsToTest = new short[]{ErrorCodes.SW_INS_NOT_SUPPORTED, ErrorCodes.SW_WRONG_LENGTH};

    for (int i = 0 ; i < errorsToTest.length ; i++) {
      System.out.println("Iteration#" + i);
      NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);
      ByteArrayHelper.setShort(errCode, (short)0, errorsToTest[i]);
      when(nfcApduRunner.sendAPDUList(GET_APPLET_STATE_APDU_LIST)).thenReturn(new RAPDU(new byte[]{APP_PERSONALIZED, (byte) 0x90, 0x00}));
      when(nfcApduRunner.transmitCommand(IS_RECOVERY_DATA_SET_APDU)).thenReturn(new RAPDU(errCode));
      when(nfcApduRunner.sendTonWalletAppletAPDU(IS_RECOVERY_DATA_SET_APDU)).thenCallRealMethod();
      when(nfcApduRunner.sendAPDU(IS_RECOVERY_DATA_SET_APDU)).thenCallRealMethod();

      final int ind = i;
      RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity,  nfcApduRunner);
      CallbackFromCard reject = new CallbackFromCard() {
        @Override
        public void invoke(Object... args) {
          System.out.println("Error msg:" + args[0]);
          String err = (String) args[0];
          try {
            assertTrue(err.contains(ErrorCodes.getMsg(errorsToTest[ind])) && err.contains(ByteArrayHelper.hex(errorsToTest[ind])));
            failFlag = false;
          }
          catch (AssertionError e ){
            e.printStackTrace();
            failFlag = true;
            failMsg = e.getMessage();
          }
        }
      };

      recoveryDataApi.isRecoveryDataSet(new PromiseImpl(failCalback, reject));
      lock.await(2000, TimeUnit.MILLISECONDS);
      if (failFlag) fail(failMsg);

    }
  }

  @Test
  public void getRecoveryDataHashPositiveTest() throws Exception {
    Context activity = mock(Context.class);
    NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);//NfcApduRunner.getInstance();
    String hardcodedHash = "aaaa1111aaaa11112222333322223333aaaa1111aaaa11112222333322223333";
    when(nfcApduRunner.sendTonWalletAppletAPDU(GET_RECOVERY_DATA_HASH_APDU)).thenReturn(new RAPDU(ByteArrayHelper.bConcat(ByteArrayHelper.bytes(hardcodedHash), new byte[]{(byte) 0x90, (byte) 0x00})));
    RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity,  nfcApduRunner);
    CallbackFromCard resolve = new CallbackFromCard() {
      @Override
      public void invoke(Object... args) {
        System.out.println(args[0]);
        try {
          assertEquals(args[0], hardcodedHash.toUpperCase());
          failFlag = false;
        }
        catch (AssertionError e){
          e.printStackTrace();
          failFlag = true;
          failMsg = e.getMessage();
        }
      }
    };
    recoveryDataApi.getRecoveryDataHash(new PromiseImpl(resolve, failCalback));
    lock.await(2000, TimeUnit.MILLISECONDS);
    if (failFlag) fail(failMsg);
  }

  @Test
  public void getRecoveryDataHashNegativeTest() throws Exception {
    Context activity = mock(Context.class);

    byte[] errCode = new byte[2];
    short[] errorsToTest = new short[]{ErrorCodes.SW_INS_NOT_SUPPORTED, ErrorCodes.SW_WRONG_LENGTH};

    for (int i = 0 ; i < errorsToTest.length ; i++) {
      System.out.println("Iteration#" + i);
      NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);
      ByteArrayHelper.setShort(errCode, (short)0, errorsToTest[i]);
      when(nfcApduRunner.sendAPDUList(GET_APPLET_STATE_APDU_LIST)).thenReturn(new RAPDU(new byte[]{APP_PERSONALIZED, (byte) 0x90, 0x00}));
      when(nfcApduRunner.transmitCommand(GET_RECOVERY_DATA_HASH_APDU)).thenReturn(new RAPDU(errCode));
      when(nfcApduRunner.sendTonWalletAppletAPDU(GET_RECOVERY_DATA_HASH_APDU)).thenCallRealMethod();
      when(nfcApduRunner.sendAPDU(GET_RECOVERY_DATA_HASH_APDU)).thenCallRealMethod();

      final int ind = i;
      RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity,  nfcApduRunner);
      CallbackFromCard reject = new CallbackFromCard() {
        @Override
        public void invoke(Object... args) {
          System.out.println("Error msg:" + args[0]);
          String err = (String) args[0];
          try {
            assertTrue(err.contains(ErrorCodes.getMsg(errorsToTest[ind])) && err.contains(ByteArrayHelper.hex(errorsToTest[ind])));
            failFlag = false;
          }
          catch (AssertionError e ){
            e.printStackTrace();
            failFlag = true;
            failMsg = e.getMessage();
          }
        }
      };

      recoveryDataApi.getRecoveryDataHash(new PromiseImpl(failCalback, reject));
      lock.await(2000, TimeUnit.MILLISECONDS);
      if (failFlag) fail(failMsg);

    }
  }

  @Test
  public void getRecoveryDataHashNegativeTestForShortHash() throws Exception {
    Context activity = mock(Context.class);
    NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);//NfcApduRunner.getInstance();
    String hardcodedHash = "aaaa1111aaaa11112222333322223333aaaa1111aaaa111122223333222233"; // check len = 31
    when(nfcApduRunner.sendTonWalletAppletAPDU(GET_RECOVERY_DATA_HASH_APDU)).thenReturn(new RAPDU(ByteArrayHelper.bConcat(ByteArrayHelper.bytes(hardcodedHash), new byte[]{(byte) 0x90, (byte) 0x00})));
    RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity,  nfcApduRunner);
    CallbackFromCard reject = new CallbackFromCard() {
      @Override
      public void invoke(Object... args) {
        System.out.println("Error msg:" + args[0]);
        String err = (String) args[0];
        try {
         // assertTrue(err.contains(ERROR_RECOVERY_DATA_HASH_IS_INCORRECT));
          failFlag = false;
        }
        catch (AssertionError e ){
          e.printStackTrace();
          failFlag = true;
          failMsg = e.getMessage();
        }
      }
    };
    recoveryDataApi.getRecoveryDataHash(new PromiseImpl(failCalback, reject));
    lock.await(2000, TimeUnit.MILLISECONDS);
    if (failFlag) fail(failMsg);
  }

  @Test
  public void resetRecoveryDataPositiveTest() throws Exception {
    Context activity = mock(Context.class);
    NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);//NfcApduRunner.getInstance();
    when(nfcApduRunner.sendTonWalletAppletAPDU(RESET_RECOVERY_DATA_APDU)).thenReturn(new RAPDU(new byte[]{(byte) 0x90, (byte) 0x00}));
    RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity,  nfcApduRunner);
    CallbackFromCard resolve = new CallbackFromCard() {
      @Override
      public void invoke(Object... args) {
        System.out.println(args[0]);
        try {
          assertEquals(args[0], "done");
          failFlag = false;
        }
        catch (AssertionError e){
          failFlag = true;
          failMsg = e.getMessage();
          e.printStackTrace();
        }
      }
    };
    recoveryDataApi.resetRecoveryData(new PromiseImpl(resolve, failCalback));
    lock.await(2000, TimeUnit.MILLISECONDS);
    if (failFlag) fail(failMsg);
  }

  @Test
  public void resetRecoveryDataNegativeTest() throws Exception {
    Context activity = mock(Context.class);
    NfcApduRunner nfcApduRunner = mock(NfcApduRunner.class);
    byte[] errCode = new byte[2];
    ByteArrayHelper.setShort(errCode, (short)0, ErrorCodes.SW_INS_NOT_SUPPORTED);
    when(nfcApduRunner.sendAPDUList(GET_APPLET_STATE_APDU_LIST)).thenReturn(new RAPDU(new byte[]{APP_PERSONALIZED, (byte) 0x90, 0x00}));
    when(nfcApduRunner.transmitCommand(RESET_RECOVERY_DATA_APDU)).thenReturn(new RAPDU(errCode));
    when(nfcApduRunner.sendTonWalletAppletAPDU(RESET_RECOVERY_DATA_APDU)).thenCallRealMethod();
    when(nfcApduRunner.sendAPDU(RESET_RECOVERY_DATA_APDU)).thenCallRealMethod();


    RecoveryDataApi recoveryDataApi = new RecoveryDataApi(activity,  nfcApduRunner);
    CallbackFromCard reject = new CallbackFromCard() {
      @Override
      public void invoke(Object... args) {
        System.out.println("Error msg:" + args[0]);
        String err = (String) args[0];
        try {
          assertTrue(err.contains(ErrorCodes.getMsg(ErrorCodes.SW_INS_NOT_SUPPORTED)) && err.contains(ByteArrayHelper.hex(errCode)));
          failFlag = false;
        }
        catch (AssertionError e ){
          failFlag = true;
          failMsg = e.getMessage();
          e.printStackTrace();
        }
      }
    };

    recoveryDataApi.resetRecoveryData(new PromiseImpl(failCalback, reject));
    lock.await(2000, TimeUnit.MILLISECONDS);
    if (failFlag) fail(failMsg);
  }



}
