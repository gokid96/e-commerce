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

const BASE_URL = 'http://127.0.0.1:8080/api/v1';

export default function main() {
    const userId = randomIntBetween(1, 10000);
    let shouldOrder = false;
    let shouldChargeBalance = false;
    let selectedProduct = null;

    group('주문/결제 시나리오', () => {
        // 1. 인기 상품 조회
        const popularProductsResponse = http.get(`${BASE_URL}/products/ranks`, {
            tags: { name: '인기상품조회' }
        });
        check(popularProductsResponse, {
            '인기상품 조회 성공': (r) => r.status === 200,
        });

        if (popularProductsResponse.status === 200) {
            const body = JSON.parse(popularProductsResponse.body);
            if (body.data && Array.isArray(body.data.products) && body.data.products.length > 0) {
                const products = body.data.products;
                selectedProduct = products[Math.floor(Math.random() * products.length)];
                shouldChargeBalance = Math.random() < 0.2;
            }
        }

        // 2. 포인트 충전 및 조회
        if (shouldChargeBalance) {
            const payload = JSON.stringify({ amount: 10000 });
            const params = {
                headers: { 'Content-Type': 'application/json' },
                tags: { name: '포인트충전' }
            };
            const chargeResponse = http.post(`${BASE_URL}/users/${userId}/balance/charge`, payload, params);
            check(chargeResponse, {
                '포인트 충전 성공': (r) => r.status === 200 && JSON.parse(r.body).code === 200
            });

            const balanceResponse = http.get(`${BASE_URL}/users/${userId}/balance`, {
                tags: { name: '포인트조회' }
            });
            check(balanceResponse, {
                '포인트 조회 성공': (r) => r.status === 200 && JSON.parse(r.body).data.amount !== undefined
            });

            shouldOrder = Math.random() < 0.1;
        }

        // 3. 주문 생성
        // 주의: 우리 createOrder 응답은 ApiResponse<Void>라 orderId 미반환(비동기 사가).
        //       레퍼런스의 주문상태 확인(GET /orders/{orderId}) 단계는 생략한다.
        if (shouldOrder && selectedProduct) {
            const orderPayload = JSON.stringify({
                userId: userId,
                products: [
                    { productId: selectedProduct.id, quantity: 1 }
                ]
            });
            const orderParams = {
                headers: { 'Content-Type': 'application/json' },
                tags: { name: '상품주문' }
            };
            const orderResponse = http.post(`${BASE_URL}/orders`, orderPayload, orderParams);
            check(orderResponse, {
                '주문 생성 성공': (r) => r.status === 200 && JSON.parse(r.body).code === 200
            });
        } else {
            sleep(1);
        }
    });

    sleep(1);
}