package com.matricula.util;

import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

 
public class AESUtil {

     
    private static final String LLAVE = "CambiarEstaLlaveDe32CaracteresYA";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;

    private AESUtil() {
    }

    public static String cifrar(String textoPlano) {
        try {
            byte[] iv = new byte[IV_LENGTH_BYTE];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(LLAVE.getBytes("UTF-8"), "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);

            byte[] cifrado = cipher.doFinal(textoPlano.getBytes("UTF-8"));

            byte[] resultado = new byte[iv.length + cifrado.length];
            System.arraycopy(iv, 0, resultado, 0, iv.length);
            System.arraycopy(cifrado, 0, resultado, iv.length, cifrado.length);

            return Base64.getEncoder().encodeToString(resultado);
        } catch (Exception e) {
            throw new RuntimeException("Error al cifrar información", e);
        }
    }

    public static String descifrar(String textoCifradoBase64) {
        try {
            byte[] datos = Base64.getDecoder().decode(textoCifradoBase64);

            byte[] iv = new byte[IV_LENGTH_BYTE];
            System.arraycopy(datos, 0, iv, 0, iv.length);

            byte[] cifrado = new byte[datos.length - IV_LENGTH_BYTE];
            System.arraycopy(datos, IV_LENGTH_BYTE, cifrado, 0, cifrado.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(LLAVE.getBytes("UTF-8"), "AES");
            GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);

            byte[] resultado = cipher.doFinal(cifrado);
            return new String(resultado, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Error al descifrar información", e);
        }
    }
}
