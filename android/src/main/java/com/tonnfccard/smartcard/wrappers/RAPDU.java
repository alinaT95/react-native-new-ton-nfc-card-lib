package com.tonnfccard.smartcard.wrappers;

import com.tonnfccard.utils.ByteArrayHelper;

import static com.tonnfccard.api.utils.ResponsesConstants.*;

public class RAPDU {
    private static final ByteArrayHelper BYTE_ARRAY_HELPER = ByteArrayHelper.getInstance();
    private final byte[] bytes;
    private final byte[] data;
    private final byte[] sw;

    public RAPDU(String response) {
        this(BYTE_ARRAY_HELPER.bytes(response));
    }

    public RAPDU(byte[] bytes) {
        if (bytes == null || bytes.length < 2 ) throw new IllegalArgumentException(ERROR_MSG_SW_TOO_SHORT);
        if (bytes.length > 257 ) throw new IllegalArgumentException(ERROR_MSG_APDU_RESPONSE_TOO_LONG);
        int len = bytes.length;
        this.bytes = bytes;
        this.data = new byte[len-2];
        System.arraycopy(bytes, 0, data, 0, len - 2);
        this.sw = new byte[]{bytes[len-2], bytes[len-1]};
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getSW() {
        return sw;
    }

    public byte getSW1() {
        return sw[0];
    }

    public byte getSW2() {
        return sw[1];
    }

    public byte[] getBytes() {
        return bytes;
    }

    public static boolean isSuccess(RAPDU rapdu) {
        return rapdu.getSW1() == (byte)0x90 && rapdu.getSW2() == (byte)0x00;
    }

    @Override
    public String toString() {
        return BYTE_ARRAY_HELPER.hex(getSW()) +
                (getData()!=null && getData().length > 0
                        ? " '"+   BYTE_ARRAY_HELPER.hex(getData())+"'"
                        : "");
    }
}
