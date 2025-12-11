package io.salvia.gas_station.domain.station.enums

enum class StationStatus(val description: String) {
    ACTIVE("정상 운영"),
    SUSPENDED("일시 중단"),
    CLOSED("폐쇄");

    fun isOperating(): Boolean {
        return this == ACTIVE
    }
}