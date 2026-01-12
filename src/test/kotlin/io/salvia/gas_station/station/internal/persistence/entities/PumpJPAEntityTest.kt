package io.salvia.gas_station.station.internal.persistence.entities

import io.salvia.gas_station.shared.enums.FuelType
import io.salvia.gas_station.station.internal.domain.EquipmentStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate

@DisplayName("PumpJPAEntity 단위 테스트")
class PumpJPAEntityTest {

    private lateinit var pump: PumpJPAEntity

    @BeforeEach
    fun setUp() {
        pump = PumpJPAEntity(
            pumpNumber = "P-001",
            nozzleCount = 4
        )
    }

    @Nested
    @DisplayName("노즐 추가 테스트")
    inner class AddNozzleTest {

        @Test
        @DisplayName("노즐을 정상적으로 추가할 수 있다")
        fun addNozzleNormally() {
            // given
            val nozzle = PumpNozzleJPAEntity(
                nozzleNumber = 1,
                fuelType = FuelType.GASOLINE_REGULAR
            )

            // when
            pump.addNozzle(nozzle)

            // then
            assertTrue(pump.nozzles.contains(nozzle))
            assertEquals(pump, nozzle.pump)
            assertEquals(1, pump.nozzles.size)
        }

        @Test
        @DisplayName("여러 개의 노즐을 추가할 수 있다")
        fun addMultipleNozzles() {
            // given
            val nozzle1 = PumpNozzleJPAEntity(1, FuelType.GASOLINE_REGULAR)
            val nozzle2 = PumpNozzleJPAEntity(2, FuelType.GASOLINE_PREMIUM)
            val nozzle3 = PumpNozzleJPAEntity(3, FuelType.DISEL)

            // when
            pump.addNozzle(nozzle1)
            pump.addNozzle(nozzle2)
            pump.addNozzle(nozzle3)

            // then
            assertEquals(3, pump.nozzles.size)
            assertTrue(pump.nozzles.containsAll(listOf(nozzle1, nozzle2, nozzle3)))
        }

        @Test
        @DisplayName("노즐 개수가 설정된 개수를 초과하면 예외가 발생한다")
        fun cannotExceedNozzleCount() {
            // given
            val nozzle1 = PumpNozzleJPAEntity(1, FuelType.GASOLINE_REGULAR)
            val nozzle2 = PumpNozzleJPAEntity(2, FuelType.GASOLINE_PREMIUM)
            val nozzle3 = PumpNozzleJPAEntity(3, FuelType.DISEL)
            val nozzle4 = PumpNozzleJPAEntity(4, FuelType.LPG)
            val nozzle5 = PumpNozzleJPAEntity(5, FuelType.KEROSENE)

            pump.addNozzle(nozzle1)
            pump.addNozzle(nozzle2)
            pump.addNozzle(nozzle3)
            pump.addNozzle(nozzle4)

            // when & then: 5번째 노즐 추가 시도
            val exception = assertThrows<IllegalArgumentException> {
                pump.addNozzle(nozzle5)
            }
            assertEquals("노즐 개수는 4개를 초과할 수 없습니다.", exception.message)
            assertEquals(4, pump.nozzles.size)
        }

        @Test
        @DisplayName("같은 유종의 노즐을 중복 추가하면 예외가 발생한다")
        fun cannotAddDuplicateFuelType() {
            // given
            val nozzle1 = PumpNozzleJPAEntity(1, FuelType.GASOLINE_REGULAR)
            val nozzle2 = PumpNozzleJPAEntity(2, FuelType.GASOLINE_REGULAR) // 중복 유종

            pump.addNozzle(nozzle1)

            // when & then
            val exception = assertThrows<IllegalArgumentException> {
                pump.addNozzle(nozzle2)
            }
            assertTrue(exception.message!!.contains("이미"))
            assertTrue(exception.message!!.contains(FuelType.GASOLINE_REGULAR.displayName))
            assertTrue(exception.message!!.contains("노즐이 존재합니다"))
            assertEquals(1, pump.nozzles.size)
        }

        @Test
        @DisplayName("노즐 개수가 정확히 설정된 개수와 같을 때까지 추가할 수 있다")
        fun canAddUpToExactNozzleCount() {
            // given
            val nozzle1 = PumpNozzleJPAEntity(1, FuelType.GASOLINE_REGULAR)
            val nozzle2 = PumpNozzleJPAEntity(2, FuelType.GASOLINE_PREMIUM)
            val nozzle3 = PumpNozzleJPAEntity(3, FuelType.DISEL)
            val nozzle4 = PumpNozzleJPAEntity(4, FuelType.LPG)

            // when
            pump.addNozzle(nozzle1)
            pump.addNozzle(nozzle2)
            pump.addNozzle(nozzle3)
            pump.addNozzle(nozzle4)

            // then
            assertEquals(4, pump.nozzles.size)
        }
    }

    @Nested
    @DisplayName("노즐 제거 테스트")
    inner class RemoveNozzleTest {

        @Test
        @DisplayName("노즐을 정상적으로 제거할 수 있다")
        fun removeNozzleNormally() {
            // given
            val nozzle = PumpNozzleJPAEntity(1, FuelType.GASOLINE_REGULAR)
            pump.addNozzle(nozzle)

            // when
            pump.removeNozzle(nozzle)

            // then
            assertFalse(pump.nozzles.contains(nozzle))
            assertNull(nozzle.pump)
            assertEquals(0, pump.nozzles.size)
        }

        @Test
        @DisplayName("여러 노즐 중 하나만 제거할 수 있다")
        fun removeOneOfMultipleNozzles() {
            // given
            val nozzle1 = PumpNozzleJPAEntity(1, FuelType.GASOLINE_REGULAR)
            val nozzle2 = PumpNozzleJPAEntity(2, FuelType.DISEL)
            val nozzle3 = PumpNozzleJPAEntity(3, FuelType.LPG)

            pump.addNozzle(nozzle1)
            pump.addNozzle(nozzle2)
            pump.addNozzle(nozzle3)

            // when
            pump.removeNozzle(nozzle2)

            // then
            assertEquals(2, pump.nozzles.size)
            assertTrue(pump.nozzles.contains(nozzle1))
            assertFalse(pump.nozzles.contains(nozzle2))
            assertTrue(pump.nozzles.contains(nozzle3))
        }

        @Test
        @DisplayName("노즐 제거 후 다시 추가할 수 있다")
        fun reAddAfterRemoval() {
            // given
            val nozzle = PumpNozzleJPAEntity(1, FuelType.GASOLINE_REGULAR)
            pump.addNozzle(nozzle)
            pump.removeNozzle(nozzle)

            // when: 같은 유종의 새 노즐 추가
            val newNozzle = PumpNozzleJPAEntity(1, FuelType.GASOLINE_REGULAR)
            pump.addNozzle(newNozzle)

            // then
            assertEquals(1, pump.nozzles.size)
            assertTrue(pump.nozzles.contains(newNozzle))
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
            pump.completeInspection(inspectionDate, nextInspectionDate)

            // then
            assertEquals(inspectionDate, pump.lastInspectionDate)
            assertEquals(nextInspectionDate, pump.nextInspectionDate)
        }

        @Test
        @DisplayName("다음 점검일이 지났는지 확인할 수 있다")
        fun isInspectionOverDue() {
            // given
            val pastDate = LocalDate.now().minusDays(10)
            pump.completeInspection(LocalDate.now().minusMonths(6), pastDate)

            // when & then
            assertTrue(pump.isInspectionOverDue())
        }

        @Test
        @DisplayName("다음 점검일이 미래이면 점검 기한이 지나지 않았다")
        fun isNotInspectionOverDue() {
            // given
            val futureDate = LocalDate.now().plusDays(30)
            pump.completeInspection(LocalDate.now(), futureDate)

            // when & then
            assertFalse(pump.isInspectionOverDue())
        }
    }

    @Nested
    @DisplayName("상태 관리 테스트")
    inner class StatusTest {

        @Test
        @DisplayName("장비 상태를 변경할 수 있다")
        fun changeStatus() {
            // when
            pump.changeStatus(EquipmentStatus.MAINTENANCE)

            // then
            assertEquals(EquipmentStatus.MAINTENANCE, pump.status)
        }

        @Test
        @DisplayName("ACTIVE 상태일 때만 운영 가능하다")
        fun isOperational() {
            // ACTIVE
            pump.changeStatus(EquipmentStatus.ACTIVE)
            assertTrue(pump.isOperational())

            // MAINTENANCE
            pump.changeStatus(EquipmentStatus.MAINTENANCE)
            assertFalse(pump.isOperational())
            assertTrue(pump.status.isOutOfService())

            // INSPECTION
            pump.changeStatus(EquipmentStatus.INSPECTION)
            assertFalse(pump.isOperational())

            // INACTIVE
            pump.changeStatus(EquipmentStatus.INACTIVE)
            assertFalse(pump.isOperational())
        }
    }

    @Nested
    @DisplayName("초기 상태 테스트")
    inner class InitialStateTest {

        @Test
        @DisplayName("생성 시 노즐 목록은 비어있다")
        fun initialNozzlesAreEmpty() {
            assertTrue(pump.nozzles.isEmpty())
        }

        @Test
        @DisplayName("생성 시 상태는 ACTIVE이다")
        fun initialStatusIsActive() {
            assertEquals(EquipmentStatus.ACTIVE, pump.status)
        }

        @Test
        @DisplayName("생성 시 점검 정보는 null이다")
        fun initialInspectionDatesAreNull() {
            assertNull(pump.lastInspectionDate)
            assertNull(pump.nextInspectionDate)
        }

        @Test
        @DisplayName("주유기 번호와 노즐 개수가 올바르게 설정된다")
        fun initialPropertiesAreSet() {
            assertEquals("P-001", pump.pumpNumber)
            assertEquals(4, pump.nozzleCount)
        }
    }

    @Nested
    @DisplayName("복합 시나리오 테스트")
    inner class ComplexScenarioTest {

        @Test
        @DisplayName("주유기에 4가지 유종의 노즐을 모두 추가할 수 있다")
        fun addAllFourFuelTypes() {
            // given
            val gasRegular = PumpNozzleJPAEntity(1, FuelType.GASOLINE_REGULAR)
            val gasPremium = PumpNozzleJPAEntity(2, FuelType.GASOLINE_PREMIUM)
            val disel = PumpNozzleJPAEntity(3, FuelType.DISEL)
            val lpg = PumpNozzleJPAEntity(4, FuelType.LPG)

            // when
            pump.addNozzle(gasRegular)
            pump.addNozzle(gasPremium)
            pump.addNozzle(disel)
            pump.addNozzle(lpg)

            // then
            assertEquals(4, pump.nozzles.size)
            assertTrue(pump.nozzles.any { it.fuelType == FuelType.GASOLINE_REGULAR })
            assertTrue(pump.nozzles.any { it.fuelType == FuelType.GASOLINE_PREMIUM })
            assertTrue(pump.nozzles.any { it.fuelType == FuelType.DISEL })
            assertTrue(pump.nozzles.any { it.fuelType == FuelType.LPG })
        }

        @Test
        @DisplayName("노즐을 제거한 후 같은 유종의 노즐을 다시 추가할 수 있다")
        fun replaceNozzleWithSameFuelType() {
            // given
            val oldNozzle = PumpNozzleJPAEntity(1, FuelType.GASOLINE_REGULAR)
            pump.addNozzle(oldNozzle)

            // when: 제거 후 재추가
            pump.removeNozzle(oldNozzle)
            val newNozzle = PumpNozzleJPAEntity(1, FuelType.GASOLINE_REGULAR)
            pump.addNozzle(newNozzle)

            // then
            assertEquals(1, pump.nozzles.size)
            assertFalse(pump.nozzles.contains(oldNozzle))
            assertTrue(pump.nozzles.contains(newNozzle))
        }

        @Test
        @DisplayName("노즐 개수가 2인 주유기는 2개까지만 추가할 수 있다")
        fun twoNozzlePump() {
            // given
            val smallPump = PumpJPAEntity("P-SMALL", 2)
            val nozzle1 = PumpNozzleJPAEntity(1, FuelType.GASOLINE_REGULAR)
            val nozzle2 = PumpNozzleJPAEntity(2, FuelType.DISEL)
            val nozzle3 = PumpNozzleJPAEntity(3, FuelType.LPG)

            // when
            smallPump.addNozzle(nozzle1)
            smallPump.addNozzle(nozzle2)

            // then
            assertEquals(2, smallPump.nozzles.size)

            // 3번째 노즐 추가 시도
            val exception = assertThrows<IllegalArgumentException> {
                smallPump.addNozzle(nozzle3)
            }
            assertEquals("노즐 개수는 2개를 초과할 수 없습니다.", exception.message)
        }

        @Test
        @DisplayName("노즐 없이도 주유기는 정상적으로 존재할 수 있다")
        fun pumpCanExistWithoutNozzles() {
            // when & then
            assertEquals(0, pump.nozzles.size)
            assertEquals(EquipmentStatus.ACTIVE, pump.status)
            assertTrue(pump.isOperational())
        }

        @Test
        @DisplayName("노즐을 추가/제거하면서 최대 개수를 반복적으로 테스트할 수 있다")
        fun repeatedAddAndRemove() {
            // given
            val nozzle1 = PumpNozzleJPAEntity(1, FuelType.GASOLINE_REGULAR)
            val nozzle2 = PumpNozzleJPAEntity(2, FuelType.DISEL)

            // 1차: 추가
            pump.addNozzle(nozzle1)
            pump.addNozzle(nozzle2)
            assertEquals(2, pump.nozzles.size)

            // 2차: 제거
            pump.removeNozzle(nozzle1)
            assertEquals(1, pump.nozzles.size)

            // 3차: 다시 추가
            val nozzle3 = PumpNozzleJPAEntity(3, FuelType.LPG)
            pump.addNozzle(nozzle3)
            assertEquals(2, pump.nozzles.size)

            // 4차: 전체 제거
            pump.removeNozzle(nozzle2)
            pump.removeNozzle(nozzle3)
            assertEquals(0, pump.nozzles.size)
        }
    }
}
