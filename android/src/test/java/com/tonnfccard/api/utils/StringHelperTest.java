package com.tonnfccard.api.utils;

import com.tonnfccard.api.utils.StringHelper;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringHelperTest {
    @Test
    public void isHexStringTest() {
        assertFalse(StringHelper.isHexString("S0ASJH"));
        assertTrue(StringHelper.isHexString("0000AAFF"));
    }

    @Test
    public void isNumericStringTest() {
        assertTrue(StringHelper.isHexString("0123456789"));
        assertFalse(StringHelper.isHexString("0123456789A"));
    }

}
