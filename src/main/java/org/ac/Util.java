package org.ac;

import java.io.*;

public class Util {
    static String transactionIDPath = "transactionID";
    static int transactionID = 1;

    public static int getTransactionIDFromFile() {
        try {
            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(transactionIDPath));
            transactionID = dataInputStream.readInt();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return transactionID;
    }

    public static void updateTransactionIDToFile() throws IOException {
        File file = new File(transactionIDPath);
        if(!file.exists()) {
            file.createNewFile();
        }

        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(transactionIDPath));
        dataOutputStream.writeInt(transactionID+1);
    }
}
