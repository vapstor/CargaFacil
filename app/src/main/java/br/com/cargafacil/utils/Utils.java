package br.com.cargafacil.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public abstract class Utils {

    public final static String CONFIG_FILE = "CONFIG_FILE";
    public final static String MY_LOG_TAG = "VAPSTOR";
    public static long elapsedTime = 0;

    public static String sha256(String base) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}