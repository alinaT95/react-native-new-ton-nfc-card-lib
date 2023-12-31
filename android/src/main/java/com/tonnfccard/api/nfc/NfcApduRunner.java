package com.tonnfccard.api.nfc;

import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;

import com.tonnfccard.smartcard.wrappers.ApduRunner;
import com.tonnfccard.smartcard.wrappers.CAPDU;
import com.tonnfccard.smartcard.wrappers.RAPDU;

import java.io.IOException;

import static android.nfc.NfcAdapter.EXTRA_TAG;
import static com.tonnfccard.api.utils.ResponsesConstants.ERROR_BAD_RESPONSE;
import static com.tonnfccard.api.utils.ResponsesConstants.ERROR_MSG_APDU_EMPTY;
import static com.tonnfccard.api.utils.ResponsesConstants.ERROR_MSG_INTENT_EMPTY;
import static com.tonnfccard.api.utils.ResponsesConstants.ERROR_MSG_NFC_CONNECT;
import static com.tonnfccard.api.utils.ResponsesConstants.ERROR_MSG_NFC_DISABLED;
import static com.tonnfccard.api.utils.ResponsesConstants.ERROR_MSG_NFC_DISCONNECT;
import static com.tonnfccard.api.utils.ResponsesConstants.ERROR_MSG_NO_CONTEXT;
import static com.tonnfccard.api.utils.ResponsesConstants.ERROR_MSG_NO_NFC;
import static com.tonnfccard.api.utils.ResponsesConstants.ERROR_MSG_NO_TAG;
import static com.tonnfccard.api.utils.ResponsesConstants.ERROR_TRANSCEIVE;

public class NfcApduRunner extends ApduRunner {
  public static final int TIME_OUT = 60000;
  private static NfcApduRunner nfcApduRunner;
  private static Context apiContext;

  private NfcAdapter nfcAdapter;
  private IsoDep nfcTag = null;

  private NfcApduRunner(Context context) {
    super();
    apiContext = context;
  }

  public synchronized static  NfcApduRunner getInstance(Context context) throws Exception{
    if (context == null) throw new Exception(ERROR_MSG_NO_CONTEXT);
    if (nfcApduRunner == null || apiContext == null) nfcApduRunner = new NfcApduRunner(context);
    return nfcApduRunner;
  }

  public void setNfcAdapter(NfcAdapter nfcAdapter){
    this.nfcAdapter = nfcAdapter;
  }

  public boolean setCardTag(Intent intent) throws Exception{
    if (intent == null) throw new Exception(ERROR_MSG_INTENT_EMPTY);
    Tag tag = intent.getParcelableExtra(EXTRA_TAG);
    if (tag == null) return false;
    nfcTag = IsoDep.get(tag);
    return true;
  }

  public void connect() throws Exception {
    if (nfcAdapter == null) {
      nfcAdapter = NfcAdapter.getDefaultAdapter(apiContext);
    }
    if (nfcAdapter == null) {
      throw new Exception(ERROR_MSG_NO_NFC);
    } else if (!nfcAdapter.isEnabled()) {
      throw new Exception(ERROR_MSG_NFC_DISABLED);
    } else if (nfcTag == null) {
      throw new Exception(ERROR_MSG_NO_TAG);
    }
    try {
      if (!nfcTag.isConnected()) {
        nfcTag.connect();
        nfcTag.setTimeout(TIME_OUT);
      }
    } catch (IOException e) {
        throw new Exception(ERROR_MSG_NFC_CONNECT);
    }
  }

  @Override
  public void disconnectCard() throws Exception {
    if (nfcTag == null) {
      throw new Exception(ERROR_MSG_NO_TAG);
    }
    try {
      nfcTag.close();
    } catch (IOException e) {
        throw new Exception(ERROR_MSG_NFC_DISCONNECT + ", more details: " + e.getMessage());
    }
  }

  @Override
  public RAPDU transmitCommand(CAPDU commandAPDU) throws  Exception {
    if (commandAPDU == null) {
      throw new Exception(ERROR_MSG_APDU_EMPTY);
    }
    return new RAPDU(transceive(commandAPDU.getBytes()));
  }

  private byte[] transceive(byte[] apduCommandBytes) throws Exception{
    connect();
    nfcTag.setTimeout(TIME_OUT);
    byte[] response;
    try {
      response = nfcTag.transceive(apduCommandBytes);
    } catch (IOException e) {
        throw new Exception(ERROR_TRANSCEIVE + ", More details: " + e.getMessage());
    }
    if (response == null || response.length <= 1) {
      throw new Exception(ERROR_BAD_RESPONSE);
    }
    return response;
  }
}
