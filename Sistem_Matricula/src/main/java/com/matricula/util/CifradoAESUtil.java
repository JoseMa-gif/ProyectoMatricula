package com.matricula.util;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class CifradoAESUtil {

    private static final String ALGORITHM = "AES";
     
    private static final String KEY = "MatriculaSecureK"; 

    public static String cifrar(String valorPlano) throws Exception {
        if (valorPlano == null || valorPlano.trim().isEmpty()) {
            return valorPlano;
        }
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encryptedValue = cipher.doFinal(valorPlano.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(encryptedValue);
    }

    public static String descifrar(String valorCifrado) throws Exception {
        if (valorCifrado == null || valorCifrado.trim().isEmpty()) {
            return valorCifrado;
        }
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes("UTF-8"), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decodedValue = Base64.getDecoder().decode(valorCifrado);
        byte[] decryptedValue = cipher.doFinal(decodedValue);
        return new String(decryptedValue, "UTF-8");
    }
}

