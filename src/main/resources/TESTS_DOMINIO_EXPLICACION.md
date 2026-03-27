# Tests del Dominio Obligacion - Análisis

## ¿Por qué testear el dominio SIN Spring?

El test que escribiste (`ObligacionTest`) es **puro**: 
- No usa BD
- No usa Spring
- No usa mocks
- Solo instancia el modelo de dominio y ejecuta sus métodos

**Esto es ideal porque:**

1. **Rápido:** Ejecuta en milisegundos, sin levantar contexto Spring
2. **Aislado:** Solo prueba las reglas de negocio, no la infraestructura
3. **Determinista:** Siempre retorna el mismo resultado
4. **Documentación:** El test es un ejemplo de cómo usar la clase

## Tu test 1: `deberiaRegistrarMoraYActualizarEstado()`

```java
@Test
void deberiaRegistrarMoraYActualizarEstado() {
    // Arrange (preparar)
    Obligacion obligacion = Obligacion.crear(10L, "OBL-001", new BigDecimal("150000"));

    // Act (actuar)
    obligacion.registrarMora(15, new BigDecimal("50000"));

    // Assert (verificar)
    assertEquals(15, obligacion.getDiasMora());
    assertEquals(Obligacion.EstadoObligacion.EN_MORA, obligacion.getEstado());
    assertEquals(new BigDecimal("50000"), obligacion.getSaldoVencido());
}
```

### Desglose paso a paso

| Paso | Código | Explicación |
|------|--------|-------------|
| 1 | `Obligacion.crear(10L, "OBL-001", new BigDecimal("150000"))` | Crea una obligación nueva (sin ID, sale de BD después). Cliente #10, número único, saldo 150k. Estado inicial: `AL_DIA`. |
| 2 | `obligacion.registrarMora(15, new BigDecimal("50000"))` | Dice: "esta obligación está 15 días en mora, con saldo vencido de 50k" |
| 3 | `assertEquals(15, obligacion.getDiasMora())` | Verifica que los días se guardaron |
| 4 | `assertEquals(Obligacion.EstadoObligacion.EN_MORA, obligacion.getEstado())` | **Lo importante:** el estado cambió automáticamente a `EN_MORA` porque `diasMora > 0` |
| 5 | `assertEquals(new BigDecimal("50000"), obligacion.getSaldoVencido())` | Verifica el saldo vencido se guardó |

### ¿Qué regla de negocio valida?

```java
// En Obligacion.java
public void registrarMora(int diasMora, BigDecimal saldoVencido) {
    // ...validaciones...
    this.diasMora = diasMora;
    this.saldoVencido = saldoVencido;
    this.estado = diasMora > 0 ? EstadoObligacion.EN_MORA : EstadoObligacion.AL_DIA;
    // ^^^ ESTA REGLA: si hay días de mora, estado = EN_MORA
}
```

---

## Tu test 2: `deberiaLanzarErrorSiPagoEsCeroONegativo()`

```java
@Test
void deberiaLanzarErrorSiPagoEsCeroONegativo() {
    // Arrange
    Obligacion obligacion = Obligacion.crear(10L, "OBL-001", new BigDecimal("150000"));

    // Act & Assert (especial: esperamos que lance excepción)
    assertThrows(IllegalArgumentException.class, 
                 () -> obligacion.aplicarPago(BigDecimal.ZERO));
    assertThrows(IllegalArgumentException.class, 
                 () -> obligacion.aplicarPago(new BigDecimal("-1")));
}
```

### Desglose

| Paso | Código | Explicación |
|------|--------|-------------|
| 1 | `Obligacion.crear(...)` | Misma obligación que antes |
| 2 | `assertThrows(IllegalArgumentException.class, ...)` | Dice: "espero que esto lance excepción de tipo IllegalArgument" |
| 3 | `() -> obligacion.aplicarPago(BigDecimal.ZERO)` | Intenta pagar 0 → debe fallar |
| 4 | `obligacion.aplicarPago(new BigDecimal("-1"))` | Intenta pagar -1 → debe fallar |

### ¿Qué protege?

```java
// En Obligacion.java
public void aplicarPago(BigDecimal valorPago) {
    if (valorPago == null || valorPago.signum() <= 0) {
        throw new IllegalArgumentException("valorPago debe ser mayor a cero");
        // ^^^ PROTECCIÓN: no permite pagos de 0 o negativos
    }
    // ... aplicar pago ...
}
```

---

## Ampliar los tests (sugerencias)

### Test 3: Aplicar pago reduce saldos

```java
@Test
void deberiaReducirSaldoAlAplicarPago() {
    Obligacion obligacion = Obligacion.crear(10L, "OBL-001", new BigDecimal("150000"));
    obligacion.registrarMora(15, new BigDecimal("50000"));
    
    obligacion.aplicarPago(new BigDecimal("30000"));
    
    assertEquals(new BigDecimal("120000"), obligacion.getSaldoTotal());
    assertEquals(new BigDecimal("20000"), obligacion.getSaldoVencido());
}
```

### Test 4: Pago total cancela obligación

```java
@Test
void deberiaMarcarCanceladaAlPagarSaldoCompleto() {
    Obligacion obligacion = Obligacion.crear(10L, "OBL-001", new BigDecimal("100000"));
    
    obligacion.aplicarPago(new BigDecimal("100000"));
    
    assertEquals(Obligacion.EstadoObligacion.CANCELADA, obligacion.getEstado());
    assertEquals(BigDecimal.ZERO, obligacion.getSaldoTotal());
    assertEquals(0, obligacion.getDiasMora());
}
```

### Test 5: Registrar mora sin días no cambia a EN_MORA

```java
@Test
void noDeberiaMarcarEnMoraSiDiasEsCero() {
    Obligacion obligacion = Obligacion.crear(10L, "OBL-001", new BigDecimal("150000"));
    
    obligacion.registrarMora(0, BigDecimal.ZERO);
    
    assertEquals(Obligacion.EstadoObligacion.AL_DIA, obligacion.getEstado());
}
```

---

## Cómo ejecutar tests

### Todos los tests del proyecto
```bash
cd /home/fvillanueva/Escritorio/coovitelCobranzas
mvn test
```

### Solo del dominio Obligacion
```bash
mvn test -Dtest=ObligacionTest
```

### Solo un método
```bash
mvn test -Dtest=ObligacionTest#deberiaRegistrarMoraYActualizarEstado
```

### Ver cobertura
```bash
mvn jacoco:report
# Luego abre: target/site/jacoco/index.html
```

---

## Pirámide de testing (cómo debería crecer)

```
        ▲
       /│\
      / │ \
     /  │  \  E2E / UI Tests (pocos, lentos)
    /   │   \  - Selenium, Cypress
   /    │    \─────────────────────────
  /     │     \
 /  INT │      \ Integration Tests (medios)
/       │       \- Spring Boot Test, MockMvc
─────────────────- Db (H2), caché, servicios
       │
       │  Unit Tests (muchos, rápidos)
       │  - JUnit, AssertJ
       │  - Sin BD, sin Spring
       │  - Solo dominio
       ▼
```

Tu test está en **Unit Tests** (la base). Ahora puedes agregar:

1. **Integration Tests:** Controller + Service + Repo (con BD H2)
2. **E2E Tests:** HTTP real contra servidor levantado

---

## Checklist: Test bien escrito

✅ **Nombre descriptivo:** `deberiaRegistrarMoraYActualizarEstado()` (explica qué espera)
✅ **AAA pattern:** Arrange → Act → Assert
✅ **Una cosa por test:** Cada test valida una sola comportamiento
✅ **No hardcode BD:** Usa `Obligacion.crear()` que no toca BD
✅ **Assertions claros:** Usa `assertEquals` con valores esperados
✅ **Independientes:** Tests no dependen uno de otro

---

## Cómo este test protege el refactoring

Supongamos que después decides cambiar la lógica:

**Antes (tu código actual):**
```java
this.estado = diasMora > 0 ? EstadoObligacion.EN_MORA : EstadoObligacion.AL_DIA;
```

**Después (alguien accidentalmente cambió a ≥ en lugar de >):**
```java
this.estado = diasMora >= 0 ? EstadoObligacion.EN_MORA : EstadoObligacion.AL_DIA;
// ^^^ BUG: ahora 0 días marca EN_MORA
```

**Cuando ejecutas tests:**
```
deberiaRegistrarMoraYActualizarEstado() - ✅ PASA
noDeberiaMarcarEnMoraSiDiasEsCero() - ❌ FALLA!
```

El test **atrapa el bug antes de que llegue a producción**.


