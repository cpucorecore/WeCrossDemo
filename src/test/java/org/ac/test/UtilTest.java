package org.ac.test;

import org.junit.Test;
import org.ac.Util;

import static org.junit.Assert.assertEquals;


public class UtilTest {
    @Test
    public void transactionID1() throws Exception {
        int transactionID = Util.getTransactionIDFromFile();
        assertEquals(1, transactionID);
    }
}
