import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    scenarios: {
        concurrent_join: {
            executor: 'per-vu-iterations',
            vus: 5000,
            iterations: 1,
            maxDuration: '3000s',
        },
    },
};

export function setup() {
    const baseUrl = 'http://localhost:8080';
    console.log('📊 [측정 시작 - 로그인 없이 바로 테스트]\n');
    return { baseUrl: baseUrl };
}

export default function (data) {
    const vu = __VU;

    // ✅ userId 3번부터 시작 (1:호스트, 2:이미참여)
    const userId = vu + 2;  // VU 1 → userId 3
    const nickname = `테스트${userId}`;

    console.log(`[VU ${vu}] 그룹 참여 시도! (userId: ${userId})`);

    // ✅ URL 수정 (join-test)
    let response = http.post(
        `${data.baseUrl}/api/v1/groups/1/join-test?userId=${userId}&nickname=${encodeURIComponent(nickname)}`,
        null,
        {
            headers: {
                'Content-Type': 'application/json'
            }
        }
    );

    check(response, {
        'join success (200)': (r) => r.status === 200,
        'group full (500)': (r) => r.status === 500,
        'response time < 1s': (r) => r.timings.duration < 1000,
    });

    if (response.status === 200) {
        console.log(`✅ VU ${vu}: 성공! (userId: ${userId})`);
    } else {
        console.log(`❌ VU ${vu}: 실패 (${response.status}, userId: ${userId})`);
    }
}

export function teardown(data) {
    console.log('\n📊 [측정 완료]');
}