# STEP04 회고 — 클린 레이어드 아키텍처와 주문/결제 도메인

## 1. 클린 레이어드 아키텍처 (4 레이어)

계층형 구조에 DIP를 적용해 도메인 레이어를 중심에 둔다. 도메인은 어떤 외부 레이어도 모른다.

| 레이어 | 패키지 | 클래스 | 요청 DTO | 응답 DTO | 의존 대상 |
|---|---|---|---|---|---|
| 표현 | `interfaces` | `XxxController` | `XxxRequest` | `XxxResponse` | application |
| 응용 | `application` | `XxxFacade` | `XxxCriteria` | `XxxResult` | domain |
| 도메인 | `domain` | `XxxService` | `XxxCommand` | `XxxInfo` | 없음 |
| 인프라 | `infrastructure` | `XxxCoreRepository` | - | - | domain 인터페이스 구현 |

나는 feature-by-package(최상위가 도메인, 그 안에 레이어)로 구성했다.

```text
order/
├── interfaces/      OrderController, OrderRequest, OrderResponse
├── application/     OrderFacade, OrderCriteria, OrderResult
├── domain/          Order, OrderService, OrderRepository(interface), OrderCommand, OrderInfo, OrderStatus
└── infrastructure/  OrderCoreRepository (+ jpa/OrderJpaRepository)
```

Repository 3단 구조 — 도메인 인터페이스(`@Repository`) ← `CoreRepository`(`@Component`, JpaRepository 주입) → `JpaRepository`. CoreRepository가 도메인 인터페이스(자기 자신)를 주입하면 순환참조(#18)가 나므로 JpaRepository를 주입한다.

회고: 처음엔 레이어를 이렇게까지 잘게 나누는 게 과하다고 느꼈다. 그런데 도메인이 인프라를 모르게 두니, 도메인 단위 테스트를 Spring 없이 plain 클래스로 짤 수 있다는 점에서 분리의 실익이 바로 와닿았다. 3단 레포지토리도 처음엔 번거로워 보였지만, 순환참조(#18)를 직접 겪고 나서야 "무엇을 주입하느냐"가 구조의 핵심이라는 걸 체감했다.

---

## 2. 구현하며 고민한 것들

### 2-1. 도메인 협력 vs 강결합 — `couponId`냐 `Coupon`이냐

주문이 쿠폰을 참조할 때 ID(`Long`)를 받을지, 쿠폰 도메인 객체를 받을지.

나는 ID 참조(`Long userCouponId`)를 택했다.
```java
public class Order {
    private Long userCouponId;   // 도메인 객체가 아닌 ID 참조
}
```
- 이유: 도메인 간 직접 의존을 끊고 애그리거트 경계를 분명히 하기 위해. 쿠폰 사용/할인 계산은 주문이 아니라 쿠폰 쪽 책임으로 둔다.
- 단점 인지: `Long`은 어떤 값이든 들어와도 검증이 안 되고, 자칫 테이블 지향으로 흐를 위험이 있다.

회고: 쿠폰 객체를 직접 받는 쪽이 더 객체지향적이라는 관점도 분명히 있다. 다만 지금 내 목표는 도메인 경계를 깔끔히 끊어 나중에 모듈로 쪼개기 쉬운 구조를 만드는 것이라, ID 참조를 택했다. 검증이 안 되는 단점은 Facade가 쿠폰 서비스를 통해 먼저 조회·검증하고 넘기는 식으로 보완할 생각이다. 주문이 쿠폰의 행동까지 필요로 하는 순간이 오면 그때 다시 고민할 문제로 남겨둔다.

### 2-2. 레이어별 DTO 6종 — 오버엔지니어링?

`Command/Info`(domain), `Criteria/Result`(application), `Request/Response`(interfaces). 처음엔 과해 보였지만, 레이어 간 결합도를 낮추는 완충제다. Request를 도메인까지 끌고 가면 API 스펙 변경이 도메인을 흔든다.

```
DTO를 따로 안 만들고 Request를 도메인까지 끌고 간 경우 필드 하나를 수정하면 그 Request를 쓰는 모든 레이어를 다 수정해줘야 한다.
DTO를 레이어별로 따로 만들면 번거롭지만 변경이 그 경계에서 멈춰서 변환 코드만 수정하면 되고 도메인은 안 건드려도 되어서 유연하다.
```

### 2-3. 파사드, 꼭 써야 하나?

여러 도메인을 조합할 때만 Facade를 둔다. 그래서 `balance`/`coupon`엔 Facade가 있고, `payment`엔 Facade를 안 뒀다(주문 흐름을 `OrderFacade`가 조율). 단일 도메인 호출이면 Facade는 불필요한 파일만 늘린다.

회고: 처음엔 모든 도메인에 똑같이 Facade를 두는 게 일관적이라고 생각했다. 그런데 payment는 결국 주문 흐름의 일부라 자체 조율자가 필요 없었고, 억지로 Facade를 만들면 빈 껍데기만 늘어난다는 걸 알았다. 대칭성보다 "이 클래스가 실제로 조율할 게 있는가"를 기준으로 두는 게 맞았다.

### 2-4. 검증 로직, 어디에 둘까?

나는 도메인 객체 내부(정적 팩토리 `create`)에 뒀다.

```java
public static Order create(Long userId, Long userCouponId, double discountRate, List<OrderProduct> orderProducts) {
    if (orderProducts == null || orderProducts.isEmpty()) {
        throw new IllegalArgumentException("주문 상품이 없습니다.");
    }
    ...
}
```

Command에 두면(Early Validation) 모든 DTO마다 중복되고 테스트 커버리지가 약해진다. 예외는 전부 `IllegalArgumentException`으로 통일했다.

회고: 검증을 데이터 바로 옆(도메인)에 두니 규칙이 흩어지지 않고, 단위 테스트도 객체 하나만 놓고 짤 수 있어 깔끔했다. 예외 타입을 하나로 통일한 것도 컨트롤러 어드바이스에서 한 번에 처리돼 의외로 효과가 컸다. 실제로 이번에 빈 주문 검증이 빠져 있던 걸 뒤늦게 보강했는데, 검증 위치를 도메인으로 정해둔 덕에 한 군데만 고치면 됐다.

### 2-5. 도메인 클래스 vs JPA 엔티티 — 분리?

이번엔 분리하지 않고 엔티티=도메인으로 겸용했다(엔티티에 `@Entity` + 비즈니스 메서드). 분리하면 JPA 비의존, DIP에 더 맞지만, 어설픈 분리는 복잡도만 키운다고 판단. 추후 리팩토링 여지로 남김.

회고: 분리하면 더 이상적이라는 건 알지만, 지금 단계에서 도메인과 엔티티를 따로 두면 매핑 코드만 늘고 얻는 게 적다고 봤다. 대신 도메인에 JPA 애너테이션이 섞이는 비용은 분명히 인지하고 있고, ORM 독립이 실제로 필요해지는 시점이 오면 그때 분리할 생각이다. 지금은 의도된 타협이다.

### 2-6. JPA 연관관계, 최소화

같은 애그리거트만 연관관계를 맺었다: `Order ↔ OrderProduct`(`@OneToMany cascade` / `@ManyToOne`). 그 외 도메인 경계를 넘는 참조는 `Long` ID로 끊었다(`payment.orderId`, `stock.productId`, `order.userCouponId`). 무리한 연관관계는 영속성 전이, 지연로딩, 도메인의 JPA 종속을 부른다.

회고: 연관관계를 애그리거트 안으로만 제한하니 영속성 그래프가 작고 예측 가능해졌다. 경계를 넘는 참조를 ID로 끊은 것은 2-1의 쿠폰 결정과도 같은 원칙이라, 프로젝트 전체가 한 방향으로 일관되게 굴러간다는 느낌을 받았다.

---

## 3. 배운 것 / 다음

- 레이어 분리의 실익은 추상적인 "깨끗함"이 아니라 "테스트하기 쉬워진다"로 체감됐다. 도메인이 인프라를 모르니 단위 테스트에 Spring이 필요 없다.
- 경계를 넘는 참조는 ID, 검증은 도메인, 예외는 한 타입처럼 "한 번 정한 규칙을 일관되게" 적용하니 매번 고민할 일이 줄었다.
- 관성으로 코드를 늘리지 않고 "왜 그렇게 하는가"를 따져 결정했다. 트랜잭션을 Facade에 두기로 한 것, `paidAt`을 `LocalDateTime`으로 맞춘 것(ERD 정합)이 그런 예다. 지금 모든 걸 다 갖추지 않아도, 단계마다 필요한 것을 보강해 가면 된다는 걸 받아들였다.
- 트러블에서 배운 것: 순환참조(#18)와 빈 부재로 인한 contextLoads 실패(#19)를 겪으며, 도메인과 인프라는 한 묶음(같은 PR)으로 가야 빈이 항상 존재한다는 걸 체득했다.
- 다음: STEP04 #25 OrderFacade 통합(상품조회→재고차감→쿠폰사용→잔액차감→결제를 한 트랜잭션으로) → STEP05 동시성.

[참고] 항해 플러스