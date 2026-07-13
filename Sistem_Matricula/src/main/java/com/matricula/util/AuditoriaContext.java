package com.matricula.util;

import com.matricula.model.Auditoria;

public class AuditoriaContext {
    private static final ThreadLocal<Auditoria> CONTEXTO = new ThreadLocal<>();

    public static void setAuditoria(Auditoria auditoria) {
        CONTEXTO.set(auditoria);
    }

    public static Auditoria getAuditoria() {
        return CONTEXTO.get();
    }

    public static void clear() {
        CONTEXTO.remove();
    }
}
