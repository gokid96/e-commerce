import http from 'k6/http';
import { sleep, check, group } from "k6";
import { randomIntBetween } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

export const options = {
    stages: [
        { duration: '10s', target: 100 },
        { duration: '10s', target: 200 },
        { duration: '10s', target: 300 },
        { duration: '30s', target: 300 },
        { duration: '10s', target: 100 },
        { duration: '10s', target: 50 },
        { duration: '10s', target: 0 }
    ],
    thresholds: {
        http_req_duration: ['p(99)<1000'],
        http_req_failed: ['rate<0.01']
    }
};

const PRODUCT_URL = 'http://127.0.0.1:8084/api/v1';
const BALANCE_URL = 'http://127.0.0.1:8082/api/v1';
const ORDER_URL = 'http://127.0.0.1:8085/api/v1';

const ORDER_CHECK_INTERVAL = 2;

export default function main() {
    const userId = randomIntBetween(1, 10000);

    let orderId = null;
    let shouldOrder = false;
    let shouldChargeBalance = false;
    let selectedProduct = null;

    group('주문/결제 시나리오', () => {
        const popularProductsResponse = http.get(`${PRODUCT_URL}/products/ranks`, {
            tags: { name: '인기상품조회' }
        });
        check(popularProductsResponse, {
            '인기상품 조회 성공': (r) => r.status === 200,
            '인기상품 데이터 확인': (r) => {
                if (r.status !== 200) return false;
                const body = JSON.parse(r.body);
                return body.data && Array.isArray(body.data.products) && body.data.products.length > 0;
            }
        });

        if (popularProductsResponse.status === 200) {
            const body = JSON.parse(popularProductsResponse.body);
            if (body.data && Array.isArray(body.data.products) && body.data.products.length > 0) {
                const products = body.data.products;
                selectedProduct = products[Math.floor(Math.random() * products.length)];
                shouldChargeBalance = Math.random() < 0.2;
            }
        }

        if (shouldChargeBalance) {
            const payload = JSON.stringify({ amount: 10000 });
            const params = {
                headers: { 'Content-Type': 'application/json' },
                tags: { name: '잔액충전' }
            };
            const chargeResponse = http.post(`${BALANCE_URL}/users/${userId}/balance/charge`, payload, params);
            check(chargeResponse, {
                '잔액 충전 성공': (r) => r.status === 200 && JSON.parse(r.body).code === 200
            });

            const balanceResponse = http.get(`${BALANCE_URL}/users/${userId}/balance`, {
                tags: { name: '잔액조회' }
            });
            check(balanceResponse, {
                '잔액 조회 성공': (r) => r.status === 200 && JSON.parse(r.body).data.amount !== undefined
            });

            shouldOrder = Math.random() < 0.1;
        }

        if (shouldOrder && selectedProduct) {
            const orderPayload = JSON.stringify({
                userId: userId,
                products: [
                    { productId: selectedProduct.id, quantity: 1 }
                ]
            });
            const orderParams = {
                headers: { 'Content-Type': 'application/json' },
                tags: { name: '주문생성' }
            };
            const orderResponse = http.post(`${ORDER_URL}/orders`, orderPayload, orderParams);
            check(orderResponse, {
                '주문 생성 성공': (r) => r.status === 200,
                '주문 ID 확인': (r) => {
                    if (r.status !== 200) return false;
                    const body = JSON.parse(r.body);
                    if (body.data && body.data.orderId) {
                        orderId = body.data.orderId;
                        return true;
                    }
                    return false;
                }
            });

            // 주문/결제는 이벤트 기반 사가로 처리되므로, 일정 시간 대기 후 최종 상태를 확인한다.
            if (orderId) {
                sleep(ORDER_CHECK_INTERVAL);

                const orderStatusResponse = http.get(`${ORDER_URL}/orders/${orderId}`, {
                    tags: { name: '주문상태확인' }
                });
                check(orderStatusResponse, {
                    '주문 상태 조회 성공': (r) => r.status === 200,
                    '주문 완료 확인': (r) => {
                        if (r.status !== 200) return false;
                        const body = JSON.parse(r.body);
                        return body.data && body.data.status === 'COMPLETED';
                    }
                });
            }
        } else {
            sleep(1);
        }
    });

    sleep(1);
}
