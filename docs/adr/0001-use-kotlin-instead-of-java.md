# ADR-0001: 구현 언어로 Kotlin 채택

## Status

Accepted

## Context

레거시 시스템은 Java 11을 기반으로 하지만, 기존 C 기반 시스템의 구조를 복제하여 만든 시스템으로 C와 Java의 언어적 차이를 적절히 반영하지 못하고 있다.

현재 시스템에서 발생하고 있는 주요 문제점은 다음과 같다:

- **NPE(Null Pointer Exception) 간헐적 발생**: C 언어 스타일의 null 처리 패턴이 Java로 그대로 이전되어, 컴파일 타임에 null 안전성을 보장하지 못하고 있다.
- **빈번한 Full GC 발생**: 불필요한 객체 생성(방어적 복사, Boxing 등)으로 인해 메모리 관리가 적절히 되지 않아 GC 부담이 증가하고 있다.
- **보일러플레이트 코드**: `getter/setter`, `equals/hashCode` 등 반복적인 코드가 많아 생산성이 저하되고 있다.

Java 21+에서 `Record`, `Pattern Matching` 등의 기능이 추가되어 생산성이 개선되었으나, Null Safety 측면에서는 여전히 컴파일 타임 방지가 불가능하다.

## Decision

구현 언어를 Java에서 **Kotlin**으로 변경한다.

Kotlin을 선택한 이유는 다음과 같다:

1. **Null Safety**: nullable/non-nullable 타입 구분을 통해 컴파일 타임에 NPE를 방지할 수 있다.
2. **메모리 효율성**: inline functions, Sequences 등을 통해 불필요한 객체 생성을 줄일 수 있다.
3. **간결한 문법**: data class, extension functions 등을 통해 보일러플레이트를 크게 줄일 수 있다.
4. **JVM 호환성**: Java와 완전한 상호 운용성을 가지고 있어 기존 시스템의 중단 없이 점진적 전환이 가능하다.
5. **비동기 처리**: Coroutines를 통해 Java의 CompletableFuture나 Flow에 비해 훨씬 직관적인 비동기 프로그래밍이 가능하다.

## Consequences

### 긍정적 영향

- NPE 발생 가능성이 컴파일 타임에 크게 감소한다.
- 코드량이 줄어들어 가독성과 유지보수성이 향상된다.
- Coroutines를 활용한 직관적인 비동기 처리가 가능해진다.
- Spring Framework와의 공식 지원으로 생태계 활용에 문제가 없다.

### 부정적 영향

- 기존 Java 개발자들의 Kotlin 학습 비용이 발생한다.
- Java 라이브러리 사용 시 nullable 타입 처리에 주의가 필요하다.
- IDE 지원이 Java에 비해 상대적으로 무거울 수 있다.

### 중립적 영향

- 빌드 시간이 Java에 비해 다소 증가할 수 있으나, 체감할 정도는 아니다.
- 기존 Java 코드와의 혼용이 가능하여 점진적 마이그레이션이 가능하다.