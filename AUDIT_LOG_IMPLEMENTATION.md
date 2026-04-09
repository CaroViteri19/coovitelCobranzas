# Implementación de Registros de Auditoría para Autenticación y Registro

## Problema Identificado
No se estaban registrando los logs de usuarios, específicamente:
- Intentos de inicio de sesión (exitosos y fallidos)
- Registros de nuevos usuarios (exitosos y fallidos)
- Cambios de roles de usuario
- Intentos fallidos y bloqueos de cuenta

## Solución Implementada

### 1. **Integración del Servicio de Auditoría**
Se integró el `AuditService` en `AuthApplicationService` para registrar eventos de seguridad en la tabla `auditoria_eventos`.

**Cambios realizados en**: `/src/main/java/coovitelCobranza/security/application/service/AuthApplicationService.java`

#### a) Inyección de Dependencia
- Se agregó `AuditService` como dependencia inyectable
- Se actualizó el constructor para recibir la instancia de `AuditService`

```java
private final AuditService auditService;

public AuthApplicationService(..., AuditService auditService) {
    // ...
    this.auditService = auditService;
}
```

### 2. **Eventos de Auditoría Registrados**

#### En el método `register()` (Registro de Usuarios):
- **REGISTRATION_SUCCESS**: Registro exitoso de usuario
- **REGISTRATION_FAILED**: Fallo en registro por:
  - Usuario existente
  - Email existente
  - Contraseña que no cumple requisitos de complejidad

#### En el método `login()` (Inicio de Sesión):
- **LOGIN_SUCCESS**: Inicio de sesión exitoso
- **LOGIN_FAILED**: Fallo en inicio de sesión por:
  - Usuario no encontrado
  - Credenciales inválidas
  - Cuenta bloqueada
- **LOGIN_FAILED_ACCOUNT_LOCKED**: Cuenta bloqueada tras 3 intentos fallidos

#### En el método `assignRole()` (Asignación de Roles):
- **ROLE_ASSIGNED**: Cambio de roles de usuario

### 3. **Estructura del Evento de Auditoría**

Cada evento registra los siguientes campos:
```java
auditService.registerEvent(
    "SECURITY",              // module: módulo del sistema
    "USER",                  // entity: tipo de entidad
    userId,                  // entityId: ID de la entidad (null si no existe)
    "ACTION_TYPE",           // action: tipo de acción realizada
    username,                // user: usuario que realizó la acción
    userRole,                // userRole: rol del usuario (ANONYMOUS para intentos fallidos)
    "API",                   // source: origen de la acción
    "Descripción detallada", // details: descripción de lo que sucedió
    null                     // correlationId: para trazar operaciones relacionadas
);
```

### 4. **Detalles Técnicos Importantes**

- **Módulo**: Todos los eventos se registran con módulo `"SECURITY"`
- **Origen**: Se registran como `"API"` indicando que vienen del endpoint REST
- **Rol de Usuario**: 
  - En registros exitosos: `"USER"` (rol por defecto)
  - En intentos fallidos: `"ANONYMOUS"` (usuario no autenticado)
- **Entity ID**: 
  - Para registros exitosos: ID del usuario creado
  - Para intentos fallidos: `null` (usuario no existe)

### 5. **Beneficios de Implementación**

1. **Trazabilidad completa**: Se registra cada intento de autenticación
2. **Seguridad mejorada**: Se detectan intentos de fuerza bruta (3 intentos fallidos = bloqueo)
3. **Cumplimiento normativo**: Se mantiene un historial de todas las operaciones de seguridad
4. **Debugging facilitado**: Se pueden investigar problemas de autenticación
5. **Monitoreo**: Se pueden generar alertas basadas en patrones sospechosos

### 6. **Consultas de Auditoría**

Para consultar los logs de autenticación:
```sql
SELECT * FROM auditoria_eventos 
WHERE modulo = 'SECURITY' 
AND entidad = 'USER'
ORDER BY created_at DESC;
```

Para consultar intentos fallidos de un usuario específico:
```sql
SELECT * FROM auditoria_eventos 
WHERE modulo = 'SECURITY' 
AND entidad = 'USER'
AND usuario = 'username'
AND accion LIKE 'LOGIN_FAILED%'
ORDER BY created_at DESC;
```

### 7. **Tests Actualizados**

Se actualizó la clase de tests `AuthApplicationServiceTest` para mockear el `AuditService`:

```java
@Mock
private AuditService auditService;

// En setUp():
service = new AuthApplicationService(
    authenticationManager,
    jwtEncoder,
    jwtProperties,
    userRepository,
    roleRepository,
    passwordEncoder,
    auditService  // Nueva dependencia
);
```

**Resultado**: Todos los 3 tests de `AuthApplicationServiceTest` pasan exitosamente.

## Archivos Modificados

1. **AuthApplicationService.java**
   - Agregada inyección de `AuditService`
   - Agregados registros de auditoría en `register()`
   - Agregados registros de auditoría en `login()`
   - Agregados registros de auditoría en `assignRole()`

2. **AuthApplicationServiceTest.java**
   - Agregado mock de `AuditService`
   - Actualizado constructor en `setUp()`

## Validación

✅ Compilación: Exitosa sin errores
✅ Tests: 3/3 pasados en `AuthApplicationServiceTest`
✅ Compatibilidad: Integración exitosa con módulo de auditoría existente

## Próximos Pasos (Opcional)

1. Agregar un endpoint para consultar logs de auditoría filtrados por usuario
2. Implementar alertas automáticas para patrones sospechosos
3. Agregar rotación de logs de auditoría después de cierto período
4. Crear dashboard de monitoreo de intentos fallidos de login

