package io.salvia.gas_station

import org.junit.jupiter.api.Test
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.docs.Documenter

/**
 * Spring Modulith 모듈 구조 검증 테스트
 *
 * 이 테스트는 다음을 검증합니다:
 * 1. 모듈이 올바르게 정의되었는지
 * 2. 모듈 간 의존성이 허용된 것만 사용하는지
 * 3. internal 패키지가 외부에서 접근되지 않는지
 */
class ModularityTests {

    private val modules = ApplicationModules.of(GasStationApplication::class.java)

    @Test
    fun `모듈 구조가 올바르게 정의되어 있는지 검증`() {
        // 모듈 구조 검증
        modules.verify()
    }

    @Test
    fun `각 모듈이 올바르게 인식되는지 확인`() {
        // 모듈 목록 출력
        println("=== Detected Modules ===")
        modules.forEach { module ->
            println("Module: ${module.name}")
            println("  Display Name: ${module.displayName}")
            println("  Base Package: ${module.basePackage}")
            println("  Named Interfaces: ${module.namedInterfaces}")
            println()
        }

        // 최소한 station 모듈은 존재해야 함
        assert(modules.getModuleByName("station").isPresent) {
            "Station module should be detected"
        }
    }

    @Test
    fun `모듈 간 의존성이 정의된 대로 지켜지는지 검증`() {
        // 모듈 의존성 검증 (package-info.kt의 allowedDependencies 확인)
        modules.verify()

        println("=== Module Dependencies Verified ===")
        println("All module dependencies are properly configured!")
    }

    @Test
    fun `internal 패키지가 모듈 외부에서 접근되지 않는지 검증`() {
        // Spring Modulith는 자동으로 internal 패키지의 외부 접근을 차단
        // verify()가 성공하면 internal 패키지 규칙도 준수됨
        modules.verify()

        println("=== Module Internals ===")
        modules.forEach { module ->
            println("Module: ${module.name}")
            println("  Exposed packages: ${module.namedInterfaces}")
            println()
        }
    }

    @Test
    fun `모듈 문서 생성`() {
        // 모듈 구조 문서화 (선택사항)
        Documenter(modules)
            .writeModulesAsPlantUml()
            .writeIndividualModulesAsPlantUml()

        println("Module documentation has been generated in 'target/spring-modulith-docs'")
    }

    @Test
    fun `station 모듈의 구조 확인`() {
        val stationModule = modules.getModuleByName("station").orElseThrow()

        println("=== Station Module Details ===")
        println("Name: ${stationModule.name}")
        println("Display Name: ${stationModule.displayName}")
        println("Base Package: ${stationModule.basePackage}")
        println("Station module structure is valid!")
    }
}
