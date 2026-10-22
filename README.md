# Banking System REST API (Spring Boot)

A REST API version of the console Banking System, built with Spring Boot, Spring Data JPA (Hibernate) and MySQL.

## Run it
1. Install JDK 17+, Maven, and MySQL.
2. Open `src/main/resources/application.properties` and set your MySQL username and password.
3. Run: `mvn spring-boot:run` (or run `BankingApplication` from your IDE).
4. The API starts on http://localhost:8080. Tables are created automatically.

## Endpoints (test in Postman)
| Method | URL | Body |
|---|---|---|
| POST | /api/accounts | `{"holderName":"Laxmi","initialDeposit":5000}` |
| GET | /api/accounts/{accountNumber} | - |
| POST | /api/accounts/{accountNumber}/deposit | `{"amount":1000}` |
| POST | /api/accounts/{accountNumber}/withdraw | `{"amount":500}` |
| POST | /api/accounts/transfer | `{"fromAccount":"...","toAccount":"...","amount":200}` |
| GET | /api/accounts/{accountNumber}/transactions | - |

Try these error cases too: withdraw more than the balance (400), unknown account number (404),
negative amount (400), transfer to the same account (400).

## Structure
- `controller` - REST endpoints
- `service` - business logic (`@Transactional` for deposit/withdraw/transfer)
- `repository` - Spring Data JPA interfaces
- `model` - JPA entities (Account, Transaction)
- `dto` - request objects with validation
- `exception` - custom exceptions + global handler
