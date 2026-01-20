# ADR-0011: 모듈 간 직접 호출 금지 및 이벤트 기반 통신 강제

## Status

Accepted

## Context

[ADR-0005](0005-use-modular-monolith-architecture.md)에서 모듈러 모놀리스 아키텍처를 채택하고, [ADR-0007](0007-use-async-event-for-inter-module-communication.md)에서 모듈 간 비동기 이벤트 통신을 결정했다.

원칙적으로는 모듈 간 비동기 이벤트 통신을 사용해야 하나, 강한 결합을 가진 비즈니스 로직은 하나의 트랜잭션을 통해 무결성이 보장되어야 하므로, 동기 호출이 필수적이다.

다만, 이는 모듈간 독립성을 크게 해칠 수 있으며, 본 프로젝트의 대원칙을 깰 수 있기에 이에대한 금지와 대안이 필요한 상황이다.

Spring Modulith는 모듈 간 의존성 관리를 위해 다음과 같은 기능을 제공한다.

1. **Exposed Interface**: `@ApplicationModulePackage`나 패키지 구조를 통해 외부에 노출할 API를 명시적으로 지정
2. **Allowed Dependencies**: `@ApplicationModule(allowedDependencies = ...)`를 통해 특정 모듈만 의존하도록 제한
3. **Architecture Verification**: `ApplicationModules.verify()`를 통해 모듈 경계 위반을 테스트 시점에 탐지

이처럼 Spring Modulith는 **모듈 간 직접 호출을 한정적으로 허용**하는 방식을 지원한다. 그러나 다음과 같은 이유로 직접 호출 방식에는 근본적인 한계가 있다.

- **강결합 유발**: 인터페이스를 통한 추상화에도 불구하고, 호출자는 피호출자의 존재를 알아야 한다.
- **순환 의존성 위험**: 모듈 간 양방향 통신이 필요한 경우 순환 참조가 발생할 수 있다.
- **MSA 전환 장벽**: 직접 호출로 결합된 모듈은 향후 서비스 분리 시 대규모 수정이 필요하다.
- **테스트 복잡성**: 모듈 단위 테스트 시 의존 모듈을 모킹해야 한다.

반면 Spring Modulith의 **Event Publication** 메커니즘은 다음과 같은 특징을 가진다.

- **동일 트랜잭션 내 동기 처리**: `@ApplicationModuleListener`를 통해 인메모리 이벤트 버스로 동기 처리되어, 트랜잭션 내 일관성을 보장하면서도 느슨한 결합을 유지
- **트랜잭션 후 비동기 처리**: `@Async`와 `@TransactionalEventListener(phase = AFTER_COMMIT)`을 통해 Transactional Outbox 패턴으로 처리되어, 분산 환경에서도 이벤트 전달을 보장
- **Event Publication Repository**: 발행된 이벤트가 DB에 저장되어, 애플리케이션 재시작 후에도 미처리 이벤트를 재처리 가능

## Decision

**모듈 간 직접 메서드 호출을 전면 금지**하고, 모든 모듈 간 통신은 **Spring Modulith의 Event Publication**을 통해서만 이루어지도록 강제한다.

구체적인 규칙은 다음과 같다.

### 1. 모듈 간 통신 규칙

- 이벤트 발행/구독을 통한 통신만을 허용한다.
- 다른 모듈의 Use case(Service)를 직접 호출하는 것을 금지한다.
- 다른 모듈의 내부 패키지(Intrastructure 등)를 직접 호출하는 것을 금지한다.

### 2. 이벤트 처리 전략

| 상황 | 처리 방식 | 구현 |
|------|----------|------|
| 즉시 일관성 필요 | 동기 이벤트 | `@ApplicationModuleListener` |
| 최종 일관성 허용 | 비동기 이벤트 | `@Async` + `@TransactionalEventListener(AFTER_COMMIT)` |
| 외부 시스템 연동 | Outbox 패턴 | Event Publication Repository 활용 |

> Event Publication Repository에 적재 후 실패된 이벤트는 자동으로 재시도되나, 이벤트의 멱등성 보장 책임 등은 개발자에게 있음을 유의한다.

### 3. 예외 사항

다음의 경우에만 제한적으로 모듈 간 참조를 허용한다.

- **공유 커널(Shared Kernel)**: 여러 모듈에서 공통으로 사용하는 Value Object, Enum, Exception
- **모듈 공개 API 조회**: 읽기 전용 조회가 필요하고, 이벤트로 처리 시 과도한 복잡성이 발생하는 경우 (단, `@Exposed` 인터페이스만 허용)

> 이벤트로 처리 하는 것이 과도하게 복잡할 경우 이벤트를 조금 더 작게 설계할 순 없는지, 별도의 모듈로 표현할 수 없는지 등을 먼저 고려한다.

### 4. 아키텍처 검증

모든 PR에서 모듈 경계 검증 테스트가 통과해야 머지가 가능하도록 CI에 통합한다.

```kotlin
@Test
fun `모듈 간 직접 의존성이 없어야 한다`() {
    ApplicationModules.of(Application::class.java)
        .verify()
}
```

## Consequences

### 긍정적 영향

- 각 모듈이 다른 모듈의 존재를 알지 못해도 동작할 수 있다.
- 이벤트 기반 통신은 그대로 메시지 큐로 대체 가능하여, 서비스 분리 시 수정이 최소화된다.
- 한 모듈의 장애가 이벤트 큐에서 격리되어 다른 모듈로 전파되지 않는다.
- 모듈 단위 테스트 시 이벤트만 발행하면 되므로 의존 모듈 모킹이 불필요하다.
- 동기 이벤트는 동일 트랜잭션에서 처리되어 데이터 일관성이 보장된다.
- Outbox 패턴을 통해 비동기 이벤트도 최소 1회 전달(at-least-once)이 보장된다.

### 부정적 영향

- 단순한 메서드 호출 대신 이벤트를 설계해야 하므로 초기 개발 비용이 증가한다.
- 이벤트 흐름을 추적하기 위해 별도의 로깅/트레이싱 전략이 필요하다.
- 비동기 이벤트 사용 시 즉시 일관성을 포기해야 하는 경우가 있다.
- 이벤트 구조 변경 시 발행자와 구독자 간 호환성 관리가 필요하다.

### 중립적 영향

- Spring Modulith의 Event Publication Repository가 DB에 이벤트를 저장하므로, 테이블 용량 관리가 필요하다.
- 이벤트 명명 규칙, 버전 관리 등 팀 컨벤션 수립이 필요하다.
- 동기/비동기 이벤트 선택 기준에 대한 가이드라인 문서화가 필요하다.