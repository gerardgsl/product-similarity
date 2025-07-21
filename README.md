# 🛍️ Product Similarity API

This is a Spring Boot REST API developed as part of a backend technical challenge. The API provides details of similar products based on a given product ID, consuming data from an external service.

---

## 🚀 Features

- Get a list of similar products given a product ID
- Uses **Feign Client** to call external APIs
- Handles resilience with **Resilience4j** (`@Retry`, `@CircuitBreaker`, `@TimeLimiter`)
- Implements basic **in-memory caching** with `@Cacheable`
- Structured logging with **SLF4J**
- Includes **unit tests** and **integration tests**
- Includes **performance tests** with **K6**
- Docker support and `docker-compose` for testing

---

## 🧪 Endpoints

### `GET /product/{productId}/similar`

Returns a list of product details for products similar to the given ID.

#### Example response:

```json
[
  {
    "id": "2",
    "name": "Shirt Blue",
    "price": 49.99,
    "availability": true
  },
  ...
]
```

---

## 🛠️ Tech Stack

| Layer       | Technology                         |
|-------------|------------------------------------|
| Framework   | Spring Boot 3.2.5                  |
| HTTP Client | Spring Cloud OpenFeign             |
| Resilience  | Resilience4j                       |
| Caching     | Spring Cache (in-memory)           |
| Build       | Maven                              |
| Container   | Docker, Docker Compose             |

---

## 🧠 Design Decisions

- **OpenFeign** is used for simplicity and easy fallback integration with Resilience4j.
- **Parallel Streams** are used for performance when fetching multiple similar products concurrently.
- **Resilience4j** ensures that timeouts and retries are handled cleanly to avoid API cascading failures.
- **@Cacheable** improves performance on repeated requests for the same product.

---

## ▶️ Running the App

### Prerequisites

- Java 17+
- Maven

### Build and Run

```bash
mvn clean install
mvn spring-boot:run
```

The app runs on port `5000`.

---


## 📦 Caching

Caching is enabled for the `getSimilarProducts(productId)` method using Spring's `@Cacheable`. It can be configured to use Redis or Caffeine for production environments.

---

## 📌 Improvements

- Add Swagger/OpenAPI documentation
- Replace in-memory cache with Redis
- Extend resilience configuration via `application.yml`
- Add circuit breaker metrics 

---

## Future Improvements

The application could be improved in the following ways:

- **Improve Error Handling**: Provide more specific HTTP responses for different error cases (e.g. 404 when a product is not found).
- **Add Unit and Integration Tests**: Increase test coverage, especially for edge cases and error scenarios.
- **Implement OpenAPI Documentation**: Generate and expose API documentation using Swagger.
- **Add Observability**:
  - Structured logs using JSON format.
  - Integration with centralized logging tools like ELK or Grafana Loki.
  - Distributed tracing support (e.g. OpenTelemetry).
- **Make External Product Service Configurable**: Move `localhost:3001` to a configuration file or environment variable.
- **Dockerize the App**: Add a Dockerfile and docker-compose setup for easy deployment.
- **Rate Limiting & Security**: Apply rate limiting, authentication, and input validation.
- **Add Metrics Endpoint**: Use Micrometer to expose Prometheus-compatible metrics.
- **Deploy to Cloud**: Add a deployment pipeline to a cloud platform (e.g. AWS, GCP, or Azure).

Feel free to open an issue or contribute with a pull request if you'd like to help with any of these!
