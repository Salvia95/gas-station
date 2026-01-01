package io.salvia.gas_station.member

import org.springframework.modulith.ApplicationModule

/**
 * Member 모듈 메타데이터
 *
 * 회원 및 포인트 관리와 관련된 모든 기능을 담당합니다.
 */
@ApplicationModule(
    displayName = "Member Management",
    allowedDependencies = ["shared"]
)
internal class ModuleMetadata
