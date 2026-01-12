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
import java.time.LocalDate

@DisplayName("FuelTankJPAEntity 단위 테스트")
class FuelTankJPAEntityTest {

    private lateinit var tank: FuelTankJPAEntity

    @BeforeEach
    fun setUp() {
        tank = FuelTankJPAEntity(
            tankNumber = "T-001",
            fuelType = FuelType.GASOLINE_REGULAR,
            capacity = BigDecimal("50000.00")
        )
    }

    @Nested
    @DisplayName("충전(fill) 테스트")
    inner class FillTest {

        @Test
        @DisplayName("정상적으로 충전할 수 있다")
        fun fillNormally() {
            // given
            val fillAmount = BigDecimal("10000.00")

            // when
            val actualFilled = tank.fill(fillAmount)

            // then
            assertEquals(fillAmount, actualFilled)
            assertEquals(fillAmount, tank.currentAmount)
        }

        @Test
        @DisplayName("용량을 초과하는 양을 충전하면 가능한 만큼만 충전된다")
        fun fillExceedsCapacity() {
            // given
            tank.fill(BigDecimal("40000.00")) // 현재 재고: 40000L
            val excessAmount = BigDecimal("20000.00") // 요청: 20000L (총 60000L > 용량 50000L)

            // when
            val actualFilled = tank.fill(excessAmount)

            // then
            assertEquals(BigDecimal("10000.00"), actualFilled) // 실제로는 10000L만 충전
            assertEquals(BigDecimal("50000.00"), tank.currentAmount) // 용량 가득 참
        }

        @Test
        @DisplayName("이미 가득 찬 탱크에 충전하면 0이 반환된다")
        fun fillWhenFull() {
            // given
            tank.fill(BigDecimal("50000.00")) // 가득 채움

            // when
            val actualFilled = tank.fill(BigDecimal("1000.00"))

            // then
            assertEquals(0, actualFilled.compareTo(BigDecimal.ZERO))
            assertEquals(0, tank.currentAmount.compareTo(BigDecimal("50000.00")))
        }

        @Test
        @DisplayName("음수 또는 0 충전 시 예외가 발생한다")
        fun fillWithNegativeOrZeroAmount() {
            // 0 충전 시도
            val exception1 = assertThrows<IllegalArgumentException> {
                tank.fill(BigDecimal.ZERO)
            }
            assertEquals("충전량은 0보다 커야 합니다.", exception1.message)

            // 음수 충전 시도
            val exception2 = assertThrows<IllegalArgumentException> {
                tank.fill(BigDecimal("-1000.00"))
            }
            assertEquals("충전량은 0보다 커야 합니다.", exception2.message)
        }

        @Test
        @DisplayName("탱크가 ACTIVE 상태가 아니면 충전할 수 없다")
        fun fillWhenNotActive() {
            // given
            tank.changeStatus(EquipmentStatus.MAINTENANCE)

            // when & then
            val exception = assertThrows<IllegalArgumentException> {
                tank.fill(BigDecimal("10000.00"))
            }
            assertEquals("탱크가 정상 상태가 아닙니다.", exception.message)
        }
    }

    @Nested
    @DisplayName("소비(consume) 테스트")
    inner class ConsumeTest {

        @BeforeEach
        fun setUpTankWithStock() {
            tank.fill(BigDecimal("30000.00")) // 초기 재고 30000L
        }

        @Test
        @DisplayName("정상적으로 소비할 수 있다")
        fun consumeNormally() {
            // given
            val consumeAmount = BigDecimal("5000.00")

            // when
            tank.consume(consumeAmount)

            // then
            assertEquals(BigDecimal("25000.00"), tank.currentAmount)
        }

        @Test
        @DisplayName("재고가 부족하면 예외가 발생한다")
        fun consumeExceedsStock() {
            // given
            val excessAmount = BigDecimal("35000.00") // 재고 30000L보다 많음

            // when & then
            val exception = assertThrows<IllegalArgumentException> {
                tank.consume(excessAmount)
            }
            assertTrue(exception.message!!.contains("[재고 부족]"))
            assertTrue(exception.message!!.contains("재고: 30000"))
            assertTrue(exception.message!!.contains("요청: 35000"))
        }

        @Test
        @DisplayName("음수 또는 0 소비 시 예외가 발생한다")
        fun consumeWithNegativeOrZeroAmount() {
            // 0 소비 시도
            val exception1 = assertThrows<IllegalArgumentException> {
                tank.consume(BigDecimal.ZERO)
            }
            assertEquals("소비량은 0보다 커야 합니다.", exception1.message)

            // 음수 소비 시도
            val exception2 = assertThrows<IllegalArgumentException> {
                tank.consume(BigDecimal("-1000.00"))
            }
            assertEquals("소비량은 0보다 커야 합니다.", exception2.message)
        }

        @Test
        @DisplayName("재고를 정확히 소진할 수 있다")
        fun consumeExactStock() {
            // when
            tank.consume(BigDecimal("30000.00"))

            // then
            assertEquals(0, tank.currentAmount.compareTo(BigDecimal.ZERO))
        }
    }

    @Nested
    @DisplayName("안전 재고 관리 테스트")
    inner class SafetyStockTest {

        @Test
        @DisplayName("안전 재고를 정상적으로 설정할 수 있다")
        fun updateSafetyStockNormally() {
            // given
            val safetyStock = BigDecimal("5000.00")

            // when
            tank.updateSafetyStock(safetyStock)

            // then
            assertEquals(safetyStock, tank.safetyStock)
        }

        @Test
        @DisplayName("안전 재고를 0으로 설정할 수 있다")
        fun updateSafetyStockToZero() {
            // when
            tank.updateSafetyStock(BigDecimal.ZERO)

            // then
            assertEquals(BigDecimal.ZERO, tank.safetyStock)
        }

        @Test
        @DisplayName("음수 안전 재고 설정 시 예외가 발생한다")
        fun updateSafetyStockWithNegative() {
            // when & then
            val exception = assertThrows<IllegalArgumentException> {
                tank.updateSafetyStock(BigDecimal("-1000.00"))
            }
            assertEquals("안전 재고량은 0 이상이어야 합니다.", exception.message)
        }

        @Test
        @DisplayName("용량보다 큰 안전 재고 설정 시 예외가 발생한다")
        fun updateSafetyStockExceedsCapacity() {
            // when & then
            val exception = assertThrows<IllegalArgumentException> {
                tank.updateSafetyStock(BigDecimal("60000.00")) // 용량 50000L 초과
            }
            assertEquals("안전 재고는 탱크 용량 이하여야 합니다.", exception.message)
        }

        @Test
        @DisplayName("현재 재고가 안전 재고 미만일 때를 감지할 수 있다")
        fun detectBelowSafetyStock() {
            // given
            tank.updateSafetyStock(BigDecimal("10000.00"))
            tank.fill(BigDecimal("8000.00")) // 현재 재고 8000L < 안전 재고 10000L

            // when & then
            assertTrue(tank.isBelowSafetyStock())
        }

        @Test
        @DisplayName("현재 재고가 안전 재고 이상일 때를 감지할 수 있다")
        fun detectAboveSafetyStock() {
            // given
            tank.updateSafetyStock(BigDecimal("10000.00"))
            tank.fill(BigDecimal("15000.00")) // 현재 재고 15000L >= 안전 재고 10000L

            // when & then
            assertFalse(tank.isBelowSafetyStock())
        }

        @Test
        @DisplayName("현재 재고가 안전 재고와 정확히 같으면 미만이 아니다")
        fun stockEqualsToSafetyStock() {
            // given
            tank.updateSafetyStock(BigDecimal("10000.00"))
            tank.fill(BigDecimal("10000.00"))

            // when & then
            assertFalse(tank.isBelowSafetyStock())
        }
    }

    @Nested
    @DisplayName("재고율 계산 테스트")
    inner class StockRateTest {

        @Test
        @DisplayName("빈 탱크의 재고율은 0%이다")
        fun emptyTankStockRate() {
            // when
            val stockRate = tank.getStockRate()

            // then
            assertEquals(BigDecimal("0.00"), stockRate)
        }

        @Test
        @DisplayName("50% 충전 시 재고율은 50%이다")
        fun halfFilledTankStockRate() {
            // given
            tank.fill(BigDecimal("25000.00")) // 50% 충전

            // when
            val stockRate = tank.getStockRate()

            // then
            assertEquals(BigDecimal("50.00"), stockRate)
        }

        @Test
        @DisplayName("가득 찬 탱크의 재고율은 100%이다")
        fun fullTankStockRate() {
            // given
            tank.fill(BigDecimal("50000.00")) // 100% 충전

            // when
            val stockRate = tank.getStockRate()

            // then
            assertEquals(BigDecimal("100.00"), stockRate)
        }

        @Test
        @DisplayName("재고율은 소수점 둘째 자리까지 반올림된다")
        fun stockRateRounding() {
            // given
            tank.fill(BigDecimal("25000.00")) // 정확히 50%

            // when
            val stockRate = tank.getStockRate()

            // then
            assertEquals(2, stockRate.scale(), "Scale should be 2")
            assertEquals(BigDecimal("50.00"), stockRate, "Stock rate should be 50.00")

            // 추가: 다른 비율도 테스트
            tank.consume(BigDecimal("10000.00")) // 15000 / 50000 = 30%
            val stockRate2 = tank.getStockRate()
            assertEquals(2, stockRate2.scale())
            assertEquals(BigDecimal("30.00"), stockRate2)
        }

        @Test
        @DisplayName("용량이 0인 경우 재고율은 0%이다")
        fun zeroCapacityTankStockRate() {
            // given
            val zeroCapacityTank = FuelTankJPAEntity(
                tankNumber = "T-ZERO",
                fuelType = FuelType.GASOLINE_REGULAR,
                capacity = BigDecimal.ZERO
            )

            // when
            val stockRate = zeroCapacityTank.getStockRate()

            // then
            assertEquals(BigDecimal.ZERO, stockRate)
        }
    }

    @Nested
    @DisplayName("점검 정보 관리 테스트")
    inner class InspectionTest {

        @Test
        @DisplayName("점검을 완료하면 점검 날짜가 기록된다")
        fun completeInspection() {
            // given
            val inspectionDate = LocalDate.of(2024, 1, 15)
            val nextInspectionDate = LocalDate.of(2024, 7, 15)

            // when
            tank.completeInspection(inspectionDate, nextInspectionDate)

            // then
            assertEquals(inspectionDate, tank.lastInspectionDate)
            assertEquals(nextInspectionDate, tank.nextInspectionDate)
        }

        @Test
        @DisplayName("다음 점검일이 지났는지 확인할 수 있다")
        fun isInspectionOverDue() {
            // given
            val pastDate = LocalDate.now().minusDays(10)
            tank.completeInspection(LocalDate.now().minusMonths(6), pastDate)

            // when & then
            assertTrue(tank.isInspectionOverDue())
        }

        @Test
        @DisplayName("다음 점검일이 미래이면 점검 기한이 지나지 않았다")
        fun isNotInspectionOverDue() {
            // given
            val futureDate = LocalDate.now().plusDays(30)
            tank.completeInspection(LocalDate.now(), futureDate)

            // when & then
            assertFalse(tank.isInspectionOverDue())
        }
    }

    @Nested
    @DisplayName("상태 관리 테스트")
    inner class StatusTest {

        @Test
        @DisplayName("장비 상태를 변경할 수 있다")
        fun changeStatus() {
            // when
            tank.changeStatus(EquipmentStatus.MAINTENANCE)

            // then
            assertEquals(EquipmentStatus.MAINTENANCE, tank.status)
        }

        @Test
        @DisplayName("ACTIVE 상태일 때만 운영 가능하다")
        fun isOperational() {
            // ACTIVE
            tank.changeStatus(EquipmentStatus.ACTIVE)
            assertTrue(tank.isOperational())

            // MAINTENANCE
            tank.changeStatus(EquipmentStatus.MAINTENANCE)
            assertFalse(tank.isOperational())
            assertTrue(tank.status.isOutOfService())

            // INSPECTION
            tank.changeStatus(EquipmentStatus.INSPECTION)
            assertFalse(tank.isOperational())
            assertTrue(tank.status.isOutOfService())

            // INACTIVE
            tank.changeStatus(EquipmentStatus.INACTIVE)
            assertFalse(tank.isOperational())
            assertTrue(tank.status.isOutOfService())
        }
    }

    @Nested
    @DisplayName("초기 상태 테스트")
    inner class InitialStateTest {

        @Test
        @DisplayName("생성 시 현재 재고는 0이다")
        fun initialCurrentAmountIsZero() {
            assertEquals(BigDecimal.ZERO, tank.currentAmount)
        }

        @Test
        @DisplayName("생성 시 안전 재고는 0이다")
        fun initialSafetyStockIsZero() {
            assertEquals(BigDecimal.ZERO, tank.safetyStock)
        }

        @Test
        @DisplayName("생성 시 상태는 ACTIVE이다")
        fun initialStatusIsActive() {
            assertEquals(EquipmentStatus.ACTIVE, tank.status)
        }

        @Test
        @DisplayName("생성 시 점검 정보는 null이다")
        fun initialInspectionDatesAreNull() {
            assertNull(tank.lastInspectionDate)
            assertNull(tank.nextInspectionDate)
        }
    }

    @Nested
    @DisplayName("복합 시나리오 테스트")
    inner class ComplexScenarioTest {

        @Test
        @DisplayName("충전과 소비를 반복할 수 있다")
        fun fillAndConsumeMultipleTimes() {
            // 1차 충전
            tank.fill(BigDecimal("20000.00"))
            assertEquals(BigDecimal("20000.00"), tank.currentAmount)

            // 1차 소비
            tank.consume(BigDecimal("5000.00"))
            assertEquals(BigDecimal("15000.00"), tank.currentAmount)

            // 2차 충전
            tank.fill(BigDecimal("30000.00"))
            assertEquals(BigDecimal("45000.00"), tank.currentAmount)

            // 2차 소비
            tank.consume(BigDecimal("10000.00"))
            assertEquals(BigDecimal("35000.00"), tank.currentAmount)

            // 재고율 확인
            assertEquals(BigDecimal("70.00"), tank.getStockRate())
        }

        @Test
        @DisplayName("안전 재고 미만으로 떨어진 후 충전하면 정상화된다")
        fun recoverFromLowStock() {
            // given
            tank.updateSafetyStock(BigDecimal("10000.00"))
            tank.fill(BigDecimal("8000.00"))
            assertTrue(tank.isBelowSafetyStock())

            // when: 충전으로 안전 재고 이상 확보
            tank.fill(BigDecimal("5000.00"))

            // then
            assertFalse(tank.isBelowSafetyStock())
            assertEquals(BigDecimal("13000.00"), tank.currentAmount)
        }
    }
}
