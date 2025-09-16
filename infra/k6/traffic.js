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
// 60% válidos, 25% 404, 15% 500 (según mocks que definimos abajo)
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
