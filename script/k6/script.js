import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    setupTimeout: '300s',
    scenarios: {
        concurrent_join: {
            executor: 'per-vu-iterations',
            vus: 1000,
            iterations: 1,
            maxDuration: '30s',
        },
    },
};

export function setup() {
    const baseUrl = 'http://localhost:8080';
    let sessions = [];

    // ... 서버 준비 대기 ...

    const totalUsers = 1000;
    const batchSize = 100;
    const batches = Math.ceil(totalUsers / batchSize);

    console.log(`🔐 [사전 준비] ${totalUsers}명 로그인 시작\n`);

    for (let batch = 0; batch < batches; batch++) {
        const startIdx = batch * batchSize + 1;
        const endIdx = Math.min((batch + 1) * batchSize, totalUsers);
        const currentBatchSize = endIdx - startIdx + 1;

        console.log(`\n📦 배치 ${batch + 1}/${batches}: test${startIdx}~test${endIdx}`);

        http.cookieJar().clear(baseUrl);

        let requests = [];
        for (let i = startIdx; i <= endIdx; i++) {
            requests.push({
                method: 'POST',
                url: `${baseUrl}/api/v1/auth/login`,
                body: JSON.stringify({
                    email: `test${i}@test.com`,
                    password: '1234'
                }),
                params: {
                    headers: { 'Content-Type': 'application/json' },
                    timeout: '30s'
                }
            });
        }

        const startTime = Date.now();
        let responses = http.batch(requests);
        const duration = Date.now() - startTime;

        let batchSuccessCount = 0;
        let batchFailCount = 0;
        let batchSessions = []; // ✅ 배치 내 세션 저장

        responses.forEach((res, idx) => {
            if (res.status === 200 && res.cookies && res.cookies['JSESSIONID']) {
                const cookie = res.cookies['JSESSIONID'][0];
                if (cookie && cookie.value) {
                    const sessionId = cookie.value;
                    sessions.push(sessionId);
                    batchSessions.push(sessionId);
                    batchSuccessCount++;
                }
            } else {
                batchFailCount++;
                if (res.error) {
                    console.log(`  ⚠️ 실패: ${res.error}`);
                }
            }
        });

        // ✅ 배치 내 중복 검사
        const uniqueBatchSessions = new Set(batchSessions);
        const batchDuplicates = batchSessions.length - uniqueBatchSessions.size;

        if (batchDuplicates > 0) {
            console.log(`  ⚠️ 배치 내 중복 세션: ${batchDuplicates}개`);
        }

        console.log(`  ⏱️  소요 시간: ${duration}ms`);
        console.log(`  ✅ 성공: ${batchSuccessCount}/${currentBatchSize}`);
        console.log(`  ❌ 실패: ${batchFailCount}/${currentBatchSize}`);
        console.log(`  📊 누적: ${sessions.length}/${totalUsers}`);

        if (batch < batches - 1) {
            sleep(1);
        }
    }

    // ✅ 전체 중복 검사
    const uniqueSessions = new Set(sessions);
    const totalDuplicates = sessions.length - uniqueSessions.size;

    console.log(`\n========================================`);
    console.log(`✅ 총 성공: ${sessions.length}/${totalUsers}`);
    console.log(`❌ 총 실패: ${totalUsers - sessions.length}/${totalUsers}`);
    console.log(`🔍 고유 세션: ${uniqueSessions.size}개`);
    console.log(`⚠️ 중복 세션: ${totalDuplicates}개`);
    console.log(`📈 성공률: ${(sessions.length / totalUsers * 100).toFixed(2)}%`);
    console.log(`📈 고유 세션률: ${(uniqueSessions.size / sessions.length * 100).toFixed(2)}%`);
    console.log(`========================================\n`);

    if (totalDuplicates > 0) {
        console.log(`⚠️ 경고: ${totalDuplicates}개의 중복 세션이 발견되었습니다!`);
        console.log(`일부 VU가 동일한 세션을 사용할 수 있습니다.\n`);
    }

    if (sessions.length === 0) {
        throw new Error('❌ 모든 로그인 실패!');
    }

    sleep(3);
    console.log('📊 [측정 시작]\n');

    return {
        sessions,
        baseUrl: 'http://localhost:8080'
    };
}

export default function (data) {
    const vu = __VU;

    if (vu > data.sessions.length) {
        console.log(`⚠️ VU ${vu}: 세션 부족, 스킵`);
        return;
    }

    const sessionId = data.sessions[vu - 1];

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
}

export function teardown(data) {
    console.log('\n📊 [측정 완료]');
    console.log(`총 ${data.sessions.length}개 세션 사용`);
}