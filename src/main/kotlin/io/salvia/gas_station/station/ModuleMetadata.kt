package io.salvia.gas_station.station

import org.springframework.modulith.ApplicationModule

/**
 * Station 모듈 메타데이터
 *
 * 이 클래스는 주유소 관리 모듈의 메타데이터를 정의합니다.
 * - 모듈 이름: Station Management
 * - 허용된 의존성: shared 모듈만
 *
 * ## 모듈 책임
 * - 주유소 정보 관리
 * - 주유기(Pump) 관리
 * - 유류 탱크(Fuel Tank) 관리
 * - 홈로리(Home Lorry) 관리
 * - 점검(Inspection) 관리
 *
 * ## Public API
 * - [Station]: 주유소 도메인 모델
 * - [StationId]: 주유소 ID Value Object
 * - [StationService]: 주유소 서비스 인터페이스
 *
 * ## Internal Implementation
 * `internal` 패키지의 모든 구현은 모듈 외부에서 접근할 수 없습니다.
 */
@ApplicationModule(
    displayName = "Station Management",
    allowedDependencies = ["shared"]
)
internal class ModuleMetadata
