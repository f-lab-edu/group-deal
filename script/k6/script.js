import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    setupTimeout: '180s', // ← ✅ 추가! (3분)
    scenarios: {
        concurrent_join: {
            executor: 'per-vu-iterations',
            vus: 1000,
            iterations: 1,
            maxDuration: '10s',
        },
    },
};

export function setup() {
    const baseUrl = 'http://localhost:8080';
    const params = { headers: { 'Content-Type': 'application/json' } };

    let sessions = [];

    console.log('🔐 [사전 준비] 로그인 중...\n');

    for (let i = 1; i <= 1000; i++) {
        const email = `test${i}@test.com`;
        console.log(`\n[${i}/1000] ${email} 로그인 시도...`);

        // ✅ 각 로그인 전에 쿠키 jar 초기화
        http.cookieJar().clear(baseUrl);

        let res = http.post(
            `${baseUrl}/api/v1/auth/login`,
            JSON.stringify({ email: email, password: '1234' }),
            params
        );

        console.log(`  상태 코드: ${res.status}`);
        console.log(`  응답 본문: ${res.body}`);

        if (res.cookies && res.cookies['JSESSIONID']) {
            const cookie = res.cookies['JSESSIONID'][0];
            if (cookie && cookie.value) {
                sessions.push(cookie.value);
                console.log(`  ✅ 성공! 세션: ${cookie.value.substring(0, 16)}...`);
            }
        } else {
            console.error(`  ❌ 쿠키 없음!`);
        }

        sleep(0.05);
    }

    console.log(`\n========================================`);
    console.log(`✅ 성공: ${sessions.length}개`);
    console.log(`❌ 실패: ${1000 - sessions.length}개`);

    if (sessions.length === 0) {
        throw new Error('❌ 모든 로그인 실패!');
    }

    sleep(3);
    console.log('\n📊 [측정 시작]\n');

    return {
        sessions,
        baseUrl: 'http://localhost:8080'
    };
}

export default function (data) {
    const vu = __VU;
    const sessionId = data.sessions[(vu - 1) % data.sessions.length];

    console.log(`[VU ${vu}] 그룹 참여 시도! (세션: ${sessionId.substring(0, 8)}...)`);

    let response = http.post(
        `${data.baseUrl}/api/v1/groups/1/join`,
        JSON.stringify({}),
        {
            headers: {
                'Content-Type': 'application/json',
                'Cookie': `JSESSIONID=${sessionId}`
            }
        }
    );

    check(response, {
        'join success (200)': (r) => r.status === 200,
        'group full (500)': (r) => r.status === 500,
        'response time < 1s': (r) => r.timings.duration < 1000,
    });

    if (response.status === 200) {
        console.log(`✅ VU ${vu}: 성공!`);
    } else {
        console.log(`❌ VU ${vu}: 실패 (${response.status})`);
    }

    sleep(0.1);
}

export function teardown(data) {
    console.log('\n📊 [측정 완료]');
    console.log(`총 ${data.sessions.length}개 세션 사용`);
}