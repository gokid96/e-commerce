# STEP04 - DB 인덱싱 최적화

병목이 예상되는 주요 조회 기능을 선정해 인덱스를 설계하고, 트랜잭션 격리 수준 인덱스 동작 원리를 정리
 
## 트랜잭션이란?

하나의 작업 단위를 의미하며, 모든 작업이 전부 성공하거나 전부 실패해야 하는 원자적 연산을 뜻함.   
일반적으로 DB 트랜잭션을 지칭하지만, CS 전반에서 사용되는 개념이다.

### ACID - 트랜잭션 4가지 특징

- **원자성(Atomicity)**: 트랜잭션 내 모든 작업은 전부 성공하거나, 전부 실패해야 한다.
- **일관성(Consistency)**: 트랜잭션 수행 전후로 시스템의 상태는 일관성을 유지해야 한다.
- **격리성(Isolation)**: 동시에 실행되는 트랜잭션은 서로 간섭하지 않아야 한다.
- **지속성(Durability)**: 트랜잭션이 성공적으로 완료되면, 시스템 장애가 발생하더라도 그 결과는 보존되어야 한다.

## 트랜잭션 격리 수준 (Isolation Level)

여러 트랜잭션이 동시에 실행될 때 서로 간섭하지 않도록 보장하는 정도다. 즉 동시성 문제를 어디까지 허용할지 설정하는 기준이다.

#### READ UNCOMMITTED
가장 낮은 수준. 커밋되지 않은 변경도 읽을 수 있다.
- **Dirty Read**: 커밋 전 데이터를 읽었다가, 롤백되면 그 데이터가 사라지는 정합성 문제.

#### READ COMMITTED
커밋된 데이터만 읽는다(Oracle, PostgreSQL 기본).
- **Non-Repeatable Read**: 한 트랜잭션 안에서 같은 SELECT가 다른 결과를 내는 문제(중간에 다른 트랜잭션이 커밋).

#### REPEATABLE READ
MySQL InnoDB 기본 수준. 한 트랜잭션 안에선 같은 SELECT 결과가 항상 같다. MVCC(Undo 로그)로 Non-Repeatable Read를 막는다.
- **Phantom Read**: 같은 조건의 SELECT인데 다른 트랜잭션이 새 행을 INSERT해 건수가 달라지는 문제. InnoDB는 `SELECT ... FOR UPDATE` 시 Next Key Lock으로 이를 막는다.

#### SERIALIZABLE
모든 트랜잭션을 순차 실행. 부정합은 없지만 대기·데드락·성능 저하가 크다.

> **[적용]** 격리 수준은 **바꾸지 않고 InnoDB 기본(REPEATABLE READ)을 그대로** 썼다. SERIALIZABLE로 올리면 정합성은 확보되지만 전부 직렬화되어 너무 느려서 잘쓰지않는다.
> 대신 정합성이 꼭 필요한 잔액·재고·쿠폰은 **격리 수준이 아니라 락으로**

## MVCC (Multi-Version Concurrency Control)

DB가 트랜잭션 간 동시성을 제어하는 방식으로, 스냅샷을 통해 하나의 데이터에 여러 버전을 동시에 관리한다. 
이때 스냅샷을 뜨는 시점은 격리 수준에 따라 다르다. 
READ COMMITTED는 매 SELECT 문장마다 새로운 스냅샷을 떠서 다른 트랜잭션의 커밋이 반영되고(그래서 Non-Repeatable Read 발생),
REPEATABLE READ는 트랜잭션의 첫 일관된 읽기 시점에 스냅샷을 한 번 떠서 끝까지 고정한다.(그래서 같은 SELECT 결과가 항상 동일)
덕분에 읽기는 락 없이 안정적으로 수행되어 읽기 성능이 좋아지고, 
커밋 전 변경은 보이지 않아 Dirty Read도 막힌다.

## @Transactional

Spring에서 메서드 단위로 트랜잭션 범위를 잡는 어노테이션이다.

### 동작 방식
AOP 프록시가 메서드 호출을 가로채 `TransactionInterceptor`로 트랜잭션을 시작하고, 자동 커밋을 끈 커넥션을 ThreadLocal에 보관한다. 비즈니스 로직 실행 후 예외 여부에 따라 커밋/롤백한다.

### 주의할 점
- **자기 호출 시 미적용**: 프록시를 우회해서 트랜잭션이 안 걸린다.
- **private 메서드 미적용**: 프록시가 호출할 수 없어서.
- **readOnly**: JPA에서 flush·Dirty Checking을 생략해 조회 성능을 최적화한다.

### 전파 속성 (Propagation)
- **REQUIRED(기본)**: 기존 트랜잭션이 있으면 참여, 없으면 새로 시작.
- **REQUIRES_NEW**: 기존 트랜잭션을 잠시 보류(suspend)하고 독립된 새 트랜잭션을 시작한다. 새 트랜잭션이 끝나면 보류됐던 기존 트랜잭션이 다시 이어지며, 안쪽이 롤백돼도 바깥 트랜잭션은 영향받지 않는다.
- **NESTED**: 현재 트랜잭션 안에 중첩 트랜잭션 생성.

> - 여러 도메인 서비스를 한 단위로 묶는 **파사드에 `@Transactional`** 을 걸었다. 예: `OrderFacade.createOrder`(주문→잔액→재고→결제 전부 한 트랜잭션, 중간 실패 시 전부 롤백).
> - 조회 전용 파사드(`ProductFacade.getProducts` 등)에는 **`@Transactional(readOnly = true)`** 로 flush/Dirty Checking을 생략했다.
> - "자기 호출·트랜잭션 경계" 주의를 직접 겪었다. **락 finder(`@Lock`)는 활성 트랜잭션 안에서만** 동작하기 때문에, `@Transactional` 메서드 안에서만 호출하고 테스트 검증 조회는 락 없는 finder로 분리했다.

## DB 설계 - 정규화와 반정규화

### 정규화 (Normalization)
중복을 제거하고 구조를 논리적으로 쪼개는 과정
중복이 사라져 갱신 이상(update anomaly)을 막고 데이터 정합성을 확보한다.
다만 테이블이 분리되어 조회 시 JOIN이 늘어날 수 있다.

### 반정규화 (Denormalization)
조회 시 JOIN을 줄이거나 성능을 위해 일부러 중복을 허용하는 과정. 읽기 성능이 좋아진다.

> **주문 상품 스냅샷**: `OrderProduct`는 상품 ID만 들고 product를 조인하는 대신, **주문 시점의 상품명·단가를 직접 복사**해 들고 있다.
> ```java
> public class OrderProduct {
>     private Long productId;
>     private String productName;   // 주문 시점 이름 스냅샷
>     private long unitPrice;       // 주문 시점 단가 스냅샷
>     private int quantity;
> }
> ```
> 이유는 ① 상품 가격이 바뀌어도 주문 내역은 그때 값으로 보존, ② 주문 조회 시 product 조인 불필요(읽기 성능). 무결성을 일부 포기하고 이력 보존+읽기 성능을 택한 반정규화다.

### 락 경합 컬럼은 별도 테이블로 분리
재고처럼 동시성이 높아 자주 갱신되는 컬럼이 상품 테이블 안에 있으면, 재고를 깎을 때마다 상품 행에 락이 걸려 조회까지 느려진다. 재고를 별도 테이블로 빼면 락 경합이 상품 정보 조회에 영향을 주지 않는다.

> **재고 분리**: 재고를 `stock` 별도 테이블
> ```java
> @Table(name = "stock", indexes = { @Index(name = "idx_product_id", columnList = "product_id") })
> public class Stock { private Long productId; private int quantity; }
> ```
> 덕분에 STEP05의 재고 비관적 락도 이 stock 행에만 걸린다.

## 인덱스

검색 속도를 높이려 데이터를 정렬·식별 가능하게 저장하는 객체로, DB 부하를 줄인다.

### 인덱스 설계
- 중복이 적은(카디널리티 높은) 컬럼
- 삽입·수정이 적은 컬럼
- 조회에 자주 쓰는 컬럼
- 너무 많지 않게 (인덱스도 공간·쓰기 비용)

### 인덱스 종류
- **단일 인덱스**: 1개 컬럼.
- **복합 인덱스**: 여러 컬럼. **카디널리티 높은 컬럼 순**으로 구성. 컬럼 순서와 조회 조건 순서가 안 맞으면 안 탄다.
- **Covering 인덱스**: 쿼리에 필요한 컬럼을 인덱스가 다 포함(인덱스만으로 조회).

### 인덱스 동작 방식
RDB는 B-Tree로 인덱스를 저장한다. PK는 클러스터 인덱스(리프에 실제 데이터), 일반 인덱스는 넌클러스터(리프에 PK를 담아 다시 탐색).

### 인덱스 유의사항
- 컬럼을 가공(`price * 100`)하면 인덱스가 안 탄다 — 값 그대로 비교.
- 범위 조건(`BETWEEN`, `>`) 이후의 컬럼은 인덱스가 끊긴다 — `=` 조건을 앞에.
- `OR`은 Full Scan을 유발할 수 있어 `UNION`이 유리할 때가 있다.

> **인덱스**: 10만 건 더미로 `EXPLAIN ANALYZE` 전후를 비교해 설계했다(상세 수치는 `docs/report/01.DBPerformanceOptimizationReport.md`).
>
> | 테이블 | 인덱스 | 노린 쿼리 |
> |---|---|---|
> | `user_coupon` | (user_id, used_status) 복합 | 보유 쿠폰 중 미사용만 |
> | `user_coupon` | (user_id, coupon_id) 복합 | 사용 가능 쿠폰 단건 |
> | `balance` | (user_id) 단일 | 잔액 조회 |
> | `stock` | (product_id) 단일 | 재고 조회 |
> | `order_product` | (order_id) 단일 | 주문 상품 목록 |
>
> 복합 인덱스는 가이드대로 **카디널리티 높은 `user_id`를 앞**, 값이 2개뿐인 `used_status`를 뒤에 뒀고, 조회도 `user_id`를 먼저 거는 형태라 그대로 탄다(33.3ms → 0.101ms).
>
> 반대 사례도 직접 봤다. **상품 목록(`sell_status='SELLING'`)은 전체의 약 90%라 카디널리티가 낮아, 인덱스가 오히려 느렸다(39.4ms → 68.5ms).** 그래서 이 경우는 인덱스 대신 **커서 기반 페이징**으로 풀었다(0.14ms).

### 인덱스 한계
인덱스는 Read는 빠르게 하지만 CUD에 오버헤드가 있고, 많아질수록 성능이 떨어진다. 무거운 실시간 집계는 인덱스만으로 한계가 있다.

#### Sync Schedule Strategy
실시간 집계 대신 주기적 배치로 통계를 미리 만들어두는 방법. 실시간성과 정합성의 트레이드오프가 있다. 이외에 Materialized View, CQRS, NoSQL Cache 등으로도 한계를 극복한다.

> **인기 상품 배치 전환**: 인기 상품 조회를 처음엔 결제 테이블 복합 인덱스(`payment_status, paid_at`)로 최적화했지만(40.9ms → 20.9ms), 매 요청 집계는 결국 한계가 있다고 판단했다. 그래서 **스케줄러가 매일 "어제 팔린 상품 수량"을 집계해 별도 `product_rank` 테이블에 적재**하고, 조회는 그 테이블만 읽도록 바꿨다(집계 쿼리는 QueryDSL).
> ```java
> @Scheduled(cron = "0 0 1 * * *")
> public void createDailyRank() { /* 어제 결제 상품 집계 → product_rank 적재 */ }
> ```
> 조회를 가볍게 만드는 대신 실시간성을 일부 포기(어제까지)하는, 인기 상품에 합리적인 트레이드오프다.



가장 크게 남은 건, 트랜잭션과 인덱스를 관성으로 붙이는 게 아니라 **"이 쿼리·이 경합을 위해 무엇이 필요한가"를 먼저 보고 설계**하게 된 것이다. 재고를 별도 테이블로 뗀 것, 주문 상품을 스냅샷으로 반정규화한 것, 복합 인덱스 컬럼 순서를 카디널리티로 정한 것, 인덱스가 오히려 독이 되는 경우(낮은 카디널리티)를 커서 페이징·배치로 우회한 것 모두 "왜"를 설명할 수 있게 되었다. 
