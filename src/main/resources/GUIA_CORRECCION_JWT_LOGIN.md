# ✅ CORRECCIÓN: Error "Failed to select a JWK signing key"

## Problema
```json
{
    "error": "Internal server error",
    "message": "An error occurred while attempting to encode the Jwt: Failed to select a JWK signing key"
}
```

## Causa
La configuración de JWT en `SecurityConfig` no tenía una clave válida con suficientes bits (256 bits = 32 bytes mínimo para HMAC-SHA256).

## Solución Aplicada

### 1. ✅ Clave JWT Ampliada
**Archivo**: `application.properties`
```
Antes:  app.security.jwt.secret=coovitel-cobranzas-dev-jwt-secret-key-32chars!!
Ahora:  app.security.jwt.secret=coovitel-cobranzas-dev-jwt-secret-key-must-be-at-least-32-bytes-long-for-hs256!!
```

**Por qué**: La clave debe tener al menos 32 caracteres (256 bits) para que Nimbus genere correctamente el encoder/decoder de JWT.

### 2. ✅ Configuración JWT Robusta
**Archivo**: `SecurityConfig.java` (métodos `jwtEncoder` y `jwtDecoder`)

**Cambios**:
- Validación de longitud mínima de clave: `if (secret.length() < 32)`
- Creación correcta de `SecretKeySpec` con algoritmo `HmacSHA256`
- `ImmutableSecret<>(key)` pasado directamente al `NimbusJwtEncoder`
- Manejo de excepciones mejorado

---

## 🚀 PRÓXIMOS PASOS (Para ti)

### Paso 1: Actualiza el código
```bash
cd /home/fvillanueva/Escritorio/coovitelCobranzas
git pull  # O aplica los cambios manualmente desde el IDE
```

### Paso 2: Recompila
```bash
./mvnw clean compile -DskipTests
```

### Paso 3: Arranca la aplicación
```bash
./mvnw spring-boot:run
```

**Verifica en los logs que NO hay errores de JWT**:
```
✅ Expected log output:
- o.s.s.web.DefaultSecurityFilterChain     : Will secure any request with ...
- o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 ...
```

### Paso 4: Intenta login nuevamente

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "Admin123!"
  }'
```

**Respuesta esperada (200 OK)**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "username": "admin",
  "roles": ["ADMIN"],
  "expiresAt": "2026-04-07T21:05:03Z"
}
```

---

## 🔍 Si Aún No Funciona

### Síntoma 1: "JWT secret must be at least 32 characters long"
**Solución**: Verifica que `application.properties` tenga una clave > 32 caracteres:
```bash
grep "app.security.jwt.secret" src/main/resources/application.properties
```

### Síntoma 2: "Failed to encode JWT"
**Solución**: Asegúrate de que la BD tiene la tabla `users` con el usuario admin:
```sql
SELECT * FROM users WHERE username = 'admin';
```
Si está vacía, reinicia la app para que el bootstrap la llene.

### Síntoma 3: "Failed to create JWT decoder"
**Solución**: La clave debe tener formato UTF-8 válido. Verifica que no tenga caracteres especiales problemáticos.

---

## 📋 Resumen de Cambios

| Archivo | Cambio |
|---------|--------|
| `application.properties` | Clave JWT extendida a 80+ caracteres |
| `SecurityConfig.java` | Validación y creación robusta de encoder/decoder |
| `JwtProperties.java` | Sin cambios (ya está bien) |

---

## ✨ Verificación Rápida

Después de corregir, ejecuta:

```bash
# 1. Compila
./mvnw clean compile -DskipTests

# 2. Arranca la app en background
./mvnw spring-boot:run > /tmp/app.log 2>&1 &
APP_PID=$!

# 3. Espera 10 segundos
sleep 10

# 4. Intenta login
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"Admin123!"}' \
  | jq -r '.token')

# 5. Verifica
if [[ -n "$TOKEN" && "$TOKEN" != "null" ]]; then
  echo "✅ LOGIN OK - Token: ${TOKEN:0:50}..."
else
  echo "❌ LOGIN FALLÓ"
  cat /tmp/app.log | tail -n 50
fi

# 6. Mata la app
kill $APP_PID
```

---

## 📞 ¿Aún hay problemas?

Si después de esto sigue sin funcionar, comparte:
1. La salida completa del error (copy/paste del JSON)
2. Los logs de la aplicación (últimas 50 líneas)
3. Resultado de `curl -v http://localhost:8080/api/v1/auth/login ...`


