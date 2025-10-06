/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 *
 * @author Do Trong Tri - CE190397 - Sep 29, 2025
 */
public class SecurityUtil {

    private static final SecureRandom rnd = new SecureRandom();

    // Generate random token dạng HEX
    public static String generateToken(int bytesLength) {
        byte[] b = new byte[bytesLength];
        rnd.nextBytes(b);
        StringBuilder sb = new StringBuilder();
        for (byte x : b) {
            sb.append(String.format("%02x", x));
        }
        return sb.toString(); // ví dụ: "a3f4b1c5d8e9..."
    }

    // SHA-256 hex
    public static String sha256Hex(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] digest = md.digest(input.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();
        for (byte x : digest) {
            sb.append(String.format("%02x", x));
        }
        return sb.toString();
    }
}
