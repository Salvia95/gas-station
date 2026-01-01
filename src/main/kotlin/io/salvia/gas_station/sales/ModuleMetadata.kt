package io.salvia.gas_station.sales

import org.springframework.modulith.ApplicationModule

/**
 * Sales 모듈 메타데이터
 *
 * 판매 및 정산 처리와 관련된 모든 기능을 담당합니다.
 */
@ApplicationModule(
    displayName = "Sales Management",
    allowedDependencies = ["shared", "station", "inventory", "member"]
)
internal class ModuleMetadata
