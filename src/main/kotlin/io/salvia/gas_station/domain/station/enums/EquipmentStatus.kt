package io.salvia.gas_station.domain.station.enums

enum class EquipmentStatus(val description: String) {
    ACTIVE("정상 운영"),
    MAINTENANCE("정비 중"),
    INSPECTION("점검 중"),
    INACTIVE("비활성");

    fun isOperational(): Boolean {
        return this == ACTIVE
    }

    fun isOutOfService(): Boolean {
        return this in listOf(MAINTENANCE, INSPECTION, INACTIVE)
    }
}