import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    scenarios: {
        concurrent_join: {
            executor: 'per-vu-iterations',
            vus: 10,
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

    for (let i = 1; i <= 10; i++) {
        const email = `test${i}@test.com`;

        console.log(`\n[${i}/10] ${email} 로그인 시도...`);

        let res = http.post(
            `${baseUrl}/api/v1/auth/login`,
            JSON.stringify({ email: email, password: '1234' }),
            params
        );

        console.log(`  상태 코드: ${res.status}`);

        // ✅ 쿠키 객체 전체 출력
        console.log(`  쿠키 객체 타입: ${typeof res.cookies}`);
        console.log(`  쿠키 객체: ${JSON.stringify(res.cookies, null, 2)}`);

        // ✅ 쿠키 키 확인
        if (res.cookies) {
            const cookieKeys = Object.keys(res.cookies);
            console.log(`  쿠키 키 개수: ${cookieKeys.length}`);
            console.log(`  쿠키 키 목록: ${cookieKeys.join(', ')}`);
        }

        // ✅ JSESSIONID 직접 확인
        if (res.cookies && res.cookies['JSESSIONID']) {
            console.log(`  JSESSIONID 존재: true`);
            console.log(`  JSESSIONID 타입: ${typeof res.cookies['JSESSIONID']}`);
            console.log(`  JSESSIONID 길이: ${res.cookies['JSESSIONID'].length}`);

            if (Array.isArray(res.cookies['JSESSIONID'])) {
                console.log(`  JSESSIONID는 배열입니다`);
                const cookie = res.cookies['JSESSIONID'][0];
                console.log(`  첫 번째 쿠키: ${JSON.stringify(cookie)}`);

                if (cookie && cookie.value) {
                    const sessionId = cookie.value;
                    sessions.push(sessionId);
                    console.log(`  ✅ 성공! 세션: ${sessionId.substring(0, 16)}...`);
                } else {
                    console.error(`  ❌ 쿠키 객체에 value가 없습니다!`);
                }
            } else {
                console.error(`  ❌ JSESSIONID가 배열이 아닙니다!`);
            }
        } else {
            console.error(`  ❌ JSESSIONID 쿠키가 없습니다!`);
        }

        // 작은 딜레이 추가
        sleep(0.1);
    }

    console.log(`\n========================================`);
    console.log(`✅ 성공: ${sessions.length}개`);
    console.log(`❌ 실패: ${10 - sessions.length}개`);

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
        'group full (409)': (r) => r.status === 409,
        'already joined (400)': (r) => r.status === 400,
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