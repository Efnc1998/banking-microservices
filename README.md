# Banking Microservices

Sistema bancario basado en microservicios desarrollado con Java 17, Spring Boot 3.4, Maven y PostgreSQL.

## Arquitectura

El proyecto implementa **Clean Architecture** con 6 capas por microservicio:

```
┌─────────────────────────────────────────────────────────────────┐
│                        ARCHITECTURE                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│   ┌──────────┐     ┌──────────────┐     ┌──────────────────┐    │
│   │   API    │────▶│  APPLICATION │────▶│  INFRASTRUCTURE  │    │
│   │(Controller)    │  (Service)   │     │  (Repository)    │    │
│   └──────────┘     └──────────────┘     └──────────────────┘    │
│        │                  │                      │              │
│        ▼                  ▼                      ▼              │
│   ┌──────────┐     ┌──────────────┐     ┌──────────────────┐    │
│   │   DTO    │     │    DOMAIN    │     │     ENTITY       │    │
│   └──────────┘     │   (Model)    │     │     (JPA)        │    │
│                    └──────────────┘     └──────────────────┘    │
│                                                                 │
│   ┌──────────────────────────────────────────────────────────┐  │
│   │                      CLIENT                              │  │
│   │              (WebClient for inter-service)               │  │
│   └──────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

## Microservicios

| Servicio | Puerto | Descripción | Documentación |
|----------|--------|-------------|---------------|
| customer-service | 8081 | Gestión de clientes | [README](./customer-service/README.md) |
| account-service | 8082 | Gestión de cuentas y movimientos | [README](./account-service/README.md) |

## Estructura del Proyecto

```
banking-microservices/
├── README.md
├── docker-compose.yml
├── BaseDatos.sql
├── customer-service/          # Ver customer-service/README.md
│   ├── customer-domain/
│   ├── customer-dto/
│   ├── customer-application/
│   ├── customer-infrastructure/
│   ├── customer-api/
│   └── customer-client/
└── account-service/           # Ver account-service/README.md
    ├── account-domain/
    ├── account-dto/
    ├── account-application/
    ├── account-infrastructure/
    ├── account-api/
    └── account-client/
```

## Tecnologías

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| Java | 17 | Lenguaje de programación |
| Spring Boot | 3.4.0 | Framework principal |
| Spring WebFlux | 3.4.0 | API reactiva |
| Spring Data JPA | 3.4.0 | Persistencia |
| PostgreSQL | 15 | Base de datos |
| Lombok | 1.18.x | Reducción de boilerplate |
| Maven | 3.9.x | Gestión de dependencias |
| Docker | 24.x | Contenedorización |

## Requisitos Previos

- **Java 17** o superior
- **Maven 3.9+**
- **Docker** y **Docker Compose**

## Instalación y Ejecución

### Docker Compose (Recomendado)

```bash
# Levantar todos los servicios
docker-compose up --build

# Ejecutar en segundo plano
docker-compose up --build -d

# Ver logs
docker-compose logs -f

# Detener servicios
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v
```

### Ejecución Local

```bash
# 1. Levantar PostgreSQL
docker-compose up bankingdb -d

# 2. Compilar y ejecutar customer-service
cd customer-service
./mvnw clean install -DskipTests
cd customer-api && ../mvnw spring-boot:run

# 3. Compilar y ejecutar account-service (otra terminal)
cd account-service
./mvnw clean install -DskipTests
cd account-api && ../mvnw spring-boot:run
```

## Variables de Entorno

| Variable | Descripción | Default |
|----------|-------------|---------|
| `SPRING_DATASOURCE_URL` | URL de PostgreSQL | `jdbc:postgresql://localhost:5432/bankingdb` |
| `SPRING_DATASOURCE_USERNAME` | Usuario BD | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Password BD | `postgres123` |
| `CUSTOMER_SERVICE_URL` | URL customer-service | `http://localhost:8081` |

## Reglas de Negocio

1. **Movimientos**: `Crédito` suma al saldo, `Débito` resta del saldo
2. **Validación de Saldo**: No se permite retiro si el saldo resultante es negativo
3. **Tipos de Cuenta**: `Ahorro` (cuenta de ahorros), `Corriente` (cuenta corriente)

## APIs Disponibles

### Customer Service (Puerto 8081)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/customers` | Crear cliente |
| GET | `/api/v1/customers` | Listar clientes |
| GET | `/api/v1/customers/{customerId}` | Obtener cliente por ID |
| PUT | `/api/v1/customers/{customerId}` | Actualizar cliente |
| DELETE | `/api/v1/customers/{customerId}` | Eliminar cliente |
| GET | `/api/v1/customers/exists/{customerId}` | Verificar existencia |

### Account Service (Puerto 8082)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/v1/accounts` | Crear cuenta |
| GET | `/api/v1/accounts` | Listar cuentas |
| GET | `/api/v1/accounts/{accountId}` | Obtener cuenta por ID |
| GET | `/api/v1/accounts/by-number/{accountNumber}` | Obtener por número |
| GET | `/api/v1/accounts/by-customer/{customerId}` | Cuentas por cliente |
| PUT | `/api/v1/accounts/{accountId}` | Actualizar cuenta |
| DELETE | `/api/v1/accounts/{accountId}` | Eliminar cuenta |
| POST | `/api/v1/movements` | Crear movimiento |
| GET | `/api/v1/movements` | Listar movimientos |
| GET | `/api/v1/movements/{movementId}` | Obtener movimiento por ID |
| GET | `/api/v1/movements/by-account/{accountId}` | Movimientos por cuenta |
| PUT | `/api/v1/movements/{movementId}` | Actualizar movimiento |
| DELETE | `/api/v1/movements/{movementId}` | Eliminar movimiento |
| GET | `/api/v1/reports/{clientId}?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD` | Estado de cuenta |

## Payloads de Ejemplo

### Crear Cliente (POST /api/v1/customers)
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

### Crear Cuenta (POST /api/v1/accounts)
```json
{
  "accountNumber": "123456",
  "accountType": "Ahorro",
  "initialBalance": 1000.00,
  "customerId": 1
}
```
> **accountType**: `Ahorro` o `Corriente`

### Crear Movimiento (POST /api/v1/movements)
```json
{
  "type": "Débito",
  "amount": 200.00,
  "accountId": 1
}
```
> **type**: `Débito` (retiro) o `Crédito` (depósito)

## Ejecutar Tests

```bash
# Tests de customer-service
cd customer-service && ./mvnw test

# Tests de account-service
cd account-service && ./mvnw test
```

## Troubleshooting

### Error de conexión a base de datos
```bash
docker ps | grep bankingdb
docker logs bankingdb
```

### Puerto en uso (Windows)
```bash
netstat -ano | findstr :8081
taskkill /PID <PID> /F
```

### Reconstruir contenedores
```bash
docker-compose down -v
docker-compose build --no-cache
docker-compose up
```

## Autor

Edison Narváez
