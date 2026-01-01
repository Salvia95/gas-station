package io.salvia.gas_station.shared

import java.time.LocalDate

/**
 * 점검이 필요한 엔티티를 위한 인터페이스
 */
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
