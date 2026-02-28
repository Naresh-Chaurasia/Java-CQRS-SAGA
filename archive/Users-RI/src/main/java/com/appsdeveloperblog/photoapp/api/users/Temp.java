package com.appsdeveloperblog.photoapp.api.users;

import javax.crypto.SecretKey;

import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;

public class Temp {
    public static void main(String[] args) {
        SecretKey key = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS512);
String base64Key = java.util.Base64.getEncoder().encodeToString(key.getEncoded());
System.out.println("Generated key: " + base64Key);
    }
}
