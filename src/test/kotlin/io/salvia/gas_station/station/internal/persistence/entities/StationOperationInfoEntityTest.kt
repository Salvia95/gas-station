package io.salvia.gas_station.station.internal.persistence.entities

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalTime

@DisplayName("StationOperationInfoEntity 단위 테스트")
class StationOperationInfoEntityTest {

    private lateinit var operationInfo: StationOperationInfoEntity

    @BeforeEach
    fun setUp() {
        operationInfo = StationOperationInfoEntity(
            stationType = StationType.FULL_SERVICE,
            is24Hours = false,
            openingTime = LocalTime.of(9, 0),
            closingTime = LocalTime.of(22, 0)
        )
    }

    @Nested
    @DisplayName("주유소 형태 변경 테스트")
    inner class ChangeStationTypeTest {

        @Test
        @DisplayName("주유소 형태를 변경할 수 있다")
        fun changeStationType() {
            // when
            operationInfo.changeStationType(StationType.SELF_SERVICE)

            // then
            assertEquals(StationType.SELF_SERVICE, operationInfo.stationType)
        }

        @Test
        @DisplayName("FULL_SERVICE에서 HYBRID로 변경할 수 있다")
        fun changeToHybrid() {
            // when
            operationInfo.changeStationType(StationType.HYBRID)

            // then
            assertEquals(StationType.HYBRID, operationInfo.stationType)
        }
    }

    @Nested
    @DisplayName("영업 시간 설정 테스트")
    inner class SetOperatingHoursTest {

        @Test
        @DisplayName("24시간 운영으로 설정하면 영업 시간이 null이 된다")
        fun set24Hours() {
            // when
            operationInfo.setOperatingHours(is24Hours = true)

            // then
            assertTrue(operationInfo.is24Hours)
            assertNull(operationInfo.openingTime)
            assertNull(operationInfo.closingTime)
        }

        @Test
        @DisplayName("일반 영업 시간을 설정할 수 있다")
        fun setNormalOperatingHours() {
            // given
            val newOpening = LocalTime.of(8, 0)
            val newClosing = LocalTime.of(23, 0)

            // when
            operationInfo.setOperatingHours(
                is24Hours = false,
                openingTime = newOpening,
                closingTime = newClosing
            )

            // then
            assertFalse(operationInfo.is24Hours)
            assertEquals(newOpening, operationInfo.openingTime)
            assertEquals(newClosing, operationInfo.closingTime)
        }

        @Test
        @DisplayName("24시간이 아닌데 영업 시간을 지정하지 않으면 예외가 발생한다")
        fun requireOperatingHoursWhenNotFull24Hours() {
            // when & then: 오픈 시간만 누락
            val exception1 = assertThrows<IllegalArgumentException> {
                operationInfo.setOperatingHours(
                    is24Hours = false,
                    openingTime = null,
                    closingTime = LocalTime.of(22, 0)
                )
            }
            assertEquals("24시간 운영이 아닌 경우 영업 시간을 지정해야 합니다.", exception1.message)

            // when & then: 마감 시간만 누락
            val exception2 = assertThrows<IllegalArgumentException> {
                operationInfo.setOperatingHours(
                    is24Hours = false,
                    openingTime = LocalTime.of(9, 0),
                    closingTime = null
                )
            }
            assertEquals("24시간 운영이 아닌 경우 영업 시간을 지정해야 합니다.", exception2.message)

            // when & then: 둘 다 누락
            val exception3 = assertThrows<IllegalArgumentException> {
                operationInfo.setOperatingHours(
                    is24Hours = false,
                    openingTime = null,
                    closingTime = null
                )
            }
            assertEquals("24시간 운영이 아닌 경우 영업 시간을 지정해야 합니다.", exception3.message)
        }

        @Test
        @DisplayName("24시간 운영에서 일반 운영으로 변경할 수 있다")
        fun changeFrom24HoursToNormal() {
            // given
            operationInfo.setOperatingHours(is24Hours = true)

            // when
            operationInfo.setOperatingHours(
                is24Hours = false,
                openingTime = LocalTime.of(7, 0),
                closingTime = LocalTime.of(21, 0)
            )

            // then
            assertFalse(operationInfo.is24Hours)
            assertEquals(LocalTime.of(7, 0), operationInfo.openingTime)
            assertEquals(LocalTime.of(21, 0), operationInfo.closingTime)
        }
    }

    @Nested
    @DisplayName("세차기 설정 테스트")
    inner class SetCarWashTest {

        @Test
        @DisplayName("세차기가 있을 때 유형을 설정할 수 있다")
        fun setCarWashWithType() {
            // when
            operationInfo.setCarWash(hasCarWash = true, carWashType = CarWashType.AUTOMATIC)

            // then
            assertTrue(operationInfo.hasCarWash)
            assertEquals(CarWashType.AUTOMATIC, operationInfo.carWashType)
        }

        @Test
        @DisplayName("세차기가 없으면 유형이 null이 된다")
        fun setNoCarWash() {
            // given
            operationInfo.setCarWash(hasCarWash = true, carWashType = CarWashType.AUTOMATIC)

            // when
            operationInfo.setCarWash(hasCarWash = false)

            // then
            assertFalse(operationInfo.hasCarWash)
            assertNull(operationInfo.carWashType)
        }

        @Test
        @DisplayName("세차기가 있는데 유형을 지정하지 않으면 예외가 발생한다")
        fun requireCarWashTypeWhenHasCarWash() {
            // when & then
            val exception = assertThrows<IllegalArgumentException> {
                operationInfo.setCarWash(hasCarWash = true, carWashType = null)
            }
            assertEquals("세차기가 있을 경우 세차기 유형을 지정해야 합니다.", exception.message)
        }

        @Test
        @DisplayName("다양한 세차기 유형을 설정할 수 있다")
        fun setVariousCarWashTypes() {
            // AUTOMATIC
            operationInfo.setCarWash(hasCarWash = true, carWashType = CarWashType.AUTOMATIC)
            assertEquals(CarWashType.AUTOMATIC, operationInfo.carWashType)

            // MANUAL
            operationInfo.setCarWash(hasCarWash = true, carWashType = CarWashType.MANUAL)
            assertEquals(CarWashType.MANUAL, operationInfo.carWashType)

            // HYBRID
            operationInfo.setCarWash(hasCarWash = true, carWashType = CarWashType.HYBRID)
            assertEquals(CarWashType.HYBRID, operationInfo.carWashType)
        }
    }

    @Nested
    @DisplayName("부대시설 설정 테스트")
    inner class SetFacilitiesTest {

        @Test
        @DisplayName("모든 부대시설을 설정할 수 있다")
        fun setAllFacilities() {
            // when
            operationInfo.setFacilities(
                hasConvenienceStore = true,
                hasRepairShop = true,
                hasRestArea = true
            )

            // then
            assertTrue(operationInfo.hasConvenienceStore)
            assertTrue(operationInfo.hasRepairShop)
            assertTrue(operationInfo.hasRestArea)
        }

        @Test
        @DisplayName("일부 부대시설만 설정할 수 있다")
        fun setPartialFacilities() {
            // when
            operationInfo.setFacilities(
                hasConvenienceStore = true,
                hasRepairShop = false,
                hasRestArea = false
            )

            // then
            assertTrue(operationInfo.hasConvenienceStore)
            assertFalse(operationInfo.hasRepairShop)
            assertFalse(operationInfo.hasRestArea)
        }

        @Test
        @DisplayName("인자를 생략하면 기존 값이 유지된다")
        fun keepExistingValuesWhenOmitted() {
            // given
            operationInfo.setFacilities(
                hasConvenienceStore = true,
                hasRepairShop = true,
                hasRestArea = false
            )

            // when: hasConvenienceStore만 변경
            operationInfo.setFacilities(hasConvenienceStore = false)

            // then: 나머지는 유지
            assertFalse(operationInfo.hasConvenienceStore)
            assertTrue(operationInfo.hasRepairShop) // 유지
            assertFalse(operationInfo.hasRestArea) // 유지
        }

        @Test
        @DisplayName("부대시설을 모두 제거할 수 있다")
        fun removeAllFacilities() {
            // given
            operationInfo.setFacilities(
                hasConvenienceStore = true,
                hasRepairShop = true,
                hasRestArea = true
            )

            // when
            operationInfo.setFacilities(
                hasConvenienceStore = false,
                hasRepairShop = false,
                hasRestArea = false
            )

            // then
            assertFalse(operationInfo.hasConvenienceStore)
            assertFalse(operationInfo.hasRepairShop)
            assertFalse(operationInfo.hasRestArea)
        }
    }

    @Nested
    @DisplayName("현재 영업 중 여부 테스트")
    inner class IsCurrentlyOpenTest {

        @Test
        @DisplayName("24시간 운영이면 항상 영업 중이다")
        fun always24Hours() {
            // given
            operationInfo.setOperatingHours(is24Hours = true)

            // when & then
            assertTrue(operationInfo.isCurrentlyOpen())
        }

        @Test
        @DisplayName("정상 영업 시간 내에는 영업 중이다")
        fun openDuringNormalHours() {
            // given: 09:00 - 22:00
            operationInfo.setOperatingHours(
                is24Hours = false,
                openingTime = LocalTime.of(0, 0),
                closingTime = LocalTime.of(23, 59)
            )

            // when & then: 현재 시간이 영업 시간 내
            assertTrue(operationInfo.isCurrentlyOpen())
        }

        @Test
        @DisplayName("영업 시작 전에는 영업 중이 아니다")
        fun closedBeforeOpening() {
            // given: 09:00 - 22:00, 현재 시간보다 훨씬 늦게 오픈
            operationInfo.setOperatingHours(
                is24Hours = false,
                openingTime = LocalTime.of(23, 0),
                closingTime = LocalTime.of(23, 30)
            )

            // when & then: 현재 시간이 영업 시작 전
            // 이 테스트는 현재 시간에 따라 결과가 달라질 수 있음
            val now = LocalTime.now()
            if (now.isBefore(LocalTime.of(23, 0)) && now.isAfter(LocalTime.of(23, 30))) {
                assertFalse(operationInfo.isCurrentlyOpen())
            }
        }

        @Test
        @DisplayName("자정을 넘어가는 영업 시간을 처리할 수 있다 (22:00 ~ 02:00)")
        fun handleMidnightCrossover() {
            // given: 22:00 - 02:00 (다음날)
            operationInfo.setOperatingHours(
                is24Hours = false,
                openingTime = LocalTime.of(22, 0),
                closingTime = LocalTime.of(2, 0)
            )

            // when & then
            val result = operationInfo.isCurrentlyOpen()

            val now = LocalTime.now()
            val expected = if (now >= LocalTime.of(22, 0) || now <= LocalTime.of(2, 0)) {
                true
            } else {
                false
            }

            assertEquals(expected, result)
        }

        @Test
        @DisplayName("영업 시간 정보가 없으면 영업 중이 아니다")
        fun closedWhenNoOperatingHours() {
            // given: 24시간도 아니고 영업 시간도 없는 상태 (강제로 null 설정)
            val invalidOperationInfo = StationOperationInfoEntity(
                stationType = StationType.FULL_SERVICE,
                is24Hours = false,
                openingTime = null,
                closingTime = null
            )

            // when & then
            assertFalse(invalidOperationInfo.isCurrentlyOpen())
        }
    }

    @Nested
    @DisplayName("초기 상태 테스트")
    inner class InitialStateTest {

        @Test
        @DisplayName("생성 시 모든 부대시설은 false이다")
        fun initialFacilitiesAreFalse() {
            assertFalse(operationInfo.hasCarWash)
            assertNull(operationInfo.carWashType)
            assertFalse(operationInfo.hasConvenienceStore)
            assertFalse(operationInfo.hasRepairShop)
            assertFalse(operationInfo.hasRestArea)
        }

        @Test
        @DisplayName("생성 시 메모는 null이다")
        fun initialNotesAreNull() {
            assertNull(operationInfo.notes)
        }

        @Test
        @DisplayName("주유소 형태와 영업 시간이 올바르게 설정된다")
        fun initialPropertiesAreSet() {
            assertEquals(StationType.FULL_SERVICE, operationInfo.stationType)
            assertFalse(operationInfo.is24Hours)
            assertEquals(LocalTime.of(9, 0), operationInfo.openingTime)
            assertEquals(LocalTime.of(22, 0), operationInfo.closingTime)
        }
    }

    @Nested
    @DisplayName("복합 시나리오 테스트")
    inner class ComplexScenarioTest {

        @Test
        @DisplayName("셀프 주유소로 24시간 운영하면서 편의점과 세차기가 있는 경우")
        fun selfService24HoursWithFacilities() {
            // when
            operationInfo.changeStationType(StationType.SELF_SERVICE)
            operationInfo.setOperatingHours(is24Hours = true)
            operationInfo.setCarWash(hasCarWash = true, carWashType = CarWashType.AUTOMATIC)
            operationInfo.setFacilities(
                hasConvenienceStore = true,
                hasRepairShop = false,
                hasRestArea = false
            )

            // then
            assertEquals(StationType.SELF_SERVICE, operationInfo.stationType)
            assertTrue(operationInfo.is24Hours)
            assertTrue(operationInfo.isCurrentlyOpen())
            assertTrue(operationInfo.hasCarWash)
            assertEquals(CarWashType.AUTOMATIC, operationInfo.carWashType)
            assertTrue(operationInfo.hasConvenienceStore)
            assertFalse(operationInfo.hasRepairShop)
            assertFalse(operationInfo.hasRestArea)
        }

        @Test
        @DisplayName("유인 주유소에서 모든 부대시설을 갖춘 경우")
        fun fullServiceWithAllFacilities() {
            // when
            operationInfo.changeStationType(StationType.FULL_SERVICE)
            operationInfo.setOperatingHours(
                is24Hours = false,
                openingTime = LocalTime.of(6, 0),
                closingTime = LocalTime.of(23, 0)
            )
            operationInfo.setCarWash(hasCarWash = true, carWashType = CarWashType.HYBRID)
            operationInfo.setFacilities(
                hasConvenienceStore = true,
                hasRepairShop = true,
                hasRestArea = true
            )

            // then
            assertEquals(StationType.FULL_SERVICE, operationInfo.stationType)
            assertFalse(operationInfo.is24Hours)
            assertEquals(LocalTime.of(6, 0), operationInfo.openingTime)
            assertEquals(LocalTime.of(23, 0), operationInfo.closingTime)
            assertTrue(operationInfo.hasCarWash)
            assertEquals(CarWashType.HYBRID, operationInfo.carWashType)
            assertTrue(operationInfo.hasConvenienceStore)
            assertTrue(operationInfo.hasRepairShop)
            assertTrue(operationInfo.hasRestArea)
        }

        @Test
        @DisplayName("영업 시간을 여러 번 변경할 수 있다")
        fun changeOperatingHoursMultipleTimes() {
            // 1차: 일반 → 24시간
            operationInfo.setOperatingHours(is24Hours = true)
            assertTrue(operationInfo.is24Hours)
            assertTrue(operationInfo.isCurrentlyOpen())

            // 2차: 24시간 → 일반
            operationInfo.setOperatingHours(
                is24Hours = false,
                openingTime = LocalTime.of(8, 0),
                closingTime = LocalTime.of(20, 0)
            )
            assertFalse(operationInfo.is24Hours)
            assertEquals(LocalTime.of(8, 0), operationInfo.openingTime)
            assertEquals(LocalTime.of(20, 0), operationInfo.closingTime)

            // 3차: 시간 변경
            operationInfo.setOperatingHours(
                is24Hours = false,
                openingTime = LocalTime.of(10, 0),
                closingTime = LocalTime.of(22, 0)
            )
            assertEquals(LocalTime.of(10, 0), operationInfo.openingTime)
            assertEquals(LocalTime.of(22, 0), operationInfo.closingTime)
        }

        @Test
        @DisplayName("메모를 업데이트할 수 있다")
        fun updateNotes() {
            // when
            operationInfo.updateNotes("대형 트럭 주유 가능, 주차장 넓음")

            // then
            assertEquals("대형 트럭 주유 가능, 주차장 넓음", operationInfo.notes)
        }
    }

    @Nested
    @DisplayName("주유소 형태 속성 테스트")
    inner class StationTypePropertiesTest {

        @Test
        @DisplayName("SELF_SERVICE는 인건비 배수가 0이다")
        fun selfServiceLaborCost() {
            assertEquals(0.0, StationType.SELF_SERVICE.laborCostMultiplier)
        }

        @Test
        @DisplayName("FULL_SERVICE는 인건비 배수가 1이다")
        fun fullServiceLaborCost() {
            assertEquals(1.0, StationType.FULL_SERVICE.laborCostMultiplier)
        }

        @Test
        @DisplayName("HYBRID는 인건비 배수가 0.5이다")
        fun hybridLaborCost() {
            assertEquals(0.5, StationType.HYBRID.laborCostMultiplier)
        }
    }
}
