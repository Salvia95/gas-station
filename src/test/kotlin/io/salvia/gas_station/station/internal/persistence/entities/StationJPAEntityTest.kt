package io.salvia.gas_station.station.internal.persistence.entities

import io.salvia.gas_station.shared.enums.FuelType
import io.salvia.gas_station.station.internal.domain.EquipmentStatus
import io.salvia.gas_station.station.internal.domain.StationStatus
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@DisplayName("StationJPAEntity 단위 테스트")
class StationJPAEntityTest {

    private lateinit var station: StationJPAEntity

    @BeforeEach
    fun setUp() {
        station = StationJPAEntity(
            name = "테스트 주유소",
            code = "STN-2024-001",
            address = "서울시 강남구 테스트로 123",
            city = "서울",
            phoneNumber = "02-1234-5678"
        )
    }

    @Nested
    @DisplayName("기본 정보 업데이트 테스트")
    inner class UpdateBasicInfoTest {

        @Test
        @DisplayName("모든 정보를 업데이트할 수 있다")
        fun updateAllInfo() {
            // given
            val newName = "새로운 주유소"
            val newAddress = "서울시 서초구 새로운로 456"
            val newCity = "부산"
            val newPhoneNumber = "051-9876-5432"

            // when
            station.updateBasicInfo(
                name = newName,
                address = newAddress,
                city = newCity,
                phoneNumber = newPhoneNumber
            )

            // then
            assertEquals(newName, station.name)
            assertEquals(newAddress, station.address)
            assertEquals(newCity, station.city)
            assertEquals(newPhoneNumber, station.phoneNumber)
        }

        @Test
        @DisplayName("일부 정보만 업데이트할 수 있다")
        fun updatePartialInfo() {
            // given
            val originalName = station.name
            val originalAddress = station.address
            val newCity = "대구"
            val newPhoneNumber = "053-1111-2222"

            // when
            station.updateBasicInfo(
                city = newCity,
                phoneNumber = newPhoneNumber
            )

            // then
            assertEquals(originalName, station.name)
            assertEquals(originalAddress, station.address)
            assertEquals(newCity, station.city)
            assertEquals(newPhoneNumber, station.phoneNumber)
        }

        @Test
        @DisplayName("아무 정보도 전달하지 않으면 변경되지 않는다")
        fun updateNothingIfAllNull() {
            // given
            val originalName = station.name
            val originalAddress = station.address
            val originalCity = station.city
            val originalPhoneNumber = station.phoneNumber

            // when
            station.updateBasicInfo()

            // then
            assertEquals(originalName, station.name)
            assertEquals(originalAddress, station.address)
            assertEquals(originalCity, station.city)
            assertEquals(originalPhoneNumber, station.phoneNumber)
        }
    }

    @Nested
    @DisplayName("운영 정보 할당 테스트")
    inner class AssignOperationInfoTest {

        @Test
        @DisplayName("운영 정보를 할당하면 양방향 관계가 설정된다")
        fun assignOperationInfo() {
            // given
            val operationInfo = StationOperationInfoEntity(
                stationType = StationType.SELF_SERVICE,
                is24Hours = true,
                openingTime = null,
                closingTime = null
            )

            // when
            station.assignOperationInfo(operationInfo)

            // then
            assertEquals(operationInfo, station.operationInfo)
            assertEquals(station, operationInfo.station)
        }
    }

    @Nested
    @DisplayName("탱크 관리 테스트")
    inner class TankManagementTest {

        @Test
        @DisplayName("탱크를 추가하면 양방향 관계가 설정된다")
        fun addTank() {
            // given
            val tank = FuelTankJPAEntity(
                tankNumber = "T-001",
                fuelType = FuelType.GASOLINE_REGULAR,
                capacity = BigDecimal("50000.00")
            )

            // when
            station.addTank(tank)

            // then
            assertTrue(station.tanks.contains(tank))
            assertEquals(station, tank.station)
            assertEquals(1, station.tanks.size)
        }

        @Test
        @DisplayName("여러 탱크를 추가할 수 있다")
        fun addMultipleTanks() {
            // given
            val tank1 = FuelTankJPAEntity("T-001", FuelType.GASOLINE_REGULAR, BigDecimal("50000"))
            val tank2 = FuelTankJPAEntity("T-002", FuelType.DISEL, BigDecimal("40000"))
            val tank3 = FuelTankJPAEntity("T-003", FuelType.LPG, BigDecimal("30000"))

            // when
            station.addTank(tank1)
            station.addTank(tank2)
            station.addTank(tank3)

            // then
            assertEquals(3, station.tanks.size)
            assertTrue(station.tanks.containsAll(listOf(tank1, tank2, tank3)))
        }

        @Test
        @DisplayName("탱크를 제거하면 양방향 관계가 해제된다")
        fun removeTank() {
            // given
            val tank = FuelTankJPAEntity("T-001", FuelType.GASOLINE_REGULAR, BigDecimal("50000"))
            station.addTank(tank)

            // when
            station.removeTank(tank)

            // then
            assertFalse(station.tanks.contains(tank))
            assertNull(tank.station)
            assertEquals(0, station.tanks.size)
        }
    }

    @Nested
    @DisplayName("홈로리 관리 테스트")
    inner class HomeLorryManagementTest {

        @Test
        @DisplayName("홈로리를 추가하면 양방향 관계가 설정된다")
        fun addHomeLorry() {
            // given
            val homeLorry = HomeLorryJPAEntity(
                homeLorryNumber = "HL-001",
                fuelType = FuelType.KEROSENE,
                capacity = BigDecimal("10000.00")
            )

            // when
            station.addHomeLorry(homeLorry)

            // then
            assertTrue(station.homeLorrys.contains(homeLorry))
            assertEquals(station, homeLorry.station)
            assertEquals(1, station.homeLorrys.size)
        }

        @Test
        @DisplayName("홈로리를 제거하면 양방향 관계가 해제된다")
        fun removeHomeLorry() {
            // given
            val homeLorry = HomeLorryJPAEntity("HL-001", FuelType.KEROSENE, BigDecimal("10000"))
            station.addHomeLorry(homeLorry)

            // when
            station.removeHomeLorry(homeLorry)

            // then
            assertFalse(station.homeLorrys.contains(homeLorry))
            assertNull(homeLorry.station)
        }
    }

    @Nested
    @DisplayName("주유기 관리 테스트")
    inner class PumpManagementTest {

        @Test
        @DisplayName("주유기를 추가하면 양방향 관계가 설정된다")
        fun addPump() {
            // given
            val pump = PumpJPAEntity(
                pumpNumber = "P-001",
                nozzleCount = 4
            )

            // when
            station.addPump(pump)

            // then
            assertTrue(station.pumps.contains(pump))
            assertEquals(station, pump.station)
            assertEquals(1, station.pumps.size)
        }

        @Test
        @DisplayName("주유기를 제거하면 양방향 관계가 해제된다")
        fun removePump() {
            // given
            val pump = PumpJPAEntity("P-001", 4)
            station.addPump(pump)

            // when
            station.removePump(pump)

            // then
            assertFalse(station.pumps.contains(pump))
            assertNull(pump.station)
        }
    }

    @Nested
    @DisplayName("점검 관리 테스트")
    inner class InspectionManagementTest {

        @Test
        @DisplayName("점검을 추가하면 양방향 관계가 설정된다")
        fun addInspection() {
            // given
            val inspection = InspectionEntity(
                inspectionType = InspectionType.REGULAR,
                inspectionTarget = InspectionTarget.STATION,
                scheduledDate = java.time.LocalDate.now(),
                inspector = "홍길동"
            )

            // when
            station.addInspection(inspection)

            // then
            assertTrue(station.inspections.contains(inspection))
            assertEquals(station, inspection.station)
        }
    }

    @Nested
    @DisplayName("상태 관리 테스트")
    inner class StatusManagementTest {

        @Test
        @DisplayName("주유소를 활성화할 수 있다")
        fun activate() {
            // given
            station.suspend()

            // when
            station.activate()

            // then
            assertEquals(StationStatus.ACTIVE, station.status)
            assertTrue(station.isOperating())
        }

        @Test
        @DisplayName("주유소를 일시 중지할 수 있다")
        fun suspend() {
            // when
            station.suspend()

            // then
            assertEquals(StationStatus.SUSPENDED, station.status)
            assertFalse(station.isOperating())
        }

        @Test
        @DisplayName("주유소를 폐쇄할 수 있다")
        fun close() {
            // when
            station.close()

            // then
            assertEquals(StationStatus.CLOSED, station.status)
            assertFalse(station.isOperating())
        }

        @Test
        @DisplayName("ACTIVE 상태일 때만 운영 중으로 판단한다")
        fun isOperating() {
            // ACTIVE 상태
            station.activate()
            assertTrue(station.isOperating())

            // SUSPENDED 상태
            station.suspend()
            assertFalse(station.isOperating())

            // CLOSED 상태
            station.close()
            assertFalse(station.isOperating())
        }
    }

    @Nested
    @DisplayName("초기 상태 테스트")
    inner class InitialStateTest {

        @Test
        @DisplayName("생성 시 기본 상태는 ACTIVE이다")
        fun defaultStatusIsActive() {
            assertEquals(StationStatus.ACTIVE, station.status)
        }

        @Test
        @DisplayName("생성 시 모든 컬렉션은 비어있다")
        fun allCollectionsAreEmpty() {
            assertTrue(station.tanks.isEmpty())
            assertTrue(station.homeLorrys.isEmpty())
            assertTrue(station.pumps.isEmpty())
            assertTrue(station.inspections.isEmpty())
        }

        @Test
        @DisplayName("생성 시 운영 정보는 null이다")
        fun operationInfoIsNull() {
            assertNull(station.operationInfo)
        }
    }
}
