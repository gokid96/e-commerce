# STEP04 회고 — 클린 레이어드 아키텍처와 주문/결제 도메인

## 1. 클린 레이어드 아키텍처 (4 레이어)

계층형 구조에 DIP를 적용해 도메인 레이어를 중심에 두고 도메인은 어떤 외부 레이어도 모른다.

| 레이어 | 패키지 | 클래스 | 요청 DTO | 응답 DTO | 의존 대상 |
|---|---|---|---|---|---|
| 표현 | `interfaces` | `XxxController` | `XxxRequest` | `XxxResponse` | application |
| 응용 | `application` | `XxxFacade` | `XxxCriteria` | `XxxResult` | domain |
| 도메인 | `domain` | `XxxService` | `XxxCommand` | `XxxInfo` | 없음 |
| 인프라 | `infrastructure` | `XxxCoreRepository` | - | - | domain 인터페이스 구현 |

나는 feature-by-package(최상위 도메인, 그 안에 레이어)로 구성

```text
order/
├── interfaces/      OrderController, OrderRequest, OrderResponse
├── application/     OrderFacade, OrderCriteria, OrderResult
├── domain/          Order, OrderService, OrderRepository(interface), OrderCommand, OrderInfo, OrderStatus
└── infrastructure/  OrderCoreRepository (+ jpa/OrderJpaRepository)
```

### Repository 3단 구조

도메인 계층이 JPA를 직접 알지 못하도록, 리포지토리를 세 단계로 나눈다.
```
OrderRepository (도메인 인터페이스, @Repository)
        ▲ implements
OrderCoreRepository (@Component)
        │ 주입(의존)
        ▼
OrderJpaRepository (extends JpaRepository)
```
- 위 화살표: CoreRepository가 도메인 인터페이스를 구현
- 아래 화살표: CoreRepository가 JpaRepository를 주입받아 위임
```java
// domain/OrderService.java
import org.springframework.data.jpa.repository.JpaRepository;  // ← 기술이 도메인에 침투

@Service
public class OrderService {
private final OrderJpaRepository jpaRepository;  // JPA를 직접 의존

    public Order place(...) {
        return jpaRepository.save(order);  // 도메인이 JPA를 직접 호출
    }
}
```
```java
// domain/OrderService.java
// JPA import 없음. 순수 자바 + 도메인 인터페이스만
@Service
public class OrderService {
private final OrderRepository orderRepository;  // 내가 정의한 인터페이스

    public Order place(...) {
        return orderRepository.save(order);  // 이게 JPA인지 뭔지 도메인은 모름
    }
}
```
```
교체가 필요할 때는 OrderCoreRepository 이 클래스 하나만 고치면 되고,
OrderRepository 인터페이스와 그걸 쓰는 도메인 다른 계층은 건드리지 않아도 된다.

"보일러플레이트 비용을 내고 유연성과 도메인 순수성을 유지하자"
```

---

## 2. 구현하며 고민한 것들

### 1.`couponId`,`Coupon` 애그리거트
ID 참조를 택하면 애그리거트 경계는 깔끔해지지만, OOP다운 방식을 일부 포기해야한다

```java
public class Order {
    private Long userCouponId;   // 도메인 객체가 아닌 ID 참조
}
```
- 이유: 도메인 간 직접 의존을 끊고 애그리거트 경계를 분명히 하기 위해. 쿠폰 사용/할인 계산은 주문이 아니라 쿠폰 쪽 책임으로 둔다.
- 단점: `Long`은 어떤 값이든 들어와도 검증이 안되고,테이블 지향으로 흐를 위험이 있다.

```
쿠폰 객체를 직접 받는 쪽이 OOP 관점이 맞지만 지금은 ID참조로 할인율 숫자만 있으면 가능하다.목표는 도메인 경계를 끊어 모듈로 쪼개기 쉬운 구조를 만드는 것이라 ID 참조를 택했다.
주문에서 쿠폰의 행동(할인 계산, 만료·사용 여부 판단 등)을 직접 불러야하는 상황이 오면 객체참조가 필요해진다.
```

### 2. 레이어별 DTO 6종 - 오버엔지니어링?

`Command/Info`(domain), `Criteria/Result`(application), `Request/Response`(interfaces)
레이어 간 결합도를 낮추는 완충제다. Request를 도메인까지 끌고 가면 API 스펙 변경이 도메인을 흔든다.

```
DTO를 따로 안 만들고 OrderRequest를 도메인까지 그대로 끌고 갔다면, 
Request가 컨트롤러/Facade/도메인/저장 로직까지 전부 관통하고 있어 
필드 하나 바꾸는 순간 Request를 쓰던 모든 계층이 영향을 받는다.
계층별로 DTO가 나뉘어 있으면,OrderRequest가 바뀌어도 Request->Criteria로 변환하는 코드 한 군데에서 흡수되고 변환 코드만 고치면 된다.
Criteria부터 안쪽 Command,도메인은 손댈 필요가 없어진다
```
### 3. 파사드 꼭 써야 하나?

```
호출하는 시스템이 한두 개 뿐이면 굳이 감쌀 게 없고 그 묶음을 부르는 곳이 한군데 뿐이거나 앞으로늘어날 일이 없다면 그대로 두는 게 더 읽기 쉽다
반대로 여러 독립 서비스를 정해진 순서로 엮어 호출하는 로직이 있고 다른 클래스 여기저기에 흩어져있거나, 호출 순서/조건이 자주 바뀌어서 한 군데 모아두고 관리하기 좋다.
```

### 4. JPA 연관관계, 최소화

같은 애그리거트만 연관관계맺기
```
애그리거트란 항상 함께 생성/수정/삭제 되어야 하는 엔티티들을 하나로 묶는 경계선으로,바깥에서는 대표인 애그리거트 루트를 통해서만 접근한다.
그 경계 안만 객체로 연결하고 경계 밖은 ID 값으로 연결해서 연관관계를 최소화한다.
이를 통해 경계를 넘나드는 불필요한 객체 탐색이 줄어 N+1 위험이 낮아지고,
나중에 회원 도메인을 별도 서비스로 분리 하기도 훨씬 수월하다.
```
