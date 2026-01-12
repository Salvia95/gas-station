package io.salvia.gas_station.station.internal.persistence.entities

import io.salvia.gas_station.shared.enums.FuelType
import io.salvia.gas_station.station.internal.domain.EquipmentStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal

@DisplayName("PumpNozzleJPAEntity 단위 테스트")
class PumpNozzleJPAEntityTest {

    private lateinit var nozzle: PumpNozzleJPAEntity
    private lateinit var gasolineTank: FuelTankJPAEntity
    private lateinit var diselTank: FuelTankJPAEntity

    @BeforeEach
    fun setUp() {
        nozzle = PumpNozzleJPAEntity(
            nozzleNumber = 1,
            fuelType = FuelType.GASOLINE_REGULAR
        )

        gasolineTank = FuelTankJPAEntity(
            tankNumber = "T-GAS-001",
            fuelType = FuelType.GASOLINE_REGULAR,
            capacity = BigDecimal("50000.00")
        )

        diselTank = FuelTankJPAEntity(
            tankNumber = "T-DISEL-001",
            fuelType = FuelType.DISEL,
            capacity = BigDecimal("40000.00")
        )
    }

    @Nested
    @DisplayName("탱크 연결 테스트")
    inner class ConnectTankTest {

        @Test
        @DisplayName("유종이 일치하는 탱크를 연결할 수 있다")
        fun connectTankWithMatchingFuelType() {
            // when
            nozzle.connectTank(gasolineTank)

            // then
            assertEquals(gasolineTank, nozzle.connectedTank)
        }

        @Test
        @DisplayName("유종이 다른 탱크 연결 시 예외가 발생한다")
        fun connectTankWithDifferentFuelType() {
            // when & then
            val exception = assertThrows<IllegalArgumentException> {
                nozzle.connectTank(diselTank)
            }

            assertTrue(exception.message!!.contains("노즐의 유종"))
            assertTrue(exception.message!!.contains("탱크의 유종"))
            assertTrue(exception.message!!.contains("일치하지 않습니다"))
            assertTrue(exception.message!!.contains(FuelType.GASOLINE_REGULAR.displayName))
            assertTrue(exception.message!!.contains(FuelType.DISEL.displayName))
        }

        @Test
        @DisplayName("이미 연결된 탱크를 다른 탱크로 교체할 수 있다")
        fun replaceTank() {
            // given
            val firstTank = FuelTankJPAEntity(
                tankNumber = "T-GAS-001",
                fuelType = FuelType.GASOLINE_REGULAR,
                capacity = BigDecimal("50000.00")
            )
            val secondTank = FuelTankJPAEntity(
                tankNumber = "T-GAS-002",
                fuelType = FuelType.GASOLINE_REGULAR,
                capacity = BigDecimal("60000.00")
            )

            nozzle.connectTank(firstTank)

            // when
            nozzle.connectTank(secondTank)

            // then
            assertEquals(secondTank, nozzle.connectedTank)
        }
    }

    @Nested
    @DisplayName("탱크 연결 해제 테스트")
    inner class DisconnectTankTest {

        @Test
        @DisplayName("연결된 탱크를 해제할 수 있다")
        fun disconnectTank() {
            // given
            nozzle.connectTank(gasolineTank)

            // when
            nozzle.disconnectTank()

            // then
            assertNull(nozzle.connectedTank)
        }

        @Test
        @DisplayName("연결되지 않은 상태에서 해제해도 문제없다")
        fun disconnectWhenNotConnected() {
            // when & then
            assertDoesNotThrow {
                nozzle.disconnectTank()
            }
            assertNull(nozzle.connectedTank)
        }
    }

    @Nested
    @DisplayName("주유 가능 여부 테스트")
    inner class CanDispenseTest {

        @Test
        @DisplayName("탱크가 연결되지 않으면 주유할 수 없다")
        fun cannotDispenseWithoutTank() {
            // when & then
            assertFalse(nozzle.canDispense())
        }

        @Test
        @DisplayName("탱크가 ACTIVE 상태이고 안전 재고 이상이면 주유할 수 있다")
        fun canDispenseWhenTankIsActiveAndHasEnoughStock() {
            // given
            gasolineTank.updateSafetyStock(BigDecimal("5000.00"))
            gasolineTank.fill(BigDecimal("20000.00")) // 안전 재고 이상
            gasolineTank.changeStatus(EquipmentStatus.ACTIVE)
            nozzle.connectTank(gasolineTank)

            // when & then
            assertTrue(nozzle.canDispense())
        }

        @Test
        @DisplayName("탱크가 ACTIVE가 아니면 주유할 수 없다")
        fun cannotDispenseWhenTankIsNotActive() {
            // given
            gasolineTank.fill(BigDecimal("20000.00"))
            gasolineTank.changeStatus(EquipmentStatus.MAINTENANCE) // 정비 중
            nozzle.connectTank(gasolineTank)

            // when & then
            assertFalse(nozzle.canDispense())
        }

        @Test
        @DisplayName("재고가 안전 재고 이하면 주유할 수 없다")
        fun cannotDispenseWhenStockBelowSafety() {
            // given
            gasolineTank.updateSafetyStock(BigDecimal("10000.00"))
            gasolineTank.fill(BigDecimal("8000.00")) // 안전 재고 미만
            gasolineTank.changeStatus(EquipmentStatus.ACTIVE)
            nozzle.connectTank(gasolineTank)

            // when & then
            assertFalse(nozzle.canDispense())
        }

        @Test
        @DisplayName("재고가 정확히 안전 재고와 같으면 주유할 수 없다")
        fun cannotDispenseWhenStockEqualsToSafety() {
            // given
            gasolineTank.updateSafetyStock(BigDecimal("10000.00"))
            gasolineTank.fill(BigDecimal("10000.00")) // 안전 재고와 동일
            gasolineTank.changeStatus(EquipmentStatus.ACTIVE)
            nozzle.connectTank(gasolineTank)

            // when & then
            assertFalse(nozzle.canDispense())
        }

        @Test
        @DisplayName("탱크 상태가 INSPECTION이면 주유할 수 없다")
        fun cannotDispenseWhenTankIsInspection() {
            // given
            gasolineTank.fill(BigDecimal("30000.00"))
            gasolineTank.changeStatus(EquipmentStatus.INSPECTION)
            nozzle.connectTank(gasolineTank)

            // when & then
            assertFalse(nozzle.canDispense())
        }

        @Test
        @DisplayName("탱크 상태가 INACTIVE이면 주유할 수 없다")
        fun cannotDispenseWhenTankIsInactive() {
            // given
            gasolineTank.fill(BigDecimal("30000.00"))
            gasolineTank.changeStatus(EquipmentStatus.INACTIVE)
            nozzle.connectTank(gasolineTank)

            // when & then
            assertFalse(nozzle.canDispense())
        }
    }

    @Nested
    @DisplayName("초기 상태 테스트")
    inner class InitialStateTest {

        @Test
        @DisplayName("생성 시 탱크는 연결되지 않은 상태이다")
        fun initialConnectedTankIsNull() {
            // 노즐은 생성 시 @field:NotNull 검증이 있지만,
            // 실제로는 연결 전까지 null 상태
            assertNull(nozzle.connectedTank)
        }

        @Test
        @DisplayName("노즐 번호와 유종이 올바르게 설정된다")
        fun initialPropertiesAreSet() {
            assertEquals(1, nozzle.nozzleNumber)
            assertEquals(FuelType.GASOLINE_REGULAR, nozzle.fuelType)
        }
    }

    @Nested
    @DisplayName("다양한 유종 테스트")
    inner class VariousFuelTypesTest {

        @Test
        @DisplayName("고급 휘발유 노즐은 고급 휘발유 탱크만 연결할 수 있다")
        fun premiumGasolineNozzle() {
            // given
            val premiumNozzle = PumpNozzleJPAEntity(
                nozzleNumber = 2,
                fuelType = FuelType.GASOLINE_PREMIUM
            )
            val premiumTank = FuelTankJPAEntity(
                tankNumber = "T-PREM-001",
                fuelType = FuelType.GASOLINE_PREMIUM,
                capacity = BigDecimal("40000.00")
            )

            // when
            premiumNozzle.connectTank(premiumTank)

            // then
            assertEquals(premiumTank, premiumNozzle.connectedTank)
        }

        @Test
        @DisplayName("LPG 노즐은 LPG 탱크만 연결할 수 있다")
        fun lpgNozzle() {
            // given
            val lpgNozzle = PumpNozzleJPAEntity(
                nozzleNumber = 3,
                fuelType = FuelType.LPG
            )
            val lpgTank = FuelTankJPAEntity(
                tankNumber = "T-LPG-001",
                fuelType = FuelType.LPG,
                capacity = BigDecimal("30000.00")
            )

            // when
            lpgNozzle.connectTank(lpgTank)

            // then
            assertEquals(lpgTank, lpgNozzle.connectedTank)
        }

        @Test
        @DisplayName("경유 노즐은 휘발유 탱크를 연결할 수 없다")
        fun diselNozzleCannotConnectGasolineTank() {
            // given
            val diselNozzle = PumpNozzleJPAEntity(
                nozzleNumber = 4,
                fuelType = FuelType.DISEL
            )

            // when & then
            assertThrows<IllegalArgumentException> {
                diselNozzle.connectTank(gasolineTank)
            }
        }
    }

    @Nested
    @DisplayName("복합 시나리오 테스트")
    inner class ComplexScenarioTest {

        @Test
        @DisplayName("탱크 재고가 소진되면 주유 가능 상태에서 불가 상태로 변경된다")
        fun transitionFromDispensableToNonDispensable() {
            // given: 초기에는 주유 가능
            gasolineTank.updateSafetyStock(BigDecimal("5000.00"))
            gasolineTank.fill(BigDecimal("10000.00"))
            gasolineTank.changeStatus(EquipmentStatus.ACTIVE)
            nozzle.connectTank(gasolineTank)
            assertTrue(nozzle.canDispense())

            // when: 재고 소비로 안전 재고 이하로 떨어짐
            gasolineTank.consume(BigDecimal("6000.00")) // 남은 재고: 4000L < 안전 재고 5000L

            // then: 주유 불가
            assertFalse(nozzle.canDispense())
        }

        @Test
        @DisplayName("탱크 충전 후 다시 주유 가능 상태가 된다")
        fun recoverDispensabilityAfterRefill() {
            // given: 초기 주유 불가 상태
            gasolineTank.updateSafetyStock(BigDecimal("10000.00"))
            gasolineTank.fill(BigDecimal("8000.00"))
            gasolineTank.changeStatus(EquipmentStatus.ACTIVE)
            nozzle.connectTank(gasolineTank)
            assertFalse(nozzle.canDispense())

            // when: 충전으로 안전 재고 이상 확보
            gasolineTank.fill(BigDecimal("10000.00")) // 총 18000L > 안전 재고 10000L

            // then: 주유 가능
            assertTrue(nozzle.canDispense())
        }

        @Test
        @DisplayName("탱크 교체 시 주유 가능 여부가 새 탱크 기준으로 판단된다")
        fun dispensabilityChangesAfterTankReplacement() {
            // given: 첫 번째 탱크는 주유 가능
            val firstTank = FuelTankJPAEntity(
                tankNumber = "T-001",
                fuelType = FuelType.GASOLINE_REGULAR,
                capacity = BigDecimal("50000.00")
            )
            firstTank.fill(BigDecimal("30000.00"))
            firstTank.changeStatus(EquipmentStatus.ACTIVE)
            nozzle.connectTank(firstTank)
            assertTrue(nozzle.canDispense())

            // when: 두 번째 탱크로 교체 (주유 불가 상태)
            val secondTank = FuelTankJPAEntity(
                tankNumber = "T-002",
                fuelType = FuelType.GASOLINE_REGULAR,
                capacity = BigDecimal("50000.00")
            )
            secondTank.updateSafetyStock(BigDecimal("10000.00"))
            secondTank.fill(BigDecimal("5000.00")) // 안전 재고 미만
            secondTank.changeStatus(EquipmentStatus.ACTIVE)
            nozzle.connectTank(secondTank)

            // then: 주유 불가
            assertFalse(nozzle.canDispense())
        }
    }
}
