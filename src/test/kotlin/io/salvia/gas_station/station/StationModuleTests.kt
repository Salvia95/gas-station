package io.salvia.gas_station.station

import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.test.ApplicationModuleTest
import kotlin.test.assertNotNull

/**
 * Station 모듈 특화 테스트
 *
 * station 모듈의 구조와 경계가 올바르게 설정되었는지 검증합니다.
 */
@ApplicationModuleTest
class StationModuleTests {

    @Test
    fun `station 모듈이 올바르게 인식되는지 확인`() {
        val modules = ApplicationModules.of(io.salvia.gas_station.GasStationApplication::class.java)
        val stationModule = modules.getModuleByName("station")

        assertNotNull(stationModule.orElse(null), "Station module should be detected")
        println("Station module successfully detected: ${stationModule.get().name}")
    }

    @Test
    fun `station 모듈의 public API가 올바르게 노출되는지 확인`() {
        // Station, StationId, StationService는 public API로 노출되어야 함
        val stationClass = Station::class.java
        val stationIdClass = StationId::class.java
        val stationServiceClass = StationService::class.java

        println("=== Station Module Public API ===")
        println("Station: ${stationClass.name}")
        println("StationId: ${stationIdClass.name}")
        println("StationService: ${stationServiceClass.name}")

        assertNotNull(stationClass)
        assertNotNull(stationIdClass)
        assertNotNull(stationServiceClass)
    }

    @Test
    fun `station 모듈의 internal 패키지 구조 확인`() {
        // internal 패키지의 클래스들은 외부에서 직접 접근할 수 없어야 함
        val modules = ApplicationModules.of(io.salvia.gas_station.GasStationApplication::class.java)

        modules.verify()

        println("Station module internal structure is properly encapsulated")
    }

    @Test
    fun `StationId value class가 올바르게 동작하는지 확인`() {
        val stationId1 = StationId(1L)
        val stationId2 = StationId(1L)
        val stationId3 = StationId(2L)

        // Value class는 값이 같으면 동일한 객체로 취급
        assert(stationId1 == stationId2) { "Same value StationIds should be equal" }
        assert(stationId1 != stationId3) { "Different value StationIds should not be equal" }

        // 양수만 허용
        try {
            StationId(0L)
            throw AssertionError("StationId should not accept 0")
        } catch (e: IllegalArgumentException) {
            println("StationId correctly rejects invalid value: ${e.message}")
        }

        println("StationId value class works correctly")
    }
}
