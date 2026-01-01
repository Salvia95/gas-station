package io.salvia.gas_station.station.internal

/**
 * 장비 상태 관리 인터페이스 (station 모듈 내부 전용)
 */
internal interface Statusable {
    var status: EquipmentStatus

    fun changeStatus(newStatus: EquipmentStatus) {
        this.status = newStatus
    }

    fun isOperational(): Boolean {
        return status == EquipmentStatus.ACTIVE
    }
}
