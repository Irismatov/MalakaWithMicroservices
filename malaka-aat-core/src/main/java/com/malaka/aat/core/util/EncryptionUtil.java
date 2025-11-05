package com.malaka.aat.core.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class EncryptionUtil {


    public static String encodeBasicAuth(String username, String password) {
        String auth = username + ":" + password;
        return Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
    }


}
