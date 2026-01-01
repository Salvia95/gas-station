package io.salvia.gas_station.inventory

import org.springframework.modulith.ApplicationModule

/**
 * Inventory 모듈 메타데이터
 *
 * 재고 관리와 관련된 모든 기능을 담당합니다.
 */
@ApplicationModule(
    displayName = "Inventory Management",
    allowedDependencies = ["shared", "station"]
)
internal class ModuleMetadata
