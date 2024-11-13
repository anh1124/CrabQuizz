package com.example.crabquizz.Scripts;

import java.security.SecureRandom;
import java.util.Base64;

public class TokenGen {
    private static final int TOKEN_LENGTH = 50;
    private static TokenGen instance;

    // Private constructor to prevent instantiation from outside
    private TokenGen() {}

    // Singleton method to get the single instance
    public static TokenGen getInstance() {
        if (instance == null) {
            instance = new TokenGen();
        }
        return instance;
    }

    public String getRandomToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[TOKEN_LENGTH];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, TOKEN_LENGTH);
    }
}
