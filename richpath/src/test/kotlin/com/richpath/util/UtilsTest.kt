package com.richpath.util

import org.junit.Assert.assertEquals
import org.junit.Test

class UtilsTest {

    @Test
    @Throws(Exception::class)
    fun testGetDimenFromString() {
        assertEquals(22.0f, Utils.getDimenFromString("22dip"), 0f)
        assertEquals(1.5f, Utils.getDimenFromString("1.5dp"), 0f)
        assertEquals(0.7f, Utils.getDimenFromString("0.7sp"), 0f)
        assertEquals(2f, Utils.getDimenFromString("2in"), 0f)
        assertEquals(22f, Utils.getDimenFromString("22px"), 0f)
    }
    
}