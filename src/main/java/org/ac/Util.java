package org.ac;

import java.io.*;

public class Util {
    public static final String transactionIDPath = "transactionID";

    public static int getTransactionIDFromFile() {
        int transactionID = 1;

        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(transactionIDPath));
            transactionID = dataInputStream.readInt();
            dataInputStream.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        return transactionID;
    }

    public static void updateTransactionIDToFile() throws IOException {
        int currentTransactionID = getTransactionIDFromFile();

        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(transactionIDPath));
        dataOutputStream.writeInt(currentTransactionID + 1);
        dataOutputStream.close();
    }
}
