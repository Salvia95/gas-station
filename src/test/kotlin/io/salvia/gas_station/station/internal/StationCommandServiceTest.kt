package io.salvia.gas_station.station.internal

import io.salvia.gas_station.shared.enums.FuelType
import io.salvia.gas_station.shared.exceptions.EquipmentNotFoundException
import io.salvia.gas_station.shared.exceptions.FuelTypeMismatchException
import io.salvia.gas_station.shared.exceptions.StationAlreadyExistsException
import io.salvia.gas_station.shared.exceptions.StationNotFoundException
import io.salvia.gas_station.station.StationId
import io.salvia.gas_station.station.internal.domain.StationStatus
import io.salvia.gas_station.station.internal.persistence.StationRepository
import io.salvia.gas_station.station.internal.persistence.entities.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@DisplayName("StationCommandService 통합 테스트")
class StationCommandServiceTest {

    @Autowired
    private lateinit var stationCommandService: StationCommandService

    @Autowired
    private lateinit var stationRepository: StationRepository

    @Nested
    @DisplayName("주유소 생성 테스트")
    inner class CreateStationTest {

        @Test
        @DisplayName("유효한 정보로 주유소를 생성할 수 있다")
        fun createStationSuccessfully() {
            // given
            val command = CreateStationCommand(
                name = "테스트 주유소",
                code = "STN-2024-001",
                address = "서울시 강남구 테스트로 123",
                city = "서울",
                phoneNumber = "02-1234-5678"
            )

            // when
            val stationId = stationCommandService.createStation(command)

            // then
            assertNotNull(stationId)
            assertTrue(stationId.value > 0)

            // DB 검증
            val savedStation = stationRepository.findById(stationId.value).orElse(null)
            assertNotNull(savedStation)
            assertEquals(command.name, savedStation.name)
            assertEquals(command.code, savedStation.code)
            assertEquals(command.address, savedStation.address)
            assertEquals(command.city, savedStation.city)
            assertEquals(command.phoneNumber, savedStation.phoneNumber)
            assertEquals(StationStatus.ACTIVE, savedStation.status)
        }

        @Test
        @DisplayName("중복된 코드로 주유소를 생성하면 예외가 발생한다")
        fun throwsExceptionWhenCodeAlreadyExists() {
            // given
            val command1 = CreateStationCommand(
                name = "첫 번째 주유소",
                code = "STN-2024-001",
                address = "서울시 강남구",
                city = "서울",
                phoneNumber = "02-1111-1111"
            )
            stationCommandService.createStation(command1)

            val command2 = CreateStationCommand(
                name = "두 번째 주유소",
                code = "STN-2024-001", // 중복 코드
                address = "서울시 서초구",
                city = "서울",
                phoneNumber = "02-2222-2222"
            )

            // when & then
            assertThrows<StationAlreadyExistsException> {
                stationCommandService.createStation(command2)
            }
        }
    }

    @Nested
    @DisplayName("주유소 수정 테스트")
    inner class UpdateStationTest {

        private var stationId: StationId? = null

        @BeforeEach
        fun setUp() {
            val command = CreateStationCommand(
                name = "원본 주유소",
                code = "STN-2024-100",
                address = "서울시 강남구",
                city = "서울",
                phoneNumber = "02-0000-0000"
            )
            stationId = stationCommandService.createStation(command)
        }

        @Test
        @DisplayName("주유소 기본 정보를 수정할 수 있다")
        fun updateStationSuccessfully() {
            // given
            val updateCommand = UpdateStationCommand(
                name = "수정된 주유소",
                address = "서울시 서초구 수정로 456",
                city = "부산",
                phoneNumber = "051-9999-9999"
            )

            // when
            stationCommandService.updateStation(stationId!!, updateCommand)

            // then
            val updatedStation = stationRepository.findById(stationId!!.value).orElseThrow()
            assertEquals("수정된 주유소", updatedStation.name)
            assertEquals("서울시 서초구 수정로 456", updatedStation.address)
            assertEquals("부산", updatedStation.city)
            assertEquals("051-9999-9999", updatedStation.phoneNumber)
        }

        @Test
        @DisplayName("일부 정보만 수정할 수 있다")
        fun updatePartialInformation() {
            // given
            val originalStation = stationRepository.findById(stationId!!.value).orElseThrow()
            val updateCommand = UpdateStationCommand(
                name = "이름만 변경",
                city = "대구"
            )

            // when
            stationCommandService.updateStation(stationId!!, updateCommand)

            // then
            val updatedStation = stationRepository.findById(stationId!!.value).orElseThrow()
            assertEquals("이름만 변경", updatedStation.name)
            assertEquals("대구", updatedStation.city)
            assertEquals(originalStation.address, updatedStation.address) // 유지
            assertEquals(originalStation.phoneNumber, updatedStation.phoneNumber) // 유지
        }

        @Test
        @DisplayName("존재하지 않는 주유소를 수정하면 예외가 발생한다")
        fun throwsExceptionWhenStationNotFound() {
            // given
            val invalidStationId = StationId(99999L)
            val updateCommand = UpdateStationCommand(name = "존재하지 않음")

            // when & then
            assertThrows<StationNotFoundException> {
                stationCommandService.updateStation(invalidStationId, updateCommand)
            }
        }
    }

    @Nested
    @DisplayName("주유소 상태 변경 테스트")
    inner class ChangeStationStatusTest {

        private var stationId: StationId? = null

        @BeforeEach
        fun setUp() {
            val command = CreateStationCommand(
                name = "상태 테스트 주유소",
                code = "STN-2024-200",
                address = "서울시",
                city = "서울",
                phoneNumber = "02-0000-0000"
            )
            stationId = stationCommandService.createStation(command)
        }

        @Test
        @DisplayName("주유소를 일시 중지 상태로 변경할 수 있다")
        fun changeToSuspended() {
            // when
            stationCommandService.changeStationStatus(stationId!!, StationStatus.SUSPENDED)

            // then
            val station = stationRepository.findById(stationId!!.value).orElseThrow()
            assertEquals(StationStatus.SUSPENDED, station.status)
            assertFalse(station.isOperating())
        }

        @Test
        @DisplayName("주유소를 폐쇄 상태로 변경할 수 있다")
        fun changeToClosed() {
            // when
            stationCommandService.changeStationStatus(stationId!!, StationStatus.CLOSED)

            // then
            val station = stationRepository.findById(stationId!!.value).orElseThrow()
            assertEquals(StationStatus.CLOSED, station.status)
            assertFalse(station.isOperating())
        }

        @Test
        @DisplayName("중지된 주유소를 다시 활성화할 수 있다")
        fun reactivateSuspendedStation() {
            // given
            stationCommandService.changeStationStatus(stationId!!, StationStatus.SUSPENDED)

            // when
            stationCommandService.changeStationStatus(stationId!!, StationStatus.ACTIVE)

            // then
            val station = stationRepository.findById(stationId!!.value).orElseThrow()
            assertEquals(StationStatus.ACTIVE, station.status)
            assertTrue(station.isOperating())
        }
    }

    @Nested
    @DisplayName("주유소 삭제 테스트")
    inner class DeleteStationTest {

        @Test
        @DisplayName("주유소를 삭제할 수 있다")
        fun deleteStationSuccessfully() {
            // given
            val command = CreateStationCommand(
                name = "삭제될 주유소",
                code = "STN-2024-300",
                address = "서울시",
                city = "서울",
                phoneNumber = "02-0000-0000"
            )
            val stationId = stationCommandService.createStation(command)

            // when
            stationCommandService.deleteStation(stationId)

            // then
            assertFalse(stationRepository.existsById(stationId.value))
        }

        @Test
        @DisplayName("존재하지 않는 주유소를 삭제하면 예외가 발생한다")
        fun throwsExceptionWhenDeletingNonExistentStation() {
            // given
            val invalidStationId = StationId(99999L)

            // when & then
            assertThrows<StationNotFoundException> {
                stationCommandService.deleteStation(invalidStationId)
            }
        }
    }

    @Nested
    @DisplayName("운영 정보 설정 테스트")
    inner class SetOperationInfoTest {

        private var stationId: StationId? = null

        @BeforeEach
        fun setUp() {
            val command = CreateStationCommand(
                name = "운영 정보 테스트 주유소",
                code = "STN-2024-400",
                address = "서울시",
                city = "서울",
                phoneNumber = "02-0000-0000"
            )
            stationId = stationCommandService.createStation(command)
        }

        @Test
        @DisplayName("24시간 운영 정보를 설정할 수 있다")
        fun set24HoursOperation() {
            // given
            val command = SetOperationInfoCommand(
                stationType = StationType.SELF_SERVICE,
                is24Hours = true,
                hasConvenienceStore = true
            )

            // when
            stationCommandService.setOperationInfo(stationId!!, command)

            // then
            val station = stationRepository.findById(stationId!!.value).orElseThrow()
            assertNotNull(station.operationInfo)
            assertTrue(station.operationInfo!!.is24Hours)
            assertEquals(StationType.SELF_SERVICE, station.operationInfo!!.stationType)
            assertTrue(station.operationInfo!!.hasConvenienceStore)
        }

        @Test
        @DisplayName("일반 영업 시간을 설정할 수 있다")
        fun setNormalOperatingHours() {
            // given
            val command = SetOperationInfoCommand(
                stationType = StationType.FULL_SERVICE,
                is24Hours = false,
                openingTime = LocalTime.of(8, 0),
                closingTime = LocalTime.of(22, 0),
                hasCarWash = true,
                carWashType = CarWashType.AUTOMATIC
            )

            // when
            stationCommandService.setOperationInfo(stationId!!, command)

            // then
            val station = stationRepository.findById(stationId!!.value).orElseThrow()
            assertNotNull(station.operationInfo)
            assertFalse(station.operationInfo!!.is24Hours)
            assertEquals(LocalTime.of(8, 0), station.operationInfo!!.openingTime)
            assertEquals(LocalTime.of(22, 0), station.operationInfo!!.closingTime)
            assertTrue(station.operationInfo!!.hasCarWash)
            assertEquals(CarWashType.AUTOMATIC, station.operationInfo!!.carWashType)
        }
    }

    @Nested
    @DisplayName("유류 탱크 추가 테스트")
    inner class AddFuelTankTest {

        private var stationId: StationId? = null

        @BeforeEach
        fun setUp() {
            val command = CreateStationCommand(
                name = "탱크 테스트 주유소",
                code = "STN-2024-500",
                address = "서울시",
                city = "서울",
                phoneNumber = "02-0000-0000"
            )
            stationId = stationCommandService.createStation(command)
        }

        @Test
        @DisplayName("유류 탱크를 추가할 수 있다")
        fun addFuelTankSuccessfully() {
            // given
            val command = AddFuelTankCommand(
                tankNumber = "T-001",
                fuelType = FuelType.GASOLINE_REGULAR,
                capacity = BigDecimal("50000.00"),
                safetyStock = BigDecimal("5000.00"),
                installationDate = LocalDate.now()
            )

            // when
            val tankId = stationCommandService.addFuelTank(stationId!!, command)

            // then
            assertTrue(tankId > 0)

            val station = stationRepository.findById(stationId!!.value).orElseThrow()
            assertEquals(1, station.tanks.size)

            val tank = station.tanks.first()
            assertEquals("T-001", tank.tankNumber)
            assertEquals(FuelType.GASOLINE_REGULAR, tank.fuelType)
            assertEquals(0, tank.capacity.compareTo(BigDecimal("50000.00")))
            assertEquals(0, tank.safetyStock.compareTo(BigDecimal("5000.00")))
        }

        @Test
        @DisplayName("여러 개의 탱크를 추가할 수 있다")
        fun addMultipleTanks() {
            // given
            val command1 = AddFuelTankCommand("T-001", FuelType.GASOLINE_REGULAR, BigDecimal("50000"))
            val command2 = AddFuelTankCommand("T-002", FuelType.DISEL, BigDecimal("40000"))
            val command3 = AddFuelTankCommand("T-003", FuelType.LPG, BigDecimal("30000"))

            // when
            stationCommandService.addFuelTank(stationId!!, command1)
            stationCommandService.addFuelTank(stationId!!, command2)
            stationCommandService.addFuelTank(stationId!!, command3)

            // then
            val station = stationRepository.findById(stationId!!.value).orElseThrow()
            assertEquals(3, station.tanks.size)
        }
    }

    @Nested
    @DisplayName("주유기 추가 테스트")
    inner class AddPumpTest {

        private var stationId: StationId? = null

        @BeforeEach
        fun setUp() {
            val command = CreateStationCommand(
                name = "주유기 테스트 주유소",
                code = "STN-2024-600",
                address = "서울시",
                city = "서울",
                phoneNumber = "02-0000-0000"
            )
            stationId = stationCommandService.createStation(command)
        }

        @Test
        @DisplayName("주유기를 추가할 수 있다")
        fun addPumpSuccessfully() {
            // given
            val command = AddPumpCommand(
                pumpNumber = "P-001",
                nozzleCount = 4,
                installationDate = LocalDate.now()
            )

            // when
            val pumpId = stationCommandService.addPump(stationId!!, command)

            // then
            assertTrue(pumpId > 0)

            val station = stationRepository.findById(stationId!!.value).orElseThrow()
            assertEquals(1, station.pumps.size)

            val pump = station.pumps.first()
            assertEquals("P-001", pump.pumpNumber)
            assertEquals(4, pump.nozzleCount)
        }
    }

    @Nested
    @DisplayName("노즐 추가 테스트")
    inner class AddNozzleTest {

        private var stationId: StationId? = null
        private var tankId: Long = 0
        private var pumpId: Long = 0

        @BeforeEach
        fun setUp() {
            // 주유소 생성
            val stationCommand = CreateStationCommand(
                name = "노즐 테스트 주유소",
                code = "STN-2024-700",
                address = "서울시",
                city = "서울",
                phoneNumber = "02-0000-0000"
            )
            stationId = stationCommandService.createStation(stationCommand)

            // 탱크 추가
            val tankCommand = AddFuelTankCommand(
                tankNumber = "T-001",
                fuelType = FuelType.GASOLINE_REGULAR,
                capacity = BigDecimal("50000")
            )
            tankId = stationCommandService.addFuelTank(stationId!!, tankCommand)

            // 주유기 추가
            val pumpCommand = AddPumpCommand(
                pumpNumber = "P-001",
                nozzleCount = 4
            )
            pumpId = stationCommandService.addPump(stationId!!, pumpCommand)
        }

        @Test
        @DisplayName("노즐을 추가할 수 있다")
        fun addNozzleSuccessfully() {
            // given
            val command = AddNozzleCommand(
                nozzleNumber = 1,
                fuelType = FuelType.GASOLINE_REGULAR,
                tankId = tankId
            )

            // when
            val nozzleId = stationCommandService.addNozzle(stationId!!, pumpId, command)

            // then
            assertTrue(nozzleId > 0)

            val station = stationRepository.findById(stationId!!.value).orElseThrow()
            val pump = station.pumps.first()
            assertEquals(1, pump.nozzles.size)

            val nozzle = pump.nozzles.first()
            assertEquals(1, nozzle.nozzleNumber)
            assertEquals(FuelType.GASOLINE_REGULAR, nozzle.fuelType)
            assertEquals(tankId, nozzle.connectedTank?.id)
        }

        @Test
        @DisplayName("유종이 다른 탱크와 노즐을 연결하면 예외가 발생한다")
        fun throwsExceptionWhenFuelTypeMismatch() {
            // given: 경유 탱크 추가
            val diselTankCommand = AddFuelTankCommand(
                tankNumber = "T-002",
                fuelType = FuelType.DISEL,
                capacity = BigDecimal("40000")
            )
            val diselTankId = stationCommandService.addFuelTank(stationId!!, diselTankCommand)

            // 휘발유 노즐에 경유 탱크 연결 시도
            val command = AddNozzleCommand(
                nozzleNumber = 1,
                fuelType = FuelType.GASOLINE_REGULAR,
                tankId = diselTankId // 유종 불일치
            )

            // when & then
            assertThrows<FuelTypeMismatchException> {
                stationCommandService.addNozzle(stationId!!, pumpId, command)
            }
        }
    }
}
