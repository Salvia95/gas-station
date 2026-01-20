package io.salvia.gas_station.shared.exceptions

/**
 * 기본 비즈니스 예외
 */
abstract class BusinessException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

/**
 * 주유소를 찾을 수 없을 때 발생하는 예외
 */
class StationNotFoundException(
    stationId: Long
) : BusinessException("주유소를 찾을 수 없습니다. (ID: $stationId)")

/**
 * 주유소 코드가 이미 존재할 때 발생하는 예외
 */
class StationAlreadyExistsException(
    code: String
) : BusinessException("주유소 코드가 이미 존재합니다. (Code: $code)")

/**
 * 잘못된 주유소 상태 변경 시도 시 발생하는 예외
 */
class InvalidStationStatusException(
    currentStatus: String,
    requestedStatus: String
) : BusinessException("주유소 상태를 변경할 수 없습니다. (현재: $currentStatus, 요청: $requestedStatus)")

/**
 * 장비를 찾을 수 없을 때 발생하는 예외
 */
class EquipmentNotFoundException(
    equipmentType: String,
    equipmentId: Long
) : BusinessException("${equipmentType}를 찾을 수 없습니다. (ID: $equipmentId)")

/**
 * 유종 불일치 시 발생하는 예외
 */
class FuelTypeMismatchException(
    expected: String,
    actual: String
) : BusinessException("유종이 일치하지 않습니다. (예상: $expected, 실제: $actual)")

/**
 * 탱크 용량 초과 시 발생하는 예외
 */
class TankCapacityExceededException(
    tankId: Long,
    capacity: String,
    requestedAmount: String
) : BusinessException("탱크 용량을 초과했습니다. (탱크 ID: $tankId, 용량: $capacity, 요청: $requestedAmount)")

/**
 * 재고 부족 시 발생하는 예외
 */
class InsufficientStockException(
    tankId: Long,
    currentStock: String,
    requestedAmount: String
) : BusinessException("재고가 부족합니다. (탱크 ID: $tankId, 현재 재고: $currentStock, 요청: $requestedAmount)")

/**
 * 잘못된 입력값 예외
 */
class InvalidInputException(
    fieldName: String,
    reason: String
) : BusinessException("입력값이 올바르지 않습니다. ($fieldName: $reason)")
