package com.tonnfccard.api;

import android.content.Context;
import android.security.keystore.KeyProperties;
import android.security.keystore.KeyProtection;
import android.util.Log;


import com.tonnfccard.api.callback.NfcCallback;
import com.tonnfccard.smartcard.TonWalletAppletStates;
import com.tonnfccard.utils.ByteArrayHelper;
import com.tonnfccard.api.utils.ExceptionHelper;
import com.tonnfccard.api.utils.JsonHelper;
import com.tonnfccard.api.utils.StringHelper;
import com.tonnfccard.smartcard.cryptoUtils.HmacHelper;
import com.tonnfccard.smartcard.wrappers.ApduRunner;
import com.tonnfccard.smartcard.wrappers.RAPDU;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.KeyStore;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import static com.tonnfccard.api.utils.JsonHelper.STATUS_FIELD;
import static com.tonnfccard.api.utils.ResponsesConstants.*;
import static com.tonnfccard.smartcard.TonWalletAppletConstants.*;
import static com.tonnfccard.smartcard.apdu.TonWalletAppletApduCommands.GET_APP_INFO_APDU;
import static com.tonnfccard.smartcard.apdu.TonWalletAppletApduCommands.GET_SAULT_APDU;
import static com.tonnfccard.smartcard.apdu.TonWalletAppletApduCommands.GET_SERIAL_NUMBER_APDU;

public class TonWalletApi {
  private static final String SERIAl_NUMBERS_FIELD = "serial_number_field";
  private static final String TAG = "TonNfcApi";

  protected static final StringHelper STR_HELPER = StringHelper.getInstance();
  protected static final JsonHelper JSON_HELPER = JsonHelper.getInstance();
  protected static final ByteArrayHelper BYTE_ARR_HELPER = ByteArrayHelper.getInstance();
  protected static final ExceptionHelper EXCEPTION_HELPER = ExceptionHelper.getInstance();
  protected static final HmacHelper HMAC_HELPER = HmacHelper.getInstance();

  static MessageDigest digest;
  static String currentSerialNumber = EMPTY_SERIAL_NUMBER;

  public void setApduRunner(ApduRunner apduRunner) {
    this.apduRunner = apduRunner;
  }

  ApduRunner apduRunner;

  private Context activity;

  static {
    try {
      digest = MessageDigest.getInstance("SHA-256");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  TonWalletApi(Context activity, ApduRunner apduRunner) {
    this.activity = activity;
    this.apduRunner = apduRunner;
  }

  public void selectTonWalletAppletAndGetSerialNumber(final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = selectTonWalletAppletAndGetSerialNumberAndGetJson();
          resolveJson(json, callback);
          Log.d(TAG, "getSerialNumber response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String selectTonWalletAppletAndGetSerialNumberAndGetJson() throws Exception {
    String response = STR_HELPER.makeDigitalString(selectTonWalletAppletAndGetSerialNumber());
    return JSON_HELPER.createResponseJson(response);
  }

  public void selectTonWalletAppletAndGetTonAppletState(final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = selectTonWalletAppletAndGetTonAppletStateAndGetJson();
          resolveJson(json, callback);
          Log.d(TAG, "getTonAppletState response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String selectTonWalletAppletAndGetTonAppletStateAndGetJson() throws Exception {
    TonWalletAppletStates state = selectTonWalletAppletAndGetTonAppletState();
    return JSON_HELPER.createResponseJson(state.getDescription());
  }

  public void getSault(NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = getSaultAndGetJson();
          resolveJson(json, callback);
          Log.d(TAG, "getSault response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String getSaultAndGetJson() throws Exception {
    return JSON_HELPER.createResponseJson(getSaultHex());
  }

  public void disconnectCard(final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = disconnectCardAndGetJson();
          resolveJson(json, callback);
          Log.d(TAG, "disconnectCard response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String disconnectCardAndGetJson()  throws Exception {
    apduRunner.disconnectCard();
    return JSON_HELPER.createResponseJson(DONE_MSG);
  }


  public void getAllSerialNumbers(final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = getAllSerialNumbersAndGetJson();
          resolveJson(json, callback);
          Log.d(TAG, "getAllSerialNumbers response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String getAllSerialNumbersAndGetJson() throws Exception {
    List<String> allSerialNumbers =  getAllSerialNumbers();
    if (allSerialNumbers.isEmpty()) {
      return JSON_HELPER.createResponseJson(HMAC_KEYS_DOES_NOT_FOUND_MSG);
    }
    else {
      JSONObject allAliasesObj = new JSONObject();
      JSONArray jArray = new JSONArray();
      for (final String sn : allSerialNumbers) {
        jArray.put(sn);
      }
      allAliasesObj.put(SERIAl_NUMBERS_FIELD, jArray);
      allAliasesObj.put(STATUS_FIELD, SUCCESS_STATUS);
      JSON_HELPER.createResponseJson(HMAC_KEYS_DOES_NOT_FOUND_MSG);
      return allAliasesObj.toString();
    }
  }

  public void deleteKeyForHmac(String serialNumber, NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json =  deleteKeyForHmacAndGetJson(serialNumber);
          resolveJson(json, callback);
          Log.d(TAG, "deleteKeyForHmac response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String deleteKeyForHmacAndGetJson(String serialNumber)  throws Exception {
    if (serialNumber.length() != SERIAL_NUMBER_SIZE)
      throw new Exception(ERROR_MSG_SERIAL_NUMBER_LEN_INCORRECT);
    if (!STR_HELPER.isNumericString(serialNumber))
      throw new Exception(ERROR_MSG_SERIAL_NUMBER_NOT_NUMERIC);
    deleteKeyForHmac(serialNumber);
    return JSON_HELPER.createResponseJson(DONE_MSG);
  }

  public void createKeyForHmac(String password, String commonSecret, String serialNumber, NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = createKeyForHmacAndGetJson(password, commonSecret, serialNumber);
          resolveJson(json, callback);
          Log.d(TAG, "createKeyForHmac response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }


  public String createKeyForHmacAndGetJson(String password, String commonSecret, String serialNumber) throws Exception {
    if (!STR_HELPER.isHexString(password))
      throw new Exception(ERROR_MSG_PASSWORD_NOT_HEX);
    if (password.length() != 2 * PASSWORD_SIZE)
      throw new Exception(ERROR_MSG_PASSWORD_LEN_INCORRECT);
    if (!STR_HELPER.isHexString(commonSecret))
      throw new Exception(ERROR_MSG_COMMON_SECRET_NOT_HEX);
    if (commonSecret.length() != 2 * COMMON_SECRET_SIZE)
      throw new Exception(ERROR_MSG_COMMON_SECRET_LEN_INCORRECT);
    if (serialNumber.length() != SERIAL_NUMBER_SIZE)
      throw new Exception(ERROR_MSG_SERIAL_NUMBER_LEN_INCORRECT);
    if (!STR_HELPER.isNumericString(serialNumber))
      throw new Exception(ERROR_MSG_SERIAL_NUMBER_NOT_NUMERIC);
    createKeyForHmac(BYTE_ARR_HELPER.bytes(password), BYTE_ARR_HELPER.bytes(commonSecret), serialNumber);
    return JSON_HELPER.createResponseJson(DONE_MSG);
  }

  public void isKeyForHmacExist(String serialNumber, NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json =  isKeyForHmacExistAndGetJson(serialNumber);
          resolveJson(json, callback);
          Log.d(TAG, "isKeyForHmacExist response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String isKeyForHmacExistAndGetJson(String serialNumber) throws Exception {
    if (serialNumber.length() != SERIAL_NUMBER_SIZE)
      throw new Exception(ERROR_MSG_SERIAL_NUMBER_LEN_INCORRECT);
    if (!STR_HELPER.isNumericString(serialNumber))
      throw new Exception(ERROR_MSG_SERIAL_NUMBER_NOT_NUMERIC);
    boolean res = isKeyForHmacExist(serialNumber);
    return JSON_HELPER.createResponseJson(res ? TRUE_MSG : FALSE_MSG);
  }

  public void selectKeyForHmac(String serialNumber, NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = selectKeyForHmacAndGetJson(serialNumber);
          resolveJson(json, callback);
          Log.d(TAG, "selectKeyForHmac response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String selectKeyForHmacAndGetJson(String serialNumber) throws Exception {
    if (serialNumber.length() != SERIAL_NUMBER_SIZE)
      throw new Exception(ERROR_MSG_SERIAL_NUMBER_LEN_INCORRECT);
    if (!STR_HELPER.isNumericString(serialNumber))
      throw new Exception(ERROR_MSG_SERIAL_NUMBER_NOT_NUMERIC);
    selectKeyForHmac(serialNumber);
    return JSON_HELPER.createResponseJson(DONE_MSG);
  }

  public void getCurrentSerialNumber(NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = getCurrentSerialNumber();
          resolveJson(json, callback);
          Log.d(TAG, "getCurrentSerialNumber response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String getCurrentSerialNumber() {
    return JSON_HELPER.createResponseJson(currentSerialNumber);
  }

  public void setCurrentSerialNumber(String currentSerialNumber) {
    this.currentSerialNumber = currentSerialNumber;
    HMAC_HELPER.setCurrentSerialNumber(currentSerialNumber);
  }

  void resolveJson(String json, NfcCallback callback){
    callback.getResolve().resolve(json);
    Log.d(TAG, "json = " + json);
  }

  void createKeyForHmac(byte[] password, byte[] commonSecret, String serialNumber) throws Exception {
    byte[] key = HMAC_HELPER.computeMac(digest.digest(password), commonSecret);
    final SecretKey hmacSha256Key = new SecretKeySpec(key, 0, key.length, KeyProperties.KEY_ALGORITHM_HMAC_SHA256);
    final KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
    String keyAlias = HmacHelper.HMAC_KEY_ALIAS + serialNumber;
    Log.d(TAG, "keyAlias = " + keyAlias);
    keyStore.load(null);
    if (keyStore.containsAlias(keyAlias)) keyStore.deleteEntry(keyAlias);
    keyStore.setEntry(keyAlias,
      new KeyStore.SecretKeyEntry(hmacSha256Key),
      new KeyProtection.Builder(KeyProperties.PURPOSE_SIGN)
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .build());
    setCurrentSerialNumber(serialNumber);
  }

  void reselectKeyForHmac() throws Exception {
    String serialNumber = STR_HELPER.makeDigitalString(selectTonWalletAppletAndGetSerialNumber());
    selectKeyForHmac(serialNumber);
  }

  private void deleteKeyForHmac(String serialNumber) throws Exception {
    final KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
    keyStore.load(null);
    String keyAlias = HmacHelper.HMAC_KEY_ALIAS + serialNumber;
    Log.d(TAG, "delete keyAlias  = " + keyAlias);
    if (keyStore.containsAlias(keyAlias))  {
      keyStore.deleteEntry(keyAlias);
      if (currentSerialNumber.equals(serialNumber)) {
        setCurrentSerialNumber(EMPTY_SERIAL_NUMBER);
      }
    }
    else throw new Exception(ERROR_MSG_KEY_FOR_HMAC_DOES_NOT_EXIST_IN_ANDROID_KEYCHAIN);
  }

  private void selectKeyForHmac(String serialNumber) throws Exception {
    final KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
    keyStore.load(null);
    String keyAlias = HmacHelper.HMAC_KEY_ALIAS + serialNumber;
    if (keyStore.containsAlias(keyAlias))  {
      setCurrentSerialNumber(serialNumber);
    }
    else throw new Exception(ERROR_MSG_KEY_FOR_HMAC_DOES_NOT_EXIST_IN_ANDROID_KEYCHAIN);
  }


  private boolean isKeyForHmacExist(String serialNumber) throws Exception {
    final KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
    keyStore.load(null);
    String keyAlias = HmacHelper.HMAC_KEY_ALIAS + serialNumber;
    return keyStore.containsAlias(keyAlias);
  }

  private List<String> getAllSerialNumbers() throws Exception {
    final KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
    keyStore.load(null);
    Enumeration<String> aliases = keyStore.aliases();
    List<String> serialNumbers = new ArrayList<>();
    while (aliases.hasMoreElements()) {
      String alias = aliases.nextElement();
      if (alias.startsWith(HmacHelper.HMAC_KEY_ALIAS))
        serialNumbers.add(alias.substring(HmacHelper.HMAC_KEY_ALIAS.length()));
    }
    return serialNumbers;
  }

  private String getSaultHex() throws Exception {
    return BYTE_ARR_HELPER.hex(selectTonWalletAppletAndGetSaultBytes());
  }

  TonWalletAppletStates selectTonWalletAppletAndGetTonAppletState() throws Exception {
    byte[] state = apduRunner.sendTonWalletAppletAPDU(GET_APP_INFO_APDU).getData();
    return TonWalletAppletStates.findByStateValue(state[0]);
  }

  byte[] selectTonWalletAppletAndGetSerialNumber() throws Exception {
    byte[] serialNumber = apduRunner.sendTonWalletAppletAPDU(GET_SERIAL_NUMBER_APDU).getData();
    if (serialNumber.length != SERIAL_NUMBER_SIZE) throw new Exception(ERROR_MSG_GET_SERIAL_NUMBER_RESPONSE_LEN_INCORRECT);
    return serialNumber;
  }

  byte[] getSaultBytes() throws Exception {
    byte[] sault = getSault().getData();
    if (sault.length != SAULT_LENGTH) throw new Exception(ERROR_MSG_SAULT_RESPONSE_LEN_INCORRECT);
    return sault;
  }

  private byte[] selectTonWalletAppletAndGetSaultBytes() throws Exception {
    byte[] sault = selectTonWalletAppletAndGetSault().getData();
    if (sault.length != SAULT_LENGTH) throw new Exception(ERROR_MSG_SAULT_RESPONSE_LEN_INCORRECT);
    return sault;
  }

  private RAPDU getSault() throws Exception {
    return apduRunner.sendAPDU(GET_SAULT_APDU);
  }

  private RAPDU selectTonWalletAppletAndGetSault() throws Exception {
    return apduRunner.sendTonWalletAppletAPDU(GET_SAULT_APDU);
  }

}
