package io.salvia.gas_station.common.entity

import java.time.LocalDate

interface Inspectable {
    var lastInspectionDate: LocalDate?
    var nextInspectionDate: LocalDate?

    fun completeInspection(inspectionDate: LocalDate, nextInspectionDate: LocalDate) {
        this.lastInspectionDate = inspectionDate
        this.nextInspectionDate = nextInspectionDate
    }

    fun isInspectionOverDue(): Boolean {
        val nextDate = nextInspectionDate ?: return false
        return LocalDate.now().isAfter(nextDate)
    }
}