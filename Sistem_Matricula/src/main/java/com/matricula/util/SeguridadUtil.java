package com.matricula.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Maneja el cifrado de contraseñas mediante BCrypt.
 * BCrypt genera automáticamente un salt aleatorio distinto por cada
 * contraseña, cumpliendo con el requisito de "hash con salting".
 */
public class SeguridadUtil {

    private SeguridadUtil() {
    }

    /** Genera el hash de una contraseña en texto plano. */
    public static String hashPassword(String passwordPlano) {
        return BCrypt.hashpw(passwordPlano, BCrypt.gensalt(12));
    }

    /** Verifica que una contraseña en texto plano coincida con su hash almacenado. */
    public static boolean verificarPassword(String passwordPlano, String hashAlmacenado) {
        return BCrypt.checkpw(passwordPlano, hashAlmacenado);
    }
}
