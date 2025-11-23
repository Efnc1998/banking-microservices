# Customer Service

Microservicio de gestión de clientes del sistema bancario.

## Descripción

Este servicio se encarga de la gestión completa del ciclo de vida de los clientes, incluyendo operaciones CRUD y validaciones de negocio.

## Puerto

- **8081**

## Requisitos Previos

### Para ejecución local
- **Java 17** o superior
- **Maven 3.8+** (o usar el Maven Wrapper incluido)
- **Docker Desktop** (para la base de datos PostgreSQL)

### Para ejecución con Docker
- **Docker Desktop** con Docker Compose

## Estructura del Módulo

```
customer-service/
├── customer-domain/           # Modelos de dominio (Person, Customer)
├── customer-dto/              # DTOs para la API (PersonDto, CustomerDto)
├── customer-application/      # Capa de aplicación (Services, Mappers)
├── customer-infrastructure/   # Capa de infraestructura (Entities, Repositories)
├── customer-api/              # Capa API (Controllers, Application)
└── customer-client/           # Cliente WebClient para comunicación inter-servicios
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

### Paso 2: Compilar el Proyecto

```bash
cd customer-service

# En Windows
./mvnw.cmd clean install -DskipTests

# En Linux/Mac
./mvnw clean install -DskipTests
```

### Paso 3: Ejecutar el Microservicio

```bash
cd customer-api

# En Windows
../mvnw.cmd spring-boot:run

# En Linux/Mac
../mvnw spring-boot:run
```

El servicio estará disponible en: `http://localhost:8081`

### Paso 4: Verificar que el Servicio está Corriendo

```bash
curl http://localhost:8081/actuator/health
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

### Opción 1: Solo este microservicio

Desde el directorio raíz del proyecto (`banking-microservices`):

```bash
# Iniciar base de datos y customer-service
docker-compose up -d bankingdb customer-service

# Ver logs del servicio
docker-compose logs -f customer-service
```

### Opción 2: Todos los servicios

```bash
# Iniciar todos los servicios (bankingdb, customer-service, account-service)
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

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/customers` | Crear cliente |
| GET | `/api/v1/customers` | Listar todos los clientes |
| GET | `/api/v1/customers/{customerId}` | Obtener cliente por ID |
| PUT | `/api/v1/customers/{customerId}` | Actualizar cliente |
| DELETE | `/api/v1/customers/{customerId}` | Eliminar cliente |
| GET | `/api/v1/customers/exists/{customerId}` | Verificar si existe |

## Ejemplos de Uso

### Crear Cliente

```bash
curl -X POST http://localhost:8081/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{
    "person": {
      "name": "Juan Perez",
      "identification": "1234567890",
      "address": "Av. Principal 123",
      "phone": "0991234567"
    },
    "password": "1234",
    "status": true
  }'
```

**Payload POST /api/v1/customers:**
```json
{
  "person": {
    "name": "Juan Perez",
    "identification": "1234567890",
    "address": "Av. Principal 123",
    "phone": "0991234567"
  },
  "password": "1234",
  "status": true
}
```

| Campo | Tipo | Requerido | Descripción |
|-------|------|-----------|-------------|
| person | Object | Sí | Datos de la persona |
| person.name | String | Sí | Nombre completo (máx. 100 chars) |
| person.identification | String | Sí | Identificación (máx. 20 chars) |
| person.address | String | No | Dirección (máx. 255 chars) |
| person.phone | String | No | Teléfono (máx. 20 chars) |
| password | String | Sí | Contraseña |
| status | Boolean | No | Estado activo/inactivo |

### Obtener Cliente por ID

```bash
curl http://localhost:8081/api/v1/customers/1
```

### Listar Todos los Clientes

```bash
curl http://localhost:8081/api/v1/customers
```

### Actualizar Cliente

```bash
curl -X PUT http://localhost:8081/api/v1/customers/1 \
  -H "Content-Type: application/json" \
  -d '{
    "person": {
      "name": "Juan Perez Actualizado",
      "identification": "1234567890",
      "address": "Nueva Direccion 456",
      "phone": "0997654321"
    },
    "password": "newpass",
    "status": true
  }'
```

**Payload PUT /api/v1/customers/{customerId}:**
```json
{
  "person": {
    "name": "Juan Perez Actualizado",
    "identification": "1234567890",
    "address": "Nueva Direccion 456",
    "phone": "0997654321"
  },
  "password": "newpass",
  "status": true
}
```

### Eliminar Cliente

```bash
curl -X DELETE http://localhost:8081/api/v1/customers/1
```

## Datos de Prueba

| CustomerId | Nombre | Identificación | Password |
|------------|--------|----------------|----------|
| 1 | Jose Lema | 098254785 | 1234 |
| 2 | Marianela Montalvo | 097548965 | 5678 |
| 3 | Juan Osorio | 098874587 | 1245 |

## Health Check

```
http://localhost:8081/actuator/health
```

## Configuración

### application.yml (Perfil por defecto - Local)

```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bankingdb
    username: postgres
    password: postgres123
```

### application.yml (Perfil Docker)

```yaml
spring:
  config:
    activate:
      on-profile: docker
  datasource:
    url: jdbc:postgresql://bankingdb:5432/bankingdb
```

## Tests

```bash
cd customer-service
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

### Error: "package com.fasterxml.jackson.databind.annotation does not exist"

Falta la dependencia `jackson-databind` en el módulo `customer-dto`. Verificar que el `pom.xml` incluya:

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
