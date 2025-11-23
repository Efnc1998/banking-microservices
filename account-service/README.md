# Account Service

Microservicio de gestión de cuentas bancarias y movimientos.

## Descripción

Este servicio gestiona las cuentas bancarias, movimientos (depósitos y retiros) y generación de reportes de estado de cuenta. Se comunica con `customer-service` para validar la existencia de clientes.

## Puerto

- **8082**

## Requisitos Previos

### Para ejecución local
- **Java 17** o superior
- **Maven 3.8+** (o usar el Maven Wrapper incluido)
- **Docker Desktop** (para la base de datos PostgreSQL)
- **customer-service** debe estar corriendo en el puerto 8081

### Para ejecución con Docker
- **Docker Desktop** con Docker Compose

## Estructura del Módulo

```
account-service/
├── account-domain/           # Modelos de dominio (Account, Transaction)
├── account-dto/              # DTOs para la API (AccountDto, TransactionDto, AccountStatementDto)
├── account-application/      # Capa de aplicación (AccountService, MovementService, ReportService)
├── account-infrastructure/   # Capa de infraestructura (Entities, Repositories)
├── account-api/              # Capa API (Controllers, Application)
└── account-client/           # Cliente WebClient para comunicación inter-servicios
```

---

## Ejecución Local (Desarrollo)

### Paso 1: Iniciar la Base de Datos con Docker

Desde el directorio raíz del proyecto (`banking-microservices`):

```bash
# Iniciar solo la base de datos PostgreSQL
docker-compose up -d bankingdb

# Verificar que la base de datos esté corriendo y saludable
docker ps --filter "name=bankingdb"
```

La base de datos estará disponible en:
- **Host:** localhost
- **Puerto:** 5432
- **Base de datos:** bankingdb
- **Usuario:** postgres
- **Contraseña:** postgres123

### Paso 2: Iniciar customer-service (Dependencia)

Este servicio depende de `customer-service`. Asegúrate de que esté corriendo:

```bash
cd customer-service

# En Windows
./mvnw.cmd clean install -DskipTests
cd customer-api
../mvnw.cmd spring-boot:run

# En Linux/Mac
./mvnw clean install -DskipTests
cd customer-api
../mvnw spring-boot:run
```

Verificar que customer-service responde en `http://localhost:8081/actuator/health`

### Paso 3: Compilar account-service

```bash
cd account-service

# En Windows
./mvnw.cmd clean install -DskipTests

# En Linux/Mac
./mvnw clean install -DskipTests
```

### Paso 4: Ejecutar account-service

```bash
cd account-api

# En Windows
../mvnw.cmd spring-boot:run

# En Linux/Mac
../mvnw spring-boot:run
```

El servicio estará disponible en: `http://localhost:8082`

### Paso 5: Verificar que el Servicio está Corriendo

```bash
curl http://localhost:8082/actuator/health
```

Respuesta esperada:
```json
{
  "status": "UP",
  "components": {
    "db": {"status": "UP"},
    "diskSpace": {"status": "UP"},
    "ping": {"status": "UP"}
  }
}
```

---

## Ejecución con Docker Compose

### Opción 1: Solo este microservicio (con dependencias)

Desde el directorio raíz del proyecto (`banking-microservices`):

```bash
# Iniciar base de datos, customer-service y account-service
docker-compose up -d bankingdb customer-service account-service

# Ver logs del servicio
docker-compose logs -f account-service
```

### Opción 2: Todos los servicios

```bash
# Iniciar todos los servicios
docker-compose up -d

# Ver estado de todos los contenedores
docker-compose ps
```

### Detener los Servicios

```bash
# Detener todos los servicios
docker-compose down

# Detener y eliminar volúmenes (CUIDADO: elimina datos de la BD)
docker-compose down -v
```

---

## Endpoints

### Cuentas

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/accounts` | Crear cuenta |
| GET | `/api/v1/accounts` | Listar todas las cuentas |
| GET | `/api/v1/accounts/{accountId}` | Obtener cuenta por ID |
| GET | `/api/v1/accounts/by-number/{accountNumber}` | Obtener por número de cuenta |
| GET | `/api/v1/accounts/by-customer/{customerId}` | Cuentas por cliente |
| PUT | `/api/v1/accounts/{accountId}` | Actualizar cuenta |
| DELETE | `/api/v1/accounts/{accountId}` | Eliminar cuenta |

### Movimientos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/movements` | Crear movimiento |
| GET | `/api/v1/movements` | Listar todos los movimientos |
| GET | `/api/v1/movements/{movementId}` | Obtener movimiento por ID |
| GET | `/api/v1/movements/by-account/{accountId}` | Movimientos por cuenta |
| PUT | `/api/v1/movements/{movementId}` | Actualizar movimiento |
| DELETE | `/api/v1/movements/{movementId}` | Eliminar movimiento |

### Reportes

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/v1/reports/{clientId}?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD` | Estado de cuenta |

## Ejemplos de Uso

### Crear Cuenta

```bash
curl -X POST http://localhost:8082/api/v1/accounts \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "123456",
    "accountType": "Ahorro",
    "initialBalance": 1000.00,
    "customerId": 1
  }'
```

**Payload POST /api/v1/accounts:**
```json
{
  "accountNumber": "123456",
  "accountType": "Ahorro",
  "initialBalance": 1000.00,
  "customerId": 1
}
```

| Campo | Tipo | Requerido | Valores válidos |
|-------|------|-----------|-----------------|
| accountNumber | String | Sí | Máximo 20 caracteres |
| accountType | String | Sí | `Ahorro`, `Corriente` |
| initialBalance | BigDecimal | Sí | >= 0.00 |
| customerId | Long | Sí | ID de cliente existente |

### Crear Movimiento (Depósito)

```bash
curl -X POST http://localhost:8082/api/v1/movements \
  -H "Content-Type: application/json" \
  -d '{
    "type": "Crédito",
    "amount": 500.00,
    "accountId": 1
  }'
```

### Crear Movimiento (Retiro)

```bash
curl -X POST http://localhost:8082/api/v1/movements \
  -H "Content-Type: application/json" \
  -d '{
    "type": "Débito",
    "amount": 200.00,
    "accountId": 1
  }'
```

**Payload POST /api/v1/movements:**
```json
{
  "type": "Débito",
  "amount": 200.00,
  "accountId": 1
}
```

| Campo | Tipo | Requerido | Valores válidos |
|-------|------|-----------|-----------------|
| type | String | Sí | `Débito`, `Crédito` |
| amount | BigDecimal | Sí | > 0.00 |
| accountId | Long | Sí | ID de cuenta existente |

### Obtener Estado de Cuenta

```bash
curl "http://localhost:8082/api/v1/reports/1?startDate=2024-01-01&endDate=2024-12-31"
```

### Listar Cuentas por Cliente

```bash
curl http://localhost:8082/api/v1/accounts/by-customer/1
```

## Datos de Prueba

### Cuentas

| Número | Tipo | Saldo Inicial | Cliente |
|--------|------|---------------|---------|
| 478758 | Ahorro | 2000.00 | Jose Lema |
| 225487 | Corriente | 100.00 | Marianela Montalvo |
| 495878 | Ahorro | 0.00 | Juan Osorio |
| 496825 | Ahorro | 540.00 | Marianela Montalvo |
| 585545 | Corriente | 1000.00 | Jose Lema |

## Reglas de Negocio

1. **Tipos de Movimiento**:
   - `Crédito`: Suma al saldo (depósito)
   - `Débito`: Resta del saldo (retiro)

2. **Validación de Saldo**:
   - No se permite retiro si el saldo resultante es negativo
   - Error: "Saldo no disponible"

3. **Tipos de Cuenta**:
   - `Ahorro`: Cuenta de ahorros
   - `Corriente`: Cuenta corriente

## Dependencias entre Servicios

Este servicio depende de `customer-service` para:
- Validar existencia de clientes al crear cuentas
- Obtener información de clientes para reportes

## Health Check

```
http://localhost:8082/actuator/health
```

## Configuración

### application.yml (Perfil por defecto - Local)

```yaml
server:
  port: 8082

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bankingdb
    username: postgres
    password: postgres123

customer:
  service:
    url: http://localhost:8081
```

### application.yml (Perfil Docker)

```yaml
spring:
  config:
    activate:
      on-profile: docker
  datasource:
    url: jdbc:postgresql://bankingdb:5432/bankingdb

customer:
  service:
    url: http://customer-service:8081
```

## Tests

```bash
cd account-service
./mvnw test
```

---

## Solución de Problemas

### Error: "Connection to localhost:5432 refused"

La base de datos PostgreSQL no está corriendo. Solución:

```bash
# Verificar si Docker está corriendo
docker info

# Iniciar la base de datos
docker-compose up -d bankingdb

# Esperar a que esté saludable
docker ps --filter "name=bankingdb"
```

### Error: "Connection refused" al conectar con customer-service

El servicio customer-service no está disponible. Solución:

```bash
# Verificar que customer-service esté corriendo
curl http://localhost:8081/actuator/health

# Si no responde, iniciar customer-service primero
```

### Error: "package com.fasterxml.jackson.databind.annotation does not exist"

Falta la dependencia `jackson-databind` en el módulo `account-dto`. Verificar que el `pom.xml` incluya:

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
</dependency>
```

### El servicio no inicia en Windows

Usar el wrapper de Maven con la extensión `.cmd`:

```bash
./mvnw.cmd clean install -DskipTests
./mvnw.cmd spring-boot:run
```
