-- =====================================================================
-- PROYECTO: APP DE MATRÍCULA - SISTEMA DE CONTROL DE CUENTAS
-- Base de datos: MySQL 8.x
-- Metodología de seguridad: Roles, Permisos, Auditoría, Eliminación lógica
-- =====================================================================

DROP DATABASE IF EXISTS bd_matricula;
CREATE DATABASE bd_matricula CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bd_matricula;

-- =====================================================================
-- 1. MÓDULO DE SEGURIDAD
-- =====================================================================

-- 1.1 Rol
CREATE TABLE rol (
    idRol INT AUTO_INCREMENT PRIMARY KEY,
    nombreRol VARCHAR(40) NOT NULL UNIQUE,
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

-- 1.2 Funcionalidad (páginas / módulos del sistema, estructura tipo árbol)
CREATE TABLE funcionalidad (
    idFuncionalidad INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    icono VARCHAR(60),
    padre INT NULL,
    CONSTRAINT fk_funcionalidad_padre FOREIGN KEY (padre) REFERENCES funcionalidad(idFuncionalidad)
);

-- 1.3 Rol_Funcionalidad (permisos: qué puede hacer cada rol)
CREATE TABLE rol_funcionalidad (
    idRolFuncionalidad INT AUTO_INCREMENT PRIMARY KEY,
    idRol INT NOT NULL,
    idFuncionalidad INT NOT NULL,
    ver BOOLEAN NOT NULL DEFAULT FALSE,
    crear BOOLEAN NOT NULL DEFAULT FALSE,
    editar BOOLEAN NOT NULL DEFAULT FALSE,
    eliminar BOOLEAN NOT NULL DEFAULT FALSE,
    imprimir BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_rf_rol FOREIGN KEY (idRol) REFERENCES rol(idRol),
    CONSTRAINT fk_rf_funcionalidad FOREIGN KEY (idFuncionalidad) REFERENCES funcionalidad(idFuncionalidad),
    CONSTRAINT uk_rol_funcionalidad UNIQUE (idRol, idFuncionalidad)
);

-- 1.4 Usuario
CREATE TABLE usuario (
    idUsuario INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(30) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,     -- hash + salt (BCrypt)
    secret2FA VARCHAR(255) NULL,        -- clave secreta Google Authenticator
    idRol INT NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fechaRegistro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuarioCreacion INT NULL,
    fechaModificacion TIMESTAMP NULL,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (idRol) REFERENCES rol(idRol),
    CONSTRAINT fk_usuario_creacion FOREIGN KEY (usuarioCreacion) REFERENCES usuario(idUsuario)
);

-- =====================================================================
-- 2. TABLAS MAESTRAS / CATÁLOGOS
-- =====================================================================

CREATE TABLE tipoDocumento (
    codTipoDocumento INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE     -- DNI, Carné Extranjería, Pasaporte
);

CREATE TABLE anioAcademico (
    codAnioAcademico INT AUTO_INCREMENT PRIMARY KEY,
    anio INT NOT NULL UNIQUE,
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE nivel (
    codNivel INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE     -- Inicial, Primaria, Secundaria
);

CREATE TABLE grado (
    codGrado INT AUTO_INCREMENT PRIMARY KEY,
    codNivel INT NOT NULL,
    nombre VARCHAR(20) NOT NULL,           -- 3 años, 1°, 2°...
    CONSTRAINT fk_grado_nivel FOREIGN KEY (codNivel) REFERENCES nivel(codNivel),
    CONSTRAINT uk_grado UNIQUE (codNivel, nombre)
);

CREATE TABLE tipoConcepto (
    codTipoConcepto INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(20) NOT NULL UNIQUE     -- Fijo, Mensual, Opcional
);

CREATE TABLE parametro (
    codParametro INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL UNIQUE,
    valor VARCHAR(100) NOT NULL,
    descripcion VARCHAR(150)
);

-- =====================================================================
-- 3. MÓDULO ACADÉMICO
-- =====================================================================

-- 3.1 Aula
CREATE TABLE aula (
    codAula INT AUTO_INCREMENT PRIMARY KEY,
    codAnioAcademico INT NOT NULL,
    codNivel INT NOT NULL,
    codGrado INT NOT NULL,
    seccion VARCHAR(2) NOT NULL,
    capacidadMaxima SMALLINT NOT NULL,
    version INT NOT NULL DEFAULT 1,          -- optimistic lock
    estado BOOLEAN NOT NULL DEFAULT TRUE,   -- eliminación lógica
    fechaRegistro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_aula_anio FOREIGN KEY (codAnioAcademico) REFERENCES anioAcademico(codAnioAcademico),
    CONSTRAINT fk_aula_nivel FOREIGN KEY (codNivel) REFERENCES nivel(codNivel),
    CONSTRAINT fk_aula_grado FOREIGN KEY (codGrado) REFERENCES grado(codGrado),
    CONSTRAINT uk_aula UNIQUE (codAnioAcademico, codNivel, codGrado, seccion)
);

-- 3.2 Alumno (numeroDocumento y fechaNacimiento se almacenan cifrados con AES desde la capa Java)
CREATE TABLE alumno (
    codAlumno INT AUTO_INCREMENT PRIMARY KEY,
    codTipoDocumento INT NOT NULL,
    numeroDocumento VARCHAR(255) NOT NULL,   -- cifrado AES (texto plano máx 15, cifrado ocupa más espacio)
    nombres VARCHAR(80) NOT NULL,
    apellidoPaterno VARCHAR(60) NOT NULL,
    apellidoMaterno VARCHAR(60) NOT NULL,
    fechaNacimiento VARCHAR(255) NOT NULL,   -- cifrado AES
    version INT NOT NULL DEFAULT 1,          -- optimistic lock
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fechaRegistro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_alumno_tipodoc FOREIGN KEY (codTipoDocumento) REFERENCES tipoDocumento(codTipoDocumento),
    CONSTRAINT uk_alumno_doc UNIQUE (codTipoDocumento, numeroDocumento)
);

-- 3.3 Concepto (tarifario)
CREATE TABLE concepto (
    codConcepto INT AUTO_INCREMENT PRIMARY KEY,
    codAnioAcademico INT NOT NULL,
    codTipoConcepto INT NOT NULL,
    nombreConcepto VARCHAR(80) NOT NULL,
    monto NUMERIC(10,2) NOT NULL CHECK (monto > 0),
    ordenPago SMALLINT NOT NULL,
    obligatorio BOOLEAN NOT NULL DEFAULT TRUE,
    version INT NOT NULL DEFAULT 1,          -- optimistic lock
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fechaRegistro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_concepto_anio FOREIGN KEY (codAnioAcademico) REFERENCES anioAcademico(codAnioAcademico),
    CONSTRAINT fk_concepto_tipo FOREIGN KEY (codTipoConcepto) REFERENCES tipoConcepto(codTipoConcepto),
    CONSTRAINT uk_concepto UNIQUE (codAnioAcademico, nombreConcepto)
);

-- 3.4 Matrícula
CREATE TABLE matricula (
    codMatricula INT AUTO_INCREMENT PRIMARY KEY,
    codAlumno INT NOT NULL,
    codAula INT NOT NULL,
    codAnioAcademico INT NOT NULL,
    fechaMatricula TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version INT NOT NULL DEFAULT 1,          -- optimistic lock
    estado BOOLEAN NOT NULL DEFAULT TRUE,     -- activa / trasladada / eliminada lógicamente
    usuarioRegistro INT NOT NULL,
    CONSTRAINT fk_matricula_alumno FOREIGN KEY (codAlumno) REFERENCES alumno(codAlumno),
    CONSTRAINT fk_matricula_aula FOREIGN KEY (codAula) REFERENCES aula(codAula),
    CONSTRAINT fk_matricula_anio FOREIGN KEY (codAnioAcademico) REFERENCES anioAcademico(codAnioAcademico),
    CONSTRAINT fk_matricula_usuario FOREIGN KEY (usuarioRegistro) REFERENCES usuario(idUsuario),
    CONSTRAINT uk_matricula_alumno_anio UNIQUE (codAlumno, codAnioAcademico)
);

-- 3.5 Cuota (generada automáticamente al matricular, a partir de los conceptos del año)
CREATE TABLE cuota (
    codCuota INT AUTO_INCREMENT PRIMARY KEY,
    codMatricula INT NOT NULL,
    codConcepto INT NOT NULL,
    monto NUMERIC(10,2) NOT NULL,
    ordenPago SMALLINT NOT NULL,
    version INT NOT NULL DEFAULT 1,          -- optimistic lock
    estado VARCHAR(15) NOT NULL DEFAULT 'PENDIENTE',  -- PENDIENTE, PAGADO, BLOQUEADO
    fechaPago TIMESTAMP NULL,
    CONSTRAINT fk_cuota_matricula FOREIGN KEY (codMatricula) REFERENCES matricula(codMatricula),
    CONSTRAINT fk_cuota_concepto FOREIGN KEY (codConcepto) REFERENCES concepto(codConcepto)
);

-- 3.6 Recibo (comprobante de pago, correlativo único)
CREATE TABLE recibo (
    codRecibo INT AUTO_INCREMENT PRIMARY KEY,
    correlativo VARCHAR(20) NOT NULL UNIQUE,   -- Ej: BOL-000123
    codCuota INT NOT NULL,
    monto NUMERIC(10,2) NOT NULL,
    fechaEmision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuarioRegistro INT NOT NULL,
    CONSTRAINT fk_recibo_cuota FOREIGN KEY (codCuota) REFERENCES cuota(codCuota),
    CONSTRAINT fk_recibo_usuario FOREIGN KEY (usuarioRegistro) REFERENCES usuario(idUsuario)
);

-- =====================================================================
-- 4. AUDITORÍA
-- =====================================================================

CREATE TABLE auditoria (
    codAuditoria INT AUTO_INCREMENT PRIMARY KEY,
    codUsuario INT NULL,
    modulo VARCHAR(50) NOT NULL,
    tablaAfectada VARCHAR(50) NOT NULL,
    operacion VARCHAR(20) NOT NULL,     -- INSERT, UPDATE, DELETE, LOGIN, LOGOUT, PAGO, MATRICULA
    codigoRegistro INT NULL,
    valorAnterior TEXT NULL,            -- JSON
    valorNuevo TEXT NULL,               -- JSON
    fechaHora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ipOrigen VARCHAR(45) NULL,
    equipo VARCHAR(100) NULL,
    navegador VARCHAR(150) NULL,
    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (codUsuario) REFERENCES usuario(idUsuario)
);

-- =====================================================================
-- 5. DATOS INICIALES (SEED)
-- =====================================================================

INSERT INTO rol (nombreRol) VALUES ('Superusuario'), ('Director'), ('Secretaria');

INSERT INTO tipoDocumento (nombre) VALUES ('DNI'), ('Carné de Extranjería'), ('Pasaporte');

INSERT INTO nivel (nombre) VALUES ('Inicial'), ('Primaria'), ('Secundaria');

INSERT INTO grado (codNivel, nombre) VALUES
(1,'3 años'),(1,'4 años'),(1,'5 años'),
(2,'1°'),(2,'2°'),(2,'3°'),(2,'4°'),(2,'5°'),(2,'6°'),
(3,'1°'),(3,'2°'),(3,'3°'),(3,'4°'),(3,'5°');

INSERT INTO tipoConcepto (nombre) VALUES ('Fijo'), ('Mensual'), ('Opcional');

INSERT INTO anioAcademico (anio) VALUES (2026);

INSERT INTO parametro (nombre, valor, descripcion) VALUES
('CAPACIDAD_MAXIMA_DEFECTO', '35', 'Capacidad máxima por defecto de un aula'),
('CORRELATIVO_RECIBO', '1', 'Último correlativo de recibo emitido');

-- Funcionalidades (árbol de menú)
INSERT INTO funcionalidad (nombre, icono, padre) VALUES
('Seguridad', 'shield', NULL),
('Usuarios', 'user', 1),
('Roles', 'users', 1),
('Permisos', 'lock', 1),
('Académico', 'book', NULL),
('Matrícula', 'edit', 5),
('Aulas', 'grid', 5),
('Alumnos', 'user-plus', 5),
('Conceptos', 'list', 5),
('Pagos', 'dollar-sign', NULL),
('Reportes', 'bar-chart', NULL),
('Auditoría', 'clock', NULL);

-- Usuario Superusuario inicial (password de ejemplo: "admin123" -> reemplazar por hash BCrypt real generado en Java)
INSERT INTO usuario (usuario, password, idRol, estado) VALUES
('admin', '$2a$12$REEMPLAZAR_CON_HASH_BCRYPT_REAL', 1, TRUE);

-- Permisos totales para Superusuario sobre todas las funcionalidades
INSERT INTO rol_funcionalidad (idRol, idFuncionalidad, ver, crear, editar, eliminar, imprimir)
SELECT 1, idFuncionalidad, TRUE, TRUE, TRUE, TRUE, TRUE FROM funcionalidad;

-- Permisos de solo lectura para Director
INSERT INTO rol_funcionalidad (idRol, idFuncionalidad, ver, crear, editar, eliminar, imprimir)
SELECT 2, idFuncionalidad, TRUE, FALSE, FALSE, FALSE, TRUE FROM funcionalidad;

-- Permisos totales (operativos) para Secretaria, excepto Seguridad
INSERT INTO rol_funcionalidad (idRol, idFuncionalidad, ver, crear, editar, eliminar, imprimir)
SELECT 3, idFuncionalidad, TRUE, TRUE, TRUE, FALSE, TRUE
FROM funcionalidad WHERE idFuncionalidad NOT IN (1,2,3,4);

-- =====================================================================
-- 6. TRIGGERS DE AUDITORÍA (ejemplo sobre concepto, para no permitir precio <= 0
--    y registrar automáticamente el cambio de versión / optimistic lock)
-- =====================================================================

DELIMITER $$

CREATE TRIGGER trg_concepto_before_update
BEFORE UPDATE ON concepto
FOR EACH ROW
BEGIN
    IF NEW.version <> OLD.version THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El registro fue modificado por otro usuario. Actualice la pantalla antes de continuar.';
    END IF;
    SET NEW.version = OLD.version + 1;
END$$

CREATE TRIGGER trg_aula_before_update
BEFORE UPDATE ON aula
FOR EACH ROW
BEGIN
    IF NEW.version <> OLD.version THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El registro de aula fue modificado por otro usuario. Actualice la pantalla antes de continuar.';
    END IF;
    SET NEW.version = OLD.version + 1;
END$$

CREATE TRIGGER trg_alumno_before_update
BEFORE UPDATE ON alumno
FOR EACH ROW
BEGIN
    IF NEW.version <> OLD.version THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El registro de alumno fue modificado por otro usuario. Actualice la pantalla antes de continuar.';
    END IF;
    SET NEW.version = OLD.version + 1;
END$$

CREATE TRIGGER trg_matricula_before_update
BEFORE UPDATE ON matricula
FOR EACH ROW
BEGIN
    IF NEW.version <> OLD.version THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El registro de matrícula fue modificado por otro usuario. Actualice la pantalla antes de continuar.';
    END IF;
    SET NEW.version = OLD.version + 1;
END$$

CREATE TRIGGER trg_cuota_before_update
BEFORE UPDATE ON cuota
FOR EACH ROW
BEGIN
    IF NEW.version <> OLD.version THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'El registro de cuota fue modificado por otro usuario. Actualice la pantalla antes de continuar.';
    END IF;
    SET NEW.version = OLD.version + 1;
END$$

DELIMITER ;

