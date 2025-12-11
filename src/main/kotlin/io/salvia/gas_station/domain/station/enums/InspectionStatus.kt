package io.salvia.gas_station.domain.station.enums

enum class InspectionStatus(val description: String) {
    SCHEDULED("점검 예정"),
    IN_PROGRESS("진행 중"),
    COMPLETED_NORMAL("완료-정상"),
    COMPLETED_WARNING("완료-주의"),
    COMPLETED_ACTION_REQUIRED("완료-조치 필요"),
    CANCELED("취소됨");

    fun isCompleted(): Boolean {
        return this in listOf(
            COMPLETED_NORMAL,
            COMPLETED_WARNING,
            COMPLETED_ACTION_REQUIRED
        )
    }

    fun needAction(): Boolean {
        return this in listOf(
            COMPLETED_WARNING, COMPLETED_ACTION_REQUIRED
        )
    }
}