package io.salvia.gas_station.station.internal

import io.salvia.gas_station.shared.enums.FuelType
import io.salvia.gas_station.station.internal.domain.StationStatus
import io.salvia.gas_station.station.internal.persistence.entities.StationType
import io.salvia.gas_station.station.internal.persistence.entities.CarWashType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

/**
 * 주유소 생성 커맨드
 */
data class CreateStationCommand(
    val name: String,
    val code: String,
    val address: String,
    val city: String,
    val phoneNumber: String
)

/**
 * 주유소 기본 정보 수정 커맨드
 */
data class UpdateStationCommand(
    val name: String? = null,
    val address: String? = null,
    val city: String? = null,
    val phoneNumber: String? = null
)

/**
 * 주유소 운영 정보 설정 커맨드
 */
data class SetOperationInfoCommand(
    val stationType: StationType,
    val is24Hours: Boolean,
    val openingTime: LocalTime? = null,
    val closingTime: LocalTime? = null,
    val hasCarWash: Boolean = false,
    val carWashType: CarWashType? = null,
    val hasConvenienceStore: Boolean = false,
    val hasRepairShop: Boolean = false,
    val hasRestArea: Boolean = false
)

/**
 * 유류 탱크 추가 커맨드
 */
data class AddFuelTankCommand(
    val tankNumber: String,
    val fuelType: FuelType,
    val capacity: BigDecimal,
    val safetyStock: BigDecimal = BigDecimal.ZERO,
    val installationDate: LocalDate? = null
)

/**
 * 주유기 추가 커맨드
 */
data class AddPumpCommand(
    val pumpNumber: String,
    val nozzleCount: Int,
    val installationDate: LocalDate? = null
)

/**
 * 노즐 추가 커맨드
 */
data class AddNozzleCommand(
    val nozzleNumber: Int,
    val fuelType: FuelType,
    val tankId: Long
)

/**
 * 홈로리 추가 커맨드
 */
data class AddHomeLorryCommand(
    val homeLorryNumber: String,
    val fuelType: FuelType,
    val capacity: BigDecimal
)
