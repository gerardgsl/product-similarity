# 📊 Observability and Load Testing for **Product Similarity API**

This package includes:

1. **Grafana dashboard** with enhanced and relevant metrics.
2. **Provisioning** to auto-load the dashboard.
3. **k6 scripts** with scenarios for normal traffic, error mixes, spikes, and circuit breaker testing.
4. **Additional mocks** in *simulado* for 200/404/500.
5. Recommended `application.yml` tweaks for better histograms.

> Copy/paste the files into the specified paths and launch with `docker compose up -d --build`.

---

## 0) Recommended settings (`application.yml`)

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    distribution:
      percentiles-histogram:
        http.server.requests: true
        http.client.requests: true
      percentiles:
        http.server.requests: 0.5,0.9,0.95,0.99
        http.client.requests: 0.5,0.9,0.95,0.99
```

---

## 1) Grafana auto-provisioning

**File:** `infra/grafana/provisioning/dashboards/dashboards.yml`

```yaml
apiVersion: 1
providers:
  - name: 'default'
    orgId: 1
    folder: 'Product Similarity'
    type: file
    disableDeletion: false
    editable: true
    updateIntervalSeconds: 10
    options:
      path: /etc/grafana/provisioning/dashboards
```

**File:** `infra/grafana/provisioning/datasources/datasource.yml`

```yaml
apiVersion: 1
datasources:
  - name: Prometheus
    type: prometheus
    access: proxy
    isDefault: true
    editable: true
    url: http://prometheus:9090
```

---

## 2) Dashboard JSON (auto-import)

**File:** `infra/grafana/provisioning/dashboards/product-similarity-observability.json`
*(same JSON content as in Spanish version — metrics panels included)*

> If any panel appears empty, let traffic run for 1–2 minutes. For client-side panels (`http.client.requests.*`), ensure Feign is instrumented with Micrometer (usually included with Spring Boot Actuator).

---

## 3) k6 — load scenarios

**File:** `infra/k6/traffic.js`

```js
import http from 'k6/http';
import { sleep, check } from 'k6';

export const options = {
  scenarios: {
    smoke: { executor: 'constant-vus', vus: 2, duration: '30s' },
    baseline: { executor: 'ramping-vus', startVUs: 0, stages: [ {duration:'30s', target:10}, {duration:'60s', target:20}, {duration:'30s', target:0} ], startTime: '35s' },
    error_mix: { executor: 'constant-arrival-rate', rate: 20, timeUnit: '1s', duration: '2m', preAllocatedVUs: 20, startTime: '2m' },
    spike: { executor: 'ramping-arrival-rate', startRate: 0, timeUnit: '1s', stages: [ {target:50, duration:'30s'}, {target:150, duration:'30s'}, {target:0, duration:'30s'} ], preAllocatedVUs: 60, startTime: '4m' }
  },
  thresholds: {
    http_req_failed: ['rate<0.05'],
    http_req_duration: ['p(95)<800'],
  }
};

const BASE = __ENV.BASE_URL || 'http://localhost:5000';

function pickId() {
  // 60% valid, 25% 404, 15% 500 (based on mocks)
  const r = Math.random();
  if (r < 0.60) return ['1','2','3','4'][Math.floor(Math.random()*4)];
  if (r < 0.85) return ['7','8','9'][Math.floor(Math.random()*3)];
  return ['500','501'][Math.floor(Math.random()*2)];
}

export default function () {
  const id = pickId();
  const res = http.get(`${BASE}/product/${id}/similar`, { timeout: '2s', tags: { endpoint: 'similar' } });
  check(res, { 'status ok/404/500': r => [200,404,500].includes(r.status) });
  sleep(0.1);
}

// Circuit breaker test:
//   docker stop simulado && sleep 30 && docker start simulado
// Then observe Grafana panels for breaker state and timeouts.
```

**Run:**

```bash
# locally
k6 run infra/k6/traffic.js

# via docker
docker run --rm -i --network host -e BASE_URL=http://localhost:5000 grafana/k6 run - < infra/k6/traffic.js
```

---

## 4) Extra mocks for *simulado*

**File:** `infra/simulado/mocks.json`

```json
[
  { "path": "/product/1", "status": 200, "body": {"id":"1","name":"Shirt","price":9.99,"availability":true} },
  { "path": "/product/1/similarids", "status": 200, "body": ["2","3","4"] },

  { "path": "/product/2", "status": 200, "body": {"id":"2","name":"Shirt Blue","price":49.99,"availability":true} },
  { "path": "/product/3", "status": 200, "body": {"id":"3","name":"Shirt Red","price":59.99,"availability":true} },
  { "path": "/product/4", "status": 200, "body": {"id":"4","name":"Shirt Green","price":39.99,"availability":false} },

  { "path": "/product/7", "status": 404 },
  { "path": "/product/8", "status": 404 },
  { "path": "/product/9", "status": 404 },
  { "path": "/product/7/similarids", "status": 404 },
  { "path": "/product/8/similarids", "status": 404 },
  { "path": "/product/9/similarids", "status": 404 },

  { "path": "/product/500", "status": 500 },
  { "path": "/product/501", "status": 500 },
  { "path": "/product/500/similarids", "status": 500 },
  { "path": "/product/501/similarids", "status": 500 }
]
```

---

## 5) Tricks to stress bottlenecks

* **Open circuit breaker:** `docker stop simulado` for 30–60s during tests; watch `resilience4j_circuitbreaker_state` and `not_permitted_calls`.
* **TimeLimiter thresholds:** set `timeoutDuration` lower (e.g., `400ms`) and increase load; see `resilience4j_timelimiter_calls_total{kind="timeout"}`.
* **Cache hit ratio:** bias traffic to ID `1` in `pickId()`; watch the **hit ratio** panel.
* **Client vs Server latency:** compare `http.client.requests` vs `http.server.requests` metrics.

---

## 6) Quick validation after startup

1. Prometheus: query `http_server_requests_seconds_count` and `http_client_requests_seconds_count`.
2. Grafana > Dashboard **Product Similarity — Observability+**: RPS, p95/p99, 2xx/4xx/5xx, retries, timeouts, cache ratio, heap/CPU should move.
3. Run `k6 run infra/k6/traffic.js` and observe changes in panels.

---
