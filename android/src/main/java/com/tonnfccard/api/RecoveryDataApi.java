package com.tonnfccard.api;

import android.content.Context;
import android.util.Log;

import com.tonnfccard.api.callback.NfcCallback;
import com.tonnfccard.smartcard.wrappers.ApduRunner;
import com.tonnfccard.smartcard.wrappers.RAPDU;
import static com.tonnfccard.api.utils.ResponsesConstants.*;
import static com.tonnfccard.smartcard.TonWalletAppletConstants.DATA_RECOVERY_PORTION_MAX_SIZE;
import static com.tonnfccard.smartcard.TonWalletAppletConstants.RECOVERY_DATA_MAX_SIZE;
import static com.tonnfccard.smartcard.TonWalletAppletConstants.SHA_HASH_SIZE;
import static com.tonnfccard.smartcard.apdu.TonWalletAppletApduCommands.*;

public class RecoveryDataApi extends TonWalletApi {
  private static final String TAG = "RecoveryDataApi";

  public RecoveryDataApi(Context activity,  ApduRunner apduRunner) {
    super(activity, apduRunner);
  }

  public void resetRecoveryData(final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json =  resetRecoveryDataAndGetJson();
          resolveJson(json, callback);
          Log.d(TAG, "resetRecoveryData response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String resetRecoveryDataAndGetJson() throws Exception {
    resetRecoveryData();
    return JSON_HELPER.createResponseJson(DONE_MSG);
  }

  private RAPDU resetRecoveryData() throws Exception {
    return apduRunner.sendTonWalletAppletAPDU(RESET_RECOVERY_DATA_APDU);
  }

  public void getRecoveryDataHash(final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json =  getRecoveryDataHashAndGetJson();
          resolveJson(json, callback);
          Log.d(TAG, "getRecoveryDataHash response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String getRecoveryDataHashAndGetJson() throws Exception {
    String response = BYTE_ARR_HELPER.hex(getRecoveryDataHash().getData());
    return JSON_HELPER.createResponseJson(response);
  }

  private RAPDU getRecoveryDataHash() throws Exception {
    RAPDU rapdu = apduRunner.sendTonWalletAppletAPDU(GET_RECOVERY_DATA_HASH_APDU);
    if (rapdu == null || rapdu.getData() == null || rapdu.getData().length != SHA_HASH_SIZE) throw new Exception(ERROR_MSG_RECOVERY_DATA_HASH_RESPONSE_LEN_INCORRECT);
    return rapdu;
  }


  public void getRecoveryDataLen(final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json =  getRecoveryDataLenAndGetJson();
          resolveJson(json, callback);
          Log.d(TAG, "getRecoveryDataLen response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String getRecoveryDataLenAndGetJson() throws Exception {
    String response = Integer.valueOf(getRecoveryDataLen()).toString();
    return JSON_HELPER.createResponseJson(response);
  }

  private int getRecoveryDataLen() throws Exception {
    RAPDU rapdu = apduRunner.sendTonWalletAppletAPDU(GET_RECOVERY_DATA_LEN_APDU);
    if (rapdu == null || rapdu.getData() == null || rapdu.getData().length != 0x02)
      throw new Exception(ERROR_MSG_RECOVERY_DATA_LENGTH_RESPONSE_LEN_INCORRECT);
    int len = BYTE_ARR_HELPER.makeShort(rapdu.getData(), 0);
    if (len <= 0 || len > RECOVERY_DATA_MAX_SIZE)
      throw new Exception(ERROR_MSG_RECOVERY_DATA_LENGTH_RESPONSE_INCORRECT);
    return len;
  }

  public void isRecoveryDataSet(final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = isRecoveryDataSetAndGetJson();
          resolveJson(json, callback);
          Log.d(TAG, "isRecoveryDataSet response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String  isRecoveryDataSetAndGetJson() throws Exception {
    String response = isRecoveryDataSet().getData()[0] == 0 ? FALSE_MSG : TRUE_MSG;
    return JSON_HELPER.createResponseJson(response);
  }

  private RAPDU isRecoveryDataSet() throws Exception {
    RAPDU rapdu = apduRunner.sendTonWalletAppletAPDU(IS_RECOVERY_DATA_SET_APDU);
    if (rapdu == null || rapdu.getData() == null || rapdu.getData().length != 0x01) throw new Exception(ERROR_IS_RECOVERY_DATA_SET_RESPONSE_LEN_INCORRECT);
    return rapdu;
  }

  public void addRecoveryData(final String recoveryData, final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = addRecoveryDataAndGetJson(recoveryData);
          resolveJson(json, callback);
          Log.d(TAG, "addRecoveryData  response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String addRecoveryDataAndGetJson(String recoveryData) throws Exception {
    if (!STR_HELPER.isHexString(recoveryData)) throw new Exception(ERROR_MSG_RECOVERY_DATA_NOT_HEX);
    if (recoveryData.length() == 0 || recoveryData.length() > 2 * RECOVERY_DATA_MAX_SIZE)
      throw new Exception(ERROR_MSG_RECOVERY_DATA_LEN_INCORRECT);
    addRecoveryData(BYTE_ARR_HELPER.bytes(recoveryData));
    return JSON_HELPER.createResponseJson(DONE_MSG);
  }

  private void addRecoveryData(byte[] recoveryData) throws Exception {
    int numberOfPackets = recoveryData.length / DATA_RECOVERY_PORTION_MAX_SIZE;
    for (int i = 0; i < numberOfPackets; i++) {
      System.out.println("packet#" + i);
      byte[] chunk = BYTE_ARR_HELPER.bSub(recoveryData, i * DATA_RECOVERY_PORTION_MAX_SIZE, DATA_RECOVERY_PORTION_MAX_SIZE);
      byte p1 = i == 0 ? (byte) 0x00 : (byte) 0x01;
      apduRunner.sendTonWalletAppletAPDU(addRecoveryDataPartAPDU(p1, chunk));
    }

    int tailLen = recoveryData.length % DATA_RECOVERY_PORTION_MAX_SIZE;

    if (tailLen > 0) {
      System.out.println("tail#");
      byte[] chunk = BYTE_ARR_HELPER.bSub(recoveryData, numberOfPackets * DATA_RECOVERY_PORTION_MAX_SIZE, tailLen);
      byte p1 = numberOfPackets == 0 ? (byte) 0x00 : (byte) 0x01;
      apduRunner.sendTonWalletAppletAPDU(addRecoveryDataPartAPDU(p1, chunk));
    }

    byte[] hash = digest.digest(recoveryData);
    apduRunner.sendTonWalletAppletAPDU(addRecoveryDataPartAPDU((byte) 0x02, hash));
  }

  public void getRecoveryData(final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = getRecoveryDataAndGetJson();
          resolveJson(json, callback);
          Log.d(TAG, "getRecoveryData response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String getRecoveryDataAndGetJson() throws Exception {
    String response = BYTE_ARR_HELPER.hex(getRecoveryData());
    return JSON_HELPER.createResponseJson(response);
  }

  private byte[] getRecoveryData() throws Exception {
    int len = getRecoveryDataLen();

    byte[] recoveryData = new byte[len];
    int numberOfPackets = len / DATA_RECOVERY_PORTION_MAX_SIZE;
    short startPos = 0;

    Log.d(TAG, "numberOfPackets = " + numberOfPackets);
    for (int i = 0; i < numberOfPackets; i++) {
      Log.d(TAG, "packet " + i);
      byte[] dataChunk = new byte[]{(byte) (startPos >> 8), (byte) (startPos)};
      RAPDU rapdu = apduRunner.sendTonWalletAppletAPDU(getRecoveryDataPartAPDU(dataChunk, (byte) DATA_RECOVERY_PORTION_MAX_SIZE));
      if (rapdu == null || rapdu.getData() == null || rapdu.getData().length != DATA_RECOVERY_PORTION_MAX_SIZE) throw new Exception(ERROR_RECOVERY_DATA_PORTION_INCORRECT_LEN + DATA_RECOVERY_PORTION_MAX_SIZE);
      byte[] res = rapdu.getData();
      BYTE_ARR_HELPER.arrayCopy(res, 0, recoveryData, startPos, DATA_RECOVERY_PORTION_MAX_SIZE);
      startPos += DATA_RECOVERY_PORTION_MAX_SIZE;
    }

    int tailLen = len % DATA_RECOVERY_PORTION_MAX_SIZE;
    if (tailLen > 0) {
      byte[] dataChunk = new byte[]{(byte) (startPos >> 8), (byte) (startPos)};
      RAPDU rapdu = apduRunner.sendTonWalletAppletAPDU(getRecoveryDataPartAPDU(dataChunk, (byte) tailLen));
      if (rapdu == null || rapdu.getData() == null || rapdu.getData().length != tailLen) throw new Exception(ERROR_RECOVERY_DATA_PORTION_INCORRECT_LEN + tailLen);
      byte[] res = rapdu.getData();
      BYTE_ARR_HELPER.arrayCopy(res, 0, recoveryData, startPos, tailLen);
    }
    return recoveryData;
  }

}
