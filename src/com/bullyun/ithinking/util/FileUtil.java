package com.bullyun.ithinking.util;

import java.io.*;

public class FileUtil {

    public static boolean save(byte[] data, String filename) {
        try {
            FileOutputStream outputStream = new FileOutputStream(filename);
            outputStream.write(data);
            outputStream.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static byte[] load(String filename) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            FileInputStream inputStream = new FileInputStream(filename);
            byte[] tmp = new byte[1024];
            while (true) {
                int readLen = inputStream.read(tmp);
                if (readLen <= 0) {
                    break;
                }
                byteArrayOutputStream.write(tmp, 0, readLen);
            }
        } catch (IOException e) {
            return null;
        }
        return byteArrayOutputStream.toByteArray();
    }

}
