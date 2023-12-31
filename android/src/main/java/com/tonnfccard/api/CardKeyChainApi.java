package com.tonnfccard.api;

import android.content.Context;
import android.util.Log;
import com.tonnfccard.api.callback.NfcCallback;
import com.tonnfccard.smartcard.TonWalletAppletConstants;
import com.tonnfccard.smartcard.TonWalletAppletStates;
import com.tonnfccard.smartcard.wrappers.ApduRunner;
import com.tonnfccard.smartcard.wrappers.RAPDU;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static com.tonnfccard.api.utils.JsonHelper.STATUS_FIELD;
import static com.tonnfccard.api.utils.ResponsesConstants.*;
import static com.tonnfccard.smartcard.TonWalletAppletConstants.*;
import static com.tonnfccard.smartcard.apdu.TonWalletAppletApduCommands.*;

public class CardKeyChainApi extends TonWalletApi {
  private static final String KEY_INDEX_FIELD = "index";
  private static final String KEY_LENGTH_FIELD = "length";
  private static final String KEY_HMAC_FIELD = "hmac";
  private static final String NUMBER_OF_KEYS_FIELD = "numberOfKeys";
  private static final String OCCUPIED_SIZE_FIELD = "occupiedSize";
  private static final String FREE_SIZE_FIELD = "freeSize";
  private static final String KEYS_DATA_FIELD = "keysData";
  private static final String TAG = "CardKeyChainNfcApi";

  private List<String> keyMacs = new ArrayList<>();

  public CardKeyChainApi(Context activity, ApduRunner apduRunner) {
    super(activity, apduRunner);
  }

  public void resetKeyChain(final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = resetKeyChainAndGetJson();
          resolveJson(json, callback);
          Log.d(TAG, "resetKeyChain response : " + json);
         // keyMacs.clear();
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String resetKeyChainAndGetJson() throws Exception {
    resetKeyChain();
    return JSON_HELPER.createResponseJson(DONE_MSG);
  }

  public void getKeyChainInfo(final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = getKeyChainInfoAndGetJson();
          resolveJson(json, callback);
          Log.d(TAG, "getKeyChainInfo response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String getKeyChainInfoAndGetJson() throws Exception {
    int numOfKeys = getNumberOfKeys();
    int occupiedStorageSize = getOccupiedStorageSize();
    int freeStorageSize = getFreeStorageSize();
    JSONObject jsonResponse = new JSONObject();
    jsonResponse.put(NUMBER_OF_KEYS_FIELD, numOfKeys);
    jsonResponse.put(OCCUPIED_SIZE_FIELD, occupiedStorageSize);
    jsonResponse.put(FREE_SIZE_FIELD, freeStorageSize);
    jsonResponse.put(STATUS_FIELD, SUCCESS_STATUS);
    return jsonResponse.toString();
  }

  public void getNumberOfKeys(final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = getNumberOfKeysAndGetJson();
          resolveJson(json, callback);
          Log.d(TAG, "getNumberOfKeys response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String getNumberOfKeysAndGetJson() throws Exception {
    int numOfKeys = getNumberOfKeys();
    return JSON_HELPER.createResponseJson(Integer.valueOf(numOfKeys).toString());
  }

  public void checkKeyHmacConsistency(final String keyHmac, final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = checkKeyHmacConsistencyAndGetJson(keyHmac);
          resolveJson(json, callback);
          Log.d(TAG, "checkKeyHmacConsistency response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String checkKeyHmacConsistencyAndGetJson(String keyHmac) throws Exception {
    if (!STR_HELPER.isHexString(keyHmac))
      throw new Exception(ERROR_MSG_KEY_HMAC_NOT_HEX);
    if (keyHmac.length() != 2 * HMAC_SHA_SIG_SIZE)
      throw new Exception(ERROR_MSG_KEY_HMAC_LEN_INCORRECT);
    checkKeyHmacConsistency(BYTE_ARR_HELPER.bytes(keyHmac));
    return JSON_HELPER.createResponseJson(DONE_MSG);
  }

  public void checkAvailableVolForNewKey(final Short keySize, final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = checkAvailableVolForNewKeyAndGetJson(keySize);
          resolveJson(json, callback);
          Log.d(TAG, "checkAvailableVolForNewKey response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String checkAvailableVolForNewKeyAndGetJson(final Short keySize) throws Exception {
    if (keySize <= 0 || keySize > MAX_KEY_SIZE_IN_KEYCHAIN)
      throw new Exception(ERROR_MSG_KEY_SIZE_INCORRECT);
    checkAvailableVolForNewKey(keySize);
    return JSON_HELPER.createResponseJson(DONE_MSG);
  }

  public void getIndexAndLenOfKeyInKeyChain(final String keyHmac, final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = getIndexAndLenOfKeyInKeyChainAndGetJson(keyHmac) ;
          resolveJson(json, callback);
          Log.d(TAG, "getIndexAndLenOfKeyInKeyChain response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String getIndexAndLenOfKeyInKeyChainAndGetJson(String keyHmac)  throws Exception {
    if (!STR_HELPER.isHexString(keyHmac))
      throw new Exception(ERROR_MSG_KEY_HMAC_NOT_HEX);
    if (keyHmac.length() != 2 * HMAC_SHA_SIG_SIZE)
      throw new Exception(ERROR_MSG_KEY_HMAC_LEN_INCORRECT);
    String response = getIndexAndLenOfKeyInKeyChain(BYTE_ARR_HELPER.bytes(keyHmac)).toString();
    return JSON_HELPER.createResponseJson(response);
  }

  public void getDeleteKeyRecordNumOfPackets(final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = getDeleteKeyRecordNumOfPacketsAndGetJson();
          resolveJson(json, callback);
          Log.d(TAG, "getDeleteKeyRecordNumOfPackets response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String getDeleteKeyRecordNumOfPacketsAndGetJson()  throws Exception {
    String numOfPackets = Integer.valueOf(getDeleteKeyRecordNumOfPackets()).toString();
    return JSON_HELPER.createResponseJson(numOfPackets);
  }

  public void getDeleteKeyChunkNumOfPackets(final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = getDeleteKeyChunkNumOfPacketsAndGetJson();
          resolveJson(json, callback);
          Log.d(TAG, "getDeleteKeyChunkNumOfPackets response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String getDeleteKeyChunkNumOfPacketsAndGetJson() throws Exception {
    String numOfPackets = Integer.valueOf(getDeleteKeyChunkNumOfPackets()).toString();
    return JSON_HELPER.createResponseJson(numOfPackets);
  }

  public void deleteKeyFromKeyChain(final String keyHmac, final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = deleteKeyFromKeyChainAndGetJson(keyHmac);
          resolveJson(json, callback);
          Log.d(TAG, "deleteKeyFromKeyChain response (number of remained keys) : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String deleteKeyFromKeyChainAndGetJson(String keyHmac) throws Exception {
    if (!STR_HELPER.isHexString(keyHmac))
      throw new Exception(ERROR_MSG_KEY_HMAC_NOT_HEX);
    if (keyHmac.length() != 2 * HMAC_SHA_SIG_SIZE)
      throw new Exception(ERROR_MSG_KEY_HMAC_LEN_INCORRECT);
    int numOfKeys = deleteKeyFromKeyChain(BYTE_ARR_HELPER.bytes(keyHmac));
    return JSON_HELPER.createResponseJson(Integer.valueOf(numOfKeys).toString());
  }

  public void finishDeleteKeyFromKeyChainAfterInterruption(final String keyHmac, final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = finishDeleteKeyFromKeyChainAfterInterruptionAndGetJson(keyHmac);
          resolveJson(json, callback);
          Log.d(TAG, "finishDeleteKeyFromKeyChainAfterInterruption response (number of remained keys) : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String finishDeleteKeyFromKeyChainAfterInterruptionAndGetJson(String keyHmac) throws Exception {
    if (!STR_HELPER.isHexString(keyHmac))
      throw new Exception(ERROR_MSG_KEY_HMAC_NOT_HEX);
    if (keyHmac.length() != 2 * HMAC_SHA_SIG_SIZE)
      throw new Exception(ERROR_MSG_KEY_HMAC_LEN_INCORRECT);
    int numOfKeys = finishDeleteKeyFromKeyChainAfterInterruption();
    return JSON_HELPER.createResponseJson(Integer.valueOf(numOfKeys).toString());
  }

  public void getOccupiedStorageSize(final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = getOccupiedStorageSizeAndGetJson();
          resolveJson(json, callback);
          Log.d(TAG, "getOccupiedStorageSize response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String getOccupiedStorageSizeAndGetJson() throws Exception {
    String size = Integer.valueOf(getOccupiedStorageSize()).toString();
    return JSON_HELPER.createResponseJson(size);
  }

  public void getFreeStorageSize(final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json = getFreeStorageSizeAndGetJson();
          resolveJson(json, callback);
          Log.d(TAG, "getFreeStorageSize response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String getFreeStorageSizeAndGetJson() throws Exception {
    String size = Integer.valueOf(getFreeStorageSize()).toString();
    return JSON_HELPER.createResponseJson(size);
  }

  public void getKeyFromKeyChain(final String keyHmac, final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json =  getKeyFromKeyChainAndGetJson(keyHmac);
          resolveJson(json, callback);
          Log.d(TAG, "getKey response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String getKeyFromKeyChainAndGetJson(String keyHmac) throws Exception {
    if (!STR_HELPER.isHexString(keyHmac))
      throw new Exception(ERROR_MSG_KEY_HMAC_NOT_HEX);
    if (keyHmac.length() != 2 * HMAC_SHA_SIG_SIZE)
      throw new Exception(ERROR_MSG_KEY_HMAC_LEN_INCORRECT);
    String key = BYTE_ARR_HELPER.hex(getKeyFromKeyChain(BYTE_ARR_HELPER.bytes(keyHmac)));
    return JSON_HELPER.createResponseJson(key);
  }

  public void addKeyIntoKeyChain(final String newKey, final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json =  addKeyIntoKeyChainAndGetJson(newKey);
          resolveJson(json, callback);
          Log.d(TAG, "addKey response (hmac of new key) : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String addKeyIntoKeyChainAndGetJson(String newKey) throws Exception {
    if (!STR_HELPER.isHexString(newKey))
      throw new Exception(ERROR_MSG_KEY_NOT_HEX);
    if (newKey.length() > 2 * MAX_KEY_SIZE_IN_KEYCHAIN)
      throw new Exception(ERROR_MSG_KEY_LEN_INCORRECT);
    String keyHmac = addKeyIntoKeyChain(BYTE_ARR_HELPER.bytes(newKey));
    return JSON_HELPER.createResponseJson(keyHmac);
  }

  public void changeKeyInKeyChain(final String newKey, final String oldKeyHMac, final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json =  changeKeyInKeyChainAndGetJson(newKey, oldKeyHMac);
          resolveJson(json, callback);
          Log.d(TAG, "changeKey response (hmac of new key) : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String changeKeyInKeyChainAndGetJson(String newKey, String oldKeyHMac) throws Exception {
    if (!STR_HELPER.isHexString(newKey))
      throw new Exception(ERROR_MSG_KEY_NOT_HEX);
    if (newKey.length() > 2 * MAX_KEY_SIZE_IN_KEYCHAIN)
      throw new Exception(ERROR_MSG_KEY_LEN_INCORRECT);
    if (!STR_HELPER.isHexString(oldKeyHMac))
      throw new Exception(ERROR_MSG_KEY_HMAC_NOT_HEX);
    if (oldKeyHMac.length() != 2 * HMAC_SHA_SIG_SIZE)
      throw new Exception(ERROR_MSG_KEY_HMAC_LEN_INCORRECT);
    String newKeyHmac = changeKeyInKeyChain(BYTE_ARR_HELPER.bytes(newKey), BYTE_ARR_HELPER.bytes(oldKeyHMac));
    return JSON_HELPER.createResponseJson(newKeyHmac);
  }

  public void getKeyChainDataAboutAllKeys(final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json =  getKeyChainDataAboutAllKeysAndGetJson();
          resolveJson(json, callback);
          Log.d(TAG, " getKeyChainDataAboutAllKeys response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String getKeyChainDataAboutAllKeysAndGetJson() throws Exception {
    Map<String, Integer> map = getAllHmacsOfKeysFromCard();
    JSONObject allKeysObj = new JSONObject();
    JSONArray jArray = new JSONArray();
    for (final String hmac : map.keySet()) {
      JSONObject jObject = new JSONObject();
      jObject.put(KEY_HMAC_FIELD, hmac);
      jObject.put(KEY_LENGTH_FIELD, map.get(hmac).toString());
      jArray.put(jObject);
    }
    allKeysObj.put(KEYS_DATA_FIELD, jArray);
    allKeysObj.put(STATUS_FIELD, SUCCESS_STATUS);
    return allKeysObj.toString();
  }

  public void getHmac(final String index, final NfcCallback callback) {
    new Thread(new Runnable() {
      public void run() {
        try {
          String json =  getHmacAndGetJson(index);
          resolveJson(json, callback);
          Log.d(TAG, "getHmac response : " + json);
        } catch (Exception e) {
          EXCEPTION_HELPER.handleException(e, callback, TAG);
        }
      }
    }).start();
  }

  public String getHmacAndGetJson(String index) throws Exception {
    if (!STR_HELPER.isNumericString(index))
      throw new Exception(ERROR_MSG_KEY_INDEX_STRING_NOT_NUMERIC);
    short ind = Short.parseShort(index);
    if (ind < 0 || ind > MAX_NUMBER_OF_KEYS_IN_KEYCHAIN - 1)
      throw new Exception(ERROR_MSG_KEY_INDEX_VALUE_INCORRECT);
    byte[] indBytes = new byte[2];
    BYTE_ARR_HELPER.setShort(indBytes, (short) 0, ind);
    String response = BYTE_ARR_HELPER.hex(getHmac(indBytes));
    return JSON_HELPER.createResponseJson(response);
  }

  private RAPDU resetKeyChain() throws Exception {
    reselectKeyForHmac();
    byte[] sault = getSaultBytes();
    return apduRunner.sendAPDU(getResetKeyChainAPDU(sault));
  }

  private int getNumberOfKeys() throws Exception {
    reselectKeyForHmac();
    byte[] sault = getSaultBytes();
    RAPDU rapdu = apduRunner.sendAPDU(getNumberOfKeysAPDU(sault));
    if (rapdu == null || rapdu.getData() == null || rapdu.getData().length != GET_NUMBER_OF_KEYS_LE)
      throw new Exception(ERROR_MSG_GET_NUMBER_OF_KEYS_RESPONSE_LEN_INCORRECT);
    byte[] response = rapdu.getData();
    int numOfKeys = BYTE_ARR_HELPER.makeShort(response, 0);
    if (numOfKeys < 0 || numOfKeys > MAX_NUMBER_OF_KEYS_IN_KEYCHAIN)
      throw new Exception(ERROR_MSG_NUMBER_OF_KEYS_RESPONSE_INCORRECT);
    return numOfKeys;
  }

  private void checkKeyHmacConsistency(byte[] keyHmac) throws Exception {
    reselectKeyForHmac();
    byte[] sault = getSaultBytes();
    apduRunner.sendAPDU(getCheckKeyHmacConsistencyAPDU(keyHmac, sault));
  }

  private void checkAvailableVolForNewKey(short keySize) throws Exception {
    TonWalletAppletStates appletState = selectTonWalletAppletAndGetTonAppletState();
    if (appletState != TonWalletAppletStates.PERSONALIZED)
      throw new Exception(ERROR_MSG_APPLET_IS_NOT_PERSONALIZED + appletState.getDescription() + ".");
    reselectKeyForHmac();
    byte[] sault = getSaultBytes();
    apduRunner.sendAPDU(getCheckAvailableVolForNewKeyAPDU(keySize, sault));
  }

  private void initiateChangeOfKey(byte[] index) throws Exception {
    TonWalletAppletStates appletState = selectTonWalletAppletAndGetTonAppletState();
    if (appletState != TonWalletAppletStates.PERSONALIZED)
      throw new Exception(ERROR_MSG_APPLET_IS_NOT_PERSONALIZED + appletState.getDescription() + ".");
    reselectKeyForHmac();
    byte[] sault = getSaultBytes();
    apduRunner.sendAPDU(getInitiateChangeOfKeyAPDU(index, sault));
  }

  private JSONObject getIndexAndLenOfKeyInKeyChain(byte[] keyHmac) throws Exception {
    reselectKeyForHmac();
    byte[] sault = getSaultBytes();
    RAPDU rapdu =  apduRunner.sendAPDU(getGetIndexAndLenOfKeyInKeyChainAPDU(keyHmac, sault));
    if (rapdu == null || rapdu.getData() == null || rapdu.getData().length != GET_KEY_INDEX_IN_STORAGE_AND_LEN_LE)
      throw new Exception(ERROR_MSG_GET_KEY_INDEX_IN_STORAGE_AND_LEN_RESPONSE_LEN_INCORRECT);
    byte[] response = rapdu.getData();
    int index = BYTE_ARR_HELPER.makeShort(response, 0);
    if (index < 0 || index > MAX_NUMBER_OF_KEYS_IN_KEYCHAIN - 1)
      throw new Exception(ERROR_MSG_KEY_INDEX_INCORRECT);
    int len = BYTE_ARR_HELPER.makeShort(response, 2);
    if (len <= 0 || len > MAX_KEY_SIZE_IN_KEYCHAIN)
      throw new Exception(ERROR_MSG_KEY_LENGTH_INCORRECT);
    JSONObject jsonResponse = new JSONObject();
    jsonResponse.put(KEY_INDEX_FIELD, index);
    jsonResponse.put(KEY_LENGTH_FIELD, len);
    return jsonResponse;
  }

  private int deleteKeyFromKeyChain(byte[] macBytes) throws Exception {
    JSONObject jsonObject = getIndexAndLenOfKeyInKeyChain(macBytes);
    byte[] index = BYTE_ARR_HELPER.bytes(jsonObject.getInt(KEY_INDEX_FIELD));

    initiateDeleteOfKey(index);

    int deleteKeyChunkIsDone = 0;
    while (deleteKeyChunkIsDone == 0) {
      deleteKeyChunkIsDone = deleteKeyChunk();
    }
    int deleteKeyRecordIsDone = 0;
    while (deleteKeyRecordIsDone == 0) {
      deleteKeyRecordIsDone = deleteKeyRecord();
    }
    return getNumberOfKeys();
  }

  private int finishDeleteKeyFromKeyChainAfterInterruption() throws Exception {
    TonWalletAppletStates appletState = selectTonWalletAppletAndGetTonAppletState();
    if (appletState != TonWalletAppletStates.DELETE_KEY_FROM_KEYCHAIN_MODE)
      throw new Exception(ERROR_MSG_APPLET_DOES_NOT_WAIT_TO_DELETE_KEY + appletState.getDescription() + ".");

    reselectKeyForHmac();

    int deleteKeyChunkIsDone = 0;
    while (deleteKeyChunkIsDone == 0) {
      deleteKeyChunkIsDone = deleteKeyChunk();
    }
    int deleteKeyRecordIsDone = 0;
    while (deleteKeyRecordIsDone == 0) {
      deleteKeyRecordIsDone = deleteKeyRecord();
    }
    return getNumberOfKeys();
  }

  private void initiateDeleteOfKey(byte[] index) throws Exception {
    TonWalletAppletStates appletState = selectTonWalletAppletAndGetTonAppletState();
    if (appletState !=  TonWalletAppletStates.PERSONALIZED)
      throw new Exception(ERROR_MSG_APPLET_IS_NOT_PERSONALIZED + appletState.getDescription() + ".");
    reselectKeyForHmac();
    byte[] sault = getSaultBytes();
    RAPDU rapdu = apduRunner.sendAPDU(getInitiateDeleteOfKeyAPDU(index, sault));
    if (rapdu == null || rapdu.getData() == null || rapdu.getData().length != INITIATE_DELETE_KEY_LE)
      throw new Exception(ERROR_MSG_INITIATE_DELETE_KEY_RESPONSE_LEN_INCORRECT);
    byte[] response = rapdu.getData();
    int len = BYTE_ARR_HELPER.makeShort(response, 2);
    if (len <= 0 || len > MAX_KEY_SIZE_IN_KEYCHAIN)
      throw new Exception(ERROR_MSG_KEY_LENGTH_INCORRECT);
  }

  private int deleteKeyChunk() throws Exception {
    byte[] sault = getSaultBytes();
    RAPDU rapdu = apduRunner.sendAPDU(getDeleteKeyChunkAPDU(sault));
    if (rapdu == null || rapdu.getData() == null || rapdu.getData().length != DELETE_KEY_CHUNK_LE)
      throw new Exception(ERROR_MSG_DELETE_KEY_CHUNK_RESPONSE_LEN_INCORRECT);
    byte[] response = rapdu.getData();
    int status = response[0];
    if (status < 0 || status > 2)
      throw new Exception(ERROR_MSG_DELETE_KEY_CHUNK_RESPONSE_INCORRECT);
    return status;
  }

  private int deleteKeyRecord() throws Exception {
    byte[] sault = getSaultBytes();
    RAPDU rapdu = apduRunner.sendAPDU(getDeleteKeyRecordAPDU(sault));
    if (rapdu == null || rapdu.getData() == null || rapdu.getData().length != DELETE_KEY_RECORD_LE)
      throw new Exception(ERROR_MSG_DELETE_KEY_RECORD_RESPONSE_LEN_INCORRECT);
    byte[] response = rapdu.getData();
    int status = response[0];
    if (status < 0 || status > 2)
      throw new Exception(ERROR_MSG_DELETE_KEY_RECORD_RESPONSE_INCORRECT);
    return status;
  }

  private int getDeleteKeyChunkNumOfPackets() throws Exception {
    reselectKeyForHmac();
    byte[] sault = getSaultBytes();
    RAPDU rapdu = apduRunner.sendAPDU(getDeleteKeyChunkNumOfPacketsAPDU(sault));
    if (rapdu == null || rapdu.getData() == null || rapdu.getData().length  != GET_DELETE_KEY_CHUNK_NUM_OF_PACKETS_LE)
      throw new Exception(ERROR_MSG_GET_DELETE_KEY_CHUNK_NUM_OF_PACKETS_RESPONSE_LEN_INCORRECT);
    byte[] response = rapdu.getData();
    int num = BYTE_ARR_HELPER.makeShort(response, 0);
    if (num < 0)
      throw new Exception(ERROR_MSG_GET_DELETE_KEY_CHUNK_NUM_OF_PACKETS_RESPONSE_INCORRECT);
    return num;
  }

  private int getDeleteKeyRecordNumOfPackets() throws Exception {
    reselectKeyForHmac();
    byte[] sault = getSaultBytes();
    RAPDU rapdu = apduRunner.sendAPDU(getDeleteKeyRecordNumOfPacketsAPDU(sault));
    if (rapdu == null || rapdu.getData() == null || rapdu.getData().length != GET_DELETE_KEY_RECORD_NUM_OF_PACKETS_LE)
      throw new Exception(ERROR_MSG_GET_DELETE_KEY_RECORD_NUM_OF_PACKETS_RESPONSE_LEN_INCORRECT);
    byte[] response = rapdu.getData();
    int num = BYTE_ARR_HELPER.makeShort(response, 0);
    if (num < 0)
      throw new Exception(ERROR_MSG_GET_DELETE_KEY_RECORD_NUM_OF_PACKETS_RESPONSE_INCORRECT);
    return num;
  }

  private int getOccupiedStorageSize() throws Exception {
    reselectKeyForHmac();
    byte[] sault = getSaultBytes();
    RAPDU rapdu = apduRunner.sendAPDU(getGetOccupiedSizeAPDU(sault));
    if (rapdu == null || rapdu.getData() == null || rapdu.getData().length != GET_OCCUPIED_SIZE_LE)
      throw new Exception(ERROR_MSG_GET_OCCUPIED_SIZE_RESPONSE_LEN_INCORRECT);
    byte[] response = rapdu.getData();
    int size = BYTE_ARR_HELPER.makeShort(response, 0);
    if (size < 0)
      throw new Exception(ERROR_MSG_OCCUPIED_SIZE_RESPONSE_INCORRECT);
    return size;
  }

  private int getFreeStorageSize() throws Exception {
    reselectKeyForHmac();
    byte[] sault = getSaultBytes();
    RAPDU rapdu = apduRunner.sendAPDU(getGetFreeSizeAPDU(sault));
    if (rapdu == null || rapdu.getData() == null || rapdu.getData().length != GET_FREE_SIZE_LE)
      throw new Exception(ERROR_MSG_GET_FREE_SIZE_RESPONSE_LEN_INCORRECT);
    byte[] response = rapdu.getData();
    int size = BYTE_ARR_HELPER.makeShort(response, 0);
    if (size < 0)
      throw new Exception(ERROR_MSG_FREE_SIZE_RESPONSE_INCORRECT);
    return size;
  }

  private byte[] getHmac(byte[] ind) throws Exception {
    reselectKeyForHmac();
    byte[] sault = getSaultBytes();
    RAPDU rapdu =  apduRunner.sendAPDU(getGetHmacAPDU(ind, sault));
    if (rapdu == null || rapdu.getData() == null || rapdu.getData().length != HMAC_SHA_SIG_SIZE)
      throw new Exception(ERROR_MSG_GET_HMAC_RESPONSE_LEN_INCORRECT);
    return rapdu.getData();
  }

  private byte[] getKeyFromKeyChain(byte[] macBytes) throws Exception {
    JSONObject jsonObject = getIndexAndLenOfKeyInKeyChain(macBytes);
    int keyLen = jsonObject.getInt(KEY_LENGTH_FIELD);
    byte[] ind = BYTE_ARR_HELPER.bytes(jsonObject.getInt(KEY_INDEX_FIELD));
    return getKeyFromKeyChain(keyLen, ind);
  }

  private byte[] getKeyFromKeyChain(int keyLen, byte[] ind) throws Exception {
    byte[] key = new byte[keyLen];
    byte[] sault;
    int numberOfPackets = keyLen / DATA_PORTION_MAX_SIZE;
    short startPos = 0;
    for (int i = 0; i < numberOfPackets; i++) {
      sault = getSaultBytes();
      RAPDU rapdu = apduRunner.sendAPDU(getGetKeyChunkAPDU(ind, startPos, sault, (byte) DATA_PORTION_MAX_SIZE));
      if (rapdu == null || rapdu.getData() == null || rapdu.getData().length != DATA_RECOVERY_PORTION_MAX_SIZE) throw new Exception(ERROR_KEY_DATA_PORTION_INCORRECT_LEN + DATA_RECOVERY_PORTION_MAX_SIZE);
      byte[] res = rapdu.getData();
      BYTE_ARR_HELPER.arrayCopy(res, 0, key, startPos, DATA_PORTION_MAX_SIZE);
      startPos += DATA_PORTION_MAX_SIZE;
    }
    int tailLen = keyLen % DATA_PORTION_MAX_SIZE;
    if (tailLen > 0) {
      sault = getSaultBytes();
      RAPDU rapdu = apduRunner.sendAPDU(getGetKeyChunkAPDU(ind, startPos, sault, (byte) tailLen));
      if (rapdu == null || rapdu.getData() == null || rapdu.getData().length != tailLen) throw new Exception(ERROR_KEY_DATA_PORTION_INCORRECT_LEN + DATA_RECOVERY_PORTION_MAX_SIZE);
      byte[] res = rapdu.getData();
      BYTE_ARR_HELPER.arrayCopy(res, 0, key, startPos, tailLen);
    }
    return key;
  }

  private void addKey(byte[] keyBytes) throws Exception {
    int oldNumOfKeys = getNumberOfKeys();
    int newNumberOfKeys = sendKey(keyBytes, INS_ADD_KEY_CHUNK);
    if (newNumberOfKeys != (oldNumOfKeys + 1))
      throw new Exception(ERROR_MSG_AFTER_NUM_OF_KEYS_INCORRECT_AFTER_ADD);
  }

  private void changeKey(byte[] keyBytes) throws Exception {
    int oldNumOfKeys = getNumberOfKeys();
    int newNumberOfKeys = sendKey(keyBytes, INS_CHANGE_KEY_CHUNK);
    if (oldNumOfKeys != newNumberOfKeys)
      throw new Exception(ERROR_MSG_AFTER_NUM_OF_KEYS_INCORRECT_AFTER_CHANGE);
  }

  private int sendKey(byte[] keyBytes, byte ins) throws Exception {
    int numberOfPackets = keyBytes.length / DATA_PORTION_MAX_SIZE;
    byte[] keyChunk, sault;
    for (int i = 0; i < numberOfPackets; i++) {
      sault = getSaultBytes();
      keyChunk = BYTE_ARR_HELPER.bSub(keyBytes, i * DATA_PORTION_MAX_SIZE, DATA_PORTION_MAX_SIZE);
      apduRunner.sendAPDU(getSendKeyChunkAPDU(ins, i == 0 ? (byte) 0x00 : (byte) 0x01, keyChunk, sault));
    }

    int tailLen = keyBytes.length % DATA_PORTION_MAX_SIZE;
    if (tailLen > 0) {
      sault = getSaultBytes();
      keyChunk = BYTE_ARR_HELPER.bSub(keyBytes, numberOfPackets * DATA_PORTION_MAX_SIZE, tailLen);
      apduRunner.sendAPDU(getSendKeyChunkAPDU(ins, numberOfPackets == 0 ? (byte) 0x00 : (byte) 0x01, keyChunk, sault));
    }

    byte[] mac = HMAC_HELPER.computeMac(keyBytes);
    sault = getSaultBytes();
    RAPDU rapdu = apduRunner.sendAPDU(getSendKeyChunkAPDU(ins, (byte) 0x02, mac, sault));

    if (rapdu == null || rapdu.getData() == null || rapdu.getData().length != SEND_CHUNK_LE)
      throw new Exception(ERROR_MSG_SEND_CHUNK_RESPONSE_LEN_INCORRECT);
    byte[] response = rapdu.getData();
    int numOfKeys = BYTE_ARR_HELPER.makeShort(response, 0);
    if (numOfKeys <= 0 || numOfKeys > MAX_NUMBER_OF_KEYS_IN_KEYCHAIN)
      throw new Exception(ERROR_MSG_NUMBER_OF_KEYS_RESPONSE_INCORRECT);
    return numOfKeys;
  }

  private String addKeyIntoKeyChain(byte[] keyBytes) throws Exception {
    checkAvailableVolForNewKey((short) keyBytes.length);
    addKey(keyBytes);
    return BYTE_ARR_HELPER.hex(HMAC_HELPER.computeMac(keyBytes));
  }

  private String changeKeyInKeyChain(byte[] newKeyBytes, byte[] macBytesOfOldKey) throws Exception {
    JSONObject jsonObject = getIndexAndLenOfKeyInKeyChain(macBytesOfOldKey);
    int keyLen = jsonObject.getInt(KEY_LENGTH_FIELD);
    if (keyLen != newKeyBytes.length)
      throw new IllegalArgumentException(ERROR_MSG_NEW_KEY_LEN_INCORRECT + keyLen + ".");
    initiateChangeOfKey(BYTE_ARR_HELPER.bytes(jsonObject.getInt(KEY_INDEX_FIELD)));
    changeKey(newKeyBytes);
    return BYTE_ARR_HELPER.hex(HMAC_HELPER.computeMac(newKeyBytes));
  }

  private Map<String, Integer> getAllHmacsOfKeysFromCard() throws Exception {
    Map<String, Integer> hmacs = new LinkedHashMap<>();
    keyMacs.clear();
    int numOfKeys = getNumberOfKeys();
    byte[] ind = new byte[2];
    for (short i = 0; i < numOfKeys; i++) {
      BYTE_ARR_HELPER.setShort(ind, (short) 0, i);
      byte[] data = getHmac(ind);
      byte[] mac = BYTE_ARR_HELPER.bSub(data, 0, HMAC_SHA_SIG_SIZE);
      int len = BYTE_ARR_HELPER.makeShort(data, HMAC_SHA_SIG_SIZE);
      hmacs.put(BYTE_ARR_HELPER.hex(mac), len);
      keyMacs.add(BYTE_ARR_HELPER.hex(mac));
    }
    return hmacs;
  }

}
