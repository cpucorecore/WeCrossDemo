package org.ac.test;

import org.junit.After;
import org.junit.Test;
import org.ac.Util;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class UtilTest {
    @After
    public void clean() {
        File file = new File(Util.transactionIDPath);
        if(file.exists()) {
            file.delete();
        }
    }

    @Test
    public void transactionID1() {
        int transactionID = Util.getTransactionIDFromFile();
        assertEquals(1, transactionID);
    }

    @Test
    public void transactionID2() throws Exception {
        Util.updateTransactionIDToFile();
        int transactionID = Util.getTransactionIDFromFile();
        assertEquals(2, transactionID);
    }

    @Test
    public void transactionID3() throws Exception {
        Util.updateTransactionIDToFile();
        Util.updateTransactionIDToFile();
        int transactionID = Util.getTransactionIDFromFile();
        assertEquals(3, transactionID);
    }
}
