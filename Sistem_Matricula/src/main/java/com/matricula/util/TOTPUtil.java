package com.matricula.util;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class TOTPUtil {

    private static final String ISSUER = "AppMatricula";

    public static String generarClaveSecreta() {
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        GoogleAuthenticatorKey key = gAuth.createCredentials();
        return key.getKey();
    }

    public static String generarURLCodigoQR(String secret, String usuario) {
        try {
            String url = "otpauth://totp/" + URLEncoder.encode(ISSUER + ":" + usuario, StandardCharsets.UTF_8.toString())
                    + "?secret=" + secret
                    + "&issuer=" + URLEncoder.encode(ISSUER, StandardCharsets.UTF_8.toString());
            
             
            return "https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=" + URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean validarToken(String secret, int token) {
        if (secret == null || secret.isEmpty()) {
            return false;
        }
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        return gAuth.authorize(secret, token);
    }
}
