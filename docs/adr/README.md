# Architecture Decision Records

이 디렉토리는 주유소 관리 시스템의 아키텍처 결정 기록(ADR)을 담고 있습니다.

## ADR이란?

Architecture Decision Record(ADR)는 아키텍처적으로 중요한 결정을 기록하는 문서입니다. 각 ADR은 단일 결정과 그 맥락, 결과를 설명합니다.

이 프로젝트의 ADR은 [Michael Nygard의 ADR 템플릿](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions)을 따릅니다.

## ADR 템플릿 구조

- **Title**: 결정에 대한 간결한 설명
- **Status**: 제안됨(Proposed), 승인됨(Accepted), 폐기됨(Deprecated), 대체됨(Superseded)
- **Context**: 결정이 필요한 배경과 상황
- **Decision**: 채택한 결정과 그 이유
- **Consequences**: 결정으로 인한 긍정적, 부정적, 중립적 영향

## ADR 목록

| ADR | 제목 | 상태 | 카테고리 |
|-----|------|------|----------|
| [ADR-0001](0001-use-kotlin-instead-of-java.md) | 구현 언어로 Kotlin 채택 | Accepted | 기술 스택 |
| [ADR-0002](0002-use-spring-boot-instead-of-bx-framework.md) | 웹프레임워크로 Spring Boot 채택 | Accepted | 기술 스택 |
| [ADR-0003](0003-use-postgresql-instead-of-oracle.md) | 관계형 데이터베이스로 PostgreSQL 채택 | Accepted | 기술 스택 |
| [ADR-0004](0004-use-hibernate-querydsl-instead-of-mybatis.md) | 영속성 프레임워크로 Hibernate + QueryDSL 채택 | Accepted | 기술 스택 |
| [ADR-0005](0005-use-modular-monolith-architecture.md) | 시스템 아키텍처로 모듈러 모놀리스 채택 | Accepted | 아키텍처 |
| [ADR-0006](0006-use-restful-api-instead-of-json-rpc.md) | 서비스 호출 방식으로 RESTful API 채택 | Accepted | 아키텍처 |
| [ADR-0007](0007-use-async-event-for-inter-module-communication.md) | 서비스 간 통신으로 비동기 이벤트 채택 | Accepted | 아키텍처 |
| [ADR-0008](0008-use-surrogate-key-and-fk-based-relation.md) | 데이터 관계 설계로 FK 기반 정규화 채택 | Accepted | 데이터 |
| [ADR-0009](0009-apply-cqrs-pattern-for-data-schema.md) | 데이터 스키마에 CQRS 패턴 적용 | Accepted | 데이터 |
| [ADR-0010](0010-use-flyway-for-schema-version-control.md) | Flyway를 이용한 스키마 버전 관리 | Accepted | 데이터 |
| [ADR-0011](0011-prohibit-direct-module-invocation.md) | 모듈 간 직접 호출 금지 및 이벤트 통신 강제 | Accepted | 아키텍처 |

## 카테고리별 분류

### 기술 스택 (Technology Stack)
레거시 시스템에서 현대적 기술 스택으로의 전환 결정

- ADR-0001: Java → Kotlin
- ADR-0002: BX Framework → Spring Boot
- ADR-0003: Oracle → PostgreSQL
- ADR-0004: MyBatis → Hibernate + QueryDSL

### 아키텍처 (Architecture)
시스템 구조와 통신 방식에 대한 결정

- ADR-0005: 모놀리스 → 모듈러 모놀리스
- ADR-0006: JSON-RPC → RESTful API
- ADR-0007: 동기 호출 → 비동기 이벤트
- ADR-0011: 모듈 간 직접 호출 금지 및 이벤트 통신 강제

### 데이터 (Data)
데이터 모델링과 스키마 설계에 대한 결정

- ADR-0008: N:N PK 기반 → FK 기반 정규화
- ADR-0009: 단일 DB → CQRS 적용
- ADR-0010: 수동 스키마 관리 → Flyway 버전 관리

## 새 ADR 작성 방법

1. 다음 번호의 ADR 파일을 생성합니다: `NNNN-title-with-dashes.md`
2. 위의 템플릿 구조를 따라 작성합니다.
3. 이 README의 ADR 목록에 추가합니다.
4. Pull Request를 통해 리뷰를 받습니다.

## 참고 자료

- [Documenting Architecture Decisions - Michael Nygard](https://cognitect.com/blog/2011/11/15/documenting-architecture-decisions)
- [ADR GitHub Organization](https://adr.github.io/)
- [Architecture Decision Record Examples](https://github.com/joelparkerhenderson/architecture-decision-record)