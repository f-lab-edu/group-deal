import http from 'k6/http';
import { check, sleep } from 'k6';
import { Counter } from 'k6/metrics';

const successCount = new Counter('custom_success');
const groupFullCount = new Counter('custom_group_full');
const timeoutCount = new Counter('custom_timeout');
const connectionResetCount = new Counter('custom_connection_reset');
const otherErrorCount = new Counter('custom_other_error');

export let options = {
    scenarios: {
        concurrent_join: {
            executor: 'per-vu-iterations',
            vus: 5000,
            iterations: 1,
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

    // ✅ 에러 타입 분류
    let errorType = 'none';

    if (response.status === 200) {
        // 성공
        successCount.add(1);
        errorType = 'success';
    } else if (response.status === 500) {
        // GROUP_FULL
        groupFullCount.add(1);
        errorType = 'group_full';
    } else if (response.error) {
        // 에러가 있는 경우 (status === 0)
        const errorMsg = response.error.toLowerCase();

        if (errorMsg.includes('timeout')) {
            timeoutCount.add(1);
            errorType = 'timeout';
        } else if (errorMsg.includes('connection reset')) {
            connectionResetCount.add(1);
            errorType = 'connection_reset';
        } else {
            otherErrorCount.add(1);
            errorType = 'other_error';
        }
    } else {
        // 기타 HTTP 에러
        otherErrorCount.add(1);
        errorType = 'http_error';
    }

    // ✅ 체크
    check(response, {
        'success (200)': (r) => r.status === 200,
        'group full (500)': (r) => r.status === 500,
        'timeout error': (r) => r.error && r.error.toLowerCase().includes('timeout'),
        'connection reset': (r) => r.error && r.error.toLowerCase().includes('connection reset'),
        'other error': (r) => r.status !== 200 && r.status !== 500 && r.error &&
            !r.error.toLowerCase().includes('timeout') &&
            !r.error.toLowerCase().includes('connection reset'),
    });

    // ✅ 로그 출력
    if (errorType === 'success') {
        const body = JSON.parse(response.body);
        const queueNumber = body.data?.joinMember?.queueNumber;
        console.log(`✅ VU ${vu}: 성공! (userId: ${userId}, 순번: ${queueNumber})`);
    } else if (errorType === 'group_full') {
        console.log(`🔒 VU ${vu}: 그룹 가득참 (userId: ${userId})`);
    } else if (errorType === 'timeout') {
        console.log(`⏰ VU ${vu}: 타임아웃 (userId: ${userId})`);
    } else if (errorType === 'connection_reset') {
        console.log(`💥 VU ${vu}: 연결 끊김 (userId: ${userId})`);
    } else {
        console.log(`❓ VU ${vu}: 기타 에러 (${response.status}, ${response.error}, userId: ${userId})`);
    }
}

export function teardown(data) {
    console.log('\n📊 [측정 완료]');
}