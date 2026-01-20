package io.salvia.gas_station.station.internal

import io.salvia.gas_station.shared.exceptions.*
import io.salvia.gas_station.station.StationId
import io.salvia.gas_station.station.internal.domain.StationStatus
import io.salvia.gas_station.station.internal.persistence.StationRepository
import io.salvia.gas_station.station.internal.persistence.entities.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional
internal class StationCommandServiceImpl(
    private val stationRepository: StationRepository
) : StationCommandService {

    override fun createStation(command: CreateStationCommand): StationId {
        // 중복 코드 검증
        if (stationRepository.existsByCode(command.code)) {
            throw StationAlreadyExistsException(command.code)
        }

        // 주유소 생성
        val station = StationJPAEntity(
            name = command.name,
            code = command.code,
            address = command.address,
            city = command.city,
            phoneNumber = command.phoneNumber
        )

        val savedStation = stationRepository.save(station)
        return StationId(savedStation.id!!)
    }

    override fun updateStation(stationId: StationId, command: UpdateStationCommand) {
        val station = findStationById(stationId)

        station.updateBasicInfo(
            name = command.name,
            address = command.address,
            city = command.city,
            phoneNumber = command.phoneNumber
        )
    }

    override fun changeStationStatus(stationId: StationId, status: StationStatus) {
        val station = findStationById(stationId)

        when (status) {
            StationStatus.ACTIVE -> station.activate()
            StationStatus.SUSPENDED -> station.suspend()
            StationStatus.CLOSED -> station.close()
        }
    }

    override fun deleteStation(stationId: StationId) {
        val station = findStationById(stationId)
        stationRepository.delete(station)
    }

    override fun setOperationInfo(stationId: StationId, command: SetOperationInfoCommand) {
        val station = findStationById(stationId)

        val operationInfo = StationOperationInfoEntity(
            stationType = command.stationType,
            is24Hours = command.is24Hours,
            openingTime = command.openingTime,
            closingTime = command.closingTime
        )

        // 세차기 정보 설정
        if (command.hasCarWash) {
            operationInfo.setCarWash(true, command.carWashType)
        }

        // 부대시설 설정
        operationInfo.setFacilities(
            hasConvenienceStore = command.hasConvenienceStore,
            hasRepairShop = command.hasRepairShop,
            hasRestArea = command.hasRestArea
        )

        station.assignOperationInfo(operationInfo)
    }

    override fun addFuelTank(stationId: StationId, command: AddFuelTankCommand): Long {
        val station = findStationById(stationId)

        val tank = FuelTankJPAEntity(
            tankNumber = command.tankNumber,
            fuelType = command.fuelType,
            capacity = command.capacity
        )

        // 안전 재고 설정
        if (command.safetyStock > BigDecimal.ZERO) {
            tank.updateSafetyStock(command.safetyStock)
        }

        // 설치일 설정
        command.installationDate?.let {
            tank.setInstallationInfo(it)
        }

        station.addTank(tank)

        return tank.id!!
    }

    override fun addPump(stationId: StationId, command: AddPumpCommand): Long {
        val station = findStationById(stationId)

        val pump = PumpJPAEntity(
            pumpNumber = command.pumpNumber,
            nozzleCount = command.nozzleCount
        )

        command.installationDate?.let {
            // Pump에 설치일 설정 메서드 추가 필요 (현재는 protected)
            // 임시로 리플렉션 사용하거나, 엔티티에 public 메서드 추가
        }

        station.addPump(pump)

        return pump.id!!
    }

    override fun addNozzle(stationId: StationId, pumpId: Long, command: AddNozzleCommand): Long {
        val station = findStationById(stationId)

        // 주유기 찾기
        val pump = station.pumps.find { it.id == pumpId }
            ?: throw EquipmentNotFoundException("주유기", pumpId)

        // 탱크 찾기
        val tank = station.tanks.find { it.id == command.tankId }
            ?: throw EquipmentNotFoundException("탱크", command.tankId)

        // 유종 검증
        if (tank.fuelType != command.fuelType) {
            throw FuelTypeMismatchException(
                expected = command.fuelType.displayName,
                actual = tank.fuelType.displayName
            )
        }

        // 노즐 생성 및 연결
        val nozzle = PumpNozzleJPAEntity(
            nozzleNumber = command.nozzleNumber,
            fuelType = command.fuelType
        )
        nozzle.connectTank(tank)

        pump.addNozzle(nozzle)

        return nozzle.id!!
    }

    override fun addHomeLorry(stationId: StationId, command: AddHomeLorryCommand): Long {
        val station = findStationById(stationId)

        val homeLorry = HomeLorryJPAEntity(
            homeLorryNumber = command.homeLorryNumber,
            fuelType = command.fuelType,
            capacity = command.capacity
        )

        station.addHomeLorry(homeLorry)

        return homeLorry.id!!
    }

    private fun findStationById(stationId: StationId): StationJPAEntity {
        return stationRepository.findById(stationId.value).orElseThrow {
            StationNotFoundException(stationId.value)
        }
    }
}
