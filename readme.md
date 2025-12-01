# Card API

API REST desafio Hyperativa

## Funcionalidades

- Autenticação via JWT
- Criptografia de PANs (números de cartão)
- Hash SHA-256 para consultas otimizadas
- Banco de dados MySQL
- Migrations com Flyway

## Tecnologias

- **Java 21**
- **Spring Boot 3.x**
- **Spring Security**
- **MySQL 8.0**
- **Flyway**
- **JWT**
- **Docker & Docker Compose**
- **Maven**

## Como Executar o Projeto

### Pré-requisitos

- Docker e Docker Compose instalados
- Java 21+ 
- Maven 3.6+ 

### 1. Clone o Repositório
 git clone https://github.com/fborsatto/card-api card-api

### 2. Execute Docker Compose para subir MySql
docker-compose up

### 3. Executar aplicação localmente
./mvnw spring-boot:run

## 📖 Documentação da API

A aplicação estará disponível em: `https://localhost:8443`

### 🔐 Autenticação

#### Gerar Token JWT
http POST /api/v1/auth/login Content-Type: application/json
{ "login": "admin", "password": "admin" }

**Resposta de Sucesso:**

json { "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", "type": "Bearer", "expiresIn": 86400000 }

**Exemplo com cURL:**

curl -X POST [https://localhost:8443/api/v1/auth/login](http://localhost:8080/api/v1/auth/login)
-H "Content-Type: application/json"
-d '{"login":"admin","password":"admin"}'

###  Endpoints de Cartões

> Todos os endpoints de cartões requerem autenticação. Inclua o header:
> ```
> Authorization: Bearer <seu-jwt-token>
> ```

#### 1. Registrar Novo Cartão
curl -X POST [https://localhost:8443/api/v1/cards](http://localhost:8080/api/v1/cards)
-H "Authorization: Bearer <JWT-TOKEN>"
-H "Content-Type: application/json"
-d '{"pan":"4111111111111111"}'

#### 2. Consultar Cartão por PAN
curl -X GET [https://localhost:8443/api/v1/cards/4111111111111111](http://localhost:8080/api/v1/cards/4111111111111111)
-H "Authorization: Bearer <JWT-TOKEN>"

#### 3. Enviar batch de cartoes
curl --location 'https://localhost:8443/api/v1/cards/batch' \
--header 'Bearer <JWT-TOKEN>' \
--form 'file=@"<PATH_TO_FILE>"'

### Documentação da API

https://localhost:8443/swagger-ui/index.html



