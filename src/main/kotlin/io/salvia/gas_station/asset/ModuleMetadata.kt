package io.salvia.gas_station.asset

import org.springframework.modulith.ApplicationModule

/**
 * Asset 모듈 메타데이터
 *
 * 고정자산 관리와 관련된 모든 기능을 담당합니다.
 */
@ApplicationModule(
    displayName = "Asset Management",
    allowedDependencies = ["shared", "station"]
)
internal class ModuleMetadata
