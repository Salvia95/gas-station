package io.salvia.gas_station.common.entity

import io.salvia.gas_station.domain.station.enums.EquipmentStatus

interface Statusable {
    var status: EquipmentStatus

    fun changeStauts(newStatus: EquipmentStatus) {
        this.status = newStatus
    }

    fun isOperational(): Boolean {
        return status == EquipmentStatus.ACTIVE
    }
}
