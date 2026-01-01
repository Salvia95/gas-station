package io.salvia.gas_station.inspection

import org.springframework.modulith.ApplicationModule

/**
 * Inspection 모듈 메타데이터
 *
 * 점검 관리와 관련된 모든 기능을 담당합니다.
 */
@ApplicationModule(
    displayName = "Inspection Management",
    allowedDependencies = ["shared", "station", "asset"]
)
internal class ModuleMetadata
