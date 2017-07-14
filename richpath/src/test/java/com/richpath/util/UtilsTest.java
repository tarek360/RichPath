package com.richpath.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UtilsTest {

    @Test
    public void testGetDimenFromString() throws Exception {

        assertEquals(22.0, Utils.getDimenFromString("22dip"), 0);
        assertEquals(1.5f, Utils.getDimenFromString("1.5dp"), 0);
        assertEquals(0.7f, Utils.getDimenFromString("0.7sp"), 0);
        assertEquals(2f, Utils.getDimenFromString("2in"), 0);
        assertEquals(22f, Utils.getDimenFromString("22px"), 0);

    }
}