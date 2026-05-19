

| 결정 | 이유 |
|----|----|
|정적 내부 클래스 (Command.Charge, Info.Balance)  |  한 도메인의 입출력을 한 파일에 모음. 패키지 폴더 폭발 방지.  |
|@Builder + of() 정적 팩토리 둘 다 |  of(userId, amount)는 짧은 케이스용, builder()는 필드 많을 때용.  |
|  private 생성자 + @Builder  | 외부에서 new Charge() 직접 호출 못함. 정적 메서드로만 생성.   |
| 외부 @NoArgsConstructor(PROTECTED)   | BalanceCommand 자체는 인스턴스화 안 됨 (네임스페이스 역할).   |
|  final 필드  | 불변. 한번 만든 DTO는 절대 안 바뀜.   |
| Info가 엔티티를 받아 변환 (of(Balance))   | Service는 엔티티 다루고, 외부엔 Info로 응답. 변환 책임이 Info에.   |

```
DB ─→ Balance (Entity, 도메인 객체)
    ↓ Service에서 변환
BalanceInfo.Balance (도메인 응답 DTO)  ← 여기까지 domain 레이어
    ↓ Facade에서 변환
BalanceResult.Balance (애플리케이션 결과 DTO) ← Step 5
    ↓ Controller에서 변환
BalanceResponse.Balance (HTTP 응답 DTO) ← Step 6
    ↓
JSON으로 사용자에게
```
매 레이어마다 DTO를 새로 만드는 게 번거롭지만 안전. 한 레이어 변경이 다른 레이어를 흔들지 않음.