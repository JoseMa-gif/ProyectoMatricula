package com.matricula.util;

import org.mindrot.jbcrypt.BCrypt;

 
public class SeguridadUtil {

    private SeguridadUtil() {
    }

     
    public static String hashPassword(String passwordPlano) {
        return BCrypt.hashpw(passwordPlano, BCrypt.gensalt(12));
    }

     
    public static boolean verificarPassword(String passwordPlano, String hashAlmacenado) {
        return BCrypt.checkpw(passwordPlano, hashAlmacenado);
    }
}
